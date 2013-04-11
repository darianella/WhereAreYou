package smp.project.whereareyou;

import android.os.Bundle;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends Activity {
	private Config config = new Config();
	private ConnectionUtility conn = new ConnectionUtility(); 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
	}
	
	public void register(View v) {
		
		EditText et = (EditText) findViewById(R.id.editTextMyPhone);
		CharSequence number = et.getText();
		if (TextUtils.isEmpty(number)) {
			Toast.makeText(getApplicationContext(), "No phone number!",
					Toast.LENGTH_LONG).show();
		} else {
			// SE SONO CONNESSO INVIO I DATI AL SERVER VIA POST
			View button = findViewById(R.id.buttonRegister);
			button.setEnabled(false);
			conn.postMyNumber(number.toString());
			// SE TUTTO VA BENE POSSO SETTARE LE PREF X NON REGISTRARMI PIU'
			SharedPreferences pref = getPreferences(MODE_PRIVATE);
			Editor editor = pref.edit();
			editor.putBoolean(config.FIRST_TIME_APP, false);
			editor.commit();
			Log.d("TMP_3", "messo a false, non ci dovrei più entrare");
			finish();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.register, menu);
		return true;
	}

}
