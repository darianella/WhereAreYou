package smp.project.whereareyou;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;

import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MapActivity extends FragmentActivity implements OnCameraChangeListener {
	private GoogleMap mMap = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d("map", "in mapactivity add google");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		LocationManager manager = (LocationManager) getSystemService(MapActivity.LOCATION_SERVICE);
	    if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
	    	showSettingsAlert();
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
 			mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
 			if (mMap != null) { // Check if we were successful in obtaining the map.
	        	setUpMap();
	        }
 		}
 	}
	private void setUpMap() {
		Log.d("map", "in setupmap");
	 	/*mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));*/
	 	//   mMap.setOnMapClickListener(this);
	   //    mMap.setOnMapLongClickListener(this);
	    //   mMap.setOnCameraChangeListener(this);
	       mMap.setMyLocationEnabled(true);
	}

/*	this.canGetLocation = true;
                // First get location from Network Provider
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
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
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
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
    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("GPS is settings");
        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            	startActivity(intent);
            }
        });
 
        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
            }
        });
 
        // Showing Alert Message
        alertDialog.show();
    }
	@Override
	public void onCameraChange(CameraPosition arg0) {
		// TODO Auto-generated method stub
	}
	
	/* Menu */
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
	      String LicenseInfo = GooglePlayServicesUtil.getOpenSourceSoftwareLicenseInfo(
	        getApplicationContext());
	      AlertDialog.Builder LicenseDialog = new AlertDialog.Builder(MapActivity.this);
	      LicenseDialog.setTitle("Legal Notices");
	      LicenseDialog.setMessage(LicenseInfo);
	      LicenseDialog.show();
	         return true;
	     }
	  return super.onOptionsItemSelected(item);
	 }

	
	
}