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

import org.aksonov.mages.entities.PlayerInfo;
import org.aksonov.mages.services.ILocalServerConfiguration;
import org.aksonov.mages.tools.AppHelper;
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
import android.widget.EditText;

// TODO: Auto-generated Javadoc
/**
 * The Class LocalServerConfigure.
 */
public class LocalServerConfigure extends Activity {
	
	/** The Constant APP_ID. */
	private static final String APP_ID = "app_id";
	
	/** The Constant SERVER. */
	private static final String SERVER = "server";
	
	/** The Constant USERNAME. */
	private static final String USERNAME = "username";
	
	/** The Constant PASSWORD. */
	private static final String PASSWORD = "password";

	/** The username field. */
	private EditText usernameField;
	
	/** The m service. */
	protected ILocalServerConfiguration mService = null;
	
	/** The m connection. */
	protected ServiceConnection mConnection = new ServiceConnection() {

		public void onServiceConnected(ComponentName className, IBinder service) {
			mService = ILocalServerConfiguration.Stub.asInterface((IBinder) service);
		}

		public void onServiceDisconnected(ComponentName className) {
			mService = null;
		}
	};

	/**
	 * Called when the activity is first created.
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

		Intent intent = new Intent(ILocalServerConfiguration.class.getName()).setType(AppHelper.getType(getIntent()));
		//Log.d("LocalServerConfiguration", "Calling service " +intent);
		bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

		setContentView(R.layout.local_configure);
		usernameField = (EditText) findViewById(R.id.username);

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
		SharedPreferences preferences = getPreferences(0);
		usernameField.setText(preferences.getString(USERNAME, ""));
	}

	/** The save listener. */
	private OnClickListener saveListener = new OnClickListener() {
		public void onClick(View v) {
			if (mService == null){
				AppHelper.showMessage(LocalServerConfigure.this, "Cannot create service " + ILocalServerConfiguration.class.toString());
				return;
			}
			
			final String username = usernameField.getText().toString();
			if (username.equals("")) {
				AppHelper.showMessage(LocalServerConfigure.this,
						"Empty username is not allowed");
				return;
			}
			try {
				PlayerInfo info = PlayerInfo.create();
				info.username = username;
				int session = mService.createSession();
				mService.setPlayerInfo(session, info);
			      // Save user preferences. We need an Editor object to
			      // make changes. All objects are from android.context.Context
			      SharedPreferences settings = getPreferences(0);
			      SharedPreferences.Editor editor = settings.edit();
			      editor.putString(USERNAME, username);
			      // Don't forget to commit your edits!!!
			      editor.commit();
			      
			      setResult(session);
			      finish();
			      
			} catch (Exception e) {
                e.printStackTrace();
				//Log.e("GASPConfigure", e);
			}
		}
	};
	
	/** The clear listener. */
	private OnClickListener clearListener = new OnClickListener() {
		public void onClick(View v) {
			finish();
			//usernameField.setText("");
		}
	};
}