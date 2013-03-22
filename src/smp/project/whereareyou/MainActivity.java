package smp.project.whereareyou;


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
	IntentFilter filter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Button bt = (Button) findViewById(R.id.button1);
		bt.setOnClickListener(this);
		filter = new IntentFilter(SmsStatusReceiver.AZIONE); 
		filter.addCategory(Intent.CATEGORY_DEFAULT);
		ssr = new SmsStatusReceiver();
		
		Intent intent = new Intent(this, ListActivityLocation.class);
		startActivity(intent);
		
	}
	
	@Override
	protected void onResume() { 
		super .onResume();
			Log.d("deb","chiamata la onresume");
			registerReceiver(ssr, filter);
	}
	@Override
	protected void onPause() { 
		super .onPause();
		//unregisterReceiver(ssr);	// se tolgo il commento 
		Log.d("deb","chiamata la onpause");
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



	
}
