package smp.project.whereareyou;

import android.app.IntentService;
import android.content.Intent;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;



public class SndSmsIntentService extends IntentService {
	public SndSmsIntentService() {
		super("SndMsgIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		
		String contactNum = intent.getStringExtra("contact_num");
		String ipAddr = intent.getStringExtra("ip_addr");
		
		String sms = "[WhereAreYou];" + ipAddr + "; Hai ricevuto una richiesta per WhereAreYou, scarica l'app al link: none";
		 
		try {
			SmsManager smsManager = SmsManager.getDefault();
			smsManager.sendTextMessage(contactNum, null, sms, null, null);
			Log.d("[SMS LOG]", "Sms send.");
		} catch (Exception e) {
			Log.d("[SMS LOG]", "Error! Problem sending sms.");
			Toast.makeText(getApplicationContext(), "Error! Problem sending sms.", Toast.LENGTH_LONG).show();
			e.printStackTrace();
		} 
	}
}