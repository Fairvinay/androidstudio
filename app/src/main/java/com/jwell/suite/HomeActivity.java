package com.jwell.suite;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.jwell.suite.R;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    DatabaseHelper db;
    Button mButtonScanQR;
    Button mButtonBarCodeQR;
	SharedPreferences pref;
    private static final String TAG = "HomeActivity";
    
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                default :
                    if (!isFinishing()) {
                        Log.v(TAG,  "is finishing now  ");
                    }
                    break;
            }
        }
    };
    private long pressedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        db = new DatabaseHelper(this);
        mButtonScanQR = (Button)findViewById(R.id.button_scan_qr);
        mButtonBarCodeQR= (Button)findViewById(R.id.button_barcode_qr);
        Bundle b = getIntent().getExtras();
        String username= b.getString("Username");
		if(username== null || username.equals("") ) {
			 // get from the preferences 
			 pref = getSharedPreferences("user_details",MODE_PRIVATE);
			if(pref.contains("username") && pref.contains("password")){
					 username=pref.getString("username",null);
			}
		}
        TextView textView = (TextView) findViewById(R.id.textView);
        //String userName =  savedInstanceState.getString("userName");
        String user = username.trim();
        String wcText = textView.getText().toString();
        textView.setText(wcText +" " +user);
        mButtonScanQR.setOnClickListener(getScanQRListener());
        mButtonBarCodeQR.setOnClickListener(getScanQRListener());
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


    public View.OnClickListener  getScanQRListener() {

        return new View.OnClickListener() {
            public void onClick(View view) {

               switch (view.getId()) {
                   case R.id.button_scan_qr:
                       scanActivity();
                     break;
                   case R.id.button_barcode_qr :
                       scanQRCodeActivity();
                        break;

               }
            }


        };

    }
     private void scanActivity () {

         try {

             //Intent intent = new Intent("com.google.zxing.client.android.SCAN");
             //intent.putExtra("SCAN_MODE", "QR_CODE_MODE"); // "PRODUCT_MODE for bar codes
             Intent intent =new Intent(HomeActivity.this, PictureBarcodeActivity.class);
             startActivity(intent);
             //startActivityForResult(intent, 0);

         } catch (Exception e) {

             Uri marketUri = Uri.parse("market://details?id=com.google.zxing.client.android");

             Intent marketIntent = new Intent(Intent.ACTION_VIEW,marketUri);
             startActivity(marketIntent);

         }



     }
    private void scanQRCodeActivity () {

        try {

            //Intent intent = new Intent("com.google.zxing.client.android.SCAN");
            //intent.putExtra("SCAN_MODE", "QR_CODE_MODE"); // "PRODUCT_MODE for bar codes
            Intent intent =new Intent(HomeActivity.this, ScannedBarcodeActivity.class);
            startActivity(intent);
           //startActivityForResult(intent, 0);

        } catch (Exception e) {

            Uri marketUri = Uri.parse("market://details?id=com.google.zxing.client.android");

            Intent marketIntent = new Intent(Intent.ACTION_VIEW,marketUri);
            startActivity(marketIntent);

        }



    }

    public void startApplication(String packageName)
    {
        try
        {
            Intent intent = new Intent("android.intent.action.MAIN");
            intent.addCategory("android.intent.category.LAUNCHER");

            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            List<ResolveInfo> resolveInfoList = getPackageManager().queryIntentActivities(intent, 0);

            for(ResolveInfo info : resolveInfoList)
                if(info.activityInfo.packageName.equalsIgnoreCase(packageName))
                {
                    launchComponent(info.activityInfo.packageName, info.activityInfo.name);
                    return;
                }
            Intent launchIntent = getPackageManager().getLaunchIntentForPackage(packageName);
            // No match, so application is not installed
            showInMarket(packageName);
        }
        catch (Exception e)
        {
            showInMarket(packageName);
        }
    }

    private void launchComponent(String packageName, String name)
    {
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.LAUNCHER");
        intent.setComponent(new ComponentName(packageName, name));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(intent);
    }

    private void showInMarket(String packageName)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
       ArrayList<String> runningactivities = new ArrayList<String>();
//
//    ActivityManager activityManager = (ActivityManager)getBaseContext().getSystemService (Context.ACTIVITY_SERVICE);
//
//    List<ActivityManager.RunningTaskInfo> services = activityManager.getRunningTasks(Integer.MAX_VALUE);

//    for (int i1 = 0; i1 < services.size(); i1++) {
//        runningactivities.add(0,services.get(i1).topActivity.toString());
//    }
//
//    if(runningactivities.contains("ComponentInfo{com.app/com.app.main.MyActivity}")==true){
//        Toast.makeText(getBaseContext(),"Activity is in foreground, active",1000).show();
//
//        alert.show()
//    }

}