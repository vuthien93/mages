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

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import android.os.Parcel;
import android.os.Parcelable;

public class Move implements GameEntity, Parcelable {
	private static Stack<Move> pool = new Stack<Move>();
	public static final List<Move> emptyList = new ArrayList<Move>();
	public static final Move emptyMove = new Move();
	public static final Parcelable.Creator<Move> CREATOR = new Parcelable.Creator<Move>() {
		public Move createFromParcel(Parcel in) {
			return Move.create(in);
		}

		public Move[] newArray(int size) {
			return new Move[size];
		}
	};


	// attributes
	public byte[] data = new byte[0];
	public int time;
	public byte player;
	public int id;

	// constructor
	private Move() {
		player = 0;
		time = 0;
		id = 0;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public int getTime() {
		return time;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return this.id;
	}

	public void setPlayer(byte player) {
		this.player = player;
	}

	public byte getPlayer() {
		return player;
	}

	// constructor
	private Move(int length) {
		player = 0;
		time = 0;
		data = new byte[length];
	}

	public static Move create() {
		synchronized (pool) {
			return pool.size() > 0 ? new Move()/*pool.pop()*/ : new Move();
		}
	}

	public static Move create(Move move) {
		Move newMove = Move.create();
		newMove.id = move.id;
		newMove.player = move.player;
		newMove.data = new byte[move.data.length];
		for (int i=0;i<move.data.length;i++) newMove.data[i] = move.data[i];
		newMove.time = move.time;
		
		return newMove;
		
	}

	public static void dispose(Move move) {
		synchronized (pool) {
			pool.push(move);
		}
	}

    public int describeContents() {
        return 0;
    }

    public void dispose() {
		Move.dispose(this);
	}

	public String toString() {
		return "Id: " + id + ", player: " + player + ", data : "
				+ java.util.Arrays.toString(data) + ", time: " + time;
	}
	
	public boolean equals(Object o){
		if (o == null || !(o instanceof Move)){
			return false;
		}
		
		return ((Move)o).id == this.id;
	}

	public static Move create(Parcel in) {
		Move move = Move.create();
		move.readFromParcel(in);
		return move;
	}

	public void writeToParcel(Parcel parcel, int i) {
		parcel.writeByte(player);
		parcel.writeInt(id);
		parcel.writeInt(time);
		parcel.writeByteArray(data);

		Move.dispose(this);
	}

	public void readFromParcel(Parcel parcel) {
		player = parcel.readByte();
		id = parcel.readInt();
		time = parcel.readInt();
		data = parcel.createByteArray();
	}

}
