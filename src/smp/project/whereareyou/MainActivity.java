package smp.project.whereareyou;


import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity implements OnClickListener {
	
	SmsStatusReceiver ssr;
	IntentFilter smsFilter;
	
	String ipAddr;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Button bt = (Button) findViewById(R.id.button1);
		
		onLine();
		
		bt.setOnClickListener(this);
		
		smsFilter = new IntentFilter(SmsStatusReceiver.AZIONE); 
		smsFilter.addCategory(Intent.CATEGORY_DEFAULT);
		ssr = new SmsStatusReceiver();
	}
	private void onLine() {
		Log.d("TMP", "Chiamata la onLine");
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni == null) {
			Log.d("NET", "Attenzione: Connessione dati non disponibile");
			Toast.makeText(getApplicationContext(), 
					"Attenzione: Connessione dati non disponibile",
					Toast.LENGTH_LONG).show();
		}
        else { // posso essere connesso al wifi o al mobile
        	ni = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        	if (ni.isConnected()) { // sono connesso al wifi.. 
        		Log.d("NET", "Connessione dati non disponibile, sono connesso al wifi");
        		AlertDialog.Builder builder = new AlertDialog.Builder(this);
        		builder.setTitle("Warning");
   	 			builder.setMessage("Per far funzionare l'applicazione disattivare la " +
   	 				"connessione WIFI e utilizzare quella Mobile");
   	 			builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
   	 				public void onClick(DialogInterface dialog, int id) {
   	 					final WifiManager wifiManager = (WifiManager) getSystemService(MainActivity.WIFI_SERVICE);
   	 					if (wifiManager.isWifiEnabled())
   	 						wifiManager.setWifiEnabled(false);
   	 					new IpTask().execute();
   	 						
   	 				}
   	 			});
   	 			builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
   	 				public void onClick(DialogInterface dialog, int id) {
   	 					Log.d("[AlertD]","Ho cliccato il tasto NO");
   	 					//FIXME: impostare ip a false
   	 				}
   	 			});
   	 			AlertDialog alert = builder.create();
   	 			alert.show();
        	} else { // connesso al Mobile
        		Log.d("NET", "Connesso");
        		myIp();
   	 		}
	   	}
	}
	
	private boolean myIp() {
       	boolean result = false;
       	ipAddr = "no Ip Address";
		try {	
       		for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
       			NetworkInterface intf = en.nextElement();
       			for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
       				InetAddress inetAddress = enumIpAddr.nextElement();
       				if (!inetAddress.isLoopbackAddress()) {
       					ipAddr = inetAddress.getHostAddress().toString();
       					Log.d("NET", ipAddr);
       					result = true;
       				}
       			}
       		}
       	} catch (SocketException se) {
       		se.printStackTrace();
       	}
		TextView tv = (TextView) findViewById(R.id.textView1);
		tv.setText(ipAddr);
       	return result;
	}
		
	@Override
	protected void onResume() { 
		super .onResume();
			Log.d("ACTION","chiamata la onresume");
			registerReceiver(ssr, smsFilter);
			myIp();
	}
	@Override
	protected void onPause() { 
		super .onPause();
		Log.d("ACTION","chiamata la onpause");
		unregisterReceiver(ssr);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		/*
		 * attivare activity che mostra tutti contatti che possono usare appl
		 * database? add user manually 
		 * 			+ add user for each request if not already in db
		 * */
		Intent intent = new Intent(this, ListActivityContatti.class);
		startActivity(intent);
	}
	
	public void clickedExit(View v) {
		//FIXME: da fare...
	}
	
	
	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * 	BROADCAST RECEIVER
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 *
	 * */
	
	// Riceve lo stato sul sms inviato per iniziare una connessione
	public class SmsStatusReceiver extends BroadcastReceiver {
		public static final String AZIONE = "smp.project.whereareyou.SMS_SEND";
	    
		@Override
    	public void onReceive(Context context, Intent intent) {
    		String phone_num = intent.getStringExtra("contact_num");
    		String status = intent.getStringExtra("status");
    		String name = intent.getStringExtra("contact_name");
    		TextView tv = (TextView) findViewById(R.id.textView2);
    		tv.setText(status);
    		tv = (TextView) findViewById(R.id.textView3);
    		tv.setText(name);
    		tv = (TextView) findViewById(R.id.textView4);
    		tv.setText(phone_num);
	    	}
	    }


	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * 	ASYNCTASK
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 *
	 * */
	
	
	public class IpTask extends AsyncTask <String, Integer, Void> {
		
		int attempts = 0;
//		@Override
//		protected void onPostExecute(String result) {
//			super .onPostExecute(result);
//			TextView tv = (TextView) findViewById(R.id.textView1);
//			tv.setText(ipAddr);
		
		@Override
		protected Void doInBackground(String... arg0) {
			try {
				while (attempts < 3) {
					Thread.sleep(4000);
					if (myIp()) break;
					attempts++;
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
	}
}
