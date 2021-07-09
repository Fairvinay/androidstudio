package com.jwell.suite;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;

import androidx.appcompat.app.AppCompatActivity;

public class PopUpActivity extends AppCompatActivity {
 // ttps://www.youtube.com/watch?v=fn5OlqQuOCk
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_up);
		TextView textView = (TextView) findViewById(R.id.txtScanStatus);
		 Bundle b = getIntent().getExtras();
        String fullReport= b.getString("Report");
		String partialReport = fullReport.trim();
        String wcText = textView.getText().toString();
        textView.setText(partialReport);
        // Adjust the Popup Window Dimensions
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getRealMetrics(dm);
        int width = dm.widthPixels;
        int height  = dm.heightPixels;
        getWindow().setLayout((int)(width*.5), (int)(height*.1));

    }
}
/* showing Alert Dialog
https://stackoverflow.com/questions/17264600/surfaceview-inside-dialog-or-activity-with-theme-dialog-has-layout-glitch
Good DEtailed
https://stackoverflow.com/questions/22526933/show-a-pop-up-dialog-at-the-end-of-the-game-on-surfaceview
Handler
https://stackoverflow.com/questions/3875184/cant-create-handler-inside-thread-that-has-not-called-looper-prepare/16886486#16886486
Blur Surfac vire
https://stackoverflow.com/questions/15565538/android-blur-surfaceview-used-in-camera
Dialog in Surface
https://stackoverflow.com/questions/14609723/showing-dialog-in-surfaceview


 */