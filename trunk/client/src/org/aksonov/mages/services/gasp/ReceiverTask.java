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
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import org.aksonov.mages.Helper;
import org.aksonov.mages.entities.Custom;
import org.aksonov.mages.entities.GameSettings;
import org.aksonov.mages.entities.Move;
import org.aksonov.mages.entities.Note;
import org.aksonov.mages.entities.PlayerInfo;
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
public class ReceiverTask extends ResultTask {
	
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

	/**
	 * Instantiates a new receiver task.
	 * 
	 * @param customTypes
	 *            the custom types
	 */
	public ReceiverTask(CustomTypes customTypes) {
		this.customTypes = customTypes;
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
	 * Treats the events of the servlet response data input stream.
	 * 
	 * @param dis
	 *            the data input stream
	 * @param callback
	 *            the callback
	 * 
	 * @return true, if events of
	 * 
	 * @throws IOException
	 */
	public static boolean eventsOf(DataInputStream dis, IGamePlayer player, SparseIntArray playerIds, CustomTypes customTypes, List<Integer> confirmedIds) {
		try {
			Log.d("GASP", "eventsOf start");
			// byte nb = dis.readByte();
			// Log.d("GASP", "reading byte = " + nb);
			byte type;

			// while (nb > 0) {
			type = dis.readByte(); // get the type of the events
			// 13 means CRLF - server occasionally closes chunked streaming
			if (type == 13) {
				dis.readByte();
				return false;
			}
			int id = dis.readInt();
			// Log.d("ReceiverTask", "type of event " + type + " event id: " +
			// id);
			PlayerInfo info = player.getInfo();

			int aSID = dis.readShort();
//			if (aSID == 0) {
//				throw new IllegalArgumentException("Incoming aSID is 0!");
//			}
			int playerId = playerIds.get(aSID, -1);

			switch (type) {
			case 1: // JoinEvent
				// player.onJoin(dis.readShort(), dis.readUTF());
				dis.readUTF();
				break;
			case 2: // StartEvent
				// player.onStart(gameId,
				break;
			case 3: // EndEvent
				if (playerId == -1) {
					throw new IllegalArgumentException("Unknown aSID " + aSID);
				}

				player.onEnd(playerId);
				break;
			case 4: // QuitEvent
				if (playerId == -1) {
					throw new IllegalArgumentException("Unknown aSID " + aSID);
				}

				player.onQuit(playerId);
				break;
			case 5: // DataEvent
				Object[] data = Helper.decode(dis, customTypes);
				for (int i = 0; i < data.length; i++) {
					Log.d("GASP", "=========Object received: " + data[i]);
					if (data[i] instanceof GameSettings) {
						GameSettings game = (GameSettings) data[i];
						for (int j = 0; j < game.players.size(); j++) {
							PlayerInfo pinfo = game.players.get(j);
							if (pinfo.arg1 == 0) {
								throw new IllegalArgumentException(
										"Player actor session id is not set, player: "
												+ pinfo.id);
							}

							playerIds.put(pinfo.arg1, pinfo.id);

						}
						playerId = playerIds.get(aSID, -1);
						if (playerId == -1) {
							throw new IllegalArgumentException("Unknown aSID "
									+ aSID);
						}
						player.onStart((GameSettings) data[i], playerId);
					} else if (data[i] instanceof Move) {
						//lastId = ((Move) data[i]).id;
						//lastMoveTime = System.currentTimeMillis();
						if (playerId == 1) {
							throw new IllegalArgumentException("Unknown aSID "
									+ aSID);
						}
						player.onMove(playerId, (Move) data[i]);
					} else if (data[i] instanceof Note) {
						player.onNote(playerId, (Note) data[i]);
					} else if (data[i] instanceof PlayerInfo) {
						PlayerInfo pinfo = (PlayerInfo) data[i];
						if (pinfo.arg1 == 0) {
							throw new IllegalArgumentException(
									"Player actor session id is not set, player: "
											+ pinfo.id);
						}
						playerIds.put(pinfo.arg1, pinfo.id);
						playerId = playerIds.get(aSID, -1);
						if (playerId == -1) {
							throw new IllegalArgumentException("Unknown aSID "
									+ aSID);
						}
						player.onJoin((PlayerInfo) data[i]);
					} else if (data[i] instanceof Serializable){
						if (playerId == -1) {
							throw new IllegalArgumentException("Unknown aSID "
									+ aSID);
						}
						player.onData(playerId, Custom
								.create((Serializable) data[i]));
					} else {
						if (data[i]!= null){
							Log.d("ReceiverTask", "Unknown data: " + data[i]);
						}
					}
				}
				break;
			default:
				break;
			}

			synchronized (confirmedIds) {
				Log.d("ReceiverTask", "add confirmation id to queue " + id);
				confirmedIds.add(id);
			}

			// nb--;
			// }
		} catch (Exception e) {
			Log.e("GASPClient.eventsOf", e);
			return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see org.aksonov.mages.services.gasp.ResultTask#execute()
	 */
	public int execute() {
		Log.d("ReceiverTask.execute", "begin");
		DataInputStream dis = null;
		int attempts = 0;
		try {
			while (player.getState() == GamePlayer.GAME || player.getState() == GamePlayer.WAITING) {
				attempts++;
				dis = GASPGameServerService.servletSendCometDataRequest(host,
						"InGameOutput?aSID="+actorSession);
				Log.d("GASP", "Input stream is: " + dis);

				long start = System.currentTimeMillis();
				while (/* System.currentTimeMillis() - start < 20000 && */player
						.getState() == GamePlayer.GAME) {
					// Log.d("GASP", "Available bytes: " + dis.available());
					if (System.currentTimeMillis() - start > 5000){
						Log.d("ReceiverTask", "I'm alive!");
						start = System.currentTimeMillis();
					}
					
					synchronized (playerIds) {
						if (dis.available() > 0) {
							readLine(dis);
							boolean success = eventsOf(dis, player, playerIds, customTypes, confirmedIds);
							if (!success) {
								dis.close();
								break;
								// return ResultTask.TIMEOUT_FAILURE;
							} else {
								dis.readByte();
								dis.readByte(); // reading CRLF
							}
						}
					}
					Thread.sleep(250);
					/*
					 * if (System.currentTimeMillis() - lastMoveTime > 1000){
					 * Log.d("ReceiverTask", "Didn't received move more than 1
					 * sec"); lastMoveTime = System.currentTimeMillis();
					 * synchronized (toSend){ Move move = Move.create(); move.id =
					 * -(lastId+1); toSend.add(move); } }
					 */
				}
				dis.close();
				dis = null;
				Thread.sleep(250);
			}
		} catch (Exception e) {
			Log.e("GASPClient.inGame", e);
			try {
				player.onError(0, Errors.RECEIVE_GAME_FAILED_ERROR);
			} catch (Exception ee) {
				Log.e("ReceiveTask", ee);
			}
			return ResultTask.TIMEOUT_FAILURE;
		} finally {
			if (dis != null) {
				try {
					dis.close();
				} catch (Exception e) {
				}
			}
		}
		
		try {
		if (player.getState() == GamePlayer.GAME){
			player.onError(attempts, Errors.RECEIVE_GAME_FAILED_ERROR);
			Log.d("ReceiveTask", "Exiting: player state is " + player.getState() + " attempts: " + attempts);
		} else {
			Log.d("ReceiveTask", "Exiting: player state is " + player.getState());
		}
		} catch (Exception e) {
			Log.e("ReceiveTask", e);
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
			final List<Integer> confirmedIds, SparseIntArray playerIds) {

		this.host = host;
		this.actorSession = actorSession;
		this.player = player;
		this.playerIds = playerIds;
		this.gameId = gameId;
		this.confirmedIds = confirmedIds;
		synchronized (player) {
			try {
				start();
			} catch (Exception e) {
				Log.e("ReceiverTask", e);
			}
		}
	}
}
