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
package org.aksonov.mages.entities;

import java.util.Stack;

public class PlayerInfo implements GameEntity {
	private static Stack<PlayerInfo> pool = new Stack<PlayerInfo>();

	// attributes
	public byte player = -1;
	public int id = 0;
	public int gameId = 0;
	public int session = 0;
	public int arg1 = 0;
	public int arg2 = 0;
	public int rating = 0;

	public String username = "";
	public String password = "";
	public byte[] photo = new byte[0];
	public boolean ready = false;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	// constructor
	private PlayerInfo(byte player) {
		this.player = player;
	}

	private PlayerInfo() {
		this.player = -1;
	}

	public static PlayerInfo create(byte player) {
		PlayerInfo p = PlayerInfo.create();
		p.player = player;
		return p;
	}

	public static PlayerInfo create() {
		synchronized (pool) {
			return pool.size() > 0 ? new PlayerInfo()/*pool.pop()*/ : new PlayerInfo();
		}
	}

	public static void dispose(PlayerInfo info) {
		synchronized (pool) {
			pool.push(info);
		}

	}

	// setter/getter methods
	public byte getPlayer() {
		return player;
	}

	public void setPlayer(byte player) {
		this.player = player;
	}

	@Override
	public void dispose() {
		PlayerInfo.dispose(this);
	}

	public boolean equals(Object o) {
		if (o == null || !(o instanceof PlayerInfo)) {
			return false;
		}

		return ((PlayerInfo) o).id == this.id;
	}

	public String toString() {
		return "id: " + id + " username: " + username + " gameId: " + gameId + " session:" + session;
	}

}
