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
package org.aksonov.mages.services.gasp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.aksonov.mages.GameCustomTypes;
import org.aksonov.mages.Helper;
import org.aksonov.mages.connection.ConnectionManager;
import org.aksonov.mages.connection.CustomTypesRequestEntity;
import org.aksonov.mages.connection.DataReader;
import org.aksonov.mages.entities.Custom;
import org.aksonov.mages.entities.GameSettings;
import org.aksonov.mages.entities.Move;
import org.aksonov.mages.entities.Note;
import org.aksonov.mages.entities.PlayerInfo;
import org.aksonov.mages.services.GamePlayer;
import org.aksonov.mages.services.GamePlayerProxy;
import org.aksonov.mages.services.IGamePlayer;
import org.aksonov.mages.services.IGameServer;
import org.aksonov.mages.tools.Errors;
import org.aksonov.tools.Log;
import org.mega.gasp.moods.CustomTypes;

import android.os.RemoteException;
import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;
import android.util.SparseIntArray;

// TODO: Auto-generated Javadoc
/**
 * This class represents GASP game server communication.
 * 
 * @author Pavel
 */
public class GASPGameServer extends IGameServer.Stub {

	/**
	 * The Class Pair.
	 */
	private class Pair {

		/** The player id. */
		private final Integer playerId;

		/** The game id. */
		private final Integer gameId;

		/**
		 * Instantiates a new pair.
		 * 
		 * @param playerId
		 *            the player id
		 * @param gameId
		 *            the game id
		 */
		public Pair(int playerId, int gameId) {
			this.gameId = gameId;
			this.playerId = playerId;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		public boolean equals(Object o) {
			return ((Pair) o != null && ((Pair) o).playerId == playerId && ((Pair) o).gameId == gameId);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#hashCode()
		 */
		public int hashCode() {
			return gameId.hashCode() + playerId.hashCode();
		}
	}

	/** The Constant CUSTOM_TYPES. */
	public static final CustomTypes CUSTOM_TYPES = new GameCustomTypes();

	/** The Constant TIMEOUT. */
	public static final int TIMEOUT = 15000;

	/** The Constant PERIOD. */
	public static final int PERIOD = 1000;

	/** The Constant RECEIVE_PERIOD. */
	public static final int RECEIVE_PERIOD = 200;

	/** The Constant SEND_PERIOD. */
	public static final int SEND_PERIOD = 200;

	/** The player ids. */
	private SparseIntArray playerIds = new SparseIntArray();

	/** The last modified. */
	private SparseArray<Long> lastModified = new SparseArray<Long>();

	/** The dis. */
	private SparseArray<DataInputStream> dis = new SparseArray<DataInputStream>();

	/** The dos. */
	private SparseArray<DataOutputStream> dos = new SparseArray<DataOutputStream>();

	/** The sender task. */
	private final SenderTask senderTask;

	/** The receiver task. */
	private final ReceiverTask receiverTask;

	/** The receiver task. */
	private final InGameTask inGameTask;

	/** The Constant LOGIN. */
	private static final int LOGIN = 1;

	/** The Constant END. */
	private static final int END = 2;

	/** The Constant QUIT. */
	private static final int QUIT = 3;

	/** The Constant CREATE. */
	private static final int CREATE = 4;

	/** The Constant JOIN. */
	private static final int JOIN = 5;

	/** The Constant LOBBY. */
	private static final int LOBBY = 6;

	/** The Constant START. */
	private static final int START = 9;

	/** The Constant INVITE. */
	private static final int INVITE = 10;

	/** The m handler. */
	private Handler mHandler = new Handler() {
		
		public void handleMessage(Message msg) {
			try {
				switch (msg.what) {
				case LOGIN:
					login(msg.arg1, (IGamePlayer) msg.obj);
					break;
				case INVITE:
					invite(msg.arg1, (PlayerInfo) msg.obj);
					break;
				case END:
					end(msg.arg1, msg.arg2);
					break;
				case START:
					start(msg.arg1, msg.arg2);
					removeMessages(START);
					break;
				case QUIT:
					quit(msg.arg1, msg.arg2);
					break;
				case CREATE:
					create(msg.arg1, (GameSettings) msg.obj);
					break;
				case JOIN:
					join(msg.arg1, (PlayerInfo) msg.obj);
					break;
				case LOBBY:
					removeMessages(LOBBY);
					if (lobbyTask(msg.arg1, msg.arg2 == 1)) {
						sendMessageDelayed(obtainMessage(LOBBY, msg.arg1, 0),
								PERIOD);
					}
					break;
				default:
					super.handleMessage(msg);
				}
			} catch (Exception e) {
				Log.e("Lobby", e);
			}
		}
	};

	/** The connection infos. */
	private SparseArray<GASPConnectionInfo> connectionInfos = new SparseArray<GASPConnectionInfo>();

	/** This is a list of callbacks that have been registered with the service. */
	protected final SparseArray<IGamePlayer> players = new SparseArray<IGamePlayer>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.aksonov.mages.services.IGameServer#getPlayer(int)
	 */
	
	public IGamePlayer getPlayer(int session) throws RemoteException {
		synchronized (players) {
			return players.get(session);
		}
	}

	/**
	 * The Class GASPConnectionInfo.
	 */
	class GASPConnectionInfo {

		/** The host. */
		public String host = "";

		/** The app id. */
		public int appId = 0;

		/** The username. */
		public String username = "";

		/** The password. */
		public String password = "";

		public boolean isComet = true;
		
		public int attempts = 0;
	}

	/** The data refs. */
	private SparseIntArray dataRefs = new SparseIntArray();

	/** The to send. */
	private Hashtable<Pair, List<Object>> toSend = new Hashtable<Pair, List<Object>>();

	/** The confirmed ids. */
	private Hashtable<Pair, List<Integer>> confirmedIds = new Hashtable<Pair, List<Integer>>();

	/** The custom types. */
	public final CustomTypes customTypes = CUSTOM_TYPES;

	/** The size. */
	private int size = 1;

	/**
	 * Constructor creates senderTask and receiverTask for comet-communication.
	 */
	public GASPGameServer() {
		Log.w("GASPGameServer", "Created");
		senderTask = new SenderTask(customTypes);
		receiverTask = new ReceiverTask(customTypes);
		inGameTask = new InGameTask(customTypes);
	}

	/**
	 * Creates the session.
	 * 
	 * @return the int
	 */
	public int createSession() {
		return size++;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.aksonov.mages.services.IGameServer#isConfigured(int)
	 */
	
	public boolean isConfigured(int session) {
		GASPConnectionInfo info = connectionInfos.get(session, null);
		Log.w("GASPGameService", "Session id: " + session
				+ ", retrieved session: " + info);
		return info != null && !info.host.equals("")
				&& !info.username.equals("") && info.appId != 0;
	}

	/**
	 * Sets the connect info.
	 * 
	 * @param session
	 *            the session
	 * @param host
	 *            the host
	 * @param appId
	 *            the app id
	 * @param username
	 *            the username
	 * @param password
	 *            the password
	 */
	public void setConnectInfo(int session, String host, int appId,
			String username, String password, boolean isComet) {
		GASPConnectionInfo info = new GASPConnectionInfo();
		info.appId = appId;
		info.username = username;
		info.password = password;
		info.host = host;
		info.isComet = isComet;
		Log.w("GASPGameService", "Session id: " + session + ", added session: "
				+ info);
		connectionInfos.put(session, info);
	}

	/**
	 * Check connection info.
	 * 
	 * @param session
	 *            the session
	 * 
	 * @return the gASP connection info
	 */
	private GASPConnectionInfo checkConnectionInfo(int session) {
		final GASPConnectionInfo info = connectionInfos.get(session);
		if (info == null) {
			throw new IllegalArgumentException(
					"No connection info for data ref: " + session);
		}
		if (info.host.equals("")) {
			throw new IllegalArgumentException(
					"connectionInfo.host should be set before login");
		}

		if (info.username.equals("")) {
			throw new IllegalArgumentException(
					"connectionInfo.username should be set before login");
		}

		if (info.appId == 0) {
			throw new IllegalArgumentException(
					"connectionInfo.appId should be set before login");
		}
		return info;
	}

	/**
	 * Gets the player info by session.
	 * 
	 * @param session
	 *            the session
	 * 
	 * @return the player info by session
	 * 
	 * @throws RemoteException
	 *             the dead object exception
	 */
	private PlayerInfo getPlayerInfoBySession(int session)
			throws RemoteException {
		if (getPlayer(session) == null) {
			throw new IllegalArgumentException("Session: " + session
					+ " doesn't contain player data");
		}
		PlayerInfo info = getPlayer(session).getInfo();
		if (info == null) {
			throw new IllegalArgumentException(
					"connectionInfo.player is not utilized");
		}

		if (info.arg2 == 0) {
			throw new IllegalArgumentException(
					"connectionInfo.arg2 (GASP session id) should be set before game create");
		}
		return info;
	}

	/**
	 * Gets the player info by id.
	 * 
	 * @param playerId
	 *            the player id
	 * 
	 * @return the player info by id
	 * 
	 * @throws RemoteException
	 *             the dead object exception
	 */
	private PlayerInfo getPlayerInfoById(int playerId)
			throws RemoteException {
		if (dataRefs.get(playerId, -1) == -1) {
			throw new IllegalArgumentException("No data ref for playerId: "
					+ playerId);
		}
		return getPlayerInfoBySession(dataRefs.get(playerId));
	}

	/**
	 * Gets the player info for game.
	 * 
	 * @param playerId
	 *            the player id
	 * 
	 * @return the player info for game
	 * 
	 * @throws RemoteException
	 *             the dead object exception
	 */
	private PlayerInfo getPlayerInfoForGame(int playerId)
			throws RemoteException {
		PlayerInfo info = getPlayerInfoById(playerId);
		if (info.gameId == 0) {
			throw new IllegalArgumentException(
					"connectionInfo.gameId should be set");
		}
		if (info.arg1 == 0) {
			throw new IllegalArgumentException(
					"connectionInfo.actorSessionId should be set");
		}
		return info;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.aksonov.mages.services.IGameServer#connect(int,
	 *      org.aksonov.mages.services.IGamePlayer)
	 */
	public void connect(final int session, final IGamePlayer player)
			throws RemoteException {
		mHandler.sendMessage(mHandler.obtainMessage(LOGIN, session, 0, player));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.aksonov.mages.services.IGameServer#createGame(int,
	 *      org.aksonov.mages.entities.GameSettings)
	 */
	
	public void createGame(final int session, final GameSettings settings)
			throws RemoteException {
		mHandler.sendMessage(mHandler.obtainMessage(CREATE, session, 0,
				settings));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.aksonov.mages.services.IGameServer#endGame(int, int)
	 */
	
	public void endGame(int gameId, int playerId) throws RemoteException {
		mHandler.sendMessage(mHandler.obtainMessage(END, gameId, playerId));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.aksonov.mages.services.IGameServer#joinGame(int,
	 *      org.aksonov.mages.entities.PlayerInfo)
	 */
	
	public void joinGame(final int gameId, final PlayerInfo info)
			throws RemoteException {
		mHandler.sendMessage(mHandler.obtainMessage(JOIN, gameId, 0, info));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.aksonov.mages.services.IGameServer#quitGame(int, int)
	 */
	
	public void quitGame(int gameId, int playerId) throws RemoteException {
		mHandler.sendMessage(mHandler.obtainMessage(QUIT, gameId, playerId));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.aksonov.mages.services.IGameServer#startGame(int, int)
	 */
	
	public void startGame(int gameId, int playerId) throws RemoteException {
		mHandler.sendMessage(mHandler.obtainMessage(START, gameId, playerId));
	}

	/**
	 * Start.
	 * 
	 * @param gameId
	 *            the game id
	 * @param playerId
	 *            the player id
	 * 
	 * @throws RemoteException
	 *             the dead object exception
	 */
	private void start(int gameId, int playerId) throws RemoteException {
		Log.d("GASPGameServer", "Calling prepare(" + gameId + "," + playerId
				+ ")");
		int session = dataRefs.get(playerId);
		final IGamePlayer player = players.get(session);
		PlayerInfo playerInfo = player.getInfo();

		player.setState(GamePlayer.GAME);
		final GASPConnectionInfo info = connectionInfos.get(session);
		if (info == null) {
			throw new IllegalArgumentException(
					"No connection data for playerId: " + playerId);
		}
		if (info.host.equals("")) {
			throw new IllegalArgumentException(
					"connectionInfo.host should be set before login");
		}

		if (playerInfo.arg1 == 0) {
			throw new IllegalArgumentException(
					"connectionInfo.actorSessionId should be set before game create");
		}

		List<Object> data = null;
		List<Integer> confirm = null;
		Pair p = new Pair(playerId, gameId);
		synchronized (toSend) {
			if (!toSend.containsKey(p)) {
				toSend.put(p, java.util.Collections
						.synchronizedList(new ArrayList<Object>()));
			}
			data = toSend.get(p);
		}

		synchronized (confirmedIds) {
			if (!confirmedIds.containsKey(p)) {
				confirmedIds.put(p, java.util.Collections
						.synchronizedList(new ArrayList<Integer>()));
			}
			confirm = confirmedIds.get(p);

		}
		if (!info.isComet) {
		//if (true) {
					Log
					.d("GASPGameServer", "Comet mode is OFF for session: "
							+ session);
			inGameTask.start(info.host, gameId, playerInfo.arg1, player, data,
					confirm, playerIds);

		} else {
			Log
			.d("GASPGameServer", "Comet mode is ON for session: "
					+ session);
			senderTask.start(info.host, gameId, playerInfo.arg1, player, data,
					confirm);
			receiverTask.start(info.host, gameId, playerInfo.arg1, player,
					confirm, playerIds);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.aksonov.mages.services.IGameServer#sendData(int, int,
	 *      org.aksonov.mages.entities.Custom)
	 */
	
	public void sendData(int gameId, int playerId, Custom data) {
		send(gameId, playerId, data.getObject());
	}

	/**
	 * Send.
	 * 
	 * @param gameId
	 *            the game id
	 * @param playerId
	 *            the player id
	 * @param data
	 *            the data
	 */
	private void send(int gameId, int playerId, Object data) {
		Pair p = new Pair(playerId, gameId);
		if (!toSend.containsKey(p)) {
			throw new IllegalArgumentException(
					"Send buffer is not set for pair gameId=" + gameId
							+ ", playerId=" + playerId);
		}
		synchronized (toSend) {
			List<Object> dataList = toSend.get(p);
			synchronized (dataList) {
				dataList.add(data);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.aksonov.mages.services.IGameServer#sendMove(int, int,
	 *      org.aksonov.mages.entities.Move)
	 */
	
	public void sendMove(int gameId, int playerId, Move move) {
		send(gameId, playerId, move);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.aksonov.mages.services.IGameServer#sendNote(int, int,
	 *      org.aksonov.mages.entities.Note)
	 */
	
	public void sendNote(int gameId, int playerId, Note note) {
		send(gameId, playerId, note);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.aksonov.mages.services.IGameServer#requestGameList(int)
	 */
	
	public void requestGameList(int playerId) throws RemoteException {
		Log.d("GASPGameServer",
				"Putting request for game lobby list for playerId:" + playerId);
		lastModified.put(playerId, 0L);
		mHandler.sendMessage(mHandler.obtainMessage(LOBBY, playerId, 1));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.aksonov.mages.services.IGameServer#requestPlayerList(int)
	 */
	
	public void requestPlayerList(int playerId) throws RemoteException {
		mHandler.sendMessage(mHandler.obtainMessage(LOBBY, playerId, 1));
	}

	/**
	 * Lobby task.
	 * 
	 * @param playerId
	 *            the player id
	 * @param manual
	 *            the manual
	 * 
	 * @return true, if successful
	 * 
	 * @throws RemoteException
	 *             the dead object exception
	 */
	private boolean lobbyTask(int playerId, boolean manual)
			throws RemoteException {
		Log.d("GASPGameServer", "lobbyTask playerId = " + playerId);
		final PlayerInfo playerInfo = getPlayerInfoById(playerId);
		int s = dataRefs.get(playerId);
		final GASPConnectionInfo info = connectionInfos.get(s);
		final IGamePlayer player = players.get(s);
		if (!manual && player.getState() != GamePlayer.LOBBY
				&& player.getState() != GamePlayer.WAITING) {
			Log.d("GASPGameServer",
					"lobbyTask player state is not lobby or waiting");
			return false;
		}

		int sessionId = playerInfo.arg2;
		if (sessionId == 0) {
			throw new IllegalArgumentException(
					"Session is not defined within playerInfo.arg2");
		}

		try {
			ConnectionManager.readGet(info.host, "LobbyOutput", "sID="
					+ sessionId, new DataReader() {

				
				public Object read(DataInputStream stream) throws Exception {
					while (true) {
						byte type = stream.readByte(); // get the type of the
						if (type == 0 || type > 3)
							break;
						// events
						Log.d("LobbyReceiverTask", "Type = " + type);
						Object[] data = Helper.decode(stream, customTypes);
						Log.d("LobbyReceiverTask", "Decoding " + data.length
								+ " objects ");

						switch (type) {
						case 1:
							List<GameSettings> list = new ArrayList<GameSettings>();
							for (int i = 0; i < data.length; i++) {
								list.add((GameSettings) data[i]);
							}
							player.onGameList(list);
							break;
						case 2:
							List<PlayerInfo> plist = new ArrayList<PlayerInfo>();
							for (int i = 0; i < data.length; i++) {
								plist.add((PlayerInfo) data[i]);
							}
							player.onPlayerList(plist);
							break;
						case 3:
							for (int i = 0; i < data.length; i++) {
								player.onInvitation((PlayerInfo) data[i],
										((PlayerInfo) data[i]).arg1);
							}
							break;
						}
					}
					return null;
				}

			});
		} catch (Exception e) {
			Log.e("GASPGameServer", e);
			info.attempts++;
			if (info.attempts > 5){
				 player.onError(0, Errors.CANNOT_RETRIEVE_GAMELIST_ERROR);
			return false;
			} else {
				return true;
			}
		}
		info.attempts = 0;
		return true;
	}

	/**
	 * Lobby.
	 * 
	 * @param playerId
	 *            the player id
	 * 
	 * @return true, if successful
	 * 
	 * @throws RemoteException
	 *             the dead object exception
	 */
	private boolean lobby(int playerId) throws RemoteException {
		final PlayerInfo playerInfo = getPlayerInfoById(playerId);
		int s = dataRefs.get(playerId);
		final GASPConnectionInfo info = connectionInfos.get(s);
		final IGamePlayer player = players.get(s);

		if (player.getState() != GamePlayer.LOBBY) {
			Log.d("GASPGameServer", "Player is not in lobby, return");
			return false;
		}

		int sessionId = playerInfo.arg2;
		if (sessionId == 0) {
			throw new IllegalArgumentException(
					"Session is not defined within playerInfo.arg2");
		}

		// check modified time first to save traffic
		long time = getLobbyModifiedTime(info.host, sessionId);
		if (time == -1) {
			player.onError(0, Errors.CANNOT_RETRIEVE_GAMELIST_ERROR);
			return false;
		}
		Long lastTime = lastModified.get(playerId);

		if (lastTime == null || lastTime < time) {
			lastModified.put(playerId, time);
			List<GameSettings> games = null;
			try {
				games = (List<GameSettings>) ConnectionManager.readGet(
						info.host, "Lobby", "custom_data=1&sID=" + sessionId,
						listReader);
			} catch (Exception e) {
				Log.e("GASPGameServer", e.getMessage());
			}
			if (games == null) {
				player.onError(0, Errors.CANNOT_RETRIEVE_GAMELIST_ERROR);
				return false;
			} else {
				player.onGameList(games);
			}
		}
		return true;
	}

	/**
	 * Join.
	 * 
	 * @param gameId
	 *            the game id
	 * @param playerInfo
	 *            the player info
	 * 
	 * @throws RemoteException
	 *             the dead object exception
	 */
	private void join(int gameId, final PlayerInfo playerInfo)
			throws RemoteException {
		Log.d("GASPGameServer", "Calling join(" + gameId + "," + playerInfo
				+ ")");
		int playerId = playerInfo.id;
		if (playerId == 0) {
			throw new IllegalArgumentException("Player id is not set");
		}
		if (gameId == 0) {
			throw new IllegalArgumentException(
					"gameId should be set before join game");
		}

		int session = dataRefs.get(playerId);
		final GASPConnectionInfo info = connectionInfos.get(session);
		final IGamePlayer player = players.get(session);
		int result = 0;
		try {
			result = ConnectionManager.readShort(info.host, "Join", "sID="
					+ playerInfo.arg2 + "&custom_data=1&aIID=" + gameId,
					new CustomTypesRequestEntity(customTypes,
							new Object[] { playerInfo }));
		} catch (Exception e) {
			Log.e("GASPGameServer", e.getMessage());
		}

		try {
			if (result <= 0) {
				player.setState(GamePlayer.LOBBY);
				player.onError(0, Errors.JOIN_GAME_FAILED_ERROR);
				return;
			} else {
				playerInfo.arg1 = result;
				playerInfo.gameId = gameId;
				player.onJoin(playerInfo);
				player.setInfo(playerInfo);

				playerIds.put(result, playerInfo.id);
				player.setState(GamePlayer.WAITING);
			}
		} catch (Exception e) {
			Log.d("GASPGameServer", "Exception happends, call player.onError");
			player.setState(GamePlayer.LOBBY);
			player.onError(0, Errors.NETWORK_ERROR);
		}

	}

	/**
	 * End.
	 * 
	 * @param gameId
	 *            the game id
	 * @param playerId
	 *            the player id
	 * 
	 * @throws RemoteException
	 *             the dead object exception
	 */
	private void end(int gameId, int playerId) throws RemoteException {
		final PlayerInfo playerInfo = getPlayerInfoForGame(playerId);
		int session = dataRefs.get(playerId);
		final GASPConnectionInfo info = connectionInfos.get(session);
		try {
			ConnectionManager.readEmpty(info.host, "EndAI", "aSID="
					+ playerInfo.arg1);
		} catch (Exception e) {
			Log.e("GASPGameServer", e.getMessage());
		}

	}

	/**
	 * Quit.
	 * 
	 * @param gameId
	 *            the game id
	 * @param playerId
	 *            the player id
	 * 
	 * @throws RemoteException
	 *             the dead object exception
	 */
	private void quit(int gameId, int playerId) throws RemoteException {
		final PlayerInfo playerInfo = getPlayerInfoForGame(playerId);
		int session = dataRefs.get(playerId);
		final GASPConnectionInfo info = connectionInfos.get(session);
		try {
			ConnectionManager.readEmpty(info.host, "QuitAI", "aSID="
					+ playerInfo.arg1);
		} catch (Exception e) {
			Log.e("GASPGameServer", e.getMessage());
		}
	}

	/**
	 * Creates the.
	 * 
	 * @param session
	 *            the session
	 * @param settings
	 *            the settings
	 * 
	 * @throws RemoteException
	 *             the dead object exception
	 */
	private void create(int session, GameSettings settings)
			throws RemoteException {
		Log.d("GASPGameServer", "Calling create(" + session + "," + settings
				+ ")");
		final PlayerInfo playerInfo = getPlayerInfoBySession(session);
		if (settings == null) {
			throw new IllegalArgumentException("Settings should not be null");
		}
		if (settings.players.size() > 0) {
			throw new IllegalArgumentException(
					"GameSettings.players should be empty");
		}
		IGamePlayer player = players.get(session);
		player.setState(GamePlayer.WAITING);

		final GASPConnectionInfo info = checkConnectionInfo(session);
		try {
			int result = ConnectionManager.readShort(info.host, "CreateAI",
					"sID=" + playerInfo.arg2 + "&minActors="
							+ settings.minActors + "&maxActors="
							+ settings.maxActors + "&custom_data=1&actors=",
					new CustomTypesRequestEntity(customTypes,
							new Object[] { settings }));
			if (result <= 0) {
				player.onError(0, Errors.CREATE_GAME_FAILED_ERROR);
			} else {
				playerInfo.gameId = result;
				player.setInfo(playerInfo);
				// player.onCreate(result);
				players.put(session, player);
				join(result, playerInfo);
			}
		} catch (Exception e) {
			player.onError(0, Errors.NETWORK_ERROR);
		}
	}

	/**
	 * Login.
	 * 
	 * @param session
	 *            the session
	 * @param gamePlayer
	 *            the game player
	 * 
	 * @throws RemoteException
	 *             the dead object exception
	 */
	private void login(int session, IGamePlayer gamePlayer)
			throws RemoteException {
		final GASPConnectionInfo info = checkConnectionInfo(session);

		if (gamePlayer == null) {
			throw new IllegalArgumentException("Player should be non-null");
		}
		IGamePlayer player = new GamePlayerProxy(gamePlayer);
		String username = info.username;
		String password = info.password;

		PlayerInfo playerInfo = player.getInfo();
		if (playerInfo != null) {
			player.onError(0, Errors.NOT_SUPPORTED);
			return;
		}
		// doesn't support exporting users from external data source at this
		// moment
		/*
		 * if (playerInfo != null) { username = playerInfo.username; password =
		 * playerInfo.password; session = createSession();
		 * setConnectInfo(session, info.host, info.appId, username, password); }
		 */
		int playerId = 0;
		try {
			playerId = ConnectionManager.readShort(info.host, "FirstLogin",
					"uid=1&appID=" + info.appId + "&username=" + username
							+ "&password=" + password);
		} catch (Exception e) {
            Log.e("GASPGameServer!", e);
		}

		if (playerId <= 0) {
			player.onError(0, Errors.FIRST_LOGIN_FAILED_ERROR);
		} else {
			String formData = "aID=" + playerId + "&username=" + username
					+ "&password=" + password + "&version=";
			int sessionId = 0;
			try {
				sessionId = ConnectionManager.readShort(info.host, "Login",
						formData);
			} catch (Exception e) {
				Log.e("GASPGameServer", e.getMessage());
			}

			if (sessionId <= 0) {
				player.onError(0, Errors.LOGIN_FAILED_ERROR);
			} else {
				if (playerInfo == null) {
					playerInfo = PlayerInfo.create();
				}

				playerInfo.username = username;
				playerInfo.password = password;
				playerInfo.id = playerId;
				playerInfo.arg2 = sessionId;
				playerInfo.session = session;
				dataRefs.put(playerId, session);
				player.setInfo(playerInfo);
				player.onLogin(playerInfo);

				player.setServer(this);
                Log.i("login", "LOGIN! Putting player info to session!"+session);
				players.put(session, player);
			}
		}

	}

	/**
	 * Read custom data.
	 * 
	 * @param dis
	 *            the dis
	 * @param customTypes
	 *            the custom types
	 * 
	 * @return the object
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public static Object readCustomData(DataInputStream dis,
			CustomTypes customTypes) throws Exception {
		Object customData = null;
		if (customTypes != null) {
			Object[] data = org.aksonov.mages.Helper.decode(dis, customTypes);
			if (data.length > 0) {
				customData = data[0];
			}
		}
		return customData;
	}

	/**
	 * Treats the events of the servlet response data input stream.
	 * 
	 * @param dis
	 *            the data input stream
	 * @param player
	 *            the player
	 * @param customTypes
	 *            the custom types
	 * 
	 * @return true, if events of
	 * 
	 * @throws IOException
	 */
	private static boolean eventsOf(DataInputStream dis, IGamePlayer player,
			CustomTypes customTypes) {
		try {
			Log.d("eventsOf", "eventsOf start, callback = " + player);
			// byte nb = dis.readByte();
			// Log.d("GASP", "reading byte = " + nb);
			byte type;

			// while (nb > 0) {
			type = dis.readByte(); // get the type of the events
			// 13 means CRLF - server occasionally closes chunked streaming
			if (type == 13) {
				Log.d("eventsOf", "CRLF!");
				dis.readByte();
				return false;
			}
			Log.d("GASP", "type of event " + type);

			switch (type) {
			case 1: // JoinEvent
				// player.onJoin(dis.readShort(), dis.readUTF());
				dis.readShort();
				dis.readUTF();
				break;
			case 2: // StartEvent
				// player.onStart(gameId,
				dis.readShort();
				break;
			case 3: // EndEvent
				player.onEnd(dis.readShort());
				break;
			case 4: // QuitEvent
				player.onQuit(dis.readShort());
				break;
			case 5: // DataEvent
				int aSID = dis.readShort();
				Log.d("GASP", "=====================DataEvent: total bytes "
						+ dis.available());
				Object[] data = Helper.decode(dis, customTypes);
				for (int i = 0; i < data.length; i++) {
					Log.d("GASP", "=========Object received: " + data[i]);
					if (data[i] instanceof GameSettings) {
						Log.d("GASP", "=========START!!!!!!!!!!!!!!!!!!!!!!!");
						player.onStart((GameSettings) data[i], aSID);
					} else if (data[i] instanceof PlayerInfo) {
						player.onJoin((PlayerInfo) data[i]);
					} else if (data[i] instanceof Move) {
						player.onMove(aSID, (Move) data[i]);
					} else if (data[i] instanceof Note) {
						player.onNote(aSID, (Note) data[i]);
					} else if (data[i] instanceof Move) {
						player.onData(aSID, Custom
								.create((Serializable) data[i]));
					}
				}
				break;
			default:
				break;
			}

			// nb--;
			// }
		} catch (Exception e) {
			Log.e("GASPClient.eventsOf", e);
			return false;
		}
		return true;
	}

	/**
	 * Read line.
	 * 
	 * @param dis
	 *            the dis
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void readLine(DataInputStream dis) throws IOException {
		int ch = 0;
		do {
			ch = dis.readByte();
		} while (ch != 13);
		dis.readByte();
	}

	/**
	 * Gets the lobby modified time.
	 * 
	 * @param host
	 *            the host
	 * @param sessionId
	 *            the session id
	 * 
	 * @return the lobby modified time
	 */
	private static long getLobbyModifiedTime(String host, int sessionId) {
		String formData = "check_time=1&sID=" + sessionId;
		long res = -1;
		try {
			res = ConnectionManager.readLong(host, "Lobby", formData);
		} catch (Exception e) {
			Log.e("GASPGameServer", e.getMessage());
		}

		Log.w("GASP", "GASP: checkLobbyModifiedTime returns " + res);
		return res;
	}

	/** The list reader. */
	private DataReader listReader = new DataReader() {

		
		public Object read(DataInputStream dis) throws Exception {
			List<GameSettings> games = new ArrayList<GameSettings>();
			// read the number of applications instances
			Log.w("formLobbyDatas", "Total bytes: " + dis.available());
			short aIIDs = dis.readShort();
			Log.w("GASP", "formLobbyDatas: number of instances: " + aIIDs);

			while (aIIDs != 0) {
				// read the ApplicationInstance ID
				short aIID = dis.readShort();

				// read the minimum starting number of actors
				short min = dis.readShort();

				// read the number of actors in the application instance
				short actors = dis.readShort();

				Log.w("GASP", "formLobbyDatas: number of actors: " + actors);
				// read the maximum of actors
				short max = dis.readShort();

				GameSettings settings = (GameSettings) readCustomData(dis,
						customTypes);
				if (settings == null) {
					throw new IllegalArgumentException("Game settings is null!");
				}

				games.add(settings);
				aIIDs--;
			}
			return games;
		}
	};

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.aksonov.mages.services.IGameServer#disconnect(int)
	 */
	
	public void disconnect(int session) throws RemoteException {
        Log.i("GASPGameServer","disconnect user from session:"+session);
		mHandler.removeMessages(LOBBY);
		// mHandler.removeMessages(SEND);
		// mHandler.removeMessages(RECEIVE);
		IGamePlayer player = players.get(session);
		if (player != null) {
			player.setServer(null);
		}
		synchronized (players) {
			players.remove(session);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.aksonov.mages.services.IGameServer#sendInvitation(int,
	 *      org.aksonov.mages.entities.PlayerInfo)
	 */
	
	public void sendInvitation(int session, PlayerInfo info)
			throws RemoteException {
		final PlayerInfo playerInfo = getPlayerInfoBySession(session);
		info.arg1 = playerInfo.id;
		mHandler.sendMessage(mHandler.obtainMessage(INVITE, session, 0, info));
	}

	/**
	 * Invite.
	 * 
	 * @param session
	 *            the session
	 * @param playerInfo
	 *            the player info
	 * 
	 * @throws RemoteException
	 *             the dead object exception
	 */
	private void invite(int session, PlayerInfo playerInfo)
			throws RemoteException {
		Log.d("GASPGameServer", "Calling invite(" + session + "," + playerInfo
				+ ")");
		if (playerInfo == null) {
			throw new IllegalArgumentException("Info should not be null");
		}
		IGamePlayer player = players.get(session);

		final GASPConnectionInfo info = checkConnectionInfo(session);
		try {
			int result = ConnectionManager.readShort(info.host, "Invite",
					"sID=" + player.getInfo().arg2,
					new CustomTypesRequestEntity(customTypes,
							new Object[] { playerInfo }));
			Log.d("GASPGameServer", "Invite result= " + result);
			if (result <= 0) {

				// player.onError(0, Errors.INVITE_FAILED_ERROR);
			}
		} catch (Exception e) {
			player.onError(0, Errors.NETWORK_ERROR);
		}
	}

}
