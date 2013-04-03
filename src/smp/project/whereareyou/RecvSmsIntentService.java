package smp.project.whereareyou;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;


public class RecvSmsIntentService extends IntentService {
	private static final int WAY_ID = 1;
	public RecvSmsIntentService() {
		super("RecvSmsIntentService");
	}
	@Override
	protected void onHandleIntent(Intent intent) {
		NotificationCompat.Builder builder = 
				new NotificationCompat.Builder(this)
					.setSmallIcon(R.drawable.ic_notification)
					.setContentTitle("WAY request")
					.setContentText("Ricevuta richiesta di localizzazione")
					.setAutoCancel(true)
					.setDefaults(Notification.DEFAULT_ALL);
		Intent resIntent = new Intent(this, RecvReqActivity.class);
		resIntent.putExtra("sms_number", intent.getStringExtra("sms_number"));
		resIntent.putExtra("sms_ip", intent.getStringExtra("sms_ip"));
		
		// The stack builder object will contain an artificial back stack
		//  for the started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(RecvReqActivity.class);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resIntent);
		PendingIntent resultPendingIntent =
		        stackBuilder.getPendingIntent(
		        		0, PendingIntent.FLAG_UPDATE_CURRENT);
		builder.setContentIntent(resultPendingIntent);
		NotificationManager nm = (NotificationManager) getSystemService(
												Context.NOTIFICATION_SERVICE);
		nm.notify(WAY_ID, builder.build());
	}
}