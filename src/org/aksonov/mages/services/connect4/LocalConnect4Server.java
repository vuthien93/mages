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
package org.aksonov.mages.services.connect4;

import org.aksonov.mages.Board;
import org.aksonov.mages.connect4.Connect4Board;
import org.aksonov.mages.services.LocalGameServer;

/**
 * Local Connect Four server, just extends LocalGameServer and implemnets createBoard() method as Connect4Board.create()
 * @author Pavel
 *
 */public class LocalConnect4Server extends LocalGameServer {

	
	public Board createBoard() {
		return Connect4Board.create();
	}
	

}
