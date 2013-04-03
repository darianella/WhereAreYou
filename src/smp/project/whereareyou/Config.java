package smp.project.whereareyou;

import android.provider.Settings;

public class Config {
	
	//Message
	final String TOKEN_APP_NAME = "[WhereAreYou]";
	final String TOKEN_DELIMITER = ";";
	final String SMS_TXT_MSG = "Hai ricevuto una richiesta per l'applicazione " +
								"WhereAreYou. Scaricala all'indirizzo: N.D. ...";
	
	final String NOCONNECTION_MSG = "Mobile connection is not enabled. " +
										"Do you want to go to settings menu?";
	final String ACTION_WIRELESS = Settings.ACTION_WIRELESS_SETTINGS;
	final String NOGPS_MSG = "GPS is not enabled. " +
			"							Do you want to go to settings menu?";
	final String ACTION_LOC_SRC_SETT = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
	final String SETTING_ALERT_TITLE = "Warning, the App can't work correctly";
	
	final String NO_IP = "No valid IP address";
	final String CLIENT = "client";
	final String SERVER = "server";
	//Socket
	final int SERVER_PORT = 12345;
	
	
	
	
}
