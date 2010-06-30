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
package org.aksonov.mages.services.connect4;

import org.aksonov.mages.entities.PlayerInfo;
import org.aksonov.mages.services.IGameServer;
import org.aksonov.mages.services.ILocalServerConfiguration;
import org.aksonov.mages.services.LocalGameServer;

import android.app.Service;
import android.content.Intent;
import android.os.RemoteException;
import android.os.IBinder;

public class LocalConnect4ServerService extends Service {
	private final LocalGameServer server = new LocalConnect4Server();
	private final IGameServer.Stub mBinder = server;
	private final ILocalServerConfiguration.Stub mLocalBinder = new ILocalServerConfiguration.Stub(){
		
		public void setPlayerInfo(int session, PlayerInfo player)
				throws RemoteException {
			server.setPlayerInfo(session, player);
		}

		
		public int createSession() throws RemoteException {
			return server.createSession();
		}
		
	};
	
	
	
	public IBinder onBind(Intent intent) {
		if (ILocalServerConfiguration.class.getName().equals(intent.getAction())) {
			return mLocalBinder;
		}
		return mBinder;
	}

}
