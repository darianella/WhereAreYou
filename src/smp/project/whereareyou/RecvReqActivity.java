package smp.project.whereareyou;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class RecvReqActivity extends Activity {
	
	private ConnectionUtility conn;
	final Config config = new Config();
	String serverAddr;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recv_req);
		
		Intent intent = getIntent();
		String phoneNumber = intent.getStringExtra("sms_number");
		serverAddr = intent.getStringExtra("sms_ip");
		Log.d(" log ", phoneNumber + " " + serverAddr);
		EditText te = (EditText) findViewById(R.id.editText1);
		te.setText(retrieveContactName(phoneNumber));
		TextView tv = (TextView) findViewById(R.id.textView2);
		tv.setText(phoneNumber);
		
		conn = new ConnectionUtility();
/*		
  		try {
    		InputStream inputStream = 
        		ContactsContract.Contacts
        			.openContactPhotoInputStream(getContentResolver(),
            	ContentUris.withAppendedId(ContactsContract
                    .Contacts.CONTENT_URI, new Long(cur.getLong(0))));
        	if (inputStream != null) {
// FIXME: non ruscivo a recuperare l'imm spostare tutto in mainactivity!!
        		Bitmap bitmap = BitmapFactory.decodeStream(is);
				ImageView imageView = 
							(ImageView) findViewById(R.id.image_view1);
				imageView.setImageBitmap(bitmap);
                inputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
   		} 
*/
    }
	@Override
	protected void onResume() { 
		super .onResume();
			Log.d("ACTION","chiamata la onresume");
			conn.isOnline(this);
			Log.d("Client",conn.myIp);
			TextView tv = (TextView) findViewById(R.id.textView3);
			tv.setText(conn.myIp);
			
	}
	@Override
	protected void onPause() { 
		super .onPause();
//		Log.d("ACTION","chiamata la onpause");
	}

	private String retrieveContactName(String phoneNumber) {
		String phoneNumberReverse = 
				new StringBuffer(phoneNumber).reverse().toString();
		String contactName = "Unknown number";
        ContentResolver cr = this.getContentResolver();
        Cursor cur = cr.query(
                        CommonDataKinds.Phone.CONTENT_URI,
                        new String[] {ContactsContract.Contacts._ID, 
                        		ContactsContract.Contacts.DISPLAY_NAME,
                        		CommonDataKinds.Phone.NUMBER},
                        CommonDataKinds.Phone.DATA4+" = ?",
                        new String[] {phoneNumberReverse}, 
                        null);
        cur.moveToFirst();
        if (!cur.isAfterLast()) { 
        	contactName = cur.getString(1);
        }
        Log.d("INFO", "Retrieve name: " + contactName );
        cur.close();
        return contactName;
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu;this adds items to the action bar if it is present
		getMenuInflater().inflate(R.menu.recv_req, menu);
		return true;
	}
	public void acceptReq(View v) {
		Log.d("deb", "richiesta accettata");
		
		if (conn.isMobileConnected) {
			Intent i = new Intent(this, MapActivity.class);
			i.putExtra("whoiam", config.CLIENT);
			i.putExtra("serverAddr", serverAddr);
			startActivity(i);
	     } else {
	    	 Log.d("deb", "MA NON SONO CONNESSO");
	    	 conn.isOnline(this);
        }
	}
	public void rejectReq(View v) {
		Log.d("deb", "richiesta rifiutata");
		finish();
	}
	//FIXME: per debuggare il client senza il messaggio
	public void debugClient(View v) {
		Log.d("Debug client","Simulo di aver accettato la richiesta");
		EditText et=(EditText) findViewById(R.id.editText2);
		Intent i = new Intent(this, MapActivity.class);
		i.putExtra("whoiam", "client");
		i.putExtra("serverAddr", et.getText().toString());
		startActivity(i);
	}	
}
