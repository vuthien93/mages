package org.aksonov.mages.chess.server;

import org.aksonov.mages.Board;
import org.aksonov.mages.chess.ChessBoard;
import org.aksonov.mages.server.MagesServer;


public class Server extends MagesServer {

	@Override
	public Board createBoard() {
		return ChessBoard.create();
	}
	

}
