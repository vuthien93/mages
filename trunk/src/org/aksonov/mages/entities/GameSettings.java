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

import org.aksonov.mages.Helper;

import android.os.Parcel;
import android.os.Parcelable;

public class GameSettings implements GameEntity, Parcelable {
	public static final Parcelable.Creator<GameSettings> CREATOR = new Parcelable.Creator<GameSettings>() {
		public GameSettings createFromParcel(Parcel in) {
			return GameSettings.create(in);
		}

		public GameSettings[] newArray(int size) {
			return new GameSettings[size];
		}
	};

	private static Stack<GameSettings> pool = new Stack<GameSettings>();
	public List<Move> moves = new ArrayList<Move>();
	public List<PlayerInfo> players = new ArrayList<PlayerInfo>();

	// attributes
	public int timePerMove;
	public int timePerGame;
	public int arg1;
	public int arg2;
	public int moveIncr;
	public int id = -1;
	public int minActors = 2;
	public int maxActors = 2;
	public boolean rated = false;

	public static GameSettings create() {
		synchronized (pool) {
			return pool.size() > 0 ? new GameSettings()/* pool.pop() */
					: new GameSettings();
		}
	}

	private static GameSettings create(Parcel in) {
		GameSettings settings = GameSettings.create();
		settings.readFromParcel(in);
		return settings;
	}

	public static void dispose(GameSettings settings) {
		for (int i = 0; i < settings.moves.size(); i++) {
			settings.moves.get(i).dispose();
		}
		for (int i = 0; i < settings.players.size(); i++) {
			settings.players.get(i).dispose();
		}
		settings.moves.clear();
		settings.players.clear();
		synchronized (pool) {
			pool.push(settings);
		}
	}

	// constructor
	private GameSettings() {
		timePerMove = 0;
		timePerGame = 0;
	}

	// setter/getter methods
	public int getTimePerMove() {
		return timePerMove;
	}

	public void setTimePerMove(int timePerMove) {
		this.timePerMove = timePerMove;
	}

	public int getTimePerGame() {
		return timePerGame;
	}

	public void setTimePerGame(int timePerGame) {
		this.timePerGame = timePerGame;
	}

	public void dispose() {
		GameSettings.dispose(this);
	}

	public void writeToParcel(Parcel parcel, int i) {
		parcel.writeInt(id);
		parcel.writeInt(timePerGame);
		parcel.writeInt(timePerMove);
		parcel.writeInt(moveIncr);
		parcel.writeInt(arg1);
		parcel.writeInt(arg2);
		parcel.writeByte(rated ? (byte) 1 : (byte) 0);

		parcel.writeInt(players.size());
		for (int j = 0; j < players.size(); j++) {
			players.get(j).writeToParcel(parcel, i);
		}

		parcel.writeInt(moves.size());
		for (int j = 0; j < moves.size(); j++) {
			moves.get(j).writeToParcel(parcel, i);
		}

		// GameSettings.dispose(this);
	}

	public void readFromParcel(Parcel parcel) {
		id = parcel.readInt();
		timePerGame = parcel.readInt();
		timePerMove = parcel.readInt();
		moveIncr = parcel.readInt();
		arg1 = parcel.readInt();
		arg2 = parcel.readInt();
		byte r = parcel.readByte();
		rated = r == 1 ? true : false;

		int size = parcel.readInt();
		for (int i = 0; i < size; i++) {
			PlayerInfo player = PlayerInfo.create(parcel);
			players.add(player);
		}

		size = parcel.readInt();
		for (int i = 0; i < size; i++) {
			Move move = Move.create(parcel);
			moves.add(move);
		}

	}

	public boolean equals(Object o) {
		if (o == null || !(o instanceof GameSettings)) {
			return false;
		}

		return ((GameSettings) o).id == this.id;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (rated){
			sb.append("Rated,");
		} else {
			sb.append("Unrated,");
		}
		if (timePerGame > 0) {
			sb.append(Helper.formatTime(timePerGame));
			if (timePerMove > 0) {
				sb.append("  ");
				sb.append(Helper.formatTime(timePerMove));
			}
		} else {
			sb.append("No limit");
		}
		sb.append("\nPlayers:\n");
		for (int i = 0; i < players.size(); i++) {
			sb.append(players.get(i).username);
			sb.append(" ");
		}
		return sb.toString();
	}

    public int describeContents() {
        return 0;
    }
}
