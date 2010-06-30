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

import java.util.ArrayList;
import java.util.List;

import org.aksonov.mages.entities.Move;

// TODO: Auto-generated Javadoc
/**
 * Implements Board interface with some common methods for all board games - scoring, list of moves.
 * 
 * @author Pavel
 */
public abstract class BaseBoard implements Board {
	
	/** The map. */
	public byte[][] map;
	
	/** The player. */
	protected byte player = 0;
	
	/** The moves. */
	protected List<Move> moves;
	
	/** The scores. */
	protected int[] scores;

	/**
	 * Constructor with given width, height.
	 * 
	 * @param width the width
	 * @param height the height
	 */
	protected BaseBoard(int width, int height) {
		map = new byte[width][height];
		moves = new ArrayList<Move>();
	}

	/**
	 * Dispose method.
	 */
	public void dispose() {
		if (moves != null) {
			for (int i = 0; i < moves.size(); i++) {
				moves.get(i).dispose();
			}
		}
	}

	/**
	 * Returns current player position (0, 1, .. N)
	 * 
	 * @return the current player
	 */
	
	public abstract byte getCurrentPlayer();
	
	/**
	 * Returns all made moves.
	 * 
	 * @return the moves
	 */
	
	public List<Move> getMoves() {
		return new ArrayList<Move>(moves);
	}

	/**
	 * Implementations should implement making move here.
	 * 
	 * @param move the move
	 * 
	 * @return true, if make move
	 */
	
	public abstract boolean makeMove(Move move);
	
	/* (non-Javadoc)
	 * @see org.aksonov.mages.Board#getScore(byte)
	 */
	
	public int getScore(byte player) {
		return scores[player];
	}
	
	
	/* (non-Javadoc)
	 * @see org.aksonov.mages.Board#setScore(byte, int)
	 */
	 
	public void setScore(byte player, int score){
		scores[player] = score;
	}

}
