
package com.jwell.util;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.util.HashMap;
import java.util.Map;
import android.widget.Toast;
import java.io.IOException;
import java.io.StringReader;
import android.util.Log;
import android.content.SharedPreferences;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import android.content.Context;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;


public class CheckIsXML  /*extends AppCompatActivity */{
	
	static android.content.Context ctx;
	static SharedPreferences pref;
	private static final String TAG = "ParserDocument.class";
	public static StringBuffer pairBuf = new StringBuffer();
	public static boolean notValidXml = false;
	/* 
	 @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanned_barcode);

        initViews();
        initialiseDetectorsAndSources();
    }
	*/
   /* @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      
    }*/
	public static void setAppContext(Context tx ){
		ctx = tx;
    }
   public static boolean notXML(String intentData) {
		
		 DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		  DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			 //Document doc = dBuilder.parse(intentData);
			 int eventType = 0;
			 XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser xpp = factory.newPullParser();
			xpp.setInput( new StringReader( intentData) ); // pass input whatever xml you have
			 eventType = xpp.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
			   notValidXml = false;
			   eventType = xpp.next();
			   Toast.makeText(ctx, "isValid xml "+(notValidXml) , 15).show();
			}
			  
		} catch (XmlPullParserException e) {
			System.out.println("excetion not proper ");
			notValidXml = true;
			 
		} catch (ParserConfigurationException e1) {
			// TODO Auto-generated catch block
			System.out.println("excetion not proper ");
			notValidXml = true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("excetion not proper ");
			notValidXml = true;
		}
		return notValidXml;
	} 
 
	
}