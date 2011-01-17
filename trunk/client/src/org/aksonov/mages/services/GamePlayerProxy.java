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
package org.aksonov.mages.services;

import java.util.List;

import org.aksonov.mages.entities.Custom;
import org.aksonov.mages.entities.GameSettings;
import org.aksonov.mages.entities.Move;
import org.aksonov.mages.entities.Note;
import org.aksonov.mages.entities.PlayerInfo;
import org.aksonov.tools.Log;

import android.os.RemoteException;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

// TODO: Auto-generated Javadoc
/**
 * This class-wrapper implements asynchronous game player, to avoid delays
 * within local game server. Local game server registers wrappers instead of
 * GamePlayer instances to immediately call send methods
 * 
 * @author Pavel
 */
public class GamePlayerProxy implements IGamePlayer {
	
	/** The player. */
	private final IGamePlayer player;
	
	/** The Constant CREATE. */
	private final static int CREATE = 0;
	
	/** The Constant START. */
	private final static int START = 1;
	
	/** The Constant END. */
	private final static int END = 2;
	
	/** The Constant QUIT. */
	private final static int QUIT = 3;
	
	/** The Constant JOIN. */
	private final static int JOIN = 4;
	
	/** The Constant LOGIN. */
	private final static int LOGIN = 5;
	
	/** The Constant INVITATION. */
	private final static int INVITATION = 6;
	
	/** The Constant NOTE. */
	private final static int NOTE = 7;
	
	/** The Constant MOVE. */
	private final static int MOVE = 8;
	
	/** The Constant DATA. */
	private final static int DATA = 9;
	
	/** The Constant PLAYER_LIST. */
	private final static int PLAYER_LIST = 10;
	
	/** The Constant GAME_LIST. */
	private final static int GAME_LIST = 11;
	
	/** The Constant ERROR. */
	private final static int ERROR = 12;

	/** The m handler. */
	private Handler mHandler = new Handler() {
		
		public void handleMessage(Message msg) {
			try {
				switch (msg.what) {
				case DATA:
					player.onData(msg.arg1, (Custom) msg.obj);
					break;
				case MOVE:
					player.onMove(msg.arg1, (Move) msg.obj);
					break;
				case NOTE:
					player.onNote(msg.arg1, (Note) msg.obj);
					break;
				case START:
					player.onStart((GameSettings) msg.obj, msg.arg1);
					break;
				case JOIN:
					player.onJoin((PlayerInfo) msg.obj);
					break;
				case LOGIN:
					Log.d("GamePlayerProxy", "Calling onLogin");
					player.onLogin((PlayerInfo) msg.obj);
					break;
				case END:
					player.onEnd(msg.arg1);
					break;
				case ERROR:
					player.onError(msg.arg1, msg.arg2);
					break;
				case GAME_LIST:
					player.onGameList((List<GameSettings>)msg.obj);
					break;
				case PLAYER_LIST:
					player.onPlayerList((List<PlayerInfo>)msg.obj);
					break;
				case QUIT:
					player.onQuit(msg.arg1);
					break;
				case INVITATION:
					player.onInvitation((PlayerInfo) msg.obj, msg.arg1);
					break;
				default:
					super.handleMessage(msg);
				}
			} catch (Exception e) {
				Log.e("Lobby", e);
			}
		}
	};

	/**
	 * Instantiates a new game player proxy.
	 * 
	 * @param player
	 *            the player
	 */
	public GamePlayerProxy(IGamePlayer player) {
		this.player = player;
	}

	/* (non-Javadoc)
	 * @see org.aksonov.mages.services.IGamePlayer#dispose()
	 */
	
	public void dispose() throws RemoteException {
		player.dispose();
	}

	/* (non-Javadoc)
	 * @see org.aksonov.mages.services.IGamePlayer#getInfo()
	 */
	
	public PlayerInfo getInfo() throws RemoteException {
		return player.getInfo();
	}

	/* (non-Javadoc)
	 * @see org.aksonov.mages.services.IGamePlayer#getState()
	 */
	
	public int getState() throws RemoteException {
		return player.getState();
	}

	/* (non-Javadoc)
	 * @see org.aksonov.mages.services.IGamePlayer#onData(int, org.aksonov.mages.entities.Custom)
	 */
	
	public void onData(int playerId, Custom data) throws RemoteException {
		mHandler.sendMessage(mHandler.obtainMessage(DATA, playerId, 0, data));
	}

	/* (non-Javadoc)
	 * @see org.aksonov.mages.services.IGamePlayer#onEnd(int)
	 */
	
	public void onEnd(int playerId) throws RemoteException {
		mHandler.sendMessage(mHandler.obtainMessage(END, playerId, 0));
	}

	/* (non-Javadoc)
	 * @see org.aksonov.mages.services.IGamePlayer#onError(int, int)
	 */
	
	public void onError(int attempt, int code) throws RemoteException {
		mHandler.sendMessage(mHandler.obtainMessage(ERROR, attempt, code));
	}

	/* (non-Javadoc)
	 * @see org.aksonov.mages.services.IGamePlayer#onGameList(java.util.List)
	 */
	
	public void onGameList(List<GameSettings> gameList)
			throws RemoteException {
		mHandler.sendMessage(mHandler.obtainMessage(GAME_LIST, gameList));
	}

	/* (non-Javadoc)
	 * @see org.aksonov.mages.services.IGamePlayer#onInvitation(org.aksonov.mages.entities.PlayerInfo, int)
	 */
	
	public void onInvitation(PlayerInfo info, int playerId)
			throws RemoteException {
		mHandler.sendMessage(mHandler.obtainMessage(INVITATION, playerId,
				0, info));
	}

	/* (non-Javadoc)
	 * @see org.aksonov.mages.services.IGamePlayer#onJoin(org.aksonov.mages.entities.PlayerInfo)
	 */
	
	public void onJoin(PlayerInfo info) throws RemoteException {
		mHandler.sendMessage(mHandler.obtainMessage(JOIN, info));
	}

	/* (non-Javadoc)
	 * @see org.aksonov.mages.services.IGamePlayer#onLogin(org.aksonov.mages.entities.PlayerInfo)
	 */
	
	public void onLogin(PlayerInfo info) throws RemoteException {
		Log.d("GamePlayerProxy", "Sending login message to handler");
		mHandler.sendMessage(mHandler.obtainMessage(LOGIN, info));
	}

	/* (non-Javadoc)
	 * @see org.aksonov.mages.services.IGamePlayer#onMove(int, org.aksonov.mages.entities.Move)
	 */
	
	public void onMove(int playerId, Move move) throws RemoteException {
		mHandler.sendMessage(mHandler.obtainMessage(MOVE, playerId, 0, move));
	}

	/* (non-Javadoc)
	 * @see org.aksonov.mages.services.IGamePlayer#onNote(int, org.aksonov.mages.entities.Note)
	 */
	
	public void onNote(int playerId, Note data) throws RemoteException {
		mHandler.sendMessage(mHandler.obtainMessage(NOTE, playerId, 0, data));
	}

	/* (non-Javadoc)
	 * @see org.aksonov.mages.services.IGamePlayer#onPlayerList(java.util.List)
	 */
	
	public void onPlayerList(List<PlayerInfo> playerList)
			throws RemoteException {
		mHandler.sendMessage(mHandler.obtainMessage(PLAYER_LIST, playerList));
	}

	/* (non-Javadoc)
	 * @see org.aksonov.mages.services.IGamePlayer#onQuit(int)
	 */
	
	public void onQuit(int playerId) throws RemoteException {
		mHandler.sendMessage(mHandler.obtainMessage(QUIT, playerId, 0));
	}

	/* (non-Javadoc)
	 * @see org.aksonov.mages.services.IGamePlayer#onStart(org.aksonov.mages.entities.GameSettings, int)
	 */
	
	public void onStart(GameSettings settings, int playerId)
			throws RemoteException {
		mHandler.sendMessage(mHandler.obtainMessage(START, playerId, 0,
				settings));
	}

	/* (non-Javadoc)
	 * @see org.aksonov.mages.services.IGamePlayer#registerGameListener(org.aksonov.mages.services.IPlayerGameListener)
	 */
	
	public void registerGameListener(IPlayerGameListener listener)
			throws RemoteException {
		player.registerGameListener(listener);
	}

	/* (non-Javadoc)
	 * @see org.aksonov.mages.services.IGamePlayer#registerLobbyListener(org.aksonov.mages.services.IPlayerLobbyListener)
	 */
	
	public void registerLobbyListener(IPlayerLobbyListener listener)
			throws RemoteException {

		player.registerLobbyListener(listener);
	}

	/* (non-Javadoc)
	 * @see org.aksonov.mages.services.IGamePlayer#setInfo(org.aksonov.mages.entities.PlayerInfo)
	 */
	
	public void setInfo(PlayerInfo info) throws RemoteException {
		player.setInfo(info);
	}

	/* (non-Javadoc)
	 * @see org.aksonov.mages.services.IGamePlayer#setServer(org.aksonov.mages.services.IGameServer)
	 */
	
	public void setServer(IGameServer server) throws RemoteException {
		player.setServer(server);

	}

	/* (non-Javadoc)
	 * @see org.aksonov.mages.services.IGamePlayer#setState(int)
	 */
	
	public void setState(int state) throws RemoteException {
		player.setState(state);
	}

	/* (non-Javadoc)
	 * @see org.aksonov.mages.services.IGamePlayer#unregisterGameListener(org.aksonov.mages.services.IPlayerGameListener)
	 */
	
	public void unregisterGameListener(IPlayerGameListener listener)
			throws RemoteException {
		player.unregisterGameListener(listener);
	}

	/* (non-Javadoc)
	 * @see org.aksonov.mages.services.IGamePlayer#unregisterLobbyListener(org.aksonov.mages.services.IPlayerLobbyListener)
	 */
	
	public void unregisterLobbyListener(IPlayerLobbyListener listener)
			throws RemoteException {
		player.unregisterLobbyListener(listener);

	}

	/* (non-Javadoc)
	 * @see android.os.IInterface#asBinder()
	 */
	
	public IBinder asBinder() {
		return player.asBinder();
	}

	/* (non-Javadoc)
	 * @see org.aksonov.mages.services.IGamePlayer#getServer()
	 */
	
	public IGameServer getServer() throws RemoteException {
		return player.getServer();
	}
	
	
}
