package smp.project.whereareyou;

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
		Object messages[] = (Object[]) bundle.get("pdus"); //da tutti i dati preleviamo quelli relativi al pdu: formato std sms
		SmsMessage smsMessage[] = new SmsMessage[messages.length];
		for (int n = 0; n < messages.length; n++) {
			smsMessage[n] = SmsMessage.createFromPdu((byte[]) messages[n]);
		}
		//FIXME: PARSING DI STRINGA BRUTTA, MA FUNZIONA
		String mess = new String(smsMessage[0].getMessageBody().toString());
		String[] tmp = mess.split(" ");
		// Verifico testo messaggio
		if (tmp[0].equals("[WhereAreYou]")) { // è il messaggio dell'app
			Log.d("DEB","Sms request recv");
			//se do max prio a questo receiver e riconosco il tipo di messaggio, non lo faccio gestire agli altri receiver
			this.abortBroadcast(); 
			
//	        Intent broadcastIntent = new Intent(); 
//	        broadcastIntent.setAction(DoveSeiActivity.DoveSeiReceiver.AZIONE); 
//	        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT); 
//	        context.sendBroadcast(broadcastIntent);
		} else
			Log.d("DEB","Sms no dell'appl");
				
	}
}
