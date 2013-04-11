package smp.project.whereareyou;

import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private SendSmsManagerReceiver smr;
	private IntentFilter smrFilter;
	private ConnectionUtility conn;
	private Config config = new Config();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//devo vedere se sono registrato sul server
		//->guardo localmente sulle mie pref
		SharedPreferences pref = getPreferences(MODE_PRIVATE);
//		if (pref.getBoolean(config.FIRST_TIME_APP, true)) {	//FIXME
			Log.d("TMP_1", "prima volta che uso l'app");
			startActivity(new Intent(this, RegisterActivity.class));
//		}
		
		
		
		
		smrFilter = new IntentFilter(SendSmsManagerReceiver.AZIONE);
		smrFilter.addCategory(Intent.CATEGORY_DEFAULT);
		smr = new SendSmsManagerReceiver();
		registerReceiver(smr, smrFilter);	
		
		conn = new ConnectionUtility();
		
		
		
		
		EditText et = (EditText) findViewById(R.id.editTextPhoneNum);
		et.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {
				View button = findViewById(R.id.button_send_sms);
				button.setEnabled(true);
	    }
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) { // TODO Auto-generated method stub
			}
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) { // TODO Auto-generated method stub
			}
		});
	}
	@Override
	protected void onResume() { 
		super .onResume();
			Log.d("ACTION","chiamata la onresume");
			registerReceiver(smr, smrFilter);
			conn.isOnline(this);
			TextView tv = (TextView) findViewById(R.id.textViewMyIp);
			tv.setText(conn.myIp);
	}
	@Override
	protected void onPause() { 
		super .onPause();
		Log.d("ACTION","chiamata la onpause");
		unregisterReceiver(smr);
	}
	
	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}*/

	
	public void selectContact(View v) {
		Intent intent = new Intent(this, ListActivityContatti.class);
		startActivity(intent);
	}
	
	public void sendSmsButton(View v) {
		Intent intent = new Intent(this, SndSmsIntentService.class);
		EditText et = (EditText) findViewById(R.id.editTextPhoneNum);
		CharSequence number = et.getText();
//		String number = et.getText().toString();
		if (!TextUtils.isEmpty(number)) {
			intent.putExtra("contact_num", number);
			intent.putExtra("ip_addr", conn.myIp);
			startService(intent);
			Intent intent2 = new Intent(this, MapActivity.class);
			intent2.putExtra("whoiam", config.SERVER);
			startActivity(intent2);
		} else {
			Toast.makeText(getApplicationContext(), "No number!",
					Toast.LENGTH_LONG).show();
		}
	}
	
	public void debugServer(View v) {
		Log.d("Debug server","Come se avessi inviato un mess e fossi in attesa");
		Intent intent2 = new Intent(this, MapActivity.class);
		intent2.putExtra("whoiam", config.SERVER);
		startActivity(intent2);
	}
	public void debugClient(View v) {
		Log.d("Debug client","apro il latoclient come click su notifica");
		Intent resultIntent = new Intent(this, RecvReqActivity.class);
		resultIntent.putExtra("sms_number", "11223344");
		resultIntent.putExtra("sms_ip", "1.1.1.1");
		startActivity(resultIntent);
	}
	
	/*
	   ************************************************************************
			BroadcastReceiver:  riceve le info sul contatto selezionato
	*/
	
	public class SendSmsManagerReceiver extends BroadcastReceiver {
		static final String AZIONE = "smp.project.whereareyou.READY_TO_SEND";
	    
		@Override
    	public void onReceive(Context context, Intent i) {
			removeStickyBroadcast(i); //sticky: altrimenti resta "in giro"
			TextView tv = (TextView) findViewById(R.id.textViewContactName);
    		tv.setText(i.getStringExtra("contact_name"));
    		EditText te = (EditText) findViewById(R.id.editTextPhoneNum);
    		te.setText(i.getStringExtra("contact_num"));
			if (conn.isMobileConnected) {
				Log.d("DEb","ip valido");
				View button = findViewById(R.id.button_send_sms);
				button.setEnabled(true);
	    	} else {
	    		Log.d("DEb","Impossibile inviare il messaggio->ip NON valido");
	    		tv.setText("SMS DOESN'T SEND, NO IP");
	    	}
	    }
	}
}