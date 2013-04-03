package smp.project.whereareyou;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

import android.util.Log;

public class ConnectionUtility {
	
	final Config config = new Config();
	
	final String  CU = "[ConnectionUtility]"; // tag per log
	String myIp =  config.NO_IP;
	boolean isMobileConnected = false;
	
	// decidere se togliere la possibilitˆ di funzionare con il wifi
	public void isOnline(final Context context) {
		Log.d(CU, "Chiamata la onLine");
		ConnectivityManager cm = (ConnectivityManager) context
							.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni == null) {
			Log.d(CU, "Attenzione: Connessione Mobile non disponibile");
			showSettingsAlert(context, config.NOCONNECTION_MSG, 
											config.ACTION_WIRELESS);
			isMobileConnected = false;
		}
        else { // posso essere connesso al wifi o al mobile
        	ni = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        	if (!ni.isConnected()) { // sono NON connesso al wifi
      		// quindi dovrei essere connesso al mobile
        	// ma non sono certo che non vi siano altre possibilitˆ.
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
	public void setMyIp() {
       	myIp = config.NO_IP;
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
       	}
	}
	public void showSettingsAlert(final Context context, 
										String msg, final String action) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(config.SETTING_ALERT_TITLE);
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
}