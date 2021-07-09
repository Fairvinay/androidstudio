package com.jwell.suite;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
	
     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DatabaseHelper(this);
		pref = getSharedPreferences("user_details",MODE_PRIVATE);
		
        mTextUsername = (EditText) findViewById(R.id.edittext_username);
  
        mTextPassword = (EditText) findViewById(R.id.edittext_password);
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
}