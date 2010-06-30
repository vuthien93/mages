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

// TODO: Auto-generated Javadoc
/**
 * The Interface GameTimerCallback.
 */
public interface GameTimerCallback {
	
	/**
	 * On time changed.
	 * 
	 * @param player the player
	 * @param totalTime the total time
	 * @param moveTime the move time
	 */
	void onTimeChanged(int player, long totalTime, long moveTime);
	
	/**
	 * Checks if is alive.
	 * 
	 * @return true, if is alive
	 */
	boolean isAlive();
	
	/**
	 * Sets the alive.
	 * 
	 * @param alive the new alive
	 */
	void setAlive(boolean alive);
	
}
