package smp.project.whereareyou;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

import smp.project.whereareyou.RecvReqActivity.ClientTask;


import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
//import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity implements OnClickListener {
	
	private SendSmsManagerReceiver smr;
	private IntentFilter smrFilter;
	final String  NO_IP = "No global IP address";
	
	String ipAddr = NO_IP;
	boolean isMobileConn = false; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Button bt = (Button) findViewById(R.id.button1);
		
		bt.setOnClickListener(this);
		
		smrFilter = new IntentFilter(SendSmsManagerReceiver.AZIONE); 
		smrFilter.addCategory(Intent.CATEGORY_DEFAULT);
		smr = new SendSmsManagerReceiver();
		registerReceiver(smr, smrFilter);		
		
//		isOnline();
	}
	private boolean isOnline() {
		Log.d("TMP", "Chiamata la onLine");
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni == null) {
			Log.d("NET", "Attenzione: Connessione Mobile non disponibile");
			isMobileConn = false; 
			myIp();
			Toast.makeText(getApplicationContext(), 
					"Attenzione: Connessione dati non disponibile",
					Toast.LENGTH_LONG).show();
			return false;
		}
        else { // posso essere connesso al wifi o al mobile
        	ni = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        	if (!ni.isConnected()) { // sono connesso al mobile..
        		Log.d("NET", "Connesso MOBILE");
        		isMobileConn = true;
        		myIp();
        		return true;
        	} else { // sono connesso al wifi
        		isMobileConn = false;
        		Log.d("NET", "Warning: Sono connesso al WIFI");
        		AlertDialog.Builder builder = new AlertDialog.Builder(this);
        		builder.setTitle("Warning");
   	 			builder.setMessage("Per far funzionare l'applicazione disattivare la " +
   	 				"connessione WIFI e utilizzare quella Mobile");
   	 			builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
	 				public void onClick(DialogInterface dialog, int id) {
	 					Log.d("[AlertD]","Ho cliccato il tasto NO");
	 				}
	 			});
   	 			builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
   	 				public void onClick(DialogInterface dialog, int id) {
   	 					final WifiManager wifiManager = (WifiManager) getSystemService(MainActivity.WIFI_SERVICE);
   	 					if (wifiManager.isWifiEnabled())
   	 						wifiManager.setWifiEnabled(false);
   	 					//new IpTask().execute();
   	 				}
   	 			});
   	 			
   	 			AlertDialog alert = builder.create();
   	 			alert.show();
   	 			return false;
        	}
	   	}
	}
	
	private void myIp() {
       	ipAddr = NO_IP;
		try {	
       		for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
       			NetworkInterface intf = en.nextElement();
       			for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
       				InetAddress inetAddress = enumIpAddr.nextElement();
       				if (!inetAddress.isLoopbackAddress()) {
       					ipAddr = inetAddress.getHostAddress().toString();
       					Log.d("NET", ipAddr);
       				}
       			}
       		}
       	} catch (SocketException se) {
       		se.printStackTrace();
       	}
		TextView tv = (TextView) findViewById(R.id.textView1);
		tv.setText(ipAddr);
       
	}
		
	@Override
	protected void onResume() { 
		super .onResume();
			Log.d("ACTION","chiamata la onresume");
			registerReceiver(smr, smrFilter);
			isOnline();
	}
	@Override
	protected void onPause() { 
		super .onPause();
		Log.d("ACTION","chiamata la onpause");
		unregisterReceiver(smr);
	}
	
	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}*/

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
	
	/*public void clickedExit(View v) {
		//FIXME: da fare...??
	}*/
	
	
	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * 	BROADCAST RECEIVER
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 *
	 * */
	
	// Riceve lo stato sul sms inviato per iniziare una connessione
	public class SendSmsManagerReceiver extends BroadcastReceiver {
		public static final String AZIONE = "smp.project.whereareyou.READY_TO_SEND";
	    
		@Override
    	public void onReceive(Context context, Intent i) {
			removeStickyBroadcast(i);
			Log.d("aaa1","nella parte operativa della received " + i.getAction() );
			
			
			//if (i.getAction().equals(AZIONE)) {
			
				String contactNum = i.getStringExtra("contact_num");
				String contactName = i.getStringExtra("contact_name");
				
				TextView tv = (TextView) findViewById(R.id.textView3);
	    		tv.setText(contactName);
	    		tv = (TextView) findViewById(R.id.textView4);
	    		tv.setText(contactNum);
	    		tv = (TextView) findViewById(R.id.textView2);
	    		EditText te = (EditText) findViewById(R.id.editText1);
	    		te.setText(contactNum);
				if (isMobileConn) {
					Log.d("DEb","ip valido" );
					Intent intent = new Intent(context, SndSmsIntentService.class);
					intent.putExtra("contact_num", contactNum);
					intent.putExtra("contact_name", contactName);
					intent.putExtra("ip_addr", ipAddr);
					startService(intent);
//					Log.d("DEb","avrei inviato un messaggio" );
					tv.setText("SMS SEND");
						new ServerTask().execute("un qualche testo");
					
		    	} else {
		    		Log.d("DEb","Impossibile inviare il messaggio -> ip NON valido" );
		    		tv.setText("SMS DOESN'T SEND, NO IP");
		    	}
//			}
			
	    }
	}

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * 	ASYNCTASK
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 *
	 * */
	//FIXME: per debuggare il client senza il messaggio
		public void debugClient(View v) {
			Log.d("Debug client","Simulo di aver ricevuto un messaggio..");
			Intent resultIntent = new Intent(this, RecvReqActivity.class);
			resultIntent.putExtra("sms_number", "12345");
			resultIntent.putExtra("sms_ip", "127.0.0.1");
			startActivity(resultIntent);
			
			
		}
	
	public void debugServer(View v) {
		
		Log.d("Debug server","Come se avessi inviato un mess e fossi in attesa");
		new ServerTask().execute("un qualche testo");
	}
	
	
	public class ServerTask extends AsyncTask <String, Integer, Void> {
		
		ProgressDialog dialog;
		
		protected void onPreExecute() {
			dialog = new ProgressDialog(MainActivity.this);
			dialog.setTitle("Waiting...");
	        dialog.show();
			
		}
		/*protected void onPostExecute(String result) {
			super .onPostExecute(result);
			TextView tv = (TextView) findViewById(R.id.textView1);
			tv.setText(ipAddr);
		*/
		@Override
		protected Void doInBackground(String... arg0) {
			Log.d("Debug server","Sono nella asynctask background");
			int serverPort = 12345;
			ServerSocket serverSocket;
			Socket socket;
			BufferedReader in;
	        PrintStream out;
	        String msg;
			try {
				serverSocket = new ServerSocket(serverPort);
//				Log.d("server addr:", serverSocket.getInetAddress().getHostAddress());
				socket = serverSocket.accept();
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				out = new PrintStream(socket.getOutputStream());
				
				
				
				
				dialog.dismiss();
				msg = in.readLine();
				out.println("Hello by Server");
				
				Log.d("server rx:", msg);
				
				
				out.close();
				in.close();
				socket.close();
				serverSocket.close();
				
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
			return null;
			
		
		}
	}
}

