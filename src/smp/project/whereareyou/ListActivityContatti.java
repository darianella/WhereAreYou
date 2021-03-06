package smp.project.whereareyou;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;

import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;

import android.widget.ListView;

public class ListActivityContatti extends Activity 
								implements OnItemClickListener {
	Cursor cur;
	String selectedNum;
	String ContactName;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ContentResolver cr = getContentResolver();
		String select = "((" + Contacts.DISPLAY_NAME + " NOTNULL) AND ("
                			 + Contacts.HAS_PHONE_NUMBER + "=1) AND ("
                			 + Contacts.DISPLAY_NAME + " != '' ))";
		String[] col = new String[] {
		        Contacts._ID,
		        Contacts.DISPLAY_NAME,
		        Contacts.CONTACT_STATUS,
		        Contacts.PHOTO_ID,
		        Contacts.LOOKUP_KEY,
	    };
		cur = cr.query(Contacts.CONTENT_URI,
				col, /* colonne */ 
				select, /* selezione */ 
				null, /* args */
				Contacts.DISPLAY_NAME /* sort */
				);
		ArrayAdapter<String> adapter = 
				new ArrayAdapter<String>(ListActivityContatti.this, 
										 android.R.layout.simple_list_item_1);
		ListView lv = new ListView(this);
		if (cur == null) // empty list
			adapter.add("Empty list");
		else {
			cur.moveToFirst();
			while (!cur.isAfterLast()) { 
				adapter.add(cur.getString(1)); // # col
				cur.moveToNext();
			}
		}
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(this);
		setContentView(lv);
	}
	@Override
	public void onItemClick(AdapterView<?> lv, View view, int pos, long id) {
		cur.moveToPosition(pos); //sposto il cursore sulla riga selezionata
		String idContact = cur.getString(0); //mi sposta sulla colonna di _ID
		ContactName = cur.getString(1);
		// genero un nuovo cursore che pu� accedere ai numeri associati 
		// all'id del contatto selezionato
		Cursor cur_phones = getContentResolver().query(
					ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null, 
					ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "
													+ idContact, null, null);
		List<String> list = new ArrayList<String>();
		cur_phones.moveToFirst();
		while (!cur_phones.isAfterLast()) {
            list.add(cur_phones.getString(
            			cur_phones.getColumnIndex(
            				ContactsContract.CommonDataKinds.Phone.NUMBER)));
            cur_phones.moveToNext();
        }
		cur_phones.close();
		final CharSequence[] charSequenceItems = 
				list.toArray(new CharSequence[list.size()]);
		selectedNum = charSequenceItems[0].toString(); 
		// more number for single contact
        AlertDialog.Builder builder = new AlertDialog.Builder(
        									ListActivityContatti.this);
        builder.setTitle(ContactName + "'s number")
        .setSingleChoiceItems(charSequenceItems, 0, 
        		new DialogInterface.OnClickListener() {
        			public void onClick(DialogInterface dialog, int button) {
        				selectedNum = charSequenceItems[button].toString();
        			} 
        		}
        )
        .setPositiveButton("Select", 
        	new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int button) {
    				Intent broadcastIntent = new Intent();
					broadcastIntent.putExtra("contact_name", ContactName);
					broadcastIntent.putExtra("contact_num", selectedNum);
					broadcastIntent.setAction(MainActivity
											.SendSmsManagerReceiver.AZIONE); 
					broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
					sendStickyBroadcast(broadcastIntent);
					finish();
    			} 
    		}
        ).setNegativeButton("Cancel", 
        		new DialogInterface.OnClickListener() {
        			public void onClick(DialogInterface dialog, int button) {
        				Log.d("DEB", "cancel...");
        			} 
    			}
        );
        AlertDialog alertDialog = builder.create();
		alertDialog.show();
	}
}
/*
	*//**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 *//*
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.list_activity_contatti, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
		// This ID represents the Home or Up button. In the case of this
		// activity, the Up button is shown. Use NavUtils to allow users
		// to navigate up one level in the application structure. For
		// more details, see the Navigation pattern on Android Design:
		//
		// http://developer.android.com/design/patterns/navigation.html#up-vs-back
		//
		NavUtils.navigateUpFromSameTask(this);
		return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
*/