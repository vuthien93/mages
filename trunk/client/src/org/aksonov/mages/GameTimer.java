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

import org.aksonov.tools.Log;

// TODO: Auto-generated Javadoc
/**
 * Represents timing control.
 * 
 * @author Pavel
 */
public class GameTimer implements Runnable {
	
	/** The MA x_ number. */
	private final int MAX_NUMBER = 10;
	
	/** The PRECISION. */
	private final int PRECISION = 1000;
	
	/** The callback. */
	private GameTimerCallback callback = null;
	
	/** The current player. */
	private int currentPlayer = -1;

	/** The move timers. */
	private long[] moveTimers = new long[MAX_NUMBER];
	
	/** The total timers. */
	private long[] totalTimers = new long[MAX_NUMBER];
	
	/** The last moves. */
	private long[] lastMoves = new long[MAX_NUMBER];
	
	/** The last shown. */
	private long[] lastShown = new long[MAX_NUMBER];
	
	/** The obj. */
	private Object obj = new Object();
	
	/** The move increment. */
	private final int moveIncrement;

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		while (callback.isAlive()) {
			if (currentPlayer >= 0) {
				synchronized (obj) {
					moveTimers[currentPlayer] = System.currentTimeMillis()
							- lastMoves[currentPlayer];
					if (moveTimers[currentPlayer] + totalTimers[currentPlayer]
							- lastShown[currentPlayer] > PRECISION) {
						lastShown[currentPlayer] = moveTimers[currentPlayer]
								+ totalTimers[currentPlayer];

						// call callback method
						callback.onTimeChanged(currentPlayer,
								lastShown[currentPlayer],
								moveTimers[currentPlayer]);
					}
				}
			}
			Helper.sleep(500);
		}
	}

	/**
	 * Sets the time.
	 * 
	 * @param player the player
	 * @param time the time
	 */
	public void setTime(int player, long time) {
		totalTimers[player] = time;
		callback.onTimeChanged(player, totalTimers[player], moveTimers[player]);
	}

	/**
	 * Gets the time.
	 * 
	 * @param player the player
	 * 
	 * @return the time
	 */
	public long getTime(int player) {
		return totalTimers[player];
	}

	/**
	 * Gets the current player.
	 * 
	 * @return the current player
	 */
	public int getCurrentPlayer() {
		return currentPlayer;
	}

	/**
	 * Pause.
	 */
	public void pause() {
		currentPlayer = -1;
	}

	/**
	 * Start.
	 * 
	 * @param player the player
	 * 
	 * @return the long
	 */
	public long start(int player) {
		Log.d("GameTimer", "Switching to player: " + player);
		long result = 0;
		synchronized (obj) {
			if (currentPlayer >= 0) {
				totalTimers[currentPlayer] += moveTimers[currentPlayer];
				if (currentPlayer != player) {
					totalTimers[currentPlayer] -= moveIncrement;
				}
				result = totalTimers[currentPlayer];
			}
			currentPlayer = player;
			lastMoves[currentPlayer] = System.currentTimeMillis();
		}
		return result;
	}

	/**
	 * Instantiates a new game timer.
	 * 
	 * @param callback the callback
	 * @param moveIncrement the move increment
	 */
	public GameTimer(GameTimerCallback callback, int moveIncrement) {
		if (callback == null) {
			throw new IllegalArgumentException("No callback is set");
		}
		this.moveIncrement = moveIncrement;
		this.callback = callback;
		reset();
	}

	/**
	 * Instantiates a new game timer.
	 * 
	 * @param callback the callback
	 */
	public GameTimer(GameTimerCallback callback) {
		this(callback, 0);
	}

	/**
	 * Reset.
	 * 
	 * @param array the array
	 */
	private void reset(long[] array) {
		for (int i = 0; i < array.length; i++) {
			array[i] = 0;
		}
	}

	/**
	 * Reset.
	 */
	public void reset() {
		reset(lastShown);
		reset(moveTimers);
		reset(totalTimers);
		reset(lastMoves);
		currentPlayer = -1;
	}

	/**
	 * Stop.
	 */
	public void stop() {
		reset();
		callback.setAlive(false);
		currentPlayer = -1;
	}

	/**
	 * Start.
	 */
	public void start() {
		callback.setAlive(true);
		new Thread(this).start();
	}
}
