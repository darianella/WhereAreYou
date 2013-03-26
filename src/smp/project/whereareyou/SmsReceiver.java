package smp.project.whereareyou;

import java.util.StringTokenizer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;


public class SmsReceiver extends BroadcastReceiver {
	public SmsReceiver() {
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras(); // estraiamo i dati dall'intent
		Object messages[] = (Object[]) bundle.get("pdus"); //da tutti i dati preleviamo quelli relativi al pdu: std sms
		
		SmsMessage smsMessage[] = new SmsMessage[messages.length];
		for (int n = 0; n < messages.length; n++) {
			smsMessage[n] = SmsMessage.createFromPdu((byte[]) messages[n]);
		}
		String originAddr = smsMessage[0].getOriginatingAddress();
        String mess = smsMessage[0].getMessageBody();   
        
        Log.d("[sms_number]", originAddr);
        
		
		// Verifico testo messaggio
        
//        Log.d("DEB","Sms: " + mess.split(" ").toString());
        StringTokenizer st = new StringTokenizer(mess, ";");
        
		if (st.nextToken().equals("[WhereAreYou]")) { // è il messaggio dell'app
			Log.d("DEB","Sms request recv");
			this.abortBroadcast(); // Cosi non faccio elaborare la richiesta ad altri 
	        Intent in = new Intent(context, RecvSmsIntentService.class);
	        in.putExtra("sms_number", originAddr);
	        in.putExtra("sms_ip", st.nextToken());  
	        context.startService(in);
		} else
			Log.d("DEB","Sms no dell'appl");
	}
}