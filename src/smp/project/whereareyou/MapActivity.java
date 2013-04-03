package smp.project.whereareyou;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MapActivity extends FragmentActivity 
							implements OnCameraChangeListener {
	final Config c = new Config();
	private GoogleMap mMap = null;
	ConnectionUtility cu = new ConnectionUtility();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// loading maps
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		Intent intent = getIntent();
		if (intent.getStringExtra("whoiam").equals(c.CLIENT)) {
			new ClientTask().execute(intent.getStringExtra("severAddr"));
		} else { 
			new ServerTask().execute(intent.getStringExtra("fixme: no text"));
		}
		LocationManager manager = (LocationManager) getSystemService(
												MapActivity.LOCATION_SERVICE);
	    if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
	    	cu.showSettingsAlert(this, c.NOGPS_MSG, c.ACTION_LOC_SRC_SETT);
	    }
	    setUpMapIfNeeded();
	}
	@Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }
	    	
	private void setUpMapIfNeeded() {
		Log.d("map", "in setupmapifneed");
 		if (mMap == null) {
 			mMap = ((SupportMapFragment) getSupportFragmentManager()
 									.findFragmentById(R.id.map)).getMap();
 			if (mMap != null) { 
	        	setUpMap();
	        }
 		}
 	}
	private void setUpMap() {
		Log.d("map", "in setupmap");
	 	/*mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0))
	 	 * 								.title("Marker"));*/
	 	//   mMap.setOnMapClickListener(this);
	   //    mMap.setOnMapLongClickListener(this);
	    //   mMap.setOnCameraChangeListener(this);
	       mMap.setMyLocationEnabled(true);
	}

/*	
 * 			SISTEMARE COME DEVE FUNZIONARE LA MAPPA.... 
 * this.canGetLocation = true;
                // First get location from Network Provider
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager
                                		.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager
                                    		.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
*/	
/*	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.map, menu);
		return true;
	}
*/
  /*  public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("GPS is settings");
        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. 
        Do you want to go to settings menu?");
        alertDialog.setPositiveButton("Settings", new DialogInterface
        								.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(
                		Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            	startActivity(intent);
            }
        });
 
        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface
        										.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
            }
        });
 
        // Showing Alert Message
        alertDialog.show();
    }
    
    */
	
	@Override
	public void onCameraChange(CameraPosition arg0) {
		// TODO Auto-generated method stub
	}
	/*
	  -------------------------------------------------------------------------
			
						Menu
						 
	 */
	 @Override
	 public boolean onCreateOptionsMenu(Menu menu) {
		 super.onCreateOptionsMenu(menu);
		 MenuInflater mi = getMenuInflater();
		 mi.inflate(R.menu.map, menu); 
		 return true;
	 }
	 @Override
	 public boolean onOptionsItemSelected(MenuItem item) {
	  switch (item.getItemId()) {
	     case R.id.menu_legalnotices:
	      String LicenseInfo = GooglePlayServicesUtil
	      			.getOpenSourceSoftwareLicenseInfo(getApplicationContext());
	      AlertDialog.Builder LicenseDialog = new AlertDialog
	    		  								.Builder(MapActivity.this);
	      LicenseDialog.setTitle("Legal Notices");
	      LicenseDialog.setMessage(LicenseInfo);
	      LicenseDialog.show();
	         return true;
	     }
	  return super.onOptionsItemSelected(item);
	 }
	 
	 /*
	   ------------------------------------------------------------------------

				AsyncTask client side (after receiving Sms)
			
	 */
	public class ClientTask extends AsyncTask <String, Integer, Void> {
		@Override
		protected Void doInBackground(String... params) {
			Log.d("Debug client","Sono nella asynctask background");
			PrintWriter out;
	        BufferedReader in;
	        String msg;
			try {
				Socket socket = new Socket(params[0], c.SERVER_PORT);
				// To read and write unicode char 
				out = new PrintWriter(socket.getOutputStream(), true);
				in = new BufferedReader(new InputStreamReader(
												socket.getInputStream()));
				// test
				out.println("hello by client");
				msg = in.readLine();
				Log.d("client rx:", msg);
				// finally
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
			
//			@Override
//		    protected void onPostExecute(String result) {
//		        super.onPostExecute(result);
//		        //Do anything with response..
//	    	}
		}
	/*
	  -------------------------------------------------------------------------

				AsyncTask server side (after sending Sms)
			
	*/
	public class ServerTask extends AsyncTask <String, Integer, Void> {

		ProgressDialog dialog;

		protected void onPreExecute() {
			dialog = new ProgressDialog(MapActivity.this);
			dialog.setTitle("Waiting for replay...");
	        dialog.show();
		}
		/*protected void onPostExecute(String result) {
			super .onPostExecute(result);
			TextView tv = (TextView) findViewById(R.id.textView1);
			tv.setText(ipAddr);
		*/
		@Override
		protected Void doInBackground(String... arg0) {
			Log.d("Debug server","Sono nella asynctask background");
			ServerSocket serverSocket;
			Socket socket;
			BufferedReader in;
	        PrintStream out;
	        String msg;
			try {
				serverSocket = new ServerSocket(c.SERVER_PORT);
				socket = serverSocket.accept();
				in = new BufferedReader(new InputStreamReader(
												socket.getInputStream()));
				out = new PrintStream(socket.getOutputStream());
				dialog.dismiss();
				msg = in.readLine();
				out.println("Hello by Server");
				Log.d("server rx:", msg);
				
				//finally
				out.close();
				in.close();
				socket.close();
				serverSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
			return null;
		}
	}
}