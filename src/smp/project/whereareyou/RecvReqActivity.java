package smp.project.whereareyou;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.QuickContactBadge;
import android.widget.TextView;

public class RecvReqActivity extends Activity {
	
	String ip_addr = "No ip";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recv_req);
		
		Intent intent = getIntent();
		String phoneNumber = intent.getStringExtra("sms_number");
		ip_addr = intent.getStringExtra("sms_ip");
		Log.d(" log ", phoneNumber + " " + ip_addr);
		EditText te = (EditText) findViewById(R.id.editText1);
		te.setText(retrieveContactName(phoneNumber));
		TextView tv = (TextView) findViewById(R.id.textView2);
		tv.setText(phoneNumber);
		//FIXME: VERIFICARE DI ESSERE CONNESSO A INTERNET! magari usare lo stesso blocco sia qui che nel mainActivity
		
        	
/*		try {
            InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(getContentResolver(),
                        ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(cur.getLong(0))));
            if (inputStream != null) {
                // FIXME: non ruscivo a recuperare l'immagine.. spostare tutto e testare in mainactivity!!
                Bitmap bitmap = BitmapFactory.decodeStream(is);
				ImageView imageView = (ImageView) findViewById(R.id.image_view1);
				imageView.setImageBitmap(bitmap);
                inputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
   		} */
    }

		
		
	private String retrieveContactName(String phoneNumber) {
		String phoneNumberReverse = new StringBuffer(phoneNumber).reverse().toString();
		String contactName = "Unknown number";;
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
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.recv_req, menu);
		return true;
	}
	
	public void acceptReq(View v) {
		Log.d("deb", "richiesta accettata");
		
		
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new ClientTask().execute(ip_addr);
        } else {
            Log.d("net","No network connection available.");
        }
		
		
		
		
	}
	
	public void rejectReq(View v) {
		Log.d("deb", "richiesta rifiutata");
		//FIXME: solo di prova..
		Intent i = new Intent(this, MapActivity.class);
		startActivity(i);
		
		finish();
	}
	
	
	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * 	ASYNCTASK
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 *
	 * */
	//FIXME: per debuggare il client senza il messaggio
	public void debugClient(View v) {
		Log.d("Debug client","Simulo di aver accettato la richiesta");
		EditText et=(EditText) findViewById(R.id.editText2);
		String str = et.getText().toString();
		new ClientTask().execute(str);
		
	}
	public class ClientTask extends AsyncTask <String, Integer, Void> {

		@Override
		protected Void doInBackground(String... params) {
			Log.d("Debug client","Sono nella asynctask background");
			int serverPort = 12345;
			PrintWriter out;
	        BufferedReader in;
	        String msg;
	        
			try {
				Socket socket = new Socket( params[0], serverPort );
				// per leggere e scrivere caratteri unicode sul socket
				out = new PrintWriter(socket.getOutputStream(), true);
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				out.println("hello by client");
				msg = in.readLine();
				Log.d("client rx:", msg);
				
				out.close();
				in.close();
				socket.close();
				
				
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			return null;
		}
		
//		@Override
//	    protected void onPostExecute(String result) {
//	        super.onPostExecute(result);
//	        //Do anything with response..
//    	}
		
		
	}
	
	
	
	
	
}
