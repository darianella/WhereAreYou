package smp.project.whereareyou;

import android.app.AlertDialog;
import android.app.IntentService;
import android.content.DialogInterface;
import android.content.Intent;
import android.telephony.SmsManager;
import android.util.Log;

public class SndSmsIntentService extends IntentService {
	Config config;
	
	public SndSmsIntentService() {
		super("SndMsgIntentService");
		config = new Config();
	}
	@Override
	protected void onHandleIntent(Intent intent) {
		String contactNum = intent.getStringExtra("contact_num");
		String ipAddr = intent.getStringExtra("ip_addr");
		String sms = config.TOKEN_APP_NAME + config.TOKEN_DELIMITER + 
				ipAddr + config.TOKEN_DELIMITER + config.SMS_TXT_MSG;
		try {
			SmsManager smsManager = SmsManager.getDefault();
			smsManager.sendTextMessage(contactNum, null, sms, null, null);
			Log.d("[SMS LOG]", "Sms send-> Go to map");
		} catch (Exception e) {
			e.printStackTrace();
			Log.d("[SMS LOG]", "Error! Problem sending sms.");
			AlertDialog.Builder builder = 
							new AlertDialog.Builder(getApplicationContext());
    		builder.setTitle("Error");
	 			builder.setMessage("Impossibile inviare l'SMS");
	 			builder.setPositiveButton("Ok", 
	 				new DialogInterface.OnClickListener() {
	 					public void onClick(DialogInterface dialog, int id) {
	 					//FIXME: ToDo ?
	 				}
 			});
 			AlertDialog alert = builder.create();
 			alert.show();
		} 
	}
}