package com.jwell.suite;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.content.SharedPreferences;
import android.widget.Button;
import java.util.HashMap;
import java.util.Map;

import java.io.IOException;
import java.io.StringReader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import androidx.appcompat.app.AppCompatActivity;
import com.jwell.util.*;

public class ScanReportActivity extends AppCompatActivity {
    Button mButtonHome;
	SharedPreferences pref;
	 private static final String TAG = "ScanReportActivity.class";
	 StringBuffer pairBuf = new StringBuffer();
	ParserDocument pd ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_report);
        Bundle b = getIntent().getExtras();
        String fullReport= b.getString("Report");
		pref = getSharedPreferences("user_details",MODE_PRIVATE);
		pd = new ParserDocument();
		pd.setAppActivity(ScanReportActivity.this);
		
		CheckIsXML.setAppContext(ScanReportActivity.this);
	    if(CheckIsXML.notXML(fullReport.trim()))
		{ 
			Toast.makeText(ScanReportActivity.this, "is not XML" , 15).show();	
		}else {
				parseXml(fullReport.trim());
		} 
        TextView textView = (TextView) findViewById(R.id.txtScanReportBody);
        //String userName =  savedInstanceState.getString("userName");
        String partialReport = fullReport.trim();
        String wcText = textView.getText().toString();
		if (pairBuf.length() > 1) 
		{ 
		  textView.setText(pairBuf.toString());
		} 
		else {
          textView.setText(partialReport);
		}
		 mButtonHome = (Button)findViewById(R.id.btnBackHome);
		 mButtonHome.setOnClickListener(getScanQRListener());
		 //Toast.makeText(getApplicationContext(), "Permission Denied!", Toast.LENGTH_SHORT).show();
    }

    public View.OnClickListener  getScanQRListener() {
		Toast.makeText(ScanReportActivity.this, "Registered the Home listnere", Toast.LENGTH_SHORT).show();
        return new View.OnClickListener() {
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.btnBackHome:
					
                        scanActivity();
                        break;
                }
            }
        };

    }
	 public void parseXml(String intentData){
		try {
			Toast.makeText(ScanReportActivity.this, "Parse XML Started"+intentData, Toast.LENGTH_SHORT).show();
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser xpp = factory.newPullParser();
			Map<String,String> attributes = new HashMap<>();
			String var = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><current func = \"123\" name=\"lllo\" add=\"pol\" />";
			String newVar  = intentData.replaceAll("\"", "\\\\\"");
			Toast.makeText(ScanReportActivity.this, "New XML "+newVar, Toast.LENGTH_SHORT).show();
			xpp.setInput( new StringReader( intentData) ); // pass input whatever xml you have
			int eventType = xpp.getEventType();
			if(xpp != null ) 
			{   Toast.makeText(ScanReportActivity.this, "XMlPull parser ok ", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(ScanReportActivity.this, "XMlPull parser is null ", Toast.LENGTH_SHORT).show();
			}
			if(eventType != XmlPullParser.END_DOCUMENT){
			   Toast.makeText(ScanReportActivity.this, "!= XmlPullParser.END_DOCUMENT "+eventType, Toast.LENGTH_SHORT).show();
			}
			while (eventType != XmlPullParser.END_DOCUMENT) {
			  switch (eventType) {

               case XmlPullParser.START_DOCUMENT: 				  
				 					Log.d(TAG,"Start document");
							 Toast.makeText(ScanReportActivity.this, "Start document "+eventType, Toast.LENGTH_SHORT).show();
								 attributes = getAttributes(xpp);
								break;
			   case XmlPullParser.START_TAG : 
								Log.d(TAG,"Start tag "+xpp.getName());
							Toast.makeText(ScanReportActivity.this, "Start Tag "+xpp.getName(), Toast.LENGTH_SHORT).show();
					 attributes = getAttributes(xpp);
					 break;
			   case XmlPullParser.END_TAG : 
				 			Log.d(TAG,"End tag "+xpp.getName());
					 Toast.makeText(ScanReportActivity.this, "ENDTAG "+eventType, Toast.LENGTH_SHORT).show();
							break;
			   case XmlPullParser.TEXT : 
						Log.d(TAG,"Text "+xpp.getText()); // here you get the text from xml
					 Toast.makeText(ScanReportActivity.this, "TEXT "+eventType, Toast.LENGTH_SHORT).show();
				     break; 
			  } 
				eventType = xpp.next();
			}
			Log.d(TAG,"End document");
            if(!attributes.isEmpty()) {
			   SharedPreferences.Editor editor = pref.edit();
               StringBuffer dtBuf = new StringBuffer();
				for(Map.Entry<String ,String > et : attributes.entrySet()){
					 editor.putString( et.getKey(),et.getValue());
                     dtBuf.append(et.getKey());
					 dtBuf.append(" = ");
					 dtBuf.append(et.getValue());
					 pairBuf.append(et.getKey());
					 pairBuf.append(" = ");
					 pairBuf.append(et.getValue());
					 pairBuf.append("\n");
				}	
			   Toast.makeText(ScanReportActivity.this, dtBuf.toString(), Toast.LENGTH_SHORT).show();
			    editor.commit();
			   
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private Map<String,String>  getAttributes(XmlPullParser parser)  {
	   Map<String,String> attrs=null;
       int acount=parser.getAttributeCount();
	   Toast.makeText(ScanReportActivity.this, "Get Attributes XML parser ", Toast.LENGTH_SHORT).show();
	  Toast.makeText(ScanReportActivity.this, "Atrrib Count "+acount, Toast.LENGTH_SHORT).show(); 	
		if(acount != -1) {
			Log.d(TAG,"Attributes for ["+parser.getName()+"]");
			attrs = new HashMap<String,String>(acount);
			for(int x=0;x<acount;x++) {
				Log.d(TAG,"\t["+parser.getAttributeName(x)+"]=" +
					  "["+parser.getAttributeValue(x)+"]");
				attrs.put(parser.getAttributeName(x), parser.getAttributeValue(x));
			}
		}
		else {
			Toast.makeText(ScanReportActivity.this, "Atrbitue not Readble", Toast.LENGTH_SHORT).show();
		}
		return attrs;
	}
    private void scanActivity () {
           try {
            Intent intent =new Intent(ScanReportActivity.this, HomeActivity.class);
            Bundle b =new Bundle();
			String user = "";
			if(pref != null && (pref.contains("username") && pref.contains("password"))){
					 user=pref.getString("username",null);
			}
            b.putString("Username",user);
            intent.putExtras(b);
			
			startActivity(intent);
             } catch (Exception e) {
                    Uri marketUri = Uri.parse("market://details?id=com.google.zxing.client.android");
                      Intent marketIntent = new Intent(Intent.ACTION_VIEW,marketUri);
            startActivity(marketIntent);
          }
    }
}