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

import java.util.List;

import org.aksonov.mages.entities.GameSettings;
import org.aksonov.mages.entities.PlayerInfo;
import org.aksonov.mages.services.IGamePlayerFactory;
import org.aksonov.mages.services.IGameServer;
import org.aksonov.mages.tools.AndroidLogger;
import org.aksonov.tools.Log;

import android.app.Application;
import android.content.Intent;
import android.content.pm.ResolveInfo;

// TODO: Auto-generated Javadoc
/**
 * Represents Android application class for Mages engine. It starts all needed
 * services (game servers and player factories) during startup and stops them
 * during destroy.
 * 
 * The class is also used to share some data between Lobby and InGame activities -
 * choosen GameSettings and list of active players
 * 
 * @author Pavel
 */
public class MagesApplication extends Application {
	
	/** Choosen game during lobby activity and passed to InGame activity. */
	public GameSettings game = null;
	
	/** List of active players passed to InGame activity. */
	public List<PlayerInfo> players = null;
	
	/**
	 * Method starts all needed services.
	 */
	public void onCreate(){
		super.onCreate();
		Log.setLogger(AndroidLogger.instance);
		Intent intent = new Intent(IGameServer.class.getName());
		List<ResolveInfo> mList = getPackageManager().queryIntentServices(intent, 0);
		Log.d("MagesApplication", "Starting game servers: " + mList.size());
		for (ResolveInfo info : mList){
			startService(intent.setClassName(info.serviceInfo.packageName, info.serviceInfo.name));
		}
		
		intent = new Intent(IGamePlayerFactory.class.getName());
		mList = getPackageManager().queryIntentServices(intent, 0);
		Log.d("MagesApplication", "Starting player factories: " + mList.size());
		for (ResolveInfo info : mList){
			startService(intent.setClassName(info.serviceInfo.packageName, info.serviceInfo.name));
		}
	}
	
	/**
	 * Method stops used services.
	 */
	public void onTerminate(){
		Intent intent = new Intent(IGameServer.class.getName());
		List<ResolveInfo> mList = getPackageManager().queryIntentServices(intent, 0);
		for (ResolveInfo info : mList){
			stopService(intent.setClassName(info.serviceInfo.packageName, info.serviceInfo.name));
		}
		
		intent = new Intent(IGamePlayerFactory.class.getName());
		mList = getPackageManager().queryIntentServices(intent, 0);
		for (ResolveInfo info : mList){
			stopService(intent.setClassName(info.serviceInfo.packageName, info.serviceInfo.name));
		}
		super.onTerminate();
	}

}
