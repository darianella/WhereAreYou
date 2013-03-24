/*package smp.project.whereareyou;


import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;



import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

public class MyTask extends AsyncTask <String, Integer, String> {
	
	int attempts = 0;
	String ipAddr = "none";
	
	private void myIp() {
		Log.d("TMP", "inMyIp");
		
		try {
			Thread.sleep(4000);
       		for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
       			NetworkInterface intf = en.nextElement();
       			for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
       				InetAddress inetAddress = enumIpAddr.nextElement();
       				if (!inetAddress.isLoopbackAddress()) {
       					ipAddr = inetAddress.getHostAddress().toString();
       					Log.d("NET", ipAddr);
       				}
       				else {
       					if (attempts++ < 3)
       						myIp();
       				}
       			}
       		}
       	} catch (Exception ex) {
       		Log.e("asd", ex.toString());
       	}
	}
	@Override
	protected void onPostExecute(String result) {
		super .onPostExecute(result);
		TextView tv = (TextView) findViewById(R.id.textView1);
		
		
	}
	
	@Override
	protected String doInBackground(String... arg0) {
		
			myIp();
		
		return null;
	}

}*/
