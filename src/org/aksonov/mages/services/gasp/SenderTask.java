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

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.util.List;

import org.aksonov.mages.Helper;
import org.aksonov.mages.services.GamePlayer;
import org.aksonov.mages.services.IGamePlayer;
import org.aksonov.mages.tools.Errors;
import org.aksonov.tools.Log;
import org.mega.gasp.moods.CustomTypes;

// TODO: Auto-generated Javadoc
/**
 * Represents sender part of comet-based networking protocol for GASP game
 * server.
 */
public class SenderTask extends ResultTask {
	
	/** The host. */
	private String host;
	
	/** The player. */
	private IGamePlayer player;
	
	/** The actor session. */
	private int actorSession;
	
	/** The game id. */
	private int gameId;
	
	/** The to send. */
	private List<Object> toSend = null;
	
	/** The confirmed ids. */
	private List<Integer> confirmedIds = null;
	
	/** The custom types. */
	private final CustomTypes customTypes;

	/**
	 * Instantiates a new sender task.
	 * 
	 * @param customTypes
	 *            the custom types
	 */
	public SenderTask(CustomTypes customTypes) {
		this.customTypes = customTypes;
	}

	/* (non-Javadoc)
	 * @see org.aksonov.mages.services.gasp.ResultTask#execute()
	 */
	public int execute() {
		// synchronized (player)
		{
			Log.d("SenderTask.execute", "begin");
			DataOutputStream dos = null;
			Object[] moves = null;
			try {
				while (player.getState() == GamePlayer.GAME || player.getState() == GamePlayer.WAITING) {
					HttpURLConnection inConn = GASPGameServerService
							.servletGetCometDataResponse(host, "InGameInput",
									actorSession);
					dos = new DataOutputStream(inConn.getOutputStream());

					long start = System.currentTimeMillis();
					long last = 0;
					while (System.currentTimeMillis() - start < 20000
							&& (player.getState() == GamePlayer.GAME || player.getState() == GamePlayer.WAITING)) {
						if (confirmedIds.size() > 0) {
							int[] ids = new int[0];
							synchronized (confirmedIds) {
								ids = new int[confirmedIds.size()];
								for (int i = 0; i < confirmedIds.size(); i++)
									ids[i] = confirmedIds.get(i);
								confirmedIds.clear();
							}
							dos.writeByte(2);
							dos.writeInt(ids.length);
							for (int i = 0; i < ids.length; i++) {
								Log.d("SenderTask", "Id: " + ids[i]);
								dos.writeInt(ids[i]);
							}
							dos.flush();
						}
						if (toSend.size() > 0) {
							last = System.currentTimeMillis();
							synchronized (toSend) {
								// signal do we have data or not
								moves = toSend.toArray();
								toSend.clear();
							}
							Log.d("GASP", "Sending objects, nb= "
									+ moves.length);
							dos.writeByte(1);
							Helper.encode(moves, dos, customTypes);
							dos.flush();
							moves = null;
						} else {
							if (System.currentTimeMillis() - last > 5000) {
								last = System.currentTimeMillis();
								// signal do we have data or not (needed
								// to avoid unexpected closing connection by
								// Android
								Log.d("SenderTask", "Sending empty packet");
								try {
									dos.writeByte(0);
									dos.flush();
								} catch (Exception e) {
									Log.e("SenderTask", e);
									break;
								}
							}

						}
						Thread.sleep(250);
					}
					dos.close();
					inConn.disconnect();
					dos = null;
				}
			} catch (Exception e) {
				Log.e("GASPClient.inGame, sender", e);
				if (moves != null) {
					Log
							.e("GASPClient.inGame, sender", "Moves: "
									+ moves.length);
					Log.d("SenderTask.execute", "Not all moves are sent ");
					synchronized (toSend) {
						for (int i = 0; i < moves.length; i++) {
							toSend.add(moves[i]);
						}
					}
					moves = null;
				}
				try {
					player.onError(0, Errors.SEND_GAME_FAILED_ERROR);
				} catch (Exception ee) {
					Log.e("SenderTask", ee);
				}
				return ResultTask.TIMEOUT_FAILURE;
			} finally {
				if (dos != null) {
					try {
						dos.close();
					} catch (Exception e) {
					}
				}
			}
			try {
			if (player.getState() == GamePlayer.GAME){
				player.onError(0, Errors.RECEIVE_GAME_FAILED_ERROR);
			}
			} catch (Exception e) {
				Log.e("SenderTask", e);
			}

			return ResultTask.SUCCESS;
		}
	}

	/**
	 * Starts the sender.
	 * 
	 * @param host
	 *            GASP host
	 * @param gameId
	 *            game id
	 * @param actorSession
	 *            actor session
	 * @param player
	 *            IGamePlayer instance
	 * @param toSend
	 *            data to send
	 * @param confirmedIds
	 *            confirmed move IDs
	 */
	public void start(final String host, final int gameId,
			final int actorSession, final IGamePlayer player,
			final List<Object> toSend, final List<Integer> confirmedIds) {

		this.host = host;
		this.toSend = toSend;
		this.actorSession = actorSession;
		this.player = player;
		this.confirmedIds = confirmedIds;
		this.gameId = gameId;
		synchronized (player) {
			synchronized (toSend) {
				toSend.clear();
				try {
					start();
				} catch (Exception e) {
					Log.e("SenderTask", e);
				}
			}
		}
	}
}
