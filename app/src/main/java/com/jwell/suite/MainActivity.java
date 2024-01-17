package com.jwell.suite;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.SharedPreferences;


public class MainActivity extends AppCompatActivity {
    EditText mTextUsername;
    EditText mTextPassword;
    Button mButtonLogin;
    TextView mTextViewRegister;
    DatabaseHelper db;
	
	SharedPreferences pref;
    private long pressedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DatabaseHelper(this);
		pref = getSharedPreferences("user_details",MODE_PRIVATE);
		
        mTextUsername = (EditText) findViewById(R.id.edittext_username);

        mTextPassword = (EditText) findViewById(R.id.edittext_password);
        // ScrollView view = findViewById(R.id.nested_login_main_view);
         View view  = findViewById(android.R.id.content).getRootView();
         // Set up touch listener for non-text box views to hide keyboard.
       //  if (!(view instanceof EditText)) {
             view.setOnTouchListener(new View.OnTouchListener() {
                 public boolean onTouch(View v, MotionEvent event) {
                     hideSoftKeyboard(MainActivity.this);
                     return false;
                 }
             });
      //   }
        mButtonLogin = (Button)findViewById(R.id.button_login);
        mTextViewRegister = (TextView)findViewById(R.id.textview_register);
        mTextViewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent =  new Intent(MainActivity.this, RegisterActivity.class);

                startActivity(registerIntent);
            }
        });
        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = mTextUsername.getText().toString().trim();
                String pwd = mTextPassword.getText().toString().trim();
				
				
				
                Boolean res = db.checkUser(user ,pwd);
                if (res == true){
                   // Toast.makeText(MainActivity.this,"Successfully Logged in",Toast.LENGTH_SHORT).show();
                    Intent homePage  = new Intent (MainActivity.this,HomeActivity.class);
                    Bundle b =new Bundle();
                    b.putString("Username",user);
                    homePage.putExtras(b);
					SharedPreferences.Editor editor = pref.edit();
                    editor.putString("username",user);
                    editor.putString("password",pwd);
                    editor.commit();
					
                    startActivity(homePage);

                }else {
                    Toast.makeText(MainActivity.this,"Login Error ",Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    @Override
    public void onBackPressed() {

        //webView.goBack();
        // Toast.makeText(MainActivity.this,"Webview Can Go Back ", Toast.LENGTH_SHORT).show();
        if (pressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();

        } else {
            Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT).show();
        }
        pressedTime = System.currentTimeMillis();


    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        if(inputMethodManager.isAcceptingText()){
            inputMethodManager.hideSoftInputFromWindow(
                    activity.getCurrentFocus().getWindowToken(),
                    0
            );
        }
    }

}