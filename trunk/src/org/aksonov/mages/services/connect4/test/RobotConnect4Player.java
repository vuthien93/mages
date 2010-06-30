package org.aksonov.mages.services.connect4.test;

import org.aksonov.mages.chess.ChessBoard;
import org.aksonov.mages.connect4.Connect4Board;
import org.aksonov.mages.entities.GameSettings;
import org.aksonov.mages.entities.Move;
import org.aksonov.mages.entities.Note;
import org.aksonov.mages.entities.PlayerInfo;
import org.aksonov.mages.services.GamePlayer;
import org.aksonov.tools.Log;

import android.os.RemoteException;

public class RobotConnect4Player extends GamePlayer {
	private Connect4Board board = null;

	
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


	}

	private void makeRepeaterMove(Move move) throws RemoteException {
		Move robotMove = ((Connect4Board)board).createMove(info.player, move.data[0]);

		boolean result = board.makeMove(robotMove);
		if (result) {
			server.sendMove(info.gameId, info.id, robotMove);
		} else {
			Log.d("RobotChessPlayer", "I can't repeat, so i'm resign!");
			server.sendNote(info.gameId, info.id, Note.createProposition(
					Note.RESIGN, info.id));
		}
	}
	
	
	public void onInvitation(PlayerInfo info, int playerId)
			throws RemoteException {
		super.onInvitation(info, playerId);
		Log.d("TestRobotChessPlayer",
				"Wow! i've got invitation from player id: " + playerId
						+ " to position: " + info.player);
		server.joinGame(info.gameId, info);
	}

	
	public void onJoin(PlayerInfo info) throws RemoteException {
		super.onJoin(info);
		Log.d("TestRobotChessPlayer",
				"Wow! i've been joined, now i'm trying to start game");
		if (this.info.id == info.id) {
			server.startGame(info.gameId, info.id);
		}
	}
	
	

	
	public void onStart(GameSettings settings, int playerId)
			throws RemoteException {
		super.onStart(settings, playerId);
		Log.d("TestRobotChessPlayer", "Game is started!");
		this.board = Connect4Board.create();
		if (info.gameId != settings.id) {
			throw new IllegalArgumentException("PlayerInfo.gameId "
					+ info.gameId + " is not equal to started game id: "
					+ settings.id);
		}
		if (info.player == ChessBoard.WHITE && settings.moves.size()==0) {
			Move move = ((Connect4Board)board).createMove((byte)0, (byte)4);
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
