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
import java.util.List;

import org.aksonov.mages.connection.ConnectionManager;
import org.aksonov.mages.connection.CustomTypesRequestEntity;
import org.aksonov.mages.connection.DataReader;
import org.aksonov.mages.services.GamePlayer;
import org.aksonov.mages.services.IGamePlayer;
import org.aksonov.mages.tools.Errors;
import org.aksonov.tools.Log;
import org.mega.gasp.moods.CustomTypes;

import android.util.SparseIntArray;

// TODO: Auto-generated Javadoc
/**
 * Represents receiver part of Comet-based networking protocol.
 * 
 * @author Pavel
 */
public class InGameTask extends ResultTask {

	/** The host. */
	private String host;

	/** The player. */
	private IGamePlayer player;

	/** The actor session. */
	private int actorSession;

	/** The game id. */
	private int gameId;

	/** The last id. */
	private int lastId = 0;

	/** The confirmed ids. */
	private List<Integer> confirmedIds = null;

	/** The last move time. */
	private long lastMoveTime = 0;

	/** The custom types. */
	private final CustomTypes customTypes;

	/** The player ids. */
	private SparseIntArray playerIds;

	/** The to send. */
	private List<Object> toSend = null;

	/**
	 * Instantiates a new receiver task.
	 * 
	 * @param customTypes
	 *            the custom types
	 */
	public InGameTask(CustomTypes customTypes) {
		this.customTypes = customTypes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.aksonov.mages.services.gasp.ResultTask#execute()
	 */
	public int execute() {
		Log.d("ReceiverTask.execute", "begin");
		int attempts = 0;
		try {
			while (player.getState() == GamePlayer.GAME
					|| player.getState() == GamePlayer.WAITING) {
				attempts++;

				Object[] data = new Object[0];
				synchronized (toSend) {
					Log
							.d("InGameTask", "Prepare " + toSend.size()
									+ " objects");
					data = toSend.toArray();
					toSend.clear();
				}

				ConnectionManager.readPost(host, "InGameCombined", "aSID="
						+ actorSession, new CustomTypesRequestEntity(
						customTypes, data), new DataReader() {

					
					public Object read(DataInputStream stream) throws Exception {

						int size = stream.readShort();
						Log.d("InGameTask", "Reading " + size + " objects");
						for (int i = 0; i < size; i++) {
							ReceiverTask.eventsOf(stream, player, playerIds,
									customTypes, confirmedIds);
						}
						return null;
					}

				});
				Thread.sleep(500);
			}
		} catch (Exception e) {
			Log.e("GASPClient.inGame", e);
			try {
				player.onError(0, Errors.RECEIVE_GAME_FAILED_ERROR);
			} catch (Exception ee) {
				Log.e("InGameTask", ee);
			}
			return ResultTask.TIMEOUT_FAILURE;
		}

		try {
			if (player.getState() == GamePlayer.GAME) {
				player.onError(attempts, Errors.RECEIVE_GAME_FAILED_ERROR);
				Log.d("InGameTask", "Exiting: player state is "
						+ player.getState() + " attempts: " + attempts);
			} else {
				Log.d("InGameTask", "Exiting: player state is "
						+ player.getState());
			}
		} catch (Exception e) {
			Log.e("InGameTask", e);
		}

		return ResultTask.SUCCESS;
	}

	/**
	 * Start.
	 * 
	 * @param host
	 *            the host
	 * @param gameId
	 *            the game id
	 * @param actorSession
	 *            the actor session
	 * @param player
	 *            the player
	 * @param confirmedIds
	 *            the confirmed ids
	 * @param playerIds
	 *            the player ids
	 */
	public void start(final String host, final int gameId,
			final int actorSession, final IGamePlayer player,
			List<Object> toSend, final List<Integer> confirmedIds,
			SparseIntArray playerIds) {

		this.host = host;
		this.actorSession = actorSession;
		this.player = player;
		this.playerIds = playerIds;
		this.gameId = gameId;
		this.confirmedIds = confirmedIds;
		this.toSend = toSend;
		synchronized (player) {
			try {
				start();
			} catch (Exception e) {
				Log.e("ReceiverTask", e);
			}
		}
	}
}
