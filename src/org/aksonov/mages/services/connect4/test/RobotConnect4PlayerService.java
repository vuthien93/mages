package org.aksonov.mages.services.connect4.test;

import org.aksonov.mages.entities.PlayerInfo;
import org.aksonov.mages.services.IGamePlayer;
import org.aksonov.mages.services.IGamePlayerFactory;
import org.aksonov.mages.services.IGameServer;

import android.app.Service;
import android.content.Intent;
import android.os.RemoteException;
import android.os.IBinder;

public class RobotConnect4PlayerService extends Service {
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
			player1 = createPlayer("C4robot1");
			//player1.registerLoginListener(listener);
			server.connect(serverSession, player1);
			
			player2 = createPlayer("C4robot2");
			//player2.registerLoginListener(listener);
			server.connect(serverSession, player2);
		}

		private IGamePlayer createPlayer(final String name)
				throws RemoteException {
			
			IGamePlayer player = new RobotConnect4Player();
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
