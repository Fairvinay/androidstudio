package com.jwell.suite;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.content.SharedPreferences;
//import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
  
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.CameraSource.Builder;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

public class ScannedBarcodeActivity extends AppCompatActivity {

    private static final String TAGCLASS = "ScannedBarcodeActivity.class";
    SurfaceView surfaceView;
    TextView txtBarcodeValue;
    private BarcodeDetector barcodeDetector;


    private CameraSource cameraSource;
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    Button btnAction;
    Button btnReport;
    String intentData = "";
    boolean isEmail = false;
    android.app.AlertDialog alertDialogAct;
	android.app.AlertDialog.Builder alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanned_barcode);

        initViews();
        initialiseDetectorsAndSources();
    }

    private void initViews() {
        txtBarcodeValue = findViewById(R.id.txtBarcodeValue);
        surfaceView = findViewById(R.id.surfaceView);
        btnAction = findViewById(R.id.btnAction);
        btnReport = findViewById(R.id.btnReport);

        Log.v(TAGCLASS, "initViews=" );
         alertDialog = new android.app.AlertDialog.Builder(ScannedBarcodeActivity.this);
		alertDialogAct = alertDialog.create();

		btnAction.setEnabled(false);
        btnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (intentData.length() > 0) {
                   Intent reportPage  = new Intent (ScannedBarcodeActivity.this , ScanReportActivity.class);
                        Bundle b =new Bundle();
                        b.putString("Report",intentData);
                        reportPage.putExtras(b);
                        startActivity(reportPage);

                    }else {
                        Toast.makeText(ScannedBarcodeActivity.this,"Scan Report Error ",Toast.LENGTH_SHORT).show();
                    }

/*
                    if (isEmail)
                        startActivity(new Intent(ScannedBarcodeActivity.this, EmailActivity.class).putExtra("email_address", intentData));
                    else {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(intentData)));
                    }*/
                }
          });

        btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (intentData.length() > 0) {
					
					Toast.makeText(ScannedBarcodeActivity.this,"Scan Done ",Toast.LENGTH_SHORT).show();
				
                    if (isEmail)
                        startActivity(new Intent(ScannedBarcodeActivity.this, EmailActivity.class).putExtra("email_address", intentData));
                    else {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(intentData)));
                    }
                }


            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.v(TAGCLASS, "onActivityResult=" );
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initialiseDetectorsAndSources() {
        String autoFocus = "false";
        Log.v(TAGCLASS, "initialiseDetectorsAndSources=" );
        Toast.makeText(getApplicationContext(), "Barcode scanner started", Toast.LENGTH_SHORT).show();


        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.ALL_FORMATS | Barcode.DATA_MATRIX | Barcode.QR_CODE
                | Barcode.EAN_13 | Barcode.EAN_8 | Barcode.UPC_A | Barcode.UPC_E | Barcode.CODE_39 | Barcode.CODE_93 |
                Barcode.CODE_128 | Barcode.ITF | Barcode.CODABAR | Barcode.ISBN |
                Barcode.PDF417 | Barcode.AZTEC
                        /*BarcodeFormat.RSS_14, BarcodeFormat.RSS_EXPANDED |  BarcodeFormat.UPC_EAN_EXTENSION*/)
                .build();
        /*BarcodeFormat.CODABAR, BarcodeFormat.CODE_39, BarcodeFormat.CODE_93,
                BarcodeFormat.CODE_128, BarcodeFormat.EAN_8, BarcodeFormat.EAN_13,
                BarcodeFormat.ITF, BarcodeFormat.RSS_14, BarcodeFormat.RSS_EXPANDED,
                BarcodeFormat.UPC_A, BarcodeFormat.UPC_E, BarcodeFormat.UPC_EAN_EXTENSION*/

        cameraSource = new com.google.android.gms.vision.CameraSource.Builder(getApplicationContext(), barcodeDetector)
                .setRequestedPreviewSize(1920, 1080)
                .setAutoFocusEnabled(true)
               // .setFocusMode( Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE) //you should add this feature
                .build();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(ScannedBarcodeActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(surfaceView.getHolder());
                     //   Log.v(TAGCLASS, "surfaceCreated=" );
                    } else {
                        ActivityCompat.requestPermissions(ScannedBarcodeActivity.this, new
                                String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                       // Log.v(TAGCLASS, "surfaceCreation Permit Requested=" );
                    }

                } catch (IOException e) {
                   // Log.v(TAGCLASS, "surfaceCreation exception=" );
                    e.printStackTrace();
                }


            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });
 

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
                //Log.v(TAGCLASS, "setProcessor release=" );
                Toast.makeText(getApplicationContext(), "To prevent memory leaks barcode scanner has been stopped", Toast.LENGTH_SHORT).show();
            }
            
			public void showDialogs(SparseArray<Barcode> barcodes ) { 
			
			if (alertDialog !=null && alertDialogAct !=null) {
						 
						 if(alertDialogAct.isShowing()){
							 alertDialogAct.dismiss();
					        Toast.makeText(ScannedBarcodeActivity.this, "Dialog was showing", 5).show();
							alertDialog = new android.app.AlertDialog.Builder(ScannedBarcodeActivity.this);
						 }
						 else {
						    Toast.makeText(ScannedBarcodeActivity.this, "Dialog was not showing", 5).show();
							alertDialog = new android.app.AlertDialog.Builder(ScannedBarcodeActivity.this);
						 }
						 alertDialog.setTitle("Scanner");
						 alertDialog.setMessage("Scan Done");
						 alertDialog.setNegativeButton("ReScan", new DialogInterface.OnClickListener() {    
										  @Override 
								 public void onClick(DialogInterface arg0, int arg1) {
											  //try { 
												   Toast.makeText(ScannedBarcodeActivity.this, "ReScan", 5).show();
												  txtBarcodeValue.setText("");
												//cameraSource.start(surfaceView.getHolder());
												/*} catch (IOException e) {
													e.printStackTrace();
												}
												*/
								 }
							});
						  alertDialog.setPositiveButton("Report", new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface arg0, int arg1) {
										Toast.makeText(ScannedBarcodeActivity.this, "Report", 5).show();
										Intent reportPage  = new Intent (ScannedBarcodeActivity.this , ScanReportActivity.class);
				
										 Bundle b =new Bundle();
										b.putString("Report",intentData);
										reportPage.putExtras(b);
										startActivity(reportPage);
										//startActivity(new Intent(ScannedBarcodeActivity.this,PopUpActivity.class));
									}
								});
						   alertDialogAct = alertDialog.create();
						   alertDialog.show();
			       }
			
			}
            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                Log.v(TAGCLASS, "receiveDetections " );
                StringBuffer strBarDetails = new StringBuffer();
                if (barcodes.size() != 0) {
					 //cameraSource.stop();
					 
                    // Done Scan
                    // show PopUp Activtity
                    // showPopup();
				   txtBarcodeValue.post(new Runnable() {

                        @Override
                        public void run() {
						    //txtBarcodeValue.setVisibility(View.INVISIBLE);
							 strBarDetails.append("barcode size");
							 strBarDetails.append(barcodes.size()+" ");
							 txtBarcodeValue.setText(barcodes.size()+" ");
							 if( barcodes.size() ==1 ) { 
								  strBarDetails.append(barcodes.valueAt(0).displayValue);
						      	 intentData = barcodes.valueAt(0).displayValue;
						      } 
							else { for (int i=0 ; i < barcodes.size(); i++  ){
								 //intentData = barcodes.valueAt(i).displayValue;
								  int key  =  barcodes.keyAt(i);
								  Object value  =  barcodes.valueAt(i).displayValue;
								 
								  strBarDetails.append(key);
								  strBarDetails.append('=');
								  if (value != this) {
									 strBarDetails.append(value);
									  strBarDetails.append("\n");
								 } else {
									 strBarDetails.append("(this Map)");
								 }
							 }
							  intentData = strBarDetails.toString();
							}
							
							txtBarcodeValue.setText(intentData);
							 if(intentData.indexOf("@") > -1 ) {
								 isEmail = true; 
								btnAction.setEnabled(true);
							 }
							 else { 
									// disable button 
								btnAction.setEnabled(false);
							 }
							showDialogs(barcodes);
				       }
                    });
                       
                }
            }
        });
    }

    // show Popup saying Scanning done and now release the camera source
    private void showPopup(){

        startActivity(new Intent(ScannedBarcodeActivity.this,PopUpActivity.class));

        cameraSource.stop();
        cameraSource.release();
    }
    @Override
    protected void onPause() {
        super.onPause();
        Log.v(TAGCLASS, "onPause " );
        cameraSource.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initialiseDetectorsAndSources();
    }
}
/*

	                            if (barcodes.valueAt(0).email != null) {
                                txtBarcodeValue.removeCallbacks(null);
                                intentData = barcodes.valueAt(0).email.address;

                                txtBarcodeValue.setText(intentData);
                                isEmail = true;
                                btnAction.setText("ADD CONTENT TO THE MAIL");
                            } else {
                                isEmail = false;
                                btnAction.setText("LAUNCH URL");
                                intentData = barcodes.valueAt(0).displayValue;
                                txtBarcodeValue.setText(intentData);

                            }
							
*/
