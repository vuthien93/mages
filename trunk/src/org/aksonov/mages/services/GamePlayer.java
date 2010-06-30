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
import android.os.RemoteCallbackList;

// TODO: Auto-generated Javadoc
/**
 * This class implements IGamePlayer interface and provides all basic
 * functionality for game player. Basically it just calls registered lobby and
 * game callbacks. The instances of this class will be registered to the game
 * server and its methods will be called by the server after appropriated
 * events.
 * 
 * @author Pavel
 */
public class GamePlayer extends IGamePlayer.Stub {
	
	/** The Constant LOGIN. */
	public final static int LOGIN = 0;
	
	/** The Constant LOBBY. */
	public final static int LOBBY = 1;
	
	/** The Constant JOIN_GAME. */
	public final static int JOIN_GAME = 2;
	
	/** The Constant CREATE_GAME. */
	public final static int CREATE_GAME = 3;
	
	/** The Constant GAME. */
	public final static int GAME = 4;
	
	/** The Constant QUIT. */
	public final static int QUIT = 5;
	
	/** The Constant WAITING. */
	public final static int WAITING = 6;
	
	/** Represents the game server the player registered at. */
	protected IGameServer server = null;
	
	/**
	 * Represents current state of game player. It could be LOGIN, LOBBY,
	 * WAITING, GAME
	 */
	protected int state = LOGIN;
	
	/** Represents information about player. */
	protected PlayerInfo info;
	
	/** Current game settings for player. */
	protected GameSettings settings = null;
	
	/** List of list callbacks. */
	protected final RemoteCallbackList<IPlayerLobbyListener> listCallbacks = new RemoteCallbackList<IPlayerLobbyListener>();
	
	/** List of game callbacks. */
	protected final RemoteCallbackList<IPlayerGameListener> gameCallbacks = new RemoteCallbackList<IPlayerGameListener>();

	/**
	 * Empty contructor.
	 */
	public GamePlayer() {
	}

	/**
	 * Sets the current state of the player. If state is LOBBY, the method calls
	 * requestGameList and requestPlayerList methods of the game server to
	 * initiate loading of game and player lists from the server
	 * 
	 * @param state
	 *            the state
	 * 
	 * @throws RemoteException
	 *             the dead object exception
	 */
	
	public void setState(int state) throws RemoteException {
		Log.d("GamePlayer", "Changing player state to: " + state);
		this.state = state;
		if (state == LOBBY && server != null) {
			server.requestGameList(info.id);
			server.requestPlayerList(info.id);
		}
	}

	/**
	 * Returns the current state of the player.
	 * 
	 * @return the state
	 * 
	 * @throws RemoteException
	 *             the dead object exception
	 */
	
	public int getState() throws RemoteException {
		return this.state;
	}

	/**
	 * Sets the game server associated with this player.
	 * 
	 * @param server
	 *            the server
	 * 
	 * @throws RemoteException
	 *             the dead object exception
	 */
	
	public void setServer(IGameServer server) throws RemoteException {
		this.server = server;
	}

	/**
	 * Disposes this class, disconnects from the server and kills all registered
	 * callbacks.
	 * 
	 * @throws RemoteException
	 *             the dead object exception
	 */
	
	public void dispose() throws RemoteException {
		Log.d("GamePlayer", "dispose()");
		if (server != null && info != null) {
			try {
				server.disconnect(info.session);
			} catch (Exception e) {
				Log.e("GamePlayer", e);
			}
		}
		server = null;
		listCallbacks.kill();
		gameCallbacks.kill();
	}

	/**
	 * Check server.
	 */
	private void checkServer() {
		if (server == null) {
			throw new IllegalArgumentException("Server is null!");
		}
	}

	/**
	 * Check settings.
	 */
	private void checkSettings() {
		if (settings == null) {
			throw new IllegalArgumentException("Settings is null! Player:"
					+ info);
		}
		if (settings.id == -1) {
			new IllegalArgumentException("Settings.id is not specified!");
		}
	}

	/**
	 * This method is called when some custom data is arrived from the game
	 * server.
	 * 
	 * @param playerId
	 *            the player id
	 * @param data
	 *            the data
	 * 
	 * @throws RemoteException
	 *             the dead object exception
	 */
	
	public void onData(int playerId, Custom data) throws RemoteException {
		checkServer();
		int N = gameCallbacks.beginBroadcast();
		for (int i = 0; i < N; i++) {
			gameCallbacks.getBroadcastItem(i).onData(playerId, data);
		}
		gameCallbacks.finishBroadcast();
	}

	/**
	 * This method is called during any error at the server.
	 * 
	 * @param attempt
	 *            the attempt
	 * @param code
	 *            the code
	 * 
	 * @throws RemoteException
	 *             the dead object exception
	 */
	
	public void onError(int attempt, int code) throws RemoteException {
		int N;
		if (state == GAME) {
			N = gameCallbacks.beginBroadcast();
			for (int i = 0; i < N; i++) {
				gameCallbacks.getBroadcastItem(i).onError(attempt, code);
			}
			gameCallbacks.finishBroadcast();
		} else {
			N = listCallbacks.beginBroadcast();
			for (int i = 0; i < N; i++) {
				listCallbacks.getBroadcastItem(i).onError(attempt, code);
			}
			listCallbacks.finishBroadcast();
		}
	}

	/**
	 * This method is called if someone joins the game of the player.
	 * 
	 * @param info
	 *            the info
	 * 
	 * @throws RemoteException
	 *             the dead object exception
	 */
	
	public void onJoin(PlayerInfo info) throws RemoteException {
		checkServer();
		int N = listCallbacks.beginBroadcast();
		Log.d("GamePlayer" ,"onJoin: " + N + " list listeners");
		for (int i = 0; i < N; i++) {
			listCallbacks.getBroadcastItem(i).onJoin(info);
		}
		listCallbacks.finishBroadcast();
		N = gameCallbacks.beginBroadcast();
		Log.d("GamePlayer" ,"onJoin: " + N + " game listeners");
		for (int i = 0; i < N; i++) {
			gameCallbacks.getBroadcastItem(i).onJoin(info);
		}
		gameCallbacks.finishBroadcast();
	}

	/**
	 * The method is called when some game session owner ends the game.
	 * 
	 * @param playerId
	 *            the player id
	 * 
	 * @throws RemoteException
	 *             the dead object exception
	 */
	
	public void onEnd(int playerId) throws RemoteException {
		checkServer();
		int N = gameCallbacks.beginBroadcast();
		for (int i = 0; i < N; i++) {
			gameCallbacks.getBroadcastItem(i).onEnd(playerId);
		}
		gameCallbacks.finishBroadcast();
	}

	/**
	 * Returns player info of this player.
	 * 
	 * @return the info
	 * 
	 * @throws RemoteException
	 *             the dead object exception
	 */
	
	public PlayerInfo getInfo() throws RemoteException {
		return this.info;
	}

	/**
	 * Sets player info for this player.
	 * 
	 * @param info
	 *            the info
	 * 
	 * @throws RemoteException
	 *             the dead object exception
	 */
	
	public void setInfo(PlayerInfo info) throws RemoteException {
		this.info = info;
	}

	/**
	 * The method is called when new move arrived from the game server.
	 * 
	 * @param playerId
	 *            the player id
	 * @param move
	 *            the move
	 * 
	 * @throws RemoteException
	 *             the dead object exception
	 */
	
	public void onMove(int playerId, Move move) throws RemoteException {
		checkServer();
		checkSettings();
		int N = gameCallbacks.beginBroadcast();
		for (int i = 0; i < N; i++) {
			gameCallbacks.getBroadcastItem(i).onMove(playerId, move);
		}
		gameCallbacks.finishBroadcast();
	}

	/**
	 * The method is called when new note arrived from the game server.
	 * 
	 * @param playerId
	 *            the player id
	 * @param data
	 *            the data
	 * 
	 * @throws RemoteException
	 *             the dead object exception
	 */
	
	public void onNote(int playerId, Note data) throws RemoteException {
		checkServer();
		checkSettings();
		int N = gameCallbacks.beginBroadcast();
		for (int i = 0; i < N; i++) {
			gameCallbacks.getBroadcastItem(i).onNote(playerId, data);
		}
		gameCallbacks.finishBroadcast();
	}

	/**
	 * The method is called when some game player quits the player's game
	 * session.
	 * 
	 * @param playerId
	 *            the player id
	 * 
	 * @throws RemoteException
	 *             the dead object exception
	 */
	
	public void onQuit(int playerId) throws RemoteException {
		checkServer();
		int N = gameCallbacks.beginBroadcast();
		for (int i = 0; i < N; i++) {
			gameCallbacks.getBroadcastItem(i).onQuit(playerId);
		}
		gameCallbacks.finishBroadcast();
	}

	/**
	 * The method is called when game is actually started.
	 * 
	 * @param settings
	 *            the settings
	 * @param playerId
	 *            the player id
	 * 
	 * @throws RemoteException
	 *             the dead object exception
	 */
	
	public void onStart(GameSettings settings, int playerId)
			throws RemoteException {
		setState(GAME);
		checkServer();
		this.settings = settings;
		Log.d("onStart", "player :" + info + ", set settings to " + settings);
		checkSettings();

		int N = gameCallbacks.beginBroadcast();
		for (int i = 0; i < N; i++) {
			gameCallbacks.getBroadcastItem(i).onStart(settings, playerId);
		}
		gameCallbacks.finishBroadcast();
	}

	/**
	 * The method is called when game invitation is arrived from the game
	 * server.
	 * 
	 * @param info
	 *            the info
	 * @param playerId
	 *            the player id
	 * 
	 * @throws RemoteException
	 *             the dead object exception
	 */
	
	public void onInvitation(PlayerInfo info, int playerId)
			throws RemoteException {
		int N = listCallbacks.beginBroadcast();
		for (int i = 0; i < N; i++) {
			listCallbacks.getBroadcastItem(i).onInvitation(info, playerId);
		}
		listCallbacks.finishBroadcast();
	}

	/**
	 * The method is called after successful login.
	 * 
	 * @param info
	 *            the info
	 * 
	 * @throws RemoteException
	 *             the dead object exception
	 */
	
	public void onLogin(PlayerInfo info) throws RemoteException {
		if (info == null) {
			new IllegalArgumentException("PlayerInfo is null!");
		}
		this.info = info;
		int N = listCallbacks.beginBroadcast();
		for (int i = 0; i < N; i++) {
			listCallbacks.getBroadcastItem(i).onLogin(info);
		}
		listCallbacks.finishBroadcast();
		setState(LOBBY);
	}

	/**
	 * The method is called when updated game list is arrived from the game
	 * server.
	 * 
	 * @param gameList
	 *            the game list
	 * 
	 * @throws RemoteException
	 *             the dead object exception
	 */
	
	public void onGameList(List<GameSettings> gameList)
			throws RemoteException {

		int N = listCallbacks.beginBroadcast();
		for (int i = 0; i < N; i++) {
			listCallbacks.getBroadcastItem(i).onGameList(gameList);
		}
		listCallbacks.finishBroadcast();
	}

	/**
	 * Registers game listener.
	 * 
	 * @param listener
	 *            the listener
	 * 
	 * @throws RemoteException
	 *             the dead object exception
	 */
	
	public void registerGameListener(IPlayerGameListener listener)
			throws RemoteException {
		gameCallbacks.register(listener);
	}

	/**
	 * Registers lobby listener.
	 * 
	 * @param listener
	 *            the listener
	 * 
	 * @throws RemoteException
	 *             the dead object exception
	 */
	
	public void registerLobbyListener(IPlayerLobbyListener listener)
			throws RemoteException {
		listCallbacks.register(listener);
	}

	/**
	 * Unregister game listener.
	 * 
	 * @param listener
	 *            the listener
	 * 
	 * @throws RemoteException
	 *             the dead object exception
	 */
	
	public void unregisterGameListener(IPlayerGameListener listener)
			throws RemoteException {
		gameCallbacks.unregister(listener);
	}

	/**
	 * Unregister lobby listener.
	 * 
	 * @param listener
	 *            the listener
	 * 
	 * @throws RemoteException
	 *             the dead object exception
	 */
	
	public void unregisterLobbyListener(IPlayerLobbyListener listener)
			throws RemoteException {
		listCallbacks.unregister(listener);
	}

	/**
	 * The method is called when updated player list is arrived from the game
	 * server.
	 * 
	 * @param playerList
	 *            the player list
	 * 
	 * @throws RemoteException
	 *             the dead object exception
	 */
	
	public void onPlayerList(List<PlayerInfo> playerList)
			throws RemoteException {

		Log.d("GamePlayer", "playerList" + playerList.size() + " state is: "
				+ state);
		if (state == LOBBY) {

			int N = listCallbacks.beginBroadcast();
			for (int i = 0; i < N; i++) {
				listCallbacks.getBroadcastItem(i).onPlayerList(playerList);
			}
			listCallbacks.finishBroadcast();
		} else {
			int N = gameCallbacks.beginBroadcast();
			for (int i = 0; i < N; i++) {
				gameCallbacks.getBroadcastItem(i).onPlayerList(playerList);
			}
			gameCallbacks.finishBroadcast();
		}
	}

	/**
	 * Returns the game server associted with this player.
	 * 
	 * @return the server
	 * 
	 * @throws RemoteException
	 *             the dead object exception
	 */
	
	public IGameServer getServer() throws RemoteException {
		return server;
	}

}
