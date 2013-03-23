package smp.project.whereareyou;


import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;

import android.widget.Button;
import android.widget.TextView;





public class MainActivity extends Activity implements OnClickListener {
	
	SmsStatusReceiver ssr;
	IntentFilter smsFilter;
	
//	NetworkChangeReceiver ncr;
//	IntentFilter connectivityFilter;

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Button bt = (Button) findViewById(R.id.button1);
		
		if (isOnline()) {
			Log.d("NET", "sono connesso");
		} else {
			Log.d("NET", "non sono connesso");
		}
		
		bt.setOnClickListener(this);
		
		smsFilter = new IntentFilter(SmsStatusReceiver.AZIONE); 
		smsFilter.addCategory(Intent.CATEGORY_DEFAULT);
		ssr = new SmsStatusReceiver();
		
		
//		connectivityFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION); 
//		
//		connectivityFilter.addCategory(Intent.CATEGORY_DEFAULT);
//		ncr = new NetworkChangeReceiver();

		
		
	}
	/*
	 * Verifica se è attiva la connessione a internet
	 * Se sono connesso al wifi.. ricevo connessioni in ingresso o ho problemi di firewall
	 * */
	private boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if(ni == null)
			return false;
        else {
       	 	Log.d("NET", ni.getTypeName());
        
       	 	
       	// non l'ho ancora testato
   	 	try {
       		for (Enumeration<NetworkInterface> en = 
       						NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
       			NetworkInterface intf = en.nextElement();
       			for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
       				InetAddress inetAddress = enumIpAddr.nextElement();
       				if (!inetAddress.isLoopbackAddress()) {
       					Log.d("prova", inetAddress.getHostAddress().toString());
       				}
       			}
       		}
       	} catch (Exception ex) {
       		Log.e("asd", ex.toString());
       	}
       		
       		
       	 	
       	 	
       	 	
       	 	
       	 	
       	 	
        	return ni.isConnected();
        }
	}
	
	@Override
	protected void onResume() { 
		super .onResume();
			Log.d("ACTION","chiamata la onresume");
			registerReceiver(ssr, smsFilter);
			//registerReceiver(ssr, connectivityFilter);
	}
	@Override
	protected void onPause() { 
		super .onPause();
		Log.d("ACTION","chiamata la onpause");
		//unregisterReceiver(ssr);	// se tolgo il commento non riceve l'intent dall sms
	//	unregisterReceiver(ncr);
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
	// Riceve gli intent inviati dal ConnectivityManager in seguito a cambiamenti nella rete..
	// rivedere...
	/*public class NetworkChangeReceiver extends BroadcastReceiver {
		
	    
		@Override
    	public void onReceive(Context context, Intent intent) {
    		Log.d("NET", "ricevuto intent dal connectivity manager!");
	    	}
	    }
*/


	
}
