package org.aksonov.mages.connect4.server;

import org.aksonov.mages.Board;
import org.aksonov.mages.connect4.Connect4Board;
import org.aksonov.mages.server.MagesServer;


public class Server extends MagesServer {

	@Override
	public Board createBoard() {
		return Connect4Board.create();
	}
	

}
