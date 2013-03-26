package smp.project.whereareyou;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import android.net.Uri;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.QuickContactBadge;

public class RecvReqActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recv_req);
		
		Intent intent = getIntent();
		String phoneNumber = intent.getStringExtra("sms_number");
		String ip = intent.getStringExtra("sms_ip");
		Log.d(" log ", phoneNumber + " " + ip);
		
		String phoneNumberReverse = new StringBuffer(phoneNumber).reverse().toString();
		
        ContentResolver cr = this.getContentResolver();
        
        
       
        Cursor cur = cr.query(
                        CommonDataKinds.Phone.CONTENT_URI,
                        new String[] {ContactsContract.Contacts._ID, 
                        		ContactsContract.Contacts.DISPLAY_NAME,
                        		CommonDataKinds.Phone.NUMBER},
                        CommonDataKinds.Phone.DATA4+" = ?",
                        new String[] {phoneNumberReverse}, 
                        null);
        if (cur == null) { 
        	Log.d(" log ", "numero sconosciuto");
        } else {
        	cur.moveToFirst();
        	Log.d("asdf", cur.getString(1));
        	
        	    	
     /*       
        	Bitmap photo;
        	try {
                InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(getContentResolver(),
                        ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(cur.getLong(0))));
     
                if (inputStream != null) {
                    photo = BitmapFactory.decodeStream(inputStream);
                    ImageView imageView = (ImageView) findViewById(R.id.imageView1);
                    imageView.setImageBitmap(photo);
                }
     
                assert inputStream != null;
                inputStream.close();
     
            } catch (Exception e) {
                e.printStackTrace();
            }*/
        	
        	
              }

    }

		
		
		
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.recv_req, menu);
		return true;
	}
	
	public void acceptReq() {
		Log.d("deb", "richiesta accettata");
	}
	
	public void rejectReq() {
		Log.d("deb", "richiesta rifiutata");
		finish();
	}
}
