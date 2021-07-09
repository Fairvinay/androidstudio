
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


public class ParserDocument  /*extends AppCompatActivity */{
	
	public AppCompatActivity ctx;
	static SharedPreferences shPref;
	private static final String TAG = "ParserDocument.class";
	private static final String PD_TAG = "PD_TAG :";
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
	public void setAppActivity(AppCompatActivity tx ){
		ctx = tx;
    }
	public void setSharedPref(SharedPreferences sh ){
		shPref = sh;
    }
   public  boolean notXML(String intentData) {
		
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
			}
			  
		} catch (XmlPullParserException e) {
			System.out.println("excetion not proper ");
			notValidXml = true;
			 
		} catch (ParserConfigurationException e1) {
			// TODO Auto-generated catch block
			System.out.println("excetion not proper ");
			notValidXml = true;
		} /*catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("excetion not proper ");
			notValidXml = true;
		}*/
		return notValidXml;
	} 
 public  void parseXml(String intentData, String classTag){
		try {
			Toast.makeText(ctx,classTag+ PD_TAG+ "Parse XML Started"+intentData, Toast.LENGTH_SHORT).show();
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser xpp = factory.newPullParser();
			Map<String,String> attributes = new HashMap<>();
			String var = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><current func = \"123\" name=\"lllo\" add=\"pol\" />";
			String newVar  = intentData.replaceAll("\"", "\\\\\"");
			//Toast.makeText(ctx, "New XML "+newVar, Toast.LENGTH_SHORT).show();
			xpp.setInput( new StringReader( intentData) ); // pass input whatever xml you have
			int eventType = xpp.getEventType();
			if(xpp != null ) 
			{   //Toast.makeText(ctx, "XMlPull parser ok ", Toast.LENGTH_SHORT).show();
			} else {
				//Toast.makeText(ctx, "XMlPull parser is null ", Toast.LENGTH_SHORT).show();
			}
			if(eventType != XmlPullParser.END_DOCUMENT){
			   //Toast.makeText(ctx, "!= XmlPullParser.END_DOCUMENT "+eventType, Toast.LENGTH_SHORT).show();
			}
			while (eventType != XmlPullParser.END_DOCUMENT) {
			  switch (eventType) {

               case XmlPullParser.START_DOCUMENT: 				  
				 					Log.d(TAG,"Start document");
							 //Toast.makeText(ctx, "Start document "+eventType, Toast.LENGTH_SHORT).show();
								 attributes = getAttributes(xpp);
								break;
			   case XmlPullParser.START_TAG : 
								Log.d(TAG,"Start tag "+xpp.getName());
							//Toast.makeText(ctx, "Start Tag "+xpp.getName(), Toast.LENGTH_SHORT).show();
					 attributes = getAttributes(xpp);
					 break;
			   case XmlPullParser.END_TAG : 
				 			Log.d(TAG,"End tag "+xpp.getName());
					 //Toast.makeText(ctx, "ENDTAG "+eventType, Toast.LENGTH_SHORT).show();
							break;
			   case XmlPullParser.TEXT : 
						Log.d(TAG,"Text "+xpp.getText()); // here you get the text from xml
					 //Toast.makeText(ctx, "TEXT "+eventType, Toast.LENGTH_SHORT).show();
				     break; 
			  } 
				eventType = xpp.next();
			}
			Log.d(TAG,"End document");
            if(!attributes.isEmpty()) {
			   SharedPreferences.Editor editor = shPref.edit();
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
			   //Toast.makeText(ctx, dtBuf.toString(), Toast.LENGTH_SHORT).show();
			    editor.commit();
			   
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private static Map<String,String>  getAttributes(XmlPullParser parser)  {
	   Map<String,String> attrs=null;
       int acount=parser.getAttributeCount();
	   //Toast.makeText(ctx, "Get Attributes XML parser ", Toast.LENGTH_SHORT).show();
	  //Toast.makeText(ctx, "Atrrib Count "+acount, Toast.LENGTH_SHORT).show(); 	
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
			//Toast.makeText(ctx, "Atrbitue not Readble", Toast.LENGTH_SHORT).show();
		}
		return attrs;
	}	
	
}