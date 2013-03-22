package smp.project.whereareyou;

import android.app.IntentService;
import android.content.Intent;
import android.telephony.SmsManager;
import android.util.Log;



public class SndMsgIntentService extends IntentService {
	public SndMsgIntentService() {
		super("SndMsgIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		
		String contact_num = intent.getStringExtra("contact_num");
		String sms = "[TAG] some text";
		
		 
		Intent broadcastIntent = new Intent();
		broadcastIntent.putExtra("contact_name", intent.getStringExtra("contact_name"));
		broadcastIntent.putExtra("contact_num", contact_num);
		try {
			SmsManager smsManager = SmsManager.getDefault();
			smsManager.sendTextMessage(contact_num, null, sms, null, null);
			Log.d("[SMS LOG]", "Sms send.");
			broadcastIntent.putExtra("status", "message send");
		} catch (Exception e) {
			Log.d("[SMS LOG]", "Error! Problem sending sms.");
			broadcastIntent.putExtra("status", "Error! Problem sending sms.");
			e.printStackTrace();
		} finally {
			broadcastIntent.setAction(MainActivity.SmsStatusReceiver.AZIONE); 
			broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
			sendBroadcast(broadcastIntent);
		}
	}
}