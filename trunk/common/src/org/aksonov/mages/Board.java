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

import org.aksonov.mages.entities.Move;

// TODO: Auto-generated Javadoc
/**
 * The main interface which each board game should implement.
 * 
 * @author Pavel
 */
public interface Board {
	
	/**
	 * Makes the move.
	 * 
	 * @param move move
	 * 
	 * @return true, if make move
	 */
	boolean makeMove(Move move);
	
	/**
	 * Returns made moves.
	 * 
	 * @return made moves
	 */
	List<Move> getMoves();
	
	/**
	 * Disposes the board.
	 */
	void dispose();
	
	/**
	 * Returns current player position who should move.
	 * 
	 * @return the current player
	 */
	byte getCurrentPlayer();
	
	/**
	 * Represents is game over or not.
	 * 
	 * @return is game over or not
	 */
	boolean isGameOver();
	
	/**
	 * Returns score for given player position.
	 * 
	 * @param player the player
	 * 
	 * @return the score
	 */
	int getScore(byte player);
	
	/**
	 * Sets the score for given player position.
	 * 
	 * @param player the player
	 * @param score the score
	 */
	void setScore(byte player, int score);
	
	/**
	 * Undo last move.
	 */
	void undoLastMove();
}
