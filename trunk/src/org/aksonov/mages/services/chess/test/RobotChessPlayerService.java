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
package org.aksonov.mages.services.chess.test;

import org.aksonov.mages.entities.PlayerInfo;
import org.aksonov.mages.services.IGamePlayer;
import org.aksonov.mages.services.IGamePlayerFactory;
import org.aksonov.mages.services.IGameServer;

import android.app.Service;
import android.content.Intent;
import android.os.RemoteException;
import android.os.IBinder;

public class RobotChessPlayerService extends Service {
	private IGamePlayerFactory.Stub mBinder = new IGamePlayerFactory.Stub(){
		private IGamePlayer player1;
		private IGamePlayer player2;
		
		
		public void disconnect(int session) throws RemoteException {
			if (player1 != null)
			player1.dispose();
			
			if (player2 != null)
			player2.dispose();
		}

		
		public void exportPlayers(int session, int serverSession, IGameServer server) throws RemoteException {
			if (serverSession < 0 || server == null){
				throw new IllegalArgumentException("Server/session is not set");
			}
			player1 = createPlayer("robot1");
			//player1.registerLoginListener(listener);
			server.connect(serverSession, player1);
			
			player2 = createPlayer("robot2");
			//player2.registerLoginListener(listener);
			server.connect(serverSession, player2);
		}

		private IGamePlayer createPlayer(final String name)
				throws RemoteException {
			
			IGamePlayer player = new RobotChessPlayer();
			PlayerInfo info = PlayerInfo.create();
			info.username = name;
			info.password = name;
			player.setInfo(info);
			return player;
		}

		
		public int createSession() throws RemoteException {
			return 0;
		}

		
		public boolean isConfigured(int session) throws RemoteException {
			return true;
		}
	};
	
	
	public IBinder onBind(Intent intent) {
		return mBinder;
	}}
