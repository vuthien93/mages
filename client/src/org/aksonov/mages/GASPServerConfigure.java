/***
 * Mages: Multiplayer Game Engine for mobile devices
 * Copyright (C) 2008 aksonov
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Contact: aksonov dot gmail dot com
 *
 * Author: Pavlo Aksonov
 */
package org.aksonov.mages;

import java.net.URL;
import java.util.List;

import org.aksonov.mages.services.gasp.IGASPServerConfiguration;
import org.aksonov.mages.tools.AppHelper;
import org.aksonov.mages.tools.ServerData;
import org.aksonov.mages.tools.ServerListXmlLoader;
import org.aksonov.tools.Log;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

// TODO: Auto-generated Javadoc
/**
 * Activity to configure GASP server.
 * 
 * @author Pavel
 */
public class GASPServerConfigure extends Activity  {
	
	/** The Constant APP_ID. */
	private static final String APP_ID = "app_id";
	
	/** The Constant SERVER. */
	private static final String SERVER = "server";
	
	/** The Constant USERNAME. */
	private static final String USERNAME = "username";
	
	/** The Constant PASSWORD. */
	private static final String PASSWORD = "password";

	/** The Constant COMET. */
	private static final String COMET = "comet";

	/** The username field. */
	private EditText usernameField;
	
	/** The comet field. */
	private CheckBox cometField;
	
	/** The password field. */
	private EditText passwordField;
	
	/** The server field. */
	private EditText serverField;
	
	/** The app id field. */
	private EditText appIdField;
	
	/** The type. */
	private String type;
	
	/** Used game server. */
	protected IGASPServerConfiguration mService = null;
	
	/** Connection to the game server. */
	protected ServiceConnection mConnection = new ServiceConnection() {

		public void onServiceConnected(ComponentName className, IBinder service) {
			mService = IGASPServerConfiguration.Stub.asInterface((IBinder) service);
		}

		public void onServiceDisconnected(ComponentName className) {
			mService = null;
		}
	};

	/**
	 * Called when the activity is destroyed.
	 */
	
	protected void onDestroy() {
		super.onDestroy();
		unbindService(mConnection);
	}

	/**
	 * Called when the activity is first created.
	 * 
	 * @param icicle
	 *            the icicle
	 */
	
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		bindService(new Intent(IGASPServerConfiguration.class.getName()), mConnection,
				Context.BIND_AUTO_CREATE);

		setContentView(R.layout.gasp_configure);
		usernameField = (EditText) findViewById(R.id.username);
		passwordField = (EditText) findViewById(R.id.password);
		serverField = (EditText) findViewById(R.id.gaspServer);
		appIdField = (EditText) findViewById(R.id.appId);
		cometField = (CheckBox) findViewById(R.id.comet);
		
		usernameField.requestFocus();

		Button save = (Button) findViewById(R.id.save);
		save.setOnClickListener(saveListener);

		Button clear = (Button) findViewById(R.id.clear);
		clear.setOnClickListener(clearListener);

		setData();

	}

	/**
	 * Sets the data.
	 */
	private void setData() {
		// load server data for the game
		List<ServerData> list = new ServerListXmlLoader(this)
				.load(R.xml.settings);

		if (list.size() == 0) {
			throw new IllegalArgumentException(
					"App settings doesn't contain server data");
		}
		type = AppHelper.getType(getIntent());
		int found = 0;
		for (int i=0;i<list.size();i++){
			if (list.get(i).getType().equals(type)){
				found = i;
			}
		}
		
		SharedPreferences preferences = getPreferences(0);
		appIdField.setText(preferences.getInt(type+APP_ID, list.get(found).getAppId())+"");
		serverField
				.setText(preferences.getString(type+SERVER, list.get(found).getUri()));
		usernameField.setText(preferences.getString(type+USERNAME, ""));
		
		cometField.setChecked(preferences.getBoolean(type+COMET, false));
	}

	/** The save listener. */
	private OnClickListener saveListener = new OnClickListener() {
		public void onClick(View v) {
			if (mService == null){
				AppHelper.showMessage(GASPServerConfigure.this, "Cannot create service " + IGASPServerConfiguration.class.toString());
				return;
			}
			
			final String username = usernameField.getText().toString();
			final String password = passwordField.getText().toString();
			boolean isComet = cometField.isChecked();
			String server = "";
			int appId = 0;
			if (server.equals("")) {
				try {
					URL url = new URL(serverField.getText().toString());
					server = url.toExternalForm();
				} catch (Exception e) {
					AppHelper.showMessage(GASPServerConfigure.this,
							"Invalid GASP Server");
					return;
				}
			}
			if (username.equals("")) {
				AppHelper.showMessage(GASPServerConfigure.this,
						"Empty username is not allowed");
				return;
			}
			try {
				appId = Integer.parseInt(appIdField.getText().toString());
			} catch (Exception e) {
				AppHelper.showMessage(GASPServerConfigure.this,
						"Invalid application id");
			}
			try {
				int session = mService.createSession();
				mService.setConnectInfo(session, server, appId, username,
						password, isComet);
			      // Save user preferences. We need an Editor object to
			      // make changes. All objects are from android.context.Context
			      SharedPreferences settings = getPreferences(0);
			      SharedPreferences.Editor editor = settings.edit();
			      editor.putInt(type+APP_ID, appId);
			      editor.putString(type+SERVER, server);
			      editor.putString(type+USERNAME, username);
			      editor.putBoolean(type+COMET, isComet);

			      // Don't forget to commit your edits!!!
			      editor.commit();
			      setResult(session);
			      finish();
			} catch (Exception e) {
				Log.e("GASPConfigure", e);
			}
		}
	};
	
	/** The clear listener. */
	private OnClickListener clearListener = new OnClickListener() {
		public void onClick(View v) {
			finish();
			/*appIdField.setText("");
			serverField.setText("");
			usernameField.setText("");
			passwordField.setText("");*/
		}
	};
}