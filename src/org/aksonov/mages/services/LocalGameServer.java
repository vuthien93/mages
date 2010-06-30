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

import java.util.ArrayList;
import java.util.List;

import org.aksonov.mages.Board;
import org.aksonov.mages.GameTimer;
import org.aksonov.mages.GameTimerCallback;
import org.aksonov.mages.Helper;
import org.aksonov.mages.entities.Custom;
import org.aksonov.mages.entities.GameSettings;
import org.aksonov.mages.entities.Move;
import org.aksonov.mages.entities.Note;
import org.aksonov.mages.entities.PlayerInfo;
import org.aksonov.mages.tools.Errors;
import org.aksonov.tools.Log;

import android.os.RemoteException;
import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;
import android.util.SparseIntArray;

// TODO: Auto-generated Javadoc
/**
 * Abstract class represents basic local game server - it implements all needed
 * methods except one - createBoard() which is specific for each game. It
 * register players during connect method, creates async wrappers
 * (GamePlayerProxy instances) and calls them when new event is coming.
 * 
 * Handler is used to avoid multi-threading issues.
 * 
 * @author Pavel
 */
public abstract class LocalGameServer extends IGameServer.Stub {
	
	/** The Constant gameList. */
	private static final List<GameSettings> gameList = java.util.Collections
			.synchronizedList(new ArrayList<GameSettings>());
	
	/** The Constant playerList. */
	private static final List<PlayerInfo> playerList = java.util.Collections
			.synchronizedList(new ArrayList<PlayerInfo>());
	
	/** The games. */
	protected SparseArray<GameSettings> games = new SparseArray<GameSettings>();
	
	/** The data refs. */
	protected SparseIntArray dataRefs = new SparseIntArray();
	
	/** The player games. */
	protected SparseIntArray playerGames = new SparseIntArray();
	
	/** The sessions. */
	protected SparseArray<PlayerInfo> sessions = new SparseArray<PlayerInfo>();
	
	/** The timers. */
	protected SparseArray<GameTimer> timers = new SparseArray<GameTimer>();
	
	/** The boards. */
	protected SparseArray<Board> boards = new SparseArray<Board>();
	
	/** The game. */
	private int game = 1;
	
	/** The player size. */
	private int playerSize = 1;

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
	
	/** The Constant RECEIVE. */
	private static final int RECEIVE = 7;
	
	/** The Constant SEND. */
	private static final int SEND = 8;
	
	/** The Constant PLAYERS. */
	private static final int PLAYERS = 9;
	
	/** The Constant START. */
	private static final int START = 10;
	
	/** The Constant TIME_CHANGE. */
	private static final int TIME_CHANGE = 11;
	
	/** The Constant PERIOD. */
	private static final int PERIOD = 500;

	/**
	 * This method should return the Board instance specific for concrete game.
	 * 
	 * @return the board
	 */
	public abstract Board createBoard();

	/** The m handler. */
	private Handler mHandler = new Handler() {
		
		public void handleMessage(Message msg) {
			try {
				switch (msg.what) {
				case LOGIN:
					login(msg.arg1, (IGamePlayer) msg.obj);
					break;
				case TIME_CHANGE:
					onTimeChanged(msg.arg1, msg.arg2, (PlayerInfo) msg.obj);
					break;
				case CREATE:
					create(msg.arg1, (GameSettings) msg.obj);
					break;
				case JOIN:
					join(msg.arg1, (PlayerInfo) msg.obj);
					break;
				case START:
					start(msg.arg1, msg.arg2);
					break;
				case END:
					end(msg.arg1, msg.arg2);
					break;
				case QUIT:
					quit(msg.arg1, msg.arg2);
					break;
				case LOBBY:
					if (lobby(msg.arg1, msg.arg2 == 1)) {
						removeMessages(LOBBY);
						sendMessageDelayed(obtainMessage(LOBBY, msg.arg1, 0),
								PERIOD);
					}
					break;
				case PLAYERS:
					if (playerList(msg.arg1, msg.arg2 == 1)) {
						removeMessages(PLAYERS);
						sendMessageDelayed(obtainMessage(PLAYERS, msg.arg1, 0),
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

	/** The need to update. */
	private boolean needToUpdate = true;
	
	/** The need player to update. */
	private boolean needPlayerToUpdate = true;

	/** Represents registered players at this server. */
	protected final SparseArray<IGamePlayer> players = new SparseArray<IGamePlayer>();

	/** The size. */
	private int size = 1;

	/**
	 * Creates new session.
	 * 
	 * @return new session ID
	 * 
	 * @throws RemoteException
	 *             the dead object exception
	 */
	public int createSession() throws RemoteException {
		return size++;
	}

	/**
	 * Disconnects the player associated with given session.
	 * 
	 * @param session
	 *            the session
	 * 
	 * @throws RemoteException
	 *             the dead object exception
	 */
	
	public void disconnect(int session) throws RemoteException {
		IGamePlayer player = players.get(session);
		if (player != null) {
			player.setServer(null);
			PlayerInfo info = player.getInfo();
			if (playerGames.get(info.id, -1) >= 0) {
				quitGame(playerGames.get(info.id), info.id);
			}
			dataRefs.delete(info.id);
		}
		synchronized (players) {
			players.remove(session);
		}
	}

	/**
	 * Returns the player associated with this session.
	 * 
	 * @param session
	 *            the session
	 * 
	 * @return the player
	 * 
	 * @throws RemoteException
	 *             the dead object exception
	 */
	
	public IGamePlayer getPlayer(int session) throws RemoteException {
		synchronized (players) {
			return players.get(session);
		}
	}

	/**
	 * Empty contructor.
	 */
	public LocalGameServer() {
		//Log.w("LocalGameServer", "Created!");
	}

	/**
	 * Checks given player ID and returns appropriated IGamePlayer instance.
	 * 
	 * @param playerId
	 *            player ID
	 * 
	 * @return IGamePlayer instance
	 */
	protected IGamePlayer checkPlayer(int playerId) {
		synchronized (players) {
			if (dataRefs.get(playerId, -1) == -1) {
				throw new IllegalArgumentException("Invalid playerId: "
						+ playerId + " " + dataRefs.size());
			}
			IGamePlayer player = players.get(dataRefs.get(playerId), null);
			if (player == null) {
				throw new IllegalArgumentException("Invalid playerId: "
						+ playerId + ", callback doesn't exist");
			}
			return player;
		}
	}

	/**
	 * Checks correctness of game id and player id.
	 * 
	 * @param gameId
	 *            game id
	 * @param playerId
	 *            player id
	 */
	protected void checkParams(int gameId, int playerId) {
		if (games.get(gameId, null) == null) {
			throw new IllegalArgumentException("GameId doesn't exist: "
					+ gameId);
		}
		checkPlayer(playerId);
	}

	/**
	 * Quits the game for given player.
	 * 
	 * @param gameId
	 *            the game id
	 * @param playerId
	 *            the player id
	 * 
	 * @throws RemoteException
	 *             the dead object exception
	 */
	
	public void quitGame(int gameId, int playerId) throws RemoteException {
		mHandler.sendMessage(mHandler.obtainMessage(QUIT, gameId, playerId));
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
		playerGames.delete(playerId);
		if (games.get(gameId) == null)
			return;
		checkParams(gameId, playerId);
		GameSettings game = games.get(gameId);
		synchronized (game) {
			for (int i = 0; i < game.players.size(); i++) {
				int id = game.players.get(i).id;
				IGamePlayer p = checkPlayer(id);
				p.onQuit(playerId);
				if (id == playerId) {
					if (p.getInfo().player >= 0) {
						timers.get(gameId).pause();
					}
					game.players.remove(i);
					i--;
				}
			}
		}
		if (game.players.size() == 0) {
			removeGame(game);
		}
		needToUpdate = true;
		needPlayerToUpdate = true;
		requestGameList(playerId);
		requestPlayerList(playerId);
	}

	/* (non-Javadoc)
	 * @see org.aksonov.mages.services.IGameServer#endGame(int, int)
	 */
	
	public void endGame(int gameId, int playerId) throws RemoteException {
		mHandler.sendMessage(mHandler.obtainMessage(END, gameId, playerId));
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
		checkParams(gameId, playerId);

		GameSettings game = games.get(gameId);
		// only owner can end game
		for (int i = 0; i < game.players.size(); i++) {
			int id = game.players.get(i).id;
			IGamePlayer player = checkPlayer(id);
			PlayerInfo info = player.getInfo();
			
			if (boards.get(gameId) != null && game.rated){
				Log.d("LocalGameServer", "Old rating: " + info.rating);
				info.rating = calculateRating(info, game, boards.get(gameId));
				Log.d("LocalGameServer", "New rating: " + info.rating);
				player.setInfo(info);
			}
			player.onEnd(playerId);
			player.setState(GamePlayer.LOBBY);
			playerGames.delete(id);
		}
		removeGame(game);
		playerGames.delete(playerId);
		needToUpdate = true;
		needPlayerToUpdate = true;
		requestGameList(playerId);
		requestPlayerList(playerId);
	}

	/**
	 * Removes the game.
	 * 
	 * @param game
	 *            the game
	 */
	private void removeGame(GameSettings game) {
		game.dispose();
		mHandler.removeMessages(LOBBY);
		mHandler.removeMessages(PLAYERS);
		if (timers.get(game.id, null) != null) {
			synchronized (timers) {
				timers.get(game.id).stop();
				timers.remove(game.id);
			}
		}
		if (boards.get(game.id, null) != null) {
			synchronized (boards) {
				boards.get(game.id).dispose();
				boards.remove(game.id);
			}
		}
		synchronized (games) {
			Log.d("quitGame", "Removing game: " + game.id);
			games.remove(game.id);
		}
	}

	/* (non-Javadoc)
	 * @see org.aksonov.mages.services.IGameServer#startGame(int, int)
	 */
	
	public void startGame(final int gameId, final int playerId)
			throws RemoteException {
		mHandler.sendMessage(mHandler.obtainMessage(START, gameId, playerId));
	}

	/* (non-Javadoc)
	 * @see org.aksonov.mages.services.IGameServer#createGame(int, org.aksonov.mages.entities.GameSettings)
	 */
	
	public void createGame(int session, GameSettings settings)
			throws RemoteException {
		mHandler.sendMessage(mHandler.obtainMessage(CREATE, session, 0,
				settings));
	}

	/* (non-Javadoc)
	 * @see org.aksonov.mages.services.IGameServer#joinGame(int, org.aksonov.mages.entities.PlayerInfo)
	 */
	
	public void joinGame(int gameId, PlayerInfo playerInfo)
			throws RemoteException {
		mHandler.sendMessage(mHandler
				.obtainMessage(JOIN, gameId, 0, playerInfo));
	}

	/* (non-Javadoc)
	 * @see org.aksonov.mages.services.IGameServer#sendData(int, int, org.aksonov.mages.entities.Custom)
	 */
	
	public void sendData(int gameId, int playerId, Custom data)
			throws RemoteException {
		checkParams(gameId, playerId);
		GameSettings game = games.get(gameId);
		for (int i = 0; i < game.players.size(); i++) {
			checkPlayer(game.players.get(i).id).onData(playerId, data);
		}
	}

	/* (non-Javadoc)
	 * @see org.aksonov.mages.services.IGameServer#sendMove(int, int, org.aksonov.mages.entities.Move)
	 */
	
	public void sendMove(int gameId, int playerId, Move move)
			throws RemoteException {
		Log.d("LocalGameServer", "Sending move: " + move);
		checkParams(gameId, playerId);
		Board board = boards.get(gameId, null);
		if (board == null) {
			throw new IllegalArgumentException("Board is null for this game!");
		}
		IGamePlayer player = checkPlayer(playerId);
		if (board.makeMove(move)) {
			Log.d("LocalGameServer", "Sending move: " + move);

			// switch timer
			timers.get(gameId).start(board.getCurrentPlayer());
			move.time = (int) timers.get(gameId).getTime(
					player.getInfo().player);
			GameSettings game = games.get(gameId);
			game.moves.add(move);
			for (int i = 0; i < game.players.size(); i++) {
				checkPlayer(game.players.get(i).id).onMove(playerId, move);
			}

			// check if game is over
			if (board.isGameOver()) {
				Log.d("LocalGameServer", "Game is over!");
				int arg1 = board.getScore((byte) 0);
				int arg2 = board.getScore((byte) 1);
				Note note = Note
						.createEndOfGame(Note.END, playerId, arg1, arg2);
				for (int i = 0; i < game.players.size(); i++) {
					checkPlayer(game.players.get(i).id).onNote(playerId, note);
				}
				endGame(gameId, playerId);
			}
		} else {
			Log.d("LocalGameServer", "Move is not correct: " + move);
			player.onError(0, Errors.INVALID_MOVE);
		}

	}

	/* (non-Javadoc)
	 * @see org.aksonov.mages.services.IGameServer#sendNote(int, int, org.aksonov.mages.entities.Note)
	 */
	
	public void sendNote(int gameId, int playerId, Note note)
			throws RemoteException {
		checkParams(gameId, playerId);
		GameSettings game = games.get(gameId);

		if (note.type == Note.PROPOSE_TYPE && note.reason == Note.RESIGN) {
			note.dispose();
			IGamePlayer p = checkPlayer(playerId);
			byte player = p.getInfo().player;

			int arg1 = player == 0 ? 0 : 100;
			int arg2 = player == 0 ? 100 : 0;
			if (boards.get(gameId) != null) {
				boards.get(gameId).setScore((byte)0, arg1);
				boards.get(gameId).setScore((byte)1, arg2);
			}
			note = Note.createEndOfGame(Note.RESIGN, playerId, arg1, arg2);

			for (int i = 0; i < game.players.size(); i++) {
				checkPlayer(game.players.get(i).id).onNote(playerId, note);
			}
			endGame(gameId, playerId);
		} else {
			for (int i = 0; i < game.players.size(); i++) {
				checkPlayer(game.players.get(i).id).onNote(playerId, note);
			}
		}
	}

	/**
	 * Sets the player info.
	 * 
	 * @param session
	 *            the session
	 * @param info
	 *            the info
	 */
	public void setPlayerInfo(int session, PlayerInfo info) {
		//Log.w("LocalGameService", "Session id: " + session + ", added info: "
		//		+ info);
		synchronized (sessions) {
			sessions.put(session, info);
		}
	}

	/* (non-Javadoc)
	 * @see org.aksonov.mages.services.IGameServer#isConfigured(int)
	 */
	
	public boolean isConfigured(int session) {
		PlayerInfo info = sessions.get(session, null);
		Log.w("LocalGameService", "Session id: " + session
				+ ", retrieved session: " + info);
		return info != null;
	}

	/* (non-Javadoc)
	 * @see org.aksonov.mages.services.IGameServer#connect(int, org.aksonov.mages.services.IGamePlayer)
	 */
	
	public void connect(final int session, final IGamePlayer player)
			throws RemoteException {
		if (!isConfigured(session)) {
			throw new IllegalArgumentException("Session is not configured!");
		}
		Log.d("LocalGameServer", "Connect, " + session + " player " + player);
		mHandler.sendMessage(mHandler.obtainMessage(LOGIN, session, 0, player));
	}

	/* (non-Javadoc)
	 * @see org.aksonov.mages.services.IGameServer#requestGameList(int)
	 */
	
	public void requestGameList(final int playerId) throws RemoteException {
		mHandler.sendMessage(mHandler.obtainMessage(LOBBY, playerId, 1));
	}

	/* (non-Javadoc)
	 * @see org.aksonov.mages.services.IGameServer#requestPlayerList(int)
	 */
	
	public void requestPlayerList(final int playerId)
			throws RemoteException {
		mHandler.sendMessage(mHandler.obtainMessage(PLAYERS, playerId, 1));
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
		Log.d("LocalGameServer", "login player " + gamePlayer);
		IGamePlayer player = new GamePlayerProxy(gamePlayer);
		PlayerInfo info = player.getInfo();
		if (info != null) {
			boolean found = false;
			for (int i=0;i<players.size();i++){
				if (players.get(players.keyAt(i)).getInfo().username.equals(info.username)){
					found =true;
				}
			}
			if (found){
				player.onError(0, Errors.NOT_SUPPORTED);
				return;
			}
			session = createSession();
		} else {
			synchronized (sessions) {
				info = sessions.get(session);
			}
		}
		player.setServer(this);
		players.put(session, player);
		info.session = session;
		info.id = playerSize++;
		setPlayerInfo(session, info);
		synchronized (dataRefs) {
			Log.d("LocalGameServer", "Putting playerId: " + info.id
					+ " to session " + session);
			dataRefs.put(info.id, session);
		}
		player.setInfo(info);
		Helper.sleep(500);
		player.onLogin(info);

		needPlayerToUpdate = true;
	}

	/**
	 * Player list.
	 * 
	 * @param playerId
	 *            the player id
	 * @param required
	 *            the required
	 * 
	 * @return true, if successful
	 * 
	 * @throws RemoteException
	 *             the dead object exception
	 */
	private boolean playerList(int playerId, boolean required)
			throws RemoteException {
		if (dataRefs.get(playerId, -1) == -1) return false;
		final IGamePlayer player = checkPlayer(playerId);
		if (!required
				&& (player.getState() != GamePlayer.LOBBY && player.getState() != GamePlayer.WAITING)) {
			Log.d("playerList", "state is not lobby or waiting, return");
			return false;
		}
		if (required || needPlayerToUpdate) {
			Log.d("LocalGameServer", "playerList" + dataRefs.size() + " "
					+ players.size());
			List<PlayerInfo> pList = new ArrayList<PlayerInfo>();
			synchronized (dataRefs) {
				// synchronized (playerList) {
				// playerList.clear();
				for (int i = 0; i < dataRefs.size(); i++) {
					IGamePlayer p = players.get(dataRefs.valueAt(i));
					if (playerGames.get(p.getInfo().id, -1) == -1) {
						pList.add(p.getInfo());
					}
				}
			}
			player.onPlayerList(pList);

			if (needPlayerToUpdate) {
				Log.d("LocalGameServer", "playerList" + pList.size());
				for (int i = 0; i < dataRefs.size(); i++) {
					IGamePlayer p = players.get(dataRefs.valueAt(i));
					if (p.getState() == GamePlayer.LOBBY) {
						p.onPlayerList(pList);
					}
				}
			}
			needPlayerToUpdate = false;
			// }
		}
		return true;
	}

	/**
	 * Lobby.
	 * 
	 * @param playerId
	 *            the player id
	 * @param required
	 *            the required
	 * 
	 * @return true, if successful
	 * 
	 * @throws RemoteException
	 *             the dead object exception
	 */
	private boolean lobby(int playerId, boolean required)
			throws RemoteException {
		final IGamePlayer player = checkPlayer(playerId);
		if (!required && player.getState() != GamePlayer.LOBBY) {
			Log.d("lobby", "state is not lobby, return");
			return false;
		}

		if (needToUpdate || required) {
			Log.d("LocalGameServer", "lobby" + games.size());
			List<GameSettings> gList = new ArrayList<GameSettings>();
			for (int i = 0; i < games.size(); i++) {
				gList.add(games.valueAt(i));
			}
			player.onGameList(gList);

			if (needToUpdate) {
				for (int i = 0; i < dataRefs.size(); i++) {
					IGamePlayer p = players.get(dataRefs.valueAt(i));
					if (p.getState() == GamePlayer.LOBBY) {
						p.onGameList(gList);
					}
				}
			}
			needToUpdate = false;
		}
		return true;

	}

	/* (non-Javadoc)
	 * @see org.aksonov.mages.services.IGameServer#sendInvitation(int, org.aksonov.mages.entities.PlayerInfo)
	 */
	
	public void sendInvitation(int playerId, PlayerInfo playerInfo)
			throws RemoteException {
		
		if (playerInfo.id == 0){
			throw new IllegalArgumentException("id is not set");
		}
		if (playerInfo.gameId == 0){
			throw new IllegalArgumentException("game id is not set");
		}
		
		if (playerInfo.session == 0){
			throw new IllegalArgumentException("session is not set");
		}
		Log.d("LocalGameServer", "Sending invitation to player id "
				+ playerInfo.id);
		
		IGamePlayer invitedPlayer = checkPlayer(playerInfo.id);
		invitedPlayer.onInvitation(playerInfo, playerId);

	};

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

		if (settings.players.size() != 0) {
			throw new IllegalArgumentException(
					"GameSettings players should be empty: "
							+ settings.players.size());
		}
		IGamePlayer player = players.get(session);
		Log.d("createGame", "Trying to createGame, session: " + session
				+ ", player: " + player);
		PlayerInfo info = player.getInfo();
		int gameId = game++;
		settings.id = gameId;
		info.gameId = gameId;
		player.setInfo(info);

		synchronized (players) {
			players.put(info.session, player);
		}

		// quit existing player game if any
		if (playerGames.get(info.id, -1) >= 0) {
			quitGame(playerGames.get(info.id), info.id);
		}

		synchronized (games) {
			games.put(gameId, settings);
		}
		Log.d("createGame", "Adding game to games" + games.size());
		needToUpdate = true;
		needPlayerToUpdate = true;
		join(gameId, info);

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
	private void join(int gameId, PlayerInfo playerInfo)
			throws RemoteException {
		Log.d("LocalGameServer", "joinGame: " + gameId + "  " + playerInfo);
		if (playerInfo.id == 0) {
			throw new IllegalArgumentException("Player id is not set");
		}
		checkParams(gameId, playerInfo.id);
		GameSettings game = games.get(gameId);
		IGamePlayer player = checkPlayer(playerInfo.id);

		int found = -1;
		for (int i = 0; i < game.players.size(); i++) {
			PlayerInfo p = game.players.get(i);
			if (playerInfo.player != -1 && p.player == playerInfo.player
					&& p.id != playerInfo.id) {
				// we cannot join because position is busy
				player.onError(0, Errors.JOIN_BUSY_ERROR);
				return;
			}
			if (playerInfo.id == p.id){
				found = i;
			}
		}
		
		if (found >=0)
		game.players.remove(found);
		playerInfo.gameId = gameId;

		// quit existing player game if any
		if (playerGames.get(playerInfo.id, -1) > 0
				&& playerGames.get(playerInfo.id, -1) != gameId) {
			quitGame(playerGames.get(playerInfo.id), playerInfo.id);
		}

		player.setInfo(playerInfo);
		PlayerInfo info = player.getInfo();
		playerGames.put(playerInfo.id, gameId);

		// send all current players to this player

		// notify all other players
		for (int i = 0; i < game.players.size(); i++) {
			final IGamePlayer p = checkPlayer(game.players.get(i).id);
			p.onJoin(info);
			player.onJoin(p.getInfo());
		}
		Log.d("LocalGameServer", "joinGame: adding player info " + info
				+ " to game ");
		game.players.add(info);
		player.onJoin(info);
		
		needToUpdate = true;
		needPlayerToUpdate = true;
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
		Log.d("LocalGameServer", "startGame, player id: " + playerId);
		checkParams(gameId, playerId);

		checkPlayer(playerId).setState(GamePlayer.WAITING);

		final GameSettings game = games.get(gameId);
		synchronized (game) {
			int number = 0;
			Log.d("LocalGameServer", "Current number of players: "
					+ game.players.size());
			for (int i = 0; i < game.players.size(); i++) {
				PlayerInfo info = game.players.get(i);
				int player = info.id;
				if (player == playerId) {
					info.ready = true;
				}
				if (info.player >= 0 && info.ready) {
					number++;
				}
			}
			if (number >= game.minActors && game.players.get(0).ready) {
				Log.d("LocalGameServer", "Start game!");

				if (boards.get(gameId, null) == null) {
					Log.d("LocalGameServer", "Create board");

					Board board = createBoard();
					boards.put(gameId, board);
				}

				if (timers.get(gameId, null) == null) {

					GameTimer timer = new GameTimer(new GameTimerCallback() {
						private boolean alive = true;

						
						public boolean isAlive() {
							return alive;
						}

						
						public void onTimeChanged(int player, long totalTime,
								long moveTime) {
							List<PlayerInfo> players = game.players;
							for (int i = 0; i < players.size(); i++) {
								PlayerInfo info = players.get(i);
								if (info.player == player) {
									mHandler.sendMessage(mHandler
											.obtainMessage(TIME_CHANGE,
													(int) totalTime,
													(int) moveTime, players
															.get(i)));
								}
							}
						}

						
						public void setAlive(boolean alive) {
							this.alive = alive;
						}
					}, game.moveIncr);
					timer.start();
					timers.put(gameId, timer);
				}
				GameTimer timer = timers.get(gameId);
				timer.pause();
				for (int i = 0; i < game.moves.size(); i++) {
					Move move = game.moves.get(i);
					timer.setTime(move.player, move.time);
				}

				for (int i = 0; i < game.players.size(); i++) {
					final IGamePlayer p = checkPlayer(game.players.get(i).id);
					Log.d("LocalGameServer", "Calling onStart for player: "
							+ p.getInfo());
					p.onStart(game, playerId);
					/*
					 * new Thread(new Runnable() { public void run() { try {
					 * p.onStart(game, playerId); } catch (Exception e) {
					 * Log.e("LocalGameServer", e); } } }).start();
					 */
				}
				timer.start(boards.get(gameId).getCurrentPlayer());

			} else {
				Log.d("LocalGameServer",
						"Cannot start game because number of active players is: "
								+ number + ", owner started : "
								+ game.players.get(0).ready);
			}
		}

	}

	/**
	 * On time changed.
	 * 
	 * @param totalTime
	 *            the total time
	 * @param moveTime
	 *            the move time
	 * @param info
	 *            the info
	 */
	private void onTimeChanged(int totalTime, int moveTime, PlayerInfo info) {
		GameSettings game;
		synchronized (games) {
			game = games.get(info.gameId);
		}
		try {
			if ((game.timePerGame > 0 && totalTime > game.timePerGame)
					|| (game.timePerMove > 0 && moveTime > game.timePerMove)) {
				Log.e("onTimeChanged", "Player : " + info
						+ " exceeded time limits");
				int arg1 = info.player == 0 ? 0 : 100;
				int arg2 = info.player == 0 ? 100 : 0;
				if (boards.get(game.id) != null) {
					boards.get(game.id).setScore((byte)0, arg1);
					boards.get(game.id).setScore((byte)1, arg2);
				}
				sendNote(game.id, info.id, Note.createEndOfGame(
						Note.TIME_LIMIT_REASON, info.id, arg1, arg2));
				endGame(info.gameId, info.id);
			}
		} catch (Exception e) {
			Log.e("onTimeChanged", e);
		}
	}

	/**
	 * Calculate rating.
	 * 
	 * @param info
	 *            the info
	 * @param settings
	 *            the settings
	 * @param board
	 *            the board
	 * 
	 * @return the int
	 */
	protected int calculateRating(PlayerInfo info, GameSettings settings, Board board){
		return info.rating + board.getScore(info.player) - 50 ;
	}

}
