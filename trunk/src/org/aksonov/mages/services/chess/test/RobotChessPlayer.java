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
package org.aksonov.mages.services.chess.test;

import org.aksonov.mages.chess.ChessBoard;
import org.aksonov.mages.entities.GameSettings;
import org.aksonov.mages.entities.Move;
import org.aksonov.mages.entities.Note;
import org.aksonov.mages.entities.PlayerInfo;
import org.aksonov.mages.services.GamePlayer;
import org.aksonov.tools.Log;

import android.os.RemoteException;

/**
 * Represents simple AI chess robot-repeater. This robot always accepts invitation and immediately joins the game 
 * @author Pavel
 *
 */
public class RobotChessPlayer extends GamePlayer {
	private ChessBoard board = null;

	/**
	 * This method is called when new move is arrived from the game server
	 */
	
	public void onMove(int playerId, Move move) throws RemoteException {
		super.onMove(playerId, move);
		if (board == null) {
			throw new IllegalArgumentException(
					"Board is not created for this game");
		}

		if (info.gameId == 0) {
			throw new IllegalArgumentException(
					"PlayerInfo.gameId is not initialized");
		}

		Log.d("RobotChessPlayer", "Receiving move " + move);

		if (move.player == info.player) {
			// it is just confirmation
			return;
		}
		board.makeMove(move);

		makeRepeaterMove(move);

		Log.d("RobotChessPlayer", "I've prepared move: " + move);
	}

	private void makeRepeaterMove(Move move) throws RemoteException {
		Move robotMove = board.createMove((byte) 1,
				move.data[ChessBoard.FIGURE], move.data[ChessBoard.OLD_X],
				(byte) (7 - move.data[ChessBoard.OLD_Y]),
				move.data[ChessBoard.NEW_X],
				(byte) (7 - move.data[ChessBoard.NEW_Y]));

		boolean result = board.makeMove(robotMove);
		if (result) {
			server.sendMove(info.gameId, info.id, robotMove);
		} else {
			Log.d("RobotChessPlayer", "I can't repeat, so i'm resign!");
			server.sendNote(info.gameId, info.id, Note.createProposition(
					Note.RESIGN, info.id));
		}

	}

	/**
	 * This method is called when invitation is arrived from the game server,
	 * robot always accepts invitation and joins the game
	 * 
	 * @param info PlayerInfo 
	 * @param player id - id of player who invited
	 */
	
	public void onInvitation(PlayerInfo info, int playerId)
			throws RemoteException {
		super.onInvitation(info, playerId);
		Log.d("TestRobotChessPlayer",
				"Wow! i've got invitation from player id: " + playerId
						+ " to position: " + info.player);
		server.joinGame(info.gameId, info);
	}

	/**
	 * This method is called when some one joined the game. 
	 */
	
	public void onJoin(PlayerInfo info) throws RemoteException {
		super.onJoin(info);
		Log.d("TestRobotChessPlayer",
				"Wow! i've been joined, now i'm trying to start game");
		if (this.info.id == info.id) {
			server.startGame(info.gameId, info.id);
		}
	}

	/**
	 * This method is called when game starts.
	 * @param settings - game settings
	 * @param playerId - player Id
	 */
	
	public void onStart(GameSettings settings, int playerId)
			throws RemoteException {
		super.onStart(settings, playerId);
		Log.d("TestRobotChessPlayer", "Game is started!");
		if (info.gameId != settings.id) {
			throw new IllegalArgumentException("PlayerInfo.gameId "
					+ info.gameId + " is not equal to started game id: "
					+ settings.id);
		}

		this.board = ChessBoard.create();
		if (settings.moves.size() == 0 && info.player == ChessBoard.WHITE) {
			Move move = board.createMove(ChessBoard.WHITE, ChessBoard.PONE,
					(byte) 4, (byte) 6, (byte) 4, (byte) 4);
			board.makeMove(move);

			server.sendMove(settings.id, info.id, move);
		} else if (settings.moves.size() > 0) {
			for (int i = 0; i < settings.moves.size(); i++) {
				board.makeMove(settings.moves.get(i));
			}
			if (board.getCurrentPlayer() == info.player){
				makeRepeaterMove(settings.moves.get(settings.moves.size()-1));
			}
		}

	}

}
