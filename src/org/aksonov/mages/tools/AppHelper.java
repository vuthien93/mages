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
package org.aksonov.mages.tools;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.aksonov.mages.Helper;
import org.aksonov.mages.R;
import org.aksonov.mages.entities.GameSettings;
import org.aksonov.mages.entities.PlayerInfo;
import org.aksonov.tools.Log;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.widget.Toast;

// TODO: Auto-generated Javadoc
/**
 * The Class AppHelper.
 */
public class AppHelper {
	
	/** The Constant LABEL. */
	public static final String LABEL = "label";
	
	/** The Constant INFO. */
	public static final String INFO = "info";
	
	/** The Constant INTENT. */
	public static final String INTENT = "intent";
	
	/** The Constant HAS_CONFIG. */
	public static final String HAS_CONFIG = "has_config";

	/** The Constant GAME_ACTION. */
	public static final String GAME_ACTION = "mages.intent.action.GAME";
	
	/** The Constant PLAYER_ACTION. */
	public static final String PLAYER_ACTION = "mages.intent.action.PLAY";
	
	/** The Constant SERVE_ACTION. */
	public static final String SERVE_ACTION = "mages.intent.action.SERVE";
	
	/** The Constant CONFIGURE_ACTION. */
	public static final String CONFIGURE_ACTION = "mages.intent.action.CONFIGURE";
	
	/** The Constant PLAYER_FACTORY_ACTION. */
	public static final String PLAYER_FACTORY_ACTION = "mages.intent.action.PLAYER_FACTORY";

	/** The Constant LOBBY_ACTION. */
	public static final String LOBBY_ACTION = "mages.intent.action.LOBBY";
	
	/** The Constant IN_GAME_ACTION. */
	public static final String IN_GAME_ACTION = "mages.intent.action.IN_GAME";

	// public static final String JOIN_GAME_ACTION =
	// "org.aksonov.mages.JOIN_GAME";
	/** The Constant LOGIN_ACTION. */
	public static final String LOGIN_ACTION = "org.aksonov.mages.LOGIN";

	/** The Constant GAME_APP_ID_KEY. */
	public static final String GAME_APP_ID_KEY = "com.aksonov.gasp.client.game.app_id";
	
	/** The Constant GAME_URI_KEY. */
	public static final String GAME_URI_KEY = "com.aksonov.gasp.client.game.uri";
	
	/** The Constant USERNAME_KEY. */
	public static final String USERNAME_KEY = "com.aksonov.gasp.client.game.username";
	
	/** The Constant GAME_ID_KEY. */
	public static final String GAME_ID_KEY = "com.aksonov.gasp.client.game.game_id";
	
	/** The Constant BOARD_LAYOUT_ID_KEY. */
	public static final String BOARD_LAYOUT_ID_KEY = "com.aksonov.gasp.client.game.layout_id";
	
	/** The Constant TAG. */
	public static final String TAG = "GASP";
	
	/** The Constant SESSION_ID_KEY. */
	public static final String SESSION_ID_KEY = "com.aksonov.gasp.client.game.session_id";
	
	/** The Constant TYPE_KEY. */
	public static final String TYPE_KEY = "com.aksonov.gasp.client.game.type";
	
	/** The Constant ACTOR_SESSION_ID_KEY. */
	public static final String ACTOR_SESSION_ID_KEY = "com.aksonov.gasp.client.game.actor_session_id";
	
	/** The Constant PLAYER_KEY. */
	public static final String PLAYER_KEY = "com.aksonov.gasp.client.game.player";
	
	/** The Constant PLAYER_ID_KEY. */
	public static final String PLAYER_ID_KEY = "com.aksonov.gasp.client.game.player_id";
	
	/** The Constant SERVICE_KEY. */
	public static final String SERVICE_KEY = "com.aksonov.gasp.client.game.service";
	
	/** The Constant SETTINGS_TIME_PER_MOVE. */
	public static final String SETTINGS_TIME_PER_MOVE = "com.aksonov.gasp.client.game.time_per_move";
	
	/** The Constant SETTINGS_TIME_PER_GAME. */
	public static final String SETTINGS_TIME_PER_GAME = "com.aksonov.gasp.client.game.time_per_game";

	/** The Constant CREATE_METHOD. */
	public static final String CREATE_METHOD = "create";

	/**
	 * Creates the configuration intent.
	 * 
	 * @param component
	 *            the component
	 * @param type
	 *            the type
	 * 
	 * @return the intent
	 */
	public static Intent createConfigurationIntent(ComponentName component,
			String type) {
		return new Intent(CONFIGURE_ACTION).addCategory(
				component.getClassName()).putExtra(SERVICE_KEY,
				component.flattenToString()).putExtra(TYPE_KEY, type);
	}

	/**
	 * Creates the service intent.
	 * 
	 * @param component
	 *            the component
	 * 
	 * @return the intent
	 */
	public static Intent createServiceIntent(ComponentName component) {
		return new Intent(SERVE_ACTION).setComponent(component);
	}

	/**
	 * Creates the lobby intent.
	 * 
	 * @param service
	 *            the service
	 * @param session
	 *            the session
	 * @param type
	 *            the type
	 * @param layout
	 *            the layout
	 * 
	 * @return the intent
	 */
	public static Intent createLobbyIntent(ComponentName service, int session,
			String type, int layout) {
		return new Intent(LOBBY_ACTION).putExtra(SESSION_ID_KEY, session)
				.setType(type).putExtra(SERVICE_KEY, service.flattenToString())
				.putExtra(BOARD_LAYOUT_ID_KEY, layout);
	}

	/**
	 * Creates the in game intent.
	 * 
	 * @param service
	 *            the service
	 * @param session
	 *            the session
	 * @param gameId
	 *            the game id
	 * @param type
	 *            the type
	 * @param layout
	 *            the layout
	 * @param player
	 *            the player
	 * 
	 * @return the intent
	 */
	public static Intent createInGameIntent(ComponentName service, int session,
			int gameId, String type, int layout, byte player) {
		return new Intent(IN_GAME_ACTION).putExtra(GAME_ID_KEY, gameId)
				.putExtra(SESSION_ID_KEY, session).setType(type).putExtra(
						SERVICE_KEY, service.flattenToString()).putExtra(
						BOARD_LAYOUT_ID_KEY, layout).putExtra(PLAYER_KEY,
						player);
	}

	/**
	 * Gets the service.
	 * 
	 * @param intent
	 *            the intent
	 * 
	 * @return the service
	 */
	public static ComponentName getService(Intent intent) {
		String name = intent.getExtras().getString(SERVICE_KEY);
		if (name == null)
			return null;
		return ComponentName.unflattenFromString(name);
	}

	/**
	 * Gets the app id.
	 * 
	 * @param intent
	 *            the intent
	 * 
	 * @return the app id
	 */
	public static int getAppId(Intent intent) {
		return intent.getIntExtra(GAME_APP_ID_KEY, 0);
	}

	/**
	 * Gets the board layout.
	 * 
	 * @param intent
	 *            the intent
	 * 
	 * @return the board layout
	 */
	public static int getBoardLayout(Intent intent) {
		return intent.getIntExtra(BOARD_LAYOUT_ID_KEY, 0);
	}

	/**
	 * Gets the type.
	 * 
	 * @param intent
	 *            the intent
	 * 
	 * @return the type
	 */
	public static String getType(Intent intent) {
		return intent.getStringExtra(TYPE_KEY);
	}

	/**
	 * Gets the actor session id.
	 * 
	 * @param intent
	 *            the intent
	 * 
	 * @return the actor session id
	 */
	public static int getActorSessionId(Intent intent) {
		return intent.getIntExtra(ACTOR_SESSION_ID_KEY, 0);
	}

	/**
	 * Gets the game id.
	 * 
	 * @param intent
	 *            the intent
	 * 
	 * @return the game id
	 */
	public static int getGameId(Intent intent) {
		return intent.getIntExtra(GAME_ID_KEY, 0);
	}

	/**
	 * Gets the player id.
	 * 
	 * @param intent
	 *            the intent
	 * 
	 * @return the player id
	 */
	public static int getPlayerId(Intent intent) {
		return intent.getIntExtra(PLAYER_ID_KEY, 0);
	}

	/**
	 * Gets the uri.
	 * 
	 * @param intent
	 *            the intent
	 * 
	 * @return the uri
	 */
	public static String getUri(Intent intent) {
		return intent.getStringExtra(GAME_URI_KEY);
	}

	/**
	 * Gets the player.
	 * 
	 * @param intent
	 *            the intent
	 * 
	 * @return the player
	 */
	public static byte getPlayer(Intent intent) {
		return intent.getByteExtra(PLAYER_KEY, (byte) 0);
	}

	/**
	 * Gets the session id.
	 * 
	 * @param intent
	 *            the intent
	 * 
	 * @return the session id
	 */
	public static int getSessionId(Intent intent) {
		return intent.getIntExtra(SESSION_ID_KEY, -1);
	}

	/**
	 * Gets the settings.
	 * 
	 * @param intent
	 *            the intent
	 * 
	 * @return the settings
	 */
	public static GameSettings getSettings(Intent intent) {
		GameSettings settings = GameSettings.create();
		// have to do it because Android doesn't pass custom objects via intent
		// now
		settings.setTimePerGame(intent.getIntExtra(SETTINGS_TIME_PER_GAME, 0));
		settings.setTimePerMove(intent.getIntExtra(SETTINGS_TIME_PER_MOVE, 0));
		return settings;
	}

	/**
	 * Gets the username.
	 * 
	 * @param intent
	 *            the intent
	 * 
	 * @return the username
	 */
	public static String getUsername(Intent intent) {
		return intent.getStringExtra(USERNAME_KEY);
	}

	/**
	 * Show message.
	 * 
	 * @param context
	 *            the context
	 * @param msg
	 *            the msg
	 */
	public static void showMessage(Context context, CharSequence msg) {
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}

	/**
	 * Dismiss.
	 * 
	 * @param Dialog
	 *            the Dialog
	 */
	public static void dismiss(final Dialog Dialog) {
		if (Dialog != null) {
			new Thread(new Runnable() {
				public void run() {
					Helper.sleep(1000);
					if (Dialog != null) {
						Log.d("ProgressHandler", "Dismissing Dialog");
						Dialog.dismiss();
					}
				}
			}).start();
		}
	}

	/**
	 * Show error.
	 * 
	 * @param context
	 *            the context
	 * @param attempt
	 *            the attempt
	 * @param code
	 *            the code
	 * @param okListener
	 *            the ok listener
	 */
	public static void showError(Context context, int attempt, int code,
			CancelListener okListener) {
		// AppHelper.showMessage(context,
		// context.getText(Errors.getMessageId(code)));
		new AlertDialog.Builder(context).setTitle(R.string.error).setMessage(
				Errors.getMessageId(code)).setIcon(R.drawable.warning)
				.setOnCancelListener(okListener).setPositiveButton(R.string.ok, okListener).show();
	}

	/**
	 * Gets the available players.
	 * 
	 * @param settings
	 *            the settings
	 * @param playerId
	 *            the player id
	 * 
	 * @return the available players
	 */
	public static byte[] getAvailablePlayers(GameSettings settings, int playerId) {
		boolean[] players = new boolean[settings.maxActors];
		for (int i = 0; i < players.length; i++) {
			players[i] = false;
		}
		for (int i = 0; i < settings.players.size(); i++) {
			PlayerInfo info = settings.players.get(i);
			if (info.id == playerId) {
				return new byte[] { info.player };
			}
			byte player = info.player;
			if (player >= 0 && player < settings.maxActors) {
				players[player] = true;
			}
		}

		byte[] result = new byte[5];
		for (int i = 0; i < players.length; i++) {
			result[i] = -1;
		}
		int size = 0;
		for (int i = 0; i < players.length; i++) {
			if (!players[i]) {
				result[size++] = (byte) i;
			}
		}

		return result;
	}

	/** The Constant displayNameComparator. */
	private final static Comparator<Map> displayNameComparator = new Comparator<Map>() {
		private final Collator collator = Collator.getInstance();

		public int compare(Map map1, Map map2) {
			return collator.compare(map1.get(LABEL), map2.get(LABEL));
		}
	};

	/**
	 * Activity intent.
	 * 
	 * @param pkg
	 *            the pkg
	 * @param componentName
	 *            the component name
	 * 
	 * @return the intent
	 */
	private static Intent activityIntent(String pkg, String componentName) {
		Intent result = new Intent();
		result.setClassName(pkg, componentName);
		return result;
	}

	/**
	 * Gets the activity list.
	 * 
	 * @param context
	 *            the context
	 * @param intent
	 *            the intent
	 * 
	 * @return the activity list
	 */
	public static List<Hashtable<String, Object>> getActivityList(
			Context context, Intent intent) {
		List<Hashtable<String, Object>> result = new ArrayList<Hashtable<String, Object>>();
		PackageManager pm = context.getPackageManager();
		List<ResolveInfo> list = pm.queryIntentActivities(intent
				.addCategory("android.intent.category.DEFAULT"), 0);
		for (ResolveInfo info : list) {
			Hashtable<String, Object> h = new Hashtable<String, Object>();

			CharSequence labelSeq = info.activityInfo.loadLabel(pm);
			String label = labelSeq != null ? labelSeq.toString()
					: info.activityInfo.name;

			h.put(LABEL, label);
			h.put(INTENT, activityIntent(
					info.activityInfo.applicationInfo.packageName,
					info.activityInfo.name));
			result.add(h);
		}
		Collections.sort(result, displayNameComparator);
		return result;

	}

	/**
	 * Gets the service list.
	 * 
	 * @param context
	 *            the context
	 * @param intent
	 *            the intent
	 * 
	 * @return the service list
	 */
	public static List<Hashtable<String, Object>> getServiceList(
			Context context, Intent intent) {
		List<Hashtable<String, Object>> result = new ArrayList<Hashtable<String, Object>>();
		PackageManager pm = context.getPackageManager();
		List<ResolveInfo> list = pm.queryIntentServices(intent, 0);
		for (ResolveInfo info : list) {
			Hashtable<String, Object> h = new Hashtable<String, Object>();

			CharSequence labelSeq = info.serviceInfo.loadLabel(pm);
			String label = labelSeq != null ? labelSeq.toString()
					: info.serviceInfo.name;

			h.put(LABEL, label);
			h.put(INTENT, new Intent(intent).setComponent(new ComponentName(
					info.serviceInfo.packageName, info.serviceInfo.name)));
			h.put(HAS_CONFIG, pm.queryIntentActivities(
					new Intent(AppHelper.CONFIGURE_ACTION).addCategory(
							info.serviceInfo.name).addCategory(
							"android.intent.category.DEFAULT"), 0).size() > 0);
			result.add(h);
		}
		Collections.sort(result, displayNameComparator);
		return result;

	}
}
