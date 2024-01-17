package com.jwell.suite;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
//import androidx.core.content.FileProvider;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jwell.adapter.MessageAdapter;
import com.jwell.suite.model.ImageTaken;
import com.jwell.suite.model.ScannMessage;
import com.jwell.suite.model.StringRequestBody;
import com.jwell.util.Dlog;
import com.jwell.util.JewellFileProviderPlugin;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.jwell.util.CheckIsXML;
import com.jwell.util.ParserDocument;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

import com.jwell.suite.R;
import com.jwell.util.RetrofitClientInstance;
import com.jwell.util.ServiceClass;
import com.jwell.util.ServiceInterface;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
//import okhttp3.ResponseBody;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import okhttp3.Interceptor;
//import okhttp3.MultipartBody;
import okhttp3.MultipartBody;

import okhttp3.OkHttpClient;


import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

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
    private long pressedTime;
    private View btnScanExternally;
    private boolean btnScanExternallySelected;
    private String convertImage;
    private File imageFilePhoto;
    private File fileExternal;
    private String pathExternalFile;
    private Uri mImageCaptureUri;

    private ImageTaken imageTaken;
    private Uri mCropImageUri;

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
        btnScanExternally = findViewById(R.id.btnexternaly);
        btnScanExternally.setVisibility(View.VISIBLE);
        btnScanExternally.setOnClickListener(this);
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
            case R.id.btnexternaly:
                     btnScanExternallySelected = true;

                    takeBarcodePicture();
                    // send it to Apache Ticka Server
                File file = imageFilePhoto;
                if (file == null ){
                    Toast.makeText(getBaseContext(), "Re-scan, logout and Re-scan", Toast.LENGTH_SHORT).show();
                    break;
                }
                JSONObject paramObject = new JSONObject();
                try {
                    paramObject.put("file", "data:image/jpeg;base64,"+convertImage);
                } catch (JSONException e) {
                    Dlog.d("JSON Object", "base64 put rrro ");
                }
                okhttp3.RequestBody requestFile = okhttp3.RequestBody.create(okhttp3.MediaType.parse("multipart/form-data"), file);
                okhttp3.RequestBody requestFileExt = okhttp3.RequestBody.create(okhttp3.MediaType.parse("multipart/form-data"), file);
                if(fileExternal !=null)
                    requestFileExt = okhttp3.RequestBody.create(okhttp3.MediaType.parse("multipart/form-data"), fileExternal);
                okhttp3.RequestBody fileBody = okhttp3.RequestBody.create(okhttp3.MediaType.parse("image/bmp"), file);
                MultipartBody.Part multiFpartBody = MultipartBody.Part.createFormData("file",file.getName(),fileBody);
                Dlog.d("Request", "filename "+file.getName());
                Dlog.d("Request", "abs Path "+file.getAbsoluteFile());
                MultipartBody.Part body = MultipartBody.Part.createFormData("upload", file.getName(), requestFileExt);
                if(fileExternal !=null)
                   body = MultipartBody.Part.createFormData("upload", fileExternal.getName(), requestFileExt);
                okhttp3.RequestBody name = okhttp3.RequestBody.create(okhttp3.MediaType.parse("text/plain"), "upload_test");

                /*
                 RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                               .addFormDataPart("UploadFile",pdfFile.toString(),
                                          RequestBody.create(MediaType.parse("application/octet-stream"),
                                                new File(pdfFile.toString())))
                 */
                RequestBody fbody = RequestBody.create(MediaType.parse("image/*"),
                        file);



                MultipartBody.Part imagePart = MultipartBody.Part.createFormData("filename", file.getName(), fileBody);

                MultipartBody.Part multipartBody = MultipartBody.Part.createFormData("file",file.getName(),requestFile);


                ServiceInterface serviceInterface=ServiceClass.connection();
                ServiceInterface service = RetrofitClientInstance.getRetrofitInstance().create(ServiceInterface.class);

                Dlog.d("Request", "Multipart "+multipartBody.toString());
                MultipartBody multipartSimpleBody = new MultipartBody.Builder()

                        .addPart( multiFpartBody)
                        .build();
                Dlog.d("Request", "JSON Body "+ paramObject.toString());
                okhttp3.RequestBody requestBody = StringRequestBody.create(  paramObject.toString());

                String contentType = "multipart/form-data; charset=utf-8; boundary=" + multipartSimpleBody.boundary();
                Dlog.d("Request", "Mboundary= "+ multipartSimpleBody.boundary());
                retrofit2.Call<okhttp3.ResponseBody> responseBodyCallk3 = service.postImage(multiFpartBody, name);
                responseBodyCallk3.enqueue(new Callback<okhttp3.ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        String resTEx="";
                        Dlog.d("Success", "success "+response.code());
                        Dlog.d("Success", "success "+response.message());
                        if (response.isSuccessful()) {
                            Dlog.d("Success", "body " + response.body());
                            resTEx =  response.body().toString();
                            ScannMessage scanMsg =null;String jsonStr ="";
                            try {
                                jsonStr= response.body().string();
                                Dlog.d("Success", "body " + jsonStr );
                                JSONObject scanObject = new JSONObject(jsonStr );
                                // = mainObject.getJSONObject("university");
                                String  message = scanObject.getString("message");
                                String scanText = scanObject.getString("scanText");
                                resTEx = scanText;

                                if(resTEx!=null && resTEx !="") {
                                    Dlog.d("Success", "JSONObject Parse worked   "  );

                                }
                                else {
                                    Gson gson = new GsonBuilder()
                                            .registerTypeAdapter(ScannMessage.class, new MessageAdapter()).create();
                                    scanMsg =  gson.fromJson(jsonStr , ScannMessage.class);
                                    resTEx = scanMsg.scanText;

                                }
                            } catch (Exception  e) {   //| IOException | JSONException | Exception

                                Dlog.d("Success", "body string unable to read  " );
                            }

                            if(resTEx !=null && resTEx !="" ){
                                txtResultBody.setText(txtResultBody.getText() + "\n" +resTEx + "\n");
                            }
                            else{
                                if(scanMsg !=null ){
                                    txtResultBody.setText(txtResultBody.getText() + "\n" +scanMsg.scanText + "\n");
                                }else {
                                    txtResultBody.setTextSize(34);
                                    txtResultBody.setText(txtResultBody.getText() + "\n" + "Take a Better ScreenShot" + "\n");
                                }
                            }
                        }

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Dlog.d("failure", "message = " + t.getMessage());
                        Dlog.d("failure", "cause = " + t.getCause());
                    }

                });

                /*
                Call<ResponseBody> responseBodyCall = service.addRecordPartOk(  multipartBody);
                responseBodyCall.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Response<ResponseBody> response, Retrofit retrofit) {
                        String resTEx="";
                        Dlog.d("Success", "success "+response.code());
                        Dlog.d("Success", "success "+response.message());
                        if (response.isSuccess()) {
                            Dlog.d("Success", "body "+   response.body()); // do something with this
                            if(response.body().contentType().type().contains("xml") ) {
                                try {

                                    CheckIsXML.notXML(new String(response.body().bytes()));


                                } catch (IOException e) {
                                    Dlog.d("Success", "XML not valid  " );
                                }
                                Dlog.d("Success", "XML repsonse " );
                            }

                            resTEx =  response.body().toString();
                        } else {
                            Dlog.d("error", "body "+   response.errorBody()); // do something with that
                            Dlog.d("error", "rerofit base url  "+   retrofit.baseUrl());
                            resTEx =  response.errorBody().toString();
                        }
                        if(resTEx !=null && resTEx !="" ){
                            txtResultBody.setText(txtResultBody.getText() + "\n" +resTEx + "\n");
                        }
                        else{
                            txtResultBody.setText(txtResultBody.getText() + "\n" + response.message() + "\n");
                        }

                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Dlog.d("failure", "message = " + t.getMessage());
                        Dlog.d("failure", "cause = " + t.getCause());
                        txtResultBody.setText(txtResultBody.getText() + "\n" + t.getMessage() + "\n");
                    }

                });
               */

                // get Result and display
                break;
        }

    }
    public void addFileScan(MultipartBody.Part multipartBody){
        ServiceInterface serviceInterface= ServiceClass.connection();
       // Call<okhttp3.ResponseBody> call=serviceInterface.addRecord(  multipartBody );//"getAllUsersSimple",0

    }


    public void getTaskData(){
        ServiceInterface serviceInterface= ServiceClass.connection();
        /*Call<List> call=serviceInterface.taskData("getAllUsersSimple",0);
        call.enqueue(new Callback<List>() {
            @Override
            public void onResponse(Response<List> response, Retrofit retrofit) {
                Log.v("@@@Response",""+response.toString());
                if(response.isSuccess()){
                    List listData = response.body();

                    printStudentDetails(listData);

                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.v("@@@Failure"," Message"+t.getMessage());
            }
        });*/
    }

    private void printStudentDetails(List listData) {
        final String code[] = new String[1];

        listData.forEach((x) -> code[0] +=x.toString());
        txtResultBody.setText(txtResultBody.getText() + "\n" + code[0] + "\n");

    }

    @Override
    public void onBackPressed() {

            //webView.goBack();
            // Toast.makeText(MainActivity.this,"Webview Can Go Back ", Toast.LENGTH_SHORT).show();
            if (pressedTime + 2000 > System.currentTimeMillis()) {
                super.onBackPressed();
                Intent i = new Intent(getApplicationContext(), HomeActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();

            } else {
                Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT).show();
            }
            pressedTime = System.currentTimeMillis();


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
                if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // required permissions granted, start crop image activity
                    startCropImageActivity(mCropImageUri);
                  } else {
                       Toast.makeText(this, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();
                  }
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

        // CHECK CROP AND PROCES CROP IMAGE
        processCROPActivityAndReImage(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            Log.v(TAGCLASS, "onActivityResult= requestCode "+requestCode + " resultCode "+RESULT_OK);
            launchMediaScanIntent();
            try {

                Log.v(TAGCLASS, "onActivityResult= requestCode "+requestCode + " resultCode "+RESULT_OK);
                Bitmap bitmap = decodeBitmapUri(this, imageUri);
                performcrop(bitmap);
                if (detector.isOperational() && bitmap != null) {
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                    byte[] byteArray = byteArrayOutputStream.toByteArray();

                     convertImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
                    try  {
                        String filename = "pippo.png";
                        pathExternalFile = Environment.getExternalStorageDirectory().toString();
                        Integer counter = 0;
                         fileExternal = new File(pathExternalFile, "FitnessGirl"+counter+".jpg");
                       // File sd = Environment.getExternalStorageDirectory();
                        FileOutputStream out = new FileOutputStream(filename);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out); // bmp is your Bitmap instance
                        OutputStream fOut = null;

                        // the File to save , append increasing numeric counter to prevent files from getting overwritten.
                        fOut = new FileOutputStream(fileExternal);

                        // PNG is a lossless format, the compression factor (100) is ignored
                    } catch (IOException e) {
                        Dlog.i(TAG, "could not dave to external file");
                    }
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
                                Dlog.i(TAG, code.contactInfo.title);
                                codeData.append(" Title "+ code.contactInfo.title +" \n");
								pureData.append(code.contactInfo.title );
                                break;
                            case Barcode.EMAIL:
                                Dlog.i(TAG, code.displayValue);
                                codeData.append(" Email "+ code.displayValue +" \n");
								pureData.append(code.contactInfo.title );
                                break;
                            case Barcode.ISBN:
                                Dlog.i(TAG, code.rawValue);
                                codeData.append(" ISBN "+ code.rawValue +" \n");
								pureData.append(code.rawValue );
                                break;
                            case Barcode.PHONE:
                                Dlog.i(TAG, code.phone.number);
                                codeData.append(" PHONE "+ code.phone.number +" \n");
								pureData.append( code.phone.number);
                                break;
                            case Barcode.PRODUCT:
                                Dlog.i(TAG, code.rawValue);
                                codeData.append(" PRODUCT "+ code.rawValue +" \n");
								pureData.append( code.rawValue);
                                break;
                            case Barcode.SMS:
                                Dlog.i(TAG, code.sms.message);
                                codeData.append(" SMS "+ code.sms.message +" \n");
								pureData.append( code.sms.message);
                                break;
                            case Barcode.TEXT:
                                Dlog.i(TAG, code.displayValue);
                                codeData.append(" TEXT "+ code.displayValue +" \n");
								pureData.append( code.displayValue);
                                break;
                            case Barcode.URL:
                                Dlog.i(TAG, "url: " + code.displayValue);
                                codeData.append(" URL "+ code.displayValue +" \n");
									pureData.append( code.displayValue);
                                break;
                            case Barcode.WIFI:
                                Dlog.i(TAG, code.wifi.ssid);
                                codeData.append(" WIFI "+ code.wifi.ssid +" \n");
									pureData.append( code.wifi.ssid);
                                break;
                            case Barcode.GEO:
                                Dlog.i(TAG, code.geoPoint.lat + ":" + code.geoPoint.lng);
                                codeData.append(" GEO (Lat : "+ Math.round(code.geoPoint.lat)+" Log: "+Math.round( code.geoPoint.lng)+" \n");
									pureData.append( Math.round(code.geoPoint.lat) + ":" + Math.round(code.geoPoint.lng));
                                break;
                            case Barcode.CALENDAR_EVENT:
                                Dlog.i(TAG, code.calendarEvent.description);
                                codeData.append(" CALENDAR_EVENT : "+ code.calendarEvent.description +" \n");
                                pureData.append( code.calendarEvent.description);
								break;
                            case Barcode.DRIVER_LICENSE:
                                Dlog.i(TAG, code.driverLicense.licenseNumber);
                                codeData.append(" DRIVER_LICENSE : "+ code.driverLicense.licenseNumber +" \n");
                                 pureData.append( code.driverLicense.licenseNumber);
								break;
                            default:
                                Dlog.i(TAG, code.rawValue);
                                codeData.append(" ALL Else : "+ code.rawValue +" \n");
								  pureData.append( code.rawValue);
                                break;
                        }
                    }
                    if (barcodes.size() == 0) {
                        txtResultBody.setText("No barcode could be detected. Please try again.");
                        //get scanned externally
                        btnScanExternallySelected =true;
                        btnScanExternally.setVisibility(View.VISIBLE);
                    }
                    else {
                        btnScanExternallySelected =false;
                        btnScanExternally.setVisibility(View.INVISIBLE);
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

    private void startCropImageActivity(Uri imageUri) {

        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .start(this);
    }

    private void processCROPActivityAndReImage(int requestCode, int resultCode, Intent data) {

        // handle result of pick image chooser
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri imageUriCurrent = CropImage.getPickImageResultUri(this, data);
              if (imageUriCurrent == null && imageUri!=null){
                  imageUriCurrent = imageUri;
              }
            // For API >= 23 we need to check specifically that we have permissions to read external storage.
            if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {
                // request permissions and handle the result in onRequestPermissionsResult()
                mCropImageUri = imageUri;
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            } else {
                // no permissions required or already grunted, can start crop image activity
                startCropImageActivity(imageUri);
            }
        }

        // handle result of CropImageActivity
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri cropURI  = result.getUri();
                if (cropURI != null) {

                    try {
                        Bitmap bitmap = decodeCompressedBitmapUri(this, cropURI);
                        bitmap =   resizeBitMapAspectRatio(this , bitmap , cropURI);
                        ((ImageButton) findViewById(R.id.quick_start_cropped_image)).setImageBitmap(bitmap);
                        //imageFilePhoto =
                    } catch (FileNotFoundException e) {
                        Toast.makeText(this, "Cropped File Not Found : " + result.getSampleSize(), Toast.LENGTH_LONG).show();
                    }

                }

                Toast.makeText(this, "Cropping successful, Sample: " + result.getSampleSize(), Toast.LENGTH_LONG).show();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
            }
        }


    }

    private Bitmap resizeBitMapAspectRatio(PictureBarcodeActivity pictureBarcodeActivity, Bitmap source, Uri cropURI ) {
        int maxLength = 200;
        try {
            if (source.getHeight() >= source.getWidth()) {
                int targetHeight = maxLength;
                if (source.getHeight() <= targetHeight) { // if image already smaller than the required height
                    return source;
                }

                double aspectRatio = (double) source.getWidth() / (double) source.getHeight();
                int targetWidth = (int) (targetHeight * aspectRatio);

                Bitmap result = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, false);
                if (result != source) {
                }
                if(result != null) {
                    String reducedStr = result.getWidth() + "  " + result.getHeight();
                    Toast.makeText(getApplicationContext(), "Height reduced " + reducedStr + " ", Toast.LENGTH_SHORT)
                            .show();
                }
                return result;
            } else {
                int targetWidth = maxLength;

                if (source.getWidth() <= targetWidth) { // if image already smaller than the required height
                    return source;
                }

                double aspectRatio = ((double) source.getHeight()) / ((double) source.getWidth());
                int targetHeight = (int) (targetWidth * aspectRatio);

                Bitmap result = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, false);
                if (result != source) {
                }
                if(result != null){
                    String reducedStr = result.getWidth() + "  "  + result.getHeight();
                    Toast.makeText(getApplicationContext(), "Width reduced "+ reducedStr +" ",Toast.LENGTH_SHORT)
                            .show();
                }
                return result;

            }

        }
        catch (Exception e) {
            return source;
        }
    }

    private void performcrop(Bitmap photo) {

        CropImage.startPickImageActivity(this);

        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        Bitmap croppedBmp = Bitmap.createBitmap(photo, 0, 0, width / 2,
                photo.getHeight());

        imageTaken.setImageTaken(croppedBmp);
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
    @SuppressLint("ResourceType")
    private void takeBarcodePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri contentUri = null;File photo = null; File file =null;


        try {
            //getResources().openRawResource(R.string.your_file);
           // InputStream is = getAssets().open("path/file.ext"); //          is.
            File pdfDirPath = new File(getFilesDir(), "files");
            pdfDirPath.mkdirs();
            //Wysie_Soh: Create path for temp file

            file = new File(pdfDirPath, "pic.png");
            if (file != null ){
                Toast.makeText(getApplicationContext(), "IMEI | Device File dir  "+pdfDirPath.getAbsolutePath()+" ok ",Toast.LENGTH_SHORT)
                        .show();
                Toast.makeText(getApplicationContext(), "IMEI | Device File Access grant ok ",Toast.LENGTH_SHORT)
                        .show();
                contentUri = JewellFileProviderPlugin.getUriForFile(this, getApplicationContext().getPackageName() + ".fileprovider", file);
                imageUri = contentUri;
                imageFilePhoto = file;
                mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
                        "tmp_contact_" + String.valueOf(System.currentTimeMillis()) + ".jpg"));
            }
            else{  // File(contentUri,)
                photo= new File(Environment.getExternalStorageDirectory(), "pic.png");
                if (photo != null ){
                    Toast.makeText(getApplicationContext(), "IMEI | Device File Access External ok ",Toast.LENGTH_SHORT)
                            .show();
                    file = photo;
                    imageUri =  JewellFileProviderPlugin.getUriForFile(getApplicationContext(),getApplicationContext().getPackageName() + ".fileprovider", photo);

                }
                else {
                    Toast.makeText(getApplicationContext(), "IMEI | Device File Access failed ",Toast.LENGTH_SHORT)
                            .show();
                }
            }

             /*os = new FileOutputStream(file);
            document.writeTo(os);
            document.close();
            os.close();

            shareDocument(contentUri);*/

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "IMEI | Device File Access Failed ",Toast.LENGTH_SHORT)
                    .show();
        }



          /*imageUri = FileProvider.getUriForFile(PictureBarcodeActivity.this,
                getApplicationContext().getPackageName() + ".provider", photo);
        */

        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        if(!btnScanExternallySelected )
         startActivityForResult(intent, CAMERA_REQUEST);
    }

    private void takeCropBarcodePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri contentUri = null;File photo = null; File file =null;


        try {
            File pdfDirPath = new File(getFilesDir(), "files");
            pdfDirPath.mkdirs();
            file = new File(pdfDirPath, "pic.png");
            if (file != null ){
                Toast.makeText(getApplicationContext(), "Crop File dir  "+pdfDirPath.getAbsolutePath()+" ok ",Toast.LENGTH_SHORT)
                        .show();
                Toast.makeText(getApplicationContext(), "Crop File Dir Access grant ok ",Toast.LENGTH_SHORT)
                        .show();
                contentUri = JewellFileProviderPlugin.getUriForFile(this, getApplicationContext().getPackageName() + ".fileprovider", file);
                imageUri = contentUri;
                imageFilePhoto = file;
                mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
                        "tmp_contact_" + String.valueOf(System.currentTimeMillis()) + ".jpg"));
            }
            else{  // File(contentUri,)
                photo= new File(Environment.getExternalStorageDirectory(), "pic.png");
                if (photo != null ){
                    Toast.makeText(getApplicationContext(), "Crop File Access External ok ",Toast.LENGTH_SHORT)
                            .show();
                    file = photo;
                    imageUri =  JewellFileProviderPlugin.getUriForFile(getApplicationContext(),getApplicationContext().getPackageName() + ".fileprovider", photo);

                }
                else {
                    Toast.makeText(getApplicationContext(), "IMEI | Device File Access failed ",Toast.LENGTH_SHORT)
                            .show();
                }
            }

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Crop File Access Failed ",Toast.LENGTH_SHORT)
                    .show();
        }

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

    private Bitmap decodeCompressedBitmapUri(Context ctx, Uri uri) throws FileNotFoundException {
        int targetW = 800;
        int targetH = 800;
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