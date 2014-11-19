package com.android.domji84.mcgridview.utils;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created by domji84 on 19/11/14.
 */
public class ConnectionTypeChecker {

	public static enum ConnectionType {
		UNKNOWN, MOBILE, WIFI;
	}

	public static ConnectionType getConnectionType(Context context) {
		final ConnectivityManager manager = (ConnectivityManager)
			context.getSystemService(context.CONNECTIVITY_SERVICE);
		if (manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting()) {
			return ConnectionType.MOBILE;
		} else if (manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting()) {
			return ConnectionType.WIFI;
		}
		return ConnectionType.UNKNOWN;
	}

}
