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
import org.aksonov.mages.services.GamePlayer;
import org.aksonov.mages.services.IGamePlayerFactory;
import org.aksonov.mages.services.IGameServer;
import org.aksonov.mages.services.IPlayerLobbyListener;
import org.aksonov.mages.tools.AppHelper;
import org.aksonov.mages.tools.CancelListener;
import org.aksonov.mages.tools.GameSettingsAdapter;
import org.aksonov.mages.tools.PlayerInfoAdapter;
import org.aksonov.tools.Log;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

// TODO: Auto-generated Javadoc
/**
 * Activity for connection to game server, retrieving list of games and players,
 * create/join game sessions and accept/decline game invitation.
 * 
 * It connects to choosen game server with given configuration session id and
 * register IPlayerLobbyListener to logged player.
 * 
 * @author Pavel
 */
public class Lobby extends ListActivity {
	
	/** The player. */
	private GamePlayer player = new GamePlayer();
	
	/** The Constant CREATE_ID. */
	private static final int CREATE_ID = 1;
	
	/** The Constant HELP_ID. */
	private static final int HELP_ID = 2;
	
	/** The Constant QUIT_ID. */
	private static final int QUIT_ID = 3;
	
	/** The Constant START. */
	private static final int START = 2;
	
	/** The Constant STOP. */
	private static final int STOP = 3;
	
	/** The Constant UPDATE. */
	private static final int UPDATE = 4;
	
	/** The Constant UPDATE_PLAYERS. */
	private static final int UPDATE_PLAYERS = 5;
	
	/** The Constant ON_JOIN. */
	private static final int ON_JOIN = 8;
	
	/** The Constant ON_ERROR. */
	private static final int ON_ERROR = 9;
	
	/** The Constant INVITATION. */
	private static final int INVITATION = 10;
	
	/** The m adapter. */
	private GameSettingsAdapter mAdapter;
	
	/** The m player adapter. */
	private PlayerInfoAdapter mPlayerAdapter;
	
	/** The session. */
	private int session;
	
	/** The is cancelled. */
	private boolean isCancelled = false;
	
	/** The create view. */
	private View createView;
	
	/** The join view. */
	private View joinView;
	
	/** The is joined. */
	boolean isJoined = false;
	
	/** The m waiting Dialog. */
	private ProgressDialog mWaitingDialog;
	
	/** The board layout. */
	private int boardLayout;
	
	/** The info. */
	private PlayerInfo info;
	
	/** The game. */
	private GameSettings game;

	/** The m service. */
	protected IGameServer mService = null;
	
	/** The m factory service. */
	protected IGamePlayerFactory mFactoryService = null;

	/** The join listener. */
	private DialogInterface.OnClickListener joinListener = new DialogInterface.OnClickListener() {
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
			info.player = color;
			joinCall(game.id);
		}
	};

	/**
	 * Join call.
	 * 
	 * @param gameId
	 *            the game id
	 */
	private void joinCall(int gameId) {
		isCancelled = false;
		mWaitingDialog = ProgressDialog.show(Lobby.this, null,
				getText(R.string.joining_game), true, true,
				new OnCancelListener() {
					
					public void onCancel(DialogInterface Dialog) {
						AppHelper.showMessage(Lobby.this,
								getText(R.string.cancelled_action));
						try {
							player.setState(GamePlayer.LOBBY);
						} catch (Exception e) {
							Log.e("Lobby", e);
						}
						isCancelled = true;
					}

				});
		try {
			mService.joinGame(gameId, info);
		} catch (Exception e) {
			Log.e("Lobby", e);
		}

	}

	/** The create listener. */
	private OnClickListener createListener = new OnClickListener() {
		public void onClick(View v) {
			createGame();
		}
	};

	/** Represents connection for player factory service. */
	protected ServiceConnection mFactoryConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			mFactoryService = IGamePlayerFactory.Stub
					.asInterface((IBinder) service);
			if (mFactoryService == null) {
				throw new IllegalArgumentException(
						"Cannot bind IGamePlayerFactory instance");
			}
			Log.d("Lobby", "IGamePlayerFactory service binded");

			// wait game server is instantiated
			while (mService == null) {
				Helper.sleep(100);
			}
			Log.d("Lobby", "IGamePlayerFactory service binded!!!");

			// export players to game server
			try {
				mFactoryService.exportPlayers(0, session, mService);
			} catch (Exception e) {
				Log.e("Lobby", e);
			}

		}

		public void onServiceDisconnected(ComponentName className) {
			mFactoryService = null;
		}
	};

	/** Represents connection to game server service. */
	protected ServiceConnection mConnection = new ServiceConnection() {

		public void onServiceConnected(ComponentName className, IBinder service) {
			mService = IGameServer.Stub.asInterface((IBinder) service);

			if (mService == null) {
				throw new IllegalArgumentException("Cannot bind IGameServer");
			}
			try {
				if (!mService.isConfigured(session)) {
					AppHelper.showMessage(Lobby.this,
							"Server is not correctly configured!");
					finish();
					return;
				}
				isCancelled = false;
				mWaitingDialog = ProgressDialog.show(Lobby.this, null,
						getText(R.string.logging), true, true,
						new OnCancelListener() {

							
							public void onCancel(DialogInterface Dialog) {
								AppHelper.showMessage(Lobby.this,
										getText(R.string.cancelled_action));
								isCancelled = true;
								finish();
							}

						});
				mService.connect(session, player);

			} catch (Exception e) {
				AppHelper.showMessage(Lobby.this, "Error!");
				Log.e("Lobby", e);
				finish();
				return;
			}
		}

		public void onServiceDisconnected(ComponentName className) {
			mService = null;
		}
	};

	/** The listener. */
	private IPlayerLobbyListener listener = new IPlayerLobbyListener.Stub() {
		
		public void onLogin(PlayerInfo info) throws RemoteException {
			if (!isCancelled) {
				AppHelper.dismiss(mWaitingDialog);
				isCancelled = true;
				Log.d("Lobby", "Player " + info.username
						+ "successfully logged! ");
				player.setState(GamePlayer.LOBBY);
				player.setInfo(info);
				mService.requestGameList(info.id);
				Lobby.this.info = info;
				handler.sendMessage(handler.obtainMessage(START));
			}
		}

		
		public void onJoin(PlayerInfo info) throws RemoteException {
			if (info.id == Lobby.this.info.id) {
				Log.d("Lobby", "I've been joined to game: " + info.gameId);
				if (!isJoined) {
					isJoined = true;
					handler.sendMessage(handler.obtainMessage(ON_JOIN,
							info.gameId, 0, info));
				}
			}
		}

		
		public void onError(int attempt, int code) throws RemoteException {
			handler.sendMessage(handler.obtainMessage(ON_ERROR, attempt, code));
		}

		
		public void onPlayerList(List<PlayerInfo> plList)
				throws RemoteException {
			handler.sendMessage(handler.obtainMessage(UPDATE_PLAYERS, plList));

		}

		
		public void onGameList(List<GameSettings> gameList) {
			handler.sendMessage(handler.obtainMessage(UPDATE, gameList));
		}

		
		public void onInvitation(PlayerInfo info, int playerId)
				throws RemoteException {
			Log.d("Lobby", "onInvitiation! " + info);
			handler.sendMessage(handler.obtainMessage(INVITATION, info));
		}

	};

	/** The handler. */
	private Handler handler = new Handler() {
		
		public void handleMessage(Message msg) {
			try {
				switch (msg.what) {
				case ON_JOIN:
					if (!isCancelled) {
						AppHelper.dismiss(mWaitingDialog);
						isCancelled = true;
						isJoined = true;
						removeMessages(ON_JOIN);
						startGame(msg.arg1);
					}

					break;
				case ON_ERROR:
					Log.e("Lobby", "Error: " + msg.arg2);
					if (mWaitingDialog != null && !isCancelled) {
						AppHelper.dismiss(mWaitingDialog);
					}
					isCancelled = true;
					AppHelper.showError(Lobby.this, msg.arg1, msg.arg2, cancel);

					break;

				case STOP:
					// player.setState(GamePlayer.GAME);
					break;
				case START:
					player.setState(GamePlayer.LOBBY);
					setTitle(getText(R.string.lobby) + " ["
							+ player.getInfo().username + "]");
					break;
				case INVITATION:
					final PlayerInfo plInfo = (PlayerInfo) msg.obj;
					List<GameSettings> games = mAdapter.getList();
					int found = -1;
					for (int i = 0; i < games.size(); i++) {
						if (games.get(i).id == plInfo.gameId) {
							found = i;
							Lobby.this.game = games.get(i);
						}
					}
					info.gameId = plInfo.id;
					info.player = plInfo.player;

					if (found < 0) {
						Log.d("Lobby", "Invitation: game id is absent: "
								+ plInfo.gameId);
						break;
					}
					Log.d("Lobby", "Invitation: " + plInfo + " color " + plInfo.player);

					new AlertDialog.Builder(Lobby.this).setTitle(
							R.string.invitation).setMessage(
							getText(R.string.join_confirm)
									+ " " + Lobby.this.game).setNegativeButton(
							R.string.cancel,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface Dialog,
										int whichButton) {
								}
							}).setPositiveButton(R.string.join,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface Dialog,
										int whichButton) {

									joinCall(info.gameId);
								}
							}).show();
					break;
				case UPDATE:
					mAdapter.updateList((List<GameSettings>) msg.obj);
					break;
				case UPDATE_PLAYERS:
					mPlayerAdapter.updateList((List<PlayerInfo>) msg.obj);
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
	 * Called when your activity's options menu needs to be created.
	 * 
	 * @param menu
	 *            the menu
	 * 
	 * @return true, if on create options menu
	 */
	
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		menu.add(0, CREATE_ID, 0, R.string.create_game);
		// menu.add(0, JOIN_ID, R.string.join_game);
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
		case CREATE_ID:
			createGame();
			break;
		case QUIT_ID:
			finish();
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * Displays creation game session Dialogs.
	 */
	protected void createGame() {
		new AlertDialog.Builder(Lobby.this).setTitle(R.string.game_parameters)
				.setView(createView).setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface Dialog,
									int whichButton) {
							}
						}).setPositiveButton(R.string.create,
						DialogCreateListener).show();

	}

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
	 * Displays join game session Dialog.
	 * 
	 * @param colors
	 *            list of available player positions
	 * @param title
	 *            title of the Dialog
	 * @param button
	 *            text of the positive button
	 * @param listener
	 *            listener
	 */
	protected void joinGame(byte[] colors, CharSequence title, int button,
			DialogInterface.OnClickListener listener) {
		setChecked(R.id.color0, false);
		setChecked(R.id.color0, false);
		setChecked(R.id.observer, false);
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

		new AlertDialog.Builder(Lobby.this).setTitle(title).setView(joinView)
				.setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface Dialog,
									int whichButton) {
							}
						}).setPositiveButton(button, listener).show();

	}

	/**
	 * This method initiates join Dialog.
	 * 
	 * @param l
	 *            the l
	 * @param v
	 *            the v
	 * @param position
	 *            the position
	 * @param id
	 *            the id
	 */
	
	protected void onListItemClick(ListView l, View v, int position, long id) {
		game = (GameSettings) mAdapter.getItem(position);
		try {
			info = player.getInfo();
			joinGame(AppHelper.getAvailablePlayers(game, info.id),
					getText(R.string.game_parameters), R.string.join,
					joinListener);
		} catch (Exception e) {
			Log.e("Lobby", e);
		}
	}

	/**
	 * Method just stops the lobby requests (to get game and player lists).
	 */
	
	protected void onPause() {
		super.onPause();
		handler.sendMessage(handler.obtainMessage(STOP));
	}

	/**
	 * Method restores state and initiate retrieving of game and player lists.
	 */
	
	protected void onResume() {
		try {
			isJoined = false;
			super.onResume();
			if (player.getState() != GamePlayer.LOGIN) {
				player.setState(GamePlayer.LOBBY);
			}
		} catch (Exception e) {
			Log.e("onResume", e);
		}
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onStop()
	 */
	
	protected void onStop() {
		super.onStop();
		handler.sendMessage(handler.obtainMessage(STOP));
	}

	/**
	 * Unregister lobby listener, stops the services, disconnect user from the
	 * game server.
	 */
	
	public void onDestroy() {
		handler.obtainMessage(STOP);
		try {
			player.unregisterLobbyListener(listener);
			player.setState(GamePlayer.QUIT);
			mService.disconnect(session);
		} catch (Exception e) {
			Log.e("Lobby", e);
		}
		unbindService(mConnection);
		unbindService(mFactoryConnection);
		super.onDestroy();
	}

	/**
	 * Called when the activity is first created. This method initializes all
	 * used views, register needed callbacks for buttons
	 * 
	 * @param icicle
	 *            the icicle
	 */
	
	
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		LayoutInflater inflate = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		createView = inflate.inflate(R.layout.creategame, null);
		joinView = inflate.inflate(R.layout.joingame, null);
		boardLayout = AppHelper.getBoardLayout(getIntent());

		setContentView(R.layout.lobby);
		ComponentName service = AppHelper.getService(getIntent());
		if (service == null) {
			AppHelper.showMessage(this, "null service");
			finish();
			return;
		}
		Intent intent = AppHelper.createServiceIntent(service);
		Log.d("Lobby", "Calling service: " + intent);
		bindService(intent, mConnection, 0);
		bindService(new Intent(AppHelper.PLAYER_FACTORY_ACTION)
				.setType(getIntent().getType()), mFactoryConnection, 0);

		// Set up our adapter
		mAdapter = new GameSettingsAdapter(this);
		// ListView view = (ListView) findViewById(R.id.gameList);
		// view.setAdapter(mAdapter);
		// view.setOnClickListener(gameJoinListener);
		setListAdapter(mAdapter);

		mPlayerAdapter = new PlayerInfoAdapter(this);
		ListView playerList = (ListView) findViewById(R.id.playerList);
		playerList.setAdapter(mPlayerAdapter);

		session = AppHelper.getSessionId(getIntent());
		if (session == -1) {
			AppHelper.showMessage(this, "Session is not passed");
			finish();
		}

		try {
			player.registerLobbyListener(listener);
		} catch (Exception e) {
			Log.e("Lobby", e);
		}

		Button button = (Button) findViewById(R.id.create);
		button.setOnClickListener(createListener);

	}

	/** The Dialog create listener. */
	private DialogInterface.OnClickListener DialogCreateListener = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface Dialog, int whichButton) {

			if (mService == null) {
				AppHelper.showMessage(Lobby.this, "Game server is not found");
				return;
			}
			int timePerGame = 0, timePerMove = 0, moveIncr = 0;
			String val = ((EditText) createView
					.findViewById(R.id.time_per_game)).getText().toString();
			try {
				timePerGame = Integer.parseInt(val) * 60 * 1000;
			} catch (Exception e) {
				AppHelper.showMessage(Lobby.this,
						getText(R.string.time_per_game) + " is not valid: "
								+ val);
				return;
			}

			val = ((EditText) createView.findViewById(R.id.time_per_move))
					.getText().toString();
			try {
				timePerMove = Integer.parseInt(val) * 60 * 1000;
			} catch (Exception e) {
				AppHelper.showMessage(Lobby.this,
						getText(R.string.time_per_move) + " is not valid: "
								+ val);
				return;
			}

			val = ((EditText) createView.findViewById(R.id.move_incr))
					.getText().toString();
			try {
				moveIncr = Integer.parseInt(val) * 1000;
			} catch (Exception e) {
				AppHelper.showMessage(Lobby.this, getText(R.string.move_incr)
						+ " is not valid: " + val);
				return;
			}

			int checkedId = ((RadioGroup) createView
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

			if (((CheckBox) createView.findViewById(R.id.no_limit)).isChecked()) {
				timePerMove = 0;
				timePerGame = 0;
			}

			boolean rated = false;
			if (((CheckBox) createView.findViewById(R.id.rated)).isChecked()) {
				rated = true;
			}

			try {
				PlayerInfo info = player.getInfo();
				info.player = color;
				player.setInfo(info);

				game = GameSettings.create();
				game.maxActors = 2;
				game.minActors = 2;
				game.timePerGame = timePerGame;
				game.timePerMove = timePerMove;
				game.moveIncr = moveIncr;
				game.rated = rated;

				isCancelled = false;
				mWaitingDialog = ProgressDialog.show(Lobby.this, null,
						getText(R.string.creating_game), true, true,
						new OnCancelListener() {

							
							public void onCancel(DialogInterface Dialog) {
								AppHelper.showMessage(Lobby.this,
										getText(R.string.cancelled_action));
								isCancelled = true;
								try {
									player.setState(GamePlayer.LOBBY);
								} catch (Exception e) {
									Log.e("InLobby", e);
								}

							}

						});

				mService.createGame(session, game);
			} catch (Exception e) {
				Log.e("Lobby", e);
			}
		}
	};

	/**
	 * Start game.
	 * 
	 * @param gameId
	 *            the game id
	 */
	private void startGame(int gameId) {
		try {
			PlayerInfo info = player.getInfo();
			Log.d("Lobby", "Starting ingame activity, game id = " + gameId);
			game.id = gameId;
			if (!game.players.contains(info)) {
				game.players.add(info);
			}
			((MagesApplication) getApplication()).game = game;
			((MagesApplication) getApplication()).players = mPlayerAdapter
					.getPlayers();
			startActivity(AppHelper.createInGameIntent(AppHelper
					.getService(getIntent()), session, gameId, getIntent()
					.getType(), boardLayout, info.player));
		} catch (Exception e) {
			Log.e("Lobby", e);
		}

	}

	/** The cancel. */
	private CancelListener cancel = new CancelListener() {
		public void onCancel() {
			finish();
		}
	};

}