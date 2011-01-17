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

import org.aksonov.mages.entities.Custom;
import org.aksonov.mages.entities.GameSettings;
import org.aksonov.mages.entities.Move;
import org.aksonov.mages.entities.Note;
import org.aksonov.mages.entities.PlayerInfo;
import org.aksonov.mages.services.GamePlayer;
import org.aksonov.mages.services.IGamePlayer;
import org.aksonov.mages.services.IGameServer;
import org.aksonov.mages.services.IPlayerGameListener;
import org.aksonov.mages.services.IPlayerLobbyListener;
import org.aksonov.mages.services.LocalGameServer;
import org.aksonov.mages.services.connect4.LocalConnect4Server;
import org.aksonov.mages.services.connect4.test.RobotConnect4Player;
import org.aksonov.mages.tools.AppHelper;
import org.aksonov.mages.tools.CancelListener;
import org.aksonov.mages.tools.PlayerInfoAdapter;
import org.aksonov.mages.view.BoardView;
import org.aksonov.tools.Log;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

// TODO: Auto-generated Javadoc
/**
 * This activity represents state before game start (i.e. waiting for other
 * players) and playing interaction.
 * 
 * @author Pavel
 */
public class InGame extends Activity {
	
	/** The Constant JOIN. */
	private static final int JOIN = 1;
	
	/** The Constant START. */
	private static final int START = 2;
	
	/** The Constant MOVE. */
	private static final int MOVE = 3;
	
	/** The Constant ERROR. */
	private static final int ERROR = 5;
	
	/** The Constant NOTE. */
	private static final int NOTE = 6;
	
	/** The Constant QUIT. */
	private static final int QUIT = 7;
	
	/** The Constant END. */
	private static final int END = 8;
	
	/** The Constant DATA. */
	private static final int DATA = 9;
	
	/** The Constant TIME_CHANGE. */
	private static final int TIME_CHANGE = 10;
	
	/** The Constant SEND. */
	public final static int SEND = 13;
	
	/** The Constant SEND_NOTE. */
	public final static int SEND_NOTE = 14;
	
	/** The Constant UPDATE_PLAYERS. */
	public final static int UPDATE_PLAYERS = 15;

	/** The Constant RESIGN_ID. */
	public final static int RESIGN_ID = 1;
	
	/** The Constant TIE_ID. */
	public final static int TIE_ID = 2;
	
	/** The Constant QUIT_ID. */
	public final static int QUIT_ID = 3;

	/** The Constant MAX. */
	private final static int MAX = 5;

	/** The game. */
	private GameSettings game;
	
	/** The session. */
	private int session;
	
	/** The player. */
	private IGamePlayer player;
	
	/** The info. */
	private PlayerInfo info;
	
	/** The invited info. */
	private PlayerInfo invitedInfo;
	
	/** The board. */
	private BoardView board;
	
	/** The waiting view. */
	private View waitingView;
	
	/** The m invite Dialog. */
	private AlertDialog mInviteDialog = null;
	
	/** The m service. */
	protected IGameServer mService = null;
	
	/** The m waiting Dialog. */
	private AlertDialog mWaitingDialog;
	
	/** The m waiting confirm Dialog. */
	private ProgressDialog mWaitingConfirmDialog;
	
	/** The is cancelled. */
	private boolean isCancelled = false;

	/** The chronometers. */
	protected TextView[] chronometers = new TextView[MAX];
	
	/** The chronometer moves. */
	protected TextView[] chronometerMoves = new TextView[MAX];
	
	/** The labels. */
	protected TextView[] labels = new TextView[MAX];

	/** The total times. */
	private int[] totalTimes = new int[MAX];
	
	/** The move times. */
	private int[] moveTimes = new int[MAX];
	
	/** The usernames. */
	private String[] usernames = new String[MAX];

	/** The game timer. */
	private GameTimer gameTimer;
	
	/** The settings. */
	private TextView settings;
	
	/** The msg. */
	private EditText msg;
	
	/** The is waiting for players. */
	private boolean isWaitingForPlayers = false;
	
	/** The text. */
	private TextView text;
	
	/** The scroll view. */
	private ScrollView scrollView;
	
	/** The player ids. */
	private int[] playerIds = new int[5];
	
	/** The players. */
	private SparseArray<PlayerInfo> players = new SparseArray<PlayerInfo>();
	
	/** The player view. */
	private ListView playerView;
	
	/** The m player adapter. */
	private PlayerInfoAdapter mPlayerAdapter;

	/** The waiting id. */
	private int waitingId;
	
	/** The is started. */
	private boolean isStarted = false;
	
	/** The join view. */
	private View joinView;

	/** The invite cancel. */
	private CancelListener inviteCancel = new CancelListener() {
		public void onCancel() {
			confirmStartGame();
		}
	};

	/** The cancel. */
	private CancelListener cancel = new CancelListener() {
		public void onCancel() {
			isCancelled = true;
			finish();
		}
	};

	/** The m handler. */
	private Handler mHandler = new Handler() {
		
		public void handleMessage(Message msg) {
			try {
				switch (msg.what) {
				case TIME_CHANGE:
					byte player = (Byte) msg.obj;
					if (player < 0 || player >= chronometers.length
							|| !isStarted)
						return;
					int totalTime = msg.arg1;
					int moveTime = msg.arg2;

					if (game.timePerGame > 0)
						totalTime = game.timePerGame - totalTime;

					if (totalTime < 0)
						totalTime = 0;

					if (game.timePerMove > 0)
						moveTime = game.timePerMove - moveTime;

					if (moveTime < 0)
						moveTime = 0;

					totalTimes[player] = totalTime;
					moveTimes[player] = moveTime;
					updateChronometers();

					break;
				case JOIN:
					PlayerInfo playerInfo = (PlayerInfo) msg.obj;
					if (!game.players.contains(playerInfo)) {
						game.players.add(playerInfo);
					}
					Log.d("InGame", "Player " + playerInfo
							+ " joined this game");
					sendText(playerInfo.username + " "
							+ getText(R.string.joins_the_game));

					if (playerInfo.player >= 0) {
						playerIds[playerInfo.player] = playerInfo.id;
						usernames[playerInfo.player] = playerInfo.username;
					}

					players.put(info.id, info);
					updateChronometers();
					break;
				case START:
					game = (GameSettings) msg.obj;
					board.setMoves(game.moves);
					settings.setText(game.toString());
					for (int i = 0; i < game.players.size(); i++) {
						PlayerInfo info = game.players.get(i);
						if (info.player >= 0) {
							playerIds[info.player] = info.id;
							usernames[info.player] = info.username;
						}
						players.put(info.id, info);
					}
					updateChronometers();
					startGame();
					break;
				case ERROR:
					AppHelper
							.showError(InGame.this, msg.arg1, msg.arg2, cancel);
					break;
				case UPDATE_PLAYERS:
					Log.d("InGame", "UPDATE_PLAYERS event : " + msg.obj);
					mPlayerAdapter.updateList((List<PlayerInfo>) msg.obj);
					break;
				case SEND_NOTE:
					try {
						mService.sendNote(msg.arg1, msg.arg2, (Note) msg.obj);
					} catch (Exception e) {
						Log.e("InGame", e);
					}
					break;
				case SEND:
					Move move = (Move) msg.obj;
					Log.d("InGame", "Sending move " + move);
					gameTimer.start(board.board.getCurrentPlayer());

					waitingId = move.id;
					// mWaitingConfirmDialog = ProgressDialog.show(InGame.this,
					// null, getText(R.string.sending_move), true);

					if (mService != null) {
						try {
							mService.sendMove(game.id, info.id, move);
						} catch (Exception e) {
							Log.e("GameView", e);
						}
					}
					break;
				case MOVE:
					move = (Move) msg.obj;
					Log.d("InGame", "Receiving move: " + move);
					board.onMove(move);
					confirmMove(move);
					updateChronometers();
					break;
				case QUIT:
					if (players.get(msg.arg1) != null) {
						PlayerInfo info = players.get(msg.arg1);
						sendText(info.username + " "
								+ getText(R.string.quits_the_game));
						players.remove(msg.arg1);

						updateChronometers();

						if (info.player >= 0) {
							gameTimer.pause();
							board.readonly = true;
							isStarted = false;
							confirmStartGame();

						}
					}
					break;
				case END:
					PlayerInfo info = players.get(msg.arg1);
					sendText(info.username + " "
							+ getText(R.string.quits_the_game));
					players.remove(msg.arg1);
					endGame();
					break;
				case NOTE:
					Note note = (Note) msg.obj;
					if (note.type == Note.MESSAGE_TYPE) {
						// AppHelper.showMessage(InGame.this,
						// getText(R.string.you_got_the_message));
						if (players.get(note.who) != null) {
							sendText(players.get(note.who).username + ">"
									+ note.message + '\n');
						}
					} else if (note.type == Note.END_GAME_TYPE) {
						String username1 = players.get(playerIds[0], null) == null ? "?"
								: players.get(playerIds[0]).username;
						String username2 = players.get(playerIds[1], null) == null ? "?"
								: players.get(playerIds[1]).username;
						String result1 = ((double) note.arg1 / 100) + "";
						String result2 = ((double) note.arg2 / 100) + "";
						String message = new StringBuilder().append(
								getText(R.string.end_of_game)).append(": ")
								.append(username1).append(" - ").append(
										username2).append(" : ")
								.append(result1).append(" - ").append(result2)
								.toString();
						sendText(message);
						endGame();
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

	/**
	 * Represents game player listener which just delegates all calls to
	 * handler.
	 */
	protected IPlayerGameListener listener = new IPlayerGameListener.Stub() {

		
		public void onData(int playerId, Custom data)
				throws RemoteException {
			mHandler.sendMessage(mHandler
					.obtainMessage(DATA, playerId, 0, data));
		}

		
		public void onEnd(int playerId) throws RemoteException {
			mHandler.sendMessage(mHandler.obtainMessage(END, playerId, 0));
		}

		
		public void onError(int attempt, int code) throws RemoteException {
			mHandler.sendMessage(mHandler.obtainMessage(ERROR, attempt, code));
		}

		
		public void onJoin(PlayerInfo info) throws RemoteException {
			Log.d("InGame", "onJoin: " + info);
			mHandler.sendMessage(mHandler.obtainMessage(JOIN, info));
		}

		
		public void onMove(int playerId, Move move) throws RemoteException {
			mHandler.sendMessage(mHandler
					.obtainMessage(MOVE, playerId, 0, move));
		}

		
		public void onNote(int playerId, Note data) throws RemoteException {
			mHandler.sendMessage(mHandler
					.obtainMessage(NOTE, playerId, 0, data));
		}

		
		public void onQuit(int playerId) throws RemoteException {
			mHandler.sendMessage(mHandler.obtainMessage(QUIT, playerId, 0));
		}

		
		public void onStart(GameSettings settings, int playerId)
				throws RemoteException {
			mHandler.sendMessage(mHandler.obtainMessage(START, settings));
		}

		
		public void onPlayerList(List<PlayerInfo> playerList)
				throws RemoteException {
			mHandler.sendMessage(mHandler.obtainMessage(UPDATE_PLAYERS,
					playerList));

		}
	};

	/** Represents connection to the game server service. */
	protected ServiceConnection mConnection = new ServiceConnection() {

		public void onServiceConnected(ComponentName className, IBinder service) {
			mService = IGameServer.Stub.asInterface((IBinder) service);
			if (mService == null) {
				throw new IllegalArgumentException("Server is null!");
			}
			try {
				player = mService.getPlayer(session);
				player.registerGameListener(listener);

				info = player.getInfo();
				player.setState(GamePlayer.WAITING);
				mService.requestPlayerList(info.id);
				mService.startGame(game.id, info.id);

				confirmStartGame();
			} catch (Exception e) {
				Log.e("InGame", e);
			}
		}

		public void onServiceDisconnected(ComponentName className) {
			mService = null;
		}
	};

	/**
	 * Called when the activity is destroyed.
	 */
	
	protected void onDestroy() {
		try {
			if (gameTimer != null)
				gameTimer.stop();
			if (player != null) {
				player.unregisterGameListener(listener);
				player.setState(GamePlayer.LOBBY);
			}
			// view.dispose();
			if (info != null && mService != null) {
				mService.quitGame(game.id, info.id);
			}
		} catch (Exception e) {
			Log.e("onDestroy", e);
		}
		unbindService(mConnection);
		super.onDestroy();
	}

	/**
	 * Called when the activity is first created. This method initializes all
	 * used views, register needed callbacks for buttons
	 * 
	 * @param cicle
	 *            the cicle
	 */
	
	public void onCreate(Bundle cicle) {
		super.onCreate(cicle);
		LayoutInflater inflate = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		session = AppHelper.getSessionId(getIntent());
		game = ((MagesApplication) getApplication()).game;

		mPlayerAdapter = new PlayerInfoAdapter(this);
		mPlayerAdapter
				.updateList(((MagesApplication) getApplication()).players);

		joinView = inflate.inflate(R.layout.joingame, null);

		playerView = new ListView(this);
		playerView.setAdapter(mPlayerAdapter);
		playerView.setPadding(5, 5, 5, 5);
		playerView.setBackgroundColor(Color.BLACK);
		playerView.setFocusable(true);
		playerView.setOnItemClickListener(inviteListener);

		Log.d("InGame", "onCreate, session: " + session + ", game id: "
				+ game.id);

		if (session > 0) {
			ComponentName service = AppHelper.getService(getIntent());
			if (service == null) {
				AppHelper.showMessage(this, "null service within intent");
				finish();
				return;
			}
			bindService(AppHelper.createServiceIntent(service), mConnection, 0);

			int layout = AppHelper.getBoardLayout(getIntent());
			setContentView(layout);
		} else {
			// test mode
			testMode();
			// int hLayout = R.layout.chess_horizontal;
			// int vLayout = R.layout.chess_vertical;
			int hLayout = R.layout.chess_horizontal;
			int vLayout = R.layout.c4_vertical;

			DisplayMetrics dm = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(dm);
			int height = dm.heightPixels;
			int width = dm.widthPixels;

			if (height > width) {
				setContentView(vLayout);
			} else {
				setContentView(hLayout);
			}

		}

		// set board
		board = (BoardView) findViewById(R.id.board_view);
		board.base = AppHelper.getPlayer(getIntent()) >= 0 ? AppHelper
				.getPlayer(getIntent()) : 0;
		board.readonly = true;
		board.setHandler(mHandler);

		// set chat views
		msg = (EditText) findViewById(R.id.msg);
		text = (TextView) findViewById(R.id.log);
		scrollView = (ScrollView) findViewById(R.id.scroll_log);

		Button send = (Button) findViewById(R.id.send);
		if (msg != null){
			msg.setOnKeyListener(new OnKeyListener(){

				
				public boolean onKey(View view, int keycode, KeyEvent event) {
					if (keycode == KeyEvent.KEYCODE_DPAD_CENTER || keycode == KeyEvent.KEYCODE_ENTER){
						if (msg.getText().toString().length() > 0) {
							String message = msg.getText().toString();
							// sendText(message);
							msg.setText("");
							Note note = Note.createMessage(info.id, message);
							mHandler.sendMessage(mHandler.obtainMessage(SEND_NOTE,
									game.id, info.id, note));
						}
						return true;
					}
					return false;
				}
				
			});
		}
		if (send != null) {
			send.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					if (msg.getText().toString().length() > 0) {
						String message = msg.getText().toString();
						// sendText(message);
						msg.setText("");
						Note note = Note.createMessage(info.id, message);
						mHandler.sendMessage(mHandler.obtainMessage(SEND_NOTE,
								game.id, info.id, note));
					}
				}
			});
		} else {
		}

		isWaitingForPlayers = true;

		// set chronometers
		/*
		 * chronometers[(0 + board.base) % 2] = (TextView)
		 * findViewById(R.id.chronometer0); chronometers[(1 + board.base) % 2] =
		 * (TextView) findViewById(R.id.chronometer1);
		 * 
		 * chronometerMoves[(0 + board.base) % 2] = (TextView)
		 * findViewById(R.id.chronometerMove0); chronometerMoves[(1 +
		 * board.base) % 2] = (TextView) findViewById(R.id.chronometerMove1);
		 * 
		 * labels[(0 + board.base) % 2] = (TextView) findViewById(R.id.label0);
		 * labels[(1 + board.base) % 2] = (TextView) findViewById(R.id.label1);
		 */

		settings = (TextView) findViewById(R.id.settings);

		// set undo/redo buttons
		Button undo = (Button) findViewById(R.id.back);
		Button redo = (Button) findViewById(R.id.redo);

		if (undo != null) {
			undo.setOnClickListener(new OnClickListener() {
				
				public void onClick(View arg0) {
					board.undoLastMove();
				}
			});
		}

		if (redo != null) {
			redo.setOnClickListener(new OnClickListener() {
				
				public void onClick(View arg0) {
					board.redoLastMove();
				}
			});
		}

	}

	/**
	 * Confirm move.
	 * 
	 * @param move
	 *            the move
	 */
	private void confirmMove(Move move) {
		if (move.id == waitingId && move.player == info.player) {
			// AppHelper.dismiss(mWaitingConfirmDialog);
		}
		gameTimer.setTime(move.player, move.time);
		gameTimer.start(board.board.getCurrentPlayer());

	}

	/**
	 * End game.
	 */
	private void endGame() {
		isStarted = false;
		board.readonly = true;
		gameTimer.stop();
		if (isWaitingForPlayers && !isCancelled) {
			AppHelper.dismiss(mWaitingDialog);
		}
	}

	/**
	 * Start game.
	 */
	private void startGame() {
		if (!isStarted) {
			Log.d("GameView", "Game started!");
			isStarted = true;
			if (!isCancelled) {
				AppHelper.dismiss(mWaitingDialog);
			}
			AppHelper.showMessage(InGame.this, getText(R.string.game_started));
			if (info.player >= 0)
				board.readonly = false;
			isWaitingForPlayers = false;
			// set timers (if it is not set)
			if (gameTimer == null) {
				this.gameTimer = new GameTimer(new BaseGameTimerCallback() {
					
					public void onTimeChanged(int player, long totalTime,
							long moveTime) {
						mHandler.sendMessage(mHandler.obtainMessage(
								TIME_CHANGE, (int) totalTime, (int) moveTime,
								(byte) player));
					}
				}, game.moveIncr);
				gameTimer.start();
			}
			gameTimer.start(board.board.getCurrentPlayer());
		}
	}

	/**
	 * Send text.
	 * 
	 * @param msg
	 *            the msg
	 */
	private void sendText(CharSequence msg) {
		text.setText(msg + "\n" + text.getText());
		// text.computeScroll();
		// text.scrollBy(0, 100);
		// scrollView.pageScroll(View.FOCUS_DOWN);
		// scrollView.scrollBy(0, 20);
	}

	/**
	 * Test mode.
	 */
	private void testMode() {
		try {
			mService = new LocalConnect4Server();
			player = new GamePlayer();
			info = PlayerInfo.create();
			info.username = "Pavel";
			info.player = 0;
			session = ((LocalGameServer) mService).createSession();
			((LocalGameServer) mService).setPlayerInfo(session, info);
			mService.connect(session, player);
			player.registerGameListener(listener);
			GameSettings game = GameSettings.create();
			game.timePerGame = 5 * 60 * 1000; // 5 min
			game.timePerMove = 120 * 1000; // 1 min
			game.moveIncr = 15 * 1000;
			mService.createGame(session, game);

			final IGamePlayer player2 = new RobotConnect4Player();
			final PlayerInfo info2 = PlayerInfo.create();
			info2.username = "Stupid Robot";
			info2.player = 1;
			player2.setInfo(info2);

			player.registerLobbyListener(new IPlayerLobbyListener.Stub() {

				
				public void onLogin(PlayerInfo info) throws RemoteException {
					// TODO Auto-generated method stub

				}

				
				public void onJoin(PlayerInfo info) throws RemoteException {
					mService.startGame(info.gameId, info.id);

				}

				
				public void onError(int attempt, int code)
						throws RemoteException { // TODO
					// Auto-generated
					// method stub
				}

				
				public void onGameList(List<GameSettings> gameList)
						throws RemoteException { // TODO
					// Auto-generated
					// method stub
				}

				
				public void onInvitation(PlayerInfo info, int playerId)
						throws RemoteException {

				}

				
				public void onPlayerList(List<PlayerInfo> playerList)
						throws RemoteException {
				}

			});

			int session2 = ((LocalGameServer) mService).createSession();
			((LocalGameServer) mService).setPlayerInfo(session2, info2);
			mService.connect(session2, player2);

		} catch (Exception e) {
			Log.e("InGame", e);
		}

	}

	/**
	 * Update chronometers.
	 */
	private void updateChronometers() {
		if (game != null) {
			settings.setText(game.toString());
		}

		StringBuilder sb = new StringBuilder();
		// int size = game.players.size();
		for (byte player = 0; player < game.maxActors; player++) {
			if (usernames[player] != null) {
				sb.append(usernames[player]);
				sb.append("[");
				if (labels[player] != null) {
					labels[player].setText(usernames[player]);
				}
				sb.append(Helper.formatTime(totalTimes[player]));

				if (game.timePerMove > 0) {
					sb.append("/");
					sb.append(Helper.formatTime(moveTimes[player]));
					if (chronometerMoves[player] != null) {
						chronometerMoves[player].setText(Helper
								.formatTime(moveTimes[player]));
					}
				}
				sb.append("] ");
				if (chronometers[player] != null) {
					chronometers[player].setText(Helper
							.formatTime(totalTimes[player]));
				}
			} else {
				sb.append("? [00:00] ");
			}
		}
		String title = sb.toString();
		if (!title.equals("")) {
			setTitle(title);
		}

	}

	/**
	 * Called when your activity's options menu needs to be created.
	 * 
	 * @param menu
	 *            the menu
	 * 
	 * @return true, if on create options menu
	 */
	
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		menu.add(0, RESIGN_ID, 0, R.string.resign);
		menu.add(0, QUIT_ID, 1, R.string.quit);

		return true;
	}

	/**
	 * Called right before your activity's option menu is displayed.
	 * 
	 * @param menu
	 *            the menu
	 * 
	 * @return true, if on prepare options menu
	 */
	
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		return true;
	}

	/**
	 * Called when a menu item is selected.
	 * 
	 * @param item
	 *            the item
	 * 
	 * @return true, if on options item selected
	 */
	
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case QUIT_ID:
			endGame();
			finish();
			break;
		case RESIGN_ID:
			new AlertDialog.Builder(this).setTitle(R.string.resign).setMessage(
					R.string.resign_confirmation).setNegativeButton(
					R.string.cancel, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface Dialog,
								int whichButton) {
						}
					}).setPositiveButton(R.string.resign,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface Dialog,
								int whichButton) {

							mHandler.sendMessage(mHandler.obtainMessage(
									SEND_NOTE, game.id, info.id, Note
											.createProposition(Note.RESIGN,
													info.id)));

						}
					}).show();
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * Invite players.
	 */
	protected void invitePlayers() {
		this.game.id = game.id;
		mInviteDialog = new AlertDialog.Builder(this).setTitle(
				R.string.choose_player_to_invite).setView(playerView)
				.setOnCancelListener(inviteCancel).setNegativeButton(
						R.string.back_invite, inviteCancel).show();

	}

	/**
	 * Confirm start game.
	 */
	protected void confirmStartGame() {
		if (!isStarted) {
			isWaitingForPlayers = true;
			isCancelled = false;

            LayoutInflater inflate = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            waitingView = inflate.inflate(R.layout.waiting_players, null);

			mWaitingDialog = new AlertDialog.Builder(InGame.this)
					.setOnCancelListener(cancel).setTitle(R.string.waiting)
					.setView(waitingView).setPositiveButton(
							R.string.invite_player,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface Dialog,
										int whichButton) {
									invitePlayers();
								}
							}).setNegativeButton(R.string.cancel, cancel)
					.show();

		}
	}

	/** The invite listener. */
	private OnItemClickListener inviteListener = new OnItemClickListener() {
		
		public void onItemClick(AdapterView parent, View view, int position,
				long id) {
			if (mPlayerAdapter.getItem(position) != null) {
				invite(((PlayerInfo) mPlayerAdapter.getItem(position)));
				AppHelper.dismiss(mInviteDialog);
			}
		}

	};

	/**
	 * Sets the checked.
	 * 
	 * @param id
	 *            the id
	 * @param checked
	 *            the checked
	 */
	private void setChecked(int id, boolean checked) {
		joinView.findViewById(id).setEnabled(checked);
		((RadioButton) joinView.findViewById(id)).setChecked(checked);
	}

	/**
	 * Invite.
	 * 
	 * @param info
	 *            the info
	 */
	private void invite(PlayerInfo info) {
		if (game == null) {
			throw new IllegalArgumentException("Game " + game
					+ " doesn't exist");
		}
		byte[] colors = AppHelper.getAvailablePlayers(game, info.id);
		setChecked(R.id.color0, false);
		setChecked(R.id.color0, false);
		setChecked(R.id.observer, true);

		for (int i = colors.length - 1; i >= 0; i--) {
			switch (colors[i]) {
			case 0:
				setChecked(R.id.color0, true);
				break;
			case 1:
				setChecked(R.id.color1, true);
				break;
			case -1:
				setChecked(R.id.observer, true);
				break;
			default:
				break;
			}
		}

		this.invitedInfo = info;

		new AlertDialog.Builder(InGame.this).setTitle(R.string.invite_player)
				.setOnCancelListener(inviteCancel).setView(joinView)
				.setNegativeButton(R.string.cancel, inviteCancel)
				.setPositiveButton(R.string.invite_player, inviteJoinListener)
				.show();

	}

	/** The invite join listener. */
	private DialogInterface.OnClickListener inviteJoinListener = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface Dialog, int whichButton) {

			int checkedId = ((RadioGroup) joinView
					.findViewById(R.id.menu_player)).getCheckedRadioButtonId();
			byte color = -1;// observer by default
			switch (checkedId) {
			case R.id.color0:
				color = 0;
				break;
			case R.id.color1:
				color = 1;
				break;
			}
			invitedInfo.player = color;
			invitedInfo.gameId = game.id;
			isCancelled = false;
			try {
				Log.d("InGame", "send invitation to player: " + invitedInfo
						+ ", position: " + invitedInfo.player);
				mService.sendInvitation(session, invitedInfo);
				AppHelper.showMessage(InGame.this,
						getText(R.string.invitation_sent));

			} catch (Exception e) {
				Log.e("Lobby", e);
			}
			confirmStartGame();

		}
	};

}