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


import android.os.Bundle;
import android.util.DisplayMetrics;

// TODO: Auto-generated Javadoc
/**
 * This main activity for Connect4 game provides method to return layout with
 * Connect4 game board and chess type used by intent filters.
 * 
 * @author aksonov
 */
public class Connect4 extends Game {

	/**
	 * Returns game name used by Mages engine.
	 * 
	 * @return the game name
	 */
	
	public String getGameName(){
		return "mages/connect4";
	}

	/**
	 * Returns board layout with chess board inside used by InGame activity to
	 * display chess GUI.
	 * 
	 * @return the board layout
	 */
	
	public int getBoardLayout() {
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int height = dm.heightPixels;
		int width = dm.widthPixels;

		if (height > width) {
			return R.layout.c4_vertical;
		} else {
			return R.layout.c4_horizontal;
		}
	}
	
	
	
	

}
