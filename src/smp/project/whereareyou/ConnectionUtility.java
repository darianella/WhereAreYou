package smp.project.whereareyou;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

import android.util.Log;

public class ConnectionUtility {
	
	final Config conf = new Config();
	
	final String  CU = "[ConnectionUtility]"; // tag per log
	String myIp =  conf.NO_IP;
	boolean isMobileConnected = false;
	
	/*
	   ************************************************************************
	   		 Funzioni per settare la connessione e verificarne lo stato
	*/
	
	// FIXME: decidere se togliere la possibilità di funzionare con il wifi
	public void isOnline(final Context context) {
		Log.d(CU, "Chiamata la onLine");
		ConnectivityManager cm = (ConnectivityManager) context
							.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni == null) {
			Log.d(CU, "Attenzione: Connessione Mobile non disponibile");
			showSettingsAlert(context, conf.NOCONNECTION_MSG, 
					conf.ACTION_WIRELESS);
			isMobileConnected = false;
		}
        else { // posso essere connesso al wifi o al mobile
        	ni = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        	if (!ni.isConnected()) { // sono NON connesso al wifi
      		// quindi dovrei essere connesso al mobile
        	// ma non sono certo che non vi siano altre possibilità.
        		Log.d(CU, "Connesso MOBILE");
    			setMyIp();
        	} else { // sono connesso al wifi
        		Log.d(CU, "Warning: Sono connesso al WIFI");
        		AlertDialog.Builder builder = new AlertDialog.Builder(context);
        		builder.setTitle("Warning");
   	 			builder.setMessage("Per far funzionare l'app disattivare la " 
   	 					+ "connessione WIFI e utilizzare quella Mobile");
   	 			builder.setNegativeButton("No", 
   	 				new DialogInterface.OnClickListener() {
   	 					public void onClick(DialogInterface dialog, int id) {
   	 						Log.d("[AlertD]","Ho cliccato il tasto NO");
   	 					}
	 			});
   	 			builder.setPositiveButton("Yes", 
   	 				new DialogInterface.OnClickListener() {
   	 					public void onClick(DialogInterface dialog, int id) {
   	 					// Disabilito il wifi
   	 					WifiManager wifiManager = (WifiManager) context
   	 								.getSystemService(Context.WIFI_SERVICE);
   	 					if (wifiManager.isWifiEnabled())
   	 						wifiManager.setWifiEnabled(false);
   	 					//FIXME: magari far pertire un alarm per verificare 
   	 					// se dopo un certo tempo ho ottenuto l ip?
   	 					}
   	 			});
   	 			AlertDialog alert = builder.create();
   	 			alert.show();
        	}
	   	}
	}
	public void setMyIp() { // FIXME: ora è il server a dirmelo!!
       	myIp = conf.NO_IP;
       	isMobileConnected = false;
		try {	
       		for (Enumeration<NetworkInterface> en = NetworkInterface
       						.getNetworkInterfaces(); en.hasMoreElements();) {
       			NetworkInterface intf = en.nextElement();
       			for (Enumeration<InetAddress> enumIpAddr = intf
       					.getInetAddresses(); enumIpAddr.hasMoreElements();) {	
       				InetAddress inetAddr = enumIpAddr.nextElement();
       				if (!inetAddr.isLoopbackAddress()) {
       					myIp = inetAddr.getHostAddress().toString();
       					Log.d(CU, myIp);
       					isMobileConnected = true;
       				}
       			}
       		}
       	} catch (SocketException se) {
       		se.printStackTrace();
       		Log.e("Error", se.getMessage());
       	}
	}
	public void showSettingsAlert(final Context context, 
						String msg, final String action) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(conf.SETTING_ALERT_TITLE);
        // Setting Dialog Message
        alertDialog.setMessage(msg);
        alertDialog.setPositiveButton("Settings", 
        		new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(action);
                context.startActivity(intent);
            }
        });
        alertDialog.setNegativeButton("Cancel", 
        				new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
            }
        });
        alertDialog.show();
	}
	/*
	   ************************************************************************
	   		 Funzioni per inviare richieste HTTP
	*/
	public void postMyNumber(String number) {
	    // Create a new HttpClient and Post Header
	    HttpClient httpclient = new DefaultHttpClient();
	    HttpPost httppost = new HttpPost(conf.URL_SERVER);
	    try {
	    	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	        nameValuePairs.add(new BasicNameValuePair("phoneNumber", number));
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	        // Execute HTTP Post Request
	        ResponseHandler<String> responseHandler=new BasicResponseHandler();
	        String responseBody = httpclient.execute(httppost, responseHandler);
	        Log.d("httpResponseBody_myIP", responseBody);
	    } catch (ClientProtocolException e) {
	    	Log.e("Error", e.getMessage());
	    } catch (IOException e) {
	    	Log.e("Error", e.getMessage());
	    }
	}
}