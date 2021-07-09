package com.jwell.suite;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.jwell.util.CheckIsXML;
import com.jwell.util.ParserDocument;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
/*
https://stackoverflow.com/questions/1972381/how-to-get-the-devices-imei-esn-programmatically-in-android/3976800

*/
public class PictureBarcodeActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnOpenCamera;
    TextView txtResultBody;
	ParserDocument pd ;
	SharedPreferences pref;
    private BarcodeDetector detector;
    private Uri imageUri;
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private static final int CAMERA_REQUEST = 101;
    private static final String TAG = "API123";
    private static final String SAVED_INSTANCE_URI = "uri";
    private static final String SAVED_INSTANCE_RESULT = "result";
    private static final String TAGCLASS = "PictureBarcodeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_barcode);
		 pref = getSharedPreferences("user_details",MODE_PRIVATE);
        pd = new ParserDocument();
		pd.setAppActivity(PictureBarcodeActivity.this);
		pd.setSharedPref(pref);
        initViews();

        if (savedInstanceState != null) {
            if (imageUri != null) {
                imageUri = Uri.parse(savedInstanceState.getString(SAVED_INSTANCE_URI));
                txtResultBody.setText(savedInstanceState.getString(SAVED_INSTANCE_RESULT));
            }
        }

        detector = new BarcodeDetector.Builder(getApplicationContext())
                .setBarcodeFormats(Barcode.DATA_MATRIX | Barcode.QR_CODE | Barcode.ALL_FORMATS
                       | Barcode.EAN_13 | Barcode.EAN_8 | Barcode.UPC_A | Barcode.UPC_E | Barcode.CODE_39 | Barcode.CODE_93 |
                                Barcode.CODE_128 | Barcode.ITF | Barcode.CODABAR | Barcode.ISBN |
                 Barcode.PDF417 | Barcode.AZTEC).build();
//        int permit1 = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
//        if( permit1 == PackageManager.PERMISSION_GRANTED){
//            TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
//
//        }
       /* else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 123);

        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            // ActivityCompat#requestPermissions;
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 123);

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                int i =0;
                while( i < 5) {
                Toast.makeText(getApplicationContext(), "Phone State Permission OK given ", Toast.LENGTH_SHORT).show();
                    i++;
                }
            } else {
                int i =0;
                while( i < 5) {
                    Toast.makeText(getApplicationContext(), "Telephone Permission not granted ",Toast.LENGTH_SHORT)
                            .show();
                    i++;
                }
            }
        }*/

        if (!detector.isOperational()) {
            txtResultBody.setText("Detector initialisation failed");
            return;
        }
    }

    private void initViews() {
        txtResultBody = findViewById(R.id.txtResultsBody);
        btnOpenCamera = findViewById(R.id.btnOpenCamera);
        txtResultBody = findViewById(R.id.txtResultsBody);
        btnOpenCamera.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnOpenCamera:
                String requestedPerm [] =  new
                        String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA,
                        Manifest.permission.READ_PHONE_STATE};
                ActivityCompat.requestPermissions(PictureBarcodeActivity.this,requestedPerm, REQUEST_CAMERA_PERMISSION);
                int i= 0;
                while( i < 3) {
                    Toast.makeText(getApplicationContext(), "Permission requested "+requestedPerm.length, Toast.LENGTH_SHORT).show();
                    i++;
                }

                break;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.v(TAGCLASS, "onRequestPermissionsResult=" + requestCode + " " + Arrays.toString(permissions)
        +" "+Arrays.toString(grantResults));
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Log.v(TAGCLASS, "onRequestPermissionsResult= takeBarcodePicture ");
                    takeBarcodePicture();
                } else {
                    Toast.makeText(getApplicationContext(), "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
                break;
            default :

                     if ( grantResults.length > 0 ) {
                            int i =0;
                            takeImeiCode();
                            while( i < 3) {
                                Toast.makeText(getApplicationContext(), "Permission type "+requestCode+" Total Permissions given "+grantResults.length, Toast.LENGTH_SHORT).show();
                                i++;
                            }
                         for(int j=0; j < grantResults.length ; j--) {
                               Toast.makeText(getApplicationContext(), "Permission type "+requestCode, Toast.LENGTH_SHORT).show();
                         }

                        }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,  resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            Log.v(TAGCLASS, "onActivityResult= requestCode "+requestCode + " resultCode "+RESULT_OK);
            launchMediaScanIntent();
            try {

                Log.v(TAGCLASS, "onActivityResult= requestCode "+requestCode + " resultCode "+RESULT_OK);
                Bitmap bitmap = decodeBitmapUri(this, imageUri);
                if (detector.isOperational() && bitmap != null) {
                    Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                    SparseArray<Barcode> barcodes = detector.detect(frame);
                    StringBuffer codeData = new StringBuffer();
					StringBuffer pureData = new StringBuffer();
                    for (int index = 0; index < barcodes.size(); index++) {
                        Barcode code = barcodes.valueAt(index);
                        txtResultBody.setText(txtResultBody.getText() + "\n" + code.displayValue + "\n");

                        int type = barcodes.valueAt(index).valueFormat;
                        switch (type) {
                            case Barcode.CONTACT_INFO:
                                Log.i(TAG, code.contactInfo.title);
                                codeData.append(" Title "+ code.contactInfo.title +" \n");
								pureData.append(code.contactInfo.title );
                                break;
                            case Barcode.EMAIL:
                                Log.i(TAG, code.displayValue);
                                codeData.append(" Email "+ code.displayValue +" \n");
								pureData.append(code.contactInfo.title );
                                break;
                            case Barcode.ISBN:
                                Log.i(TAG, code.rawValue);
                                codeData.append(" ISBN "+ code.rawValue +" \n");
								pureData.append(code.rawValue );
                                break;
                            case Barcode.PHONE:
                                Log.i(TAG, code.phone.number);
                                codeData.append(" PHONE "+ code.phone.number +" \n");
								pureData.append( code.phone.number);
                                break;
                            case Barcode.PRODUCT:
                                Log.i(TAG, code.rawValue);
                                codeData.append(" PRODUCT "+ code.rawValue +" \n");
								pureData.append( code.rawValue);
                                break;
                            case Barcode.SMS:
                                Log.i(TAG, code.sms.message);
                                codeData.append(" SMS "+ code.sms.message +" \n");
								pureData.append( code.sms.message);
                                break;
                            case Barcode.TEXT:
                                Log.i(TAG, code.displayValue);
                                codeData.append(" TEXT "+ code.displayValue +" \n");
								pureData.append( code.displayValue);
                                break;
                            case Barcode.URL:
                                Log.i(TAG, "url: " + code.displayValue);
                                codeData.append(" URL "+ code.displayValue +" \n");
									pureData.append( code.displayValue);
                                break;
                            case Barcode.WIFI:
                                Log.i(TAG, code.wifi.ssid);
                                codeData.append(" WIFI "+ code.wifi.ssid +" \n");
									pureData.append( code.wifi.ssid);
                                break;
                            case Barcode.GEO:
                                Log.i(TAG, code.geoPoint.lat + ":" + code.geoPoint.lng);
                                codeData.append(" GEO (Lat : "+ Math.round(code.geoPoint.lat)+" Log: "+Math.round( code.geoPoint.lng)+" \n");
									pureData.append( Math.round(code.geoPoint.lat) + ":" + Math.round(code.geoPoint.lng));
                                break;
                            case Barcode.CALENDAR_EVENT:
                                Log.i(TAG, code.calendarEvent.description);
                                codeData.append(" CALENDAR_EVENT : "+ code.calendarEvent.description +" \n");
                                pureData.append( code.calendarEvent.description);
								break;
                            case Barcode.DRIVER_LICENSE:
                                Log.i(TAG, code.driverLicense.licenseNumber);
                                codeData.append(" DRIVER_LICENSE : "+ code.driverLicense.licenseNumber +" \n");
                                 pureData.append( code.driverLicense.licenseNumber);
								break;
                            default:
                                Log.i(TAG, code.rawValue);
                                codeData.append(" ALL Else : "+ code.rawValue +" \n");
								  pureData.append( code.rawValue);
                                break;
                        }
                    }
                    if (barcodes.size() == 0) {
                        txtResultBody.setText("No barcode could be detected. Please try again.");
                    }
                    else {
						//setAppContext(PictureBarcodeActivity.this);
						CheckIsXML.setAppContext(PictureBarcodeActivity.this);
                        boolean isVal = CheckIsXML.notXML(pureData.toString());
						
						if(isVal)
						{ int i=0;
						 while( i < 10) {
						   Toast.makeText(getApplicationContext(), "Is Not a XML "+isVal,Toast.LENGTH_SHORT)
                          .show(); i++;
						 }
						 String deviceUniqueIdentifier = takeImeiCode();
						 codeData.append("\n IMEI ID : "+ deviceUniqueIdentifier);
						 txtResultBody.setText(""+ codeData.toString());
						}else {
							pd.parseXml(pureData.toString(), "PictureBarcodeActivity :");
						   Toast.makeText(getApplicationContext(), pd.pairBuf.toString(),Toast.LENGTH_SHORT)
                            .show();
							txtResultBody.setText(pd.pairBuf.toString());
						} 
                        
                    }
                } else {
                    txtResultBody.setText("Detector initialisation failed");
                }
            } catch (Exception e) {
				String msg = e.getMessage();
                Toast.makeText(getApplicationContext(), "Failed to load Image "+msg, Toast.LENGTH_SHORT)
                        .show();
                Log.e(TAG, e.toString());
            }
        }
    }
  private String  takeImeiCode () {
      String deviceUniqueIdentifier ="";
      try {
          TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);

          String devideID = "";
          if (telephonyManager !=null) {
              String imei="";
              if (android.os.Build.VERSION.SDK_INT >= 26) {
                  imei=telephonyManager.getImei();
              }
              else
              {
                  imei=telephonyManager.getDeviceId();
              }
              deviceUniqueIdentifier = imei;
              devideID = imei;
          }
          if (null == deviceUniqueIdentifier || 0 == deviceUniqueIdentifier.length())
          {
              deviceUniqueIdentifier = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
          }
      } catch (Exception et )
      { int i=0;
          while( i < 10) {
              Toast.makeText(getApplicationContext(), "IMEI | Device Id not available ",Toast.LENGTH_SHORT)
                      .show();
              i++;
          }
      }
     return deviceUniqueIdentifier;
  }
    private void takeBarcodePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photo = new File(Environment.getExternalStorageDirectory(), "pic.jpg");
        imageUri = FileProvider.getUriForFile(PictureBarcodeActivity.this,
                BuildConfig.APPLICATION_ID + ".provider", photo);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, CAMERA_REQUEST);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (imageUri != null) {
            outState.putString(SAVED_INSTANCE_URI, imageUri.toString());
            outState.putString(SAVED_INSTANCE_RESULT, txtResultBody.getText().toString());
        }
        super.onSaveInstanceState(outState);
    }

    private void launchMediaScanIntent() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(imageUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private Bitmap decodeBitmapUri(Context ctx, Uri uri) throws FileNotFoundException {
        int targetW = 600;
        int targetH = 600;
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(ctx.getContentResolver().openInputStream(uri), null, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        return BitmapFactory.decodeStream(ctx.getContentResolver()
                .openInputStream(uri), null, bmOptions);
    }
}