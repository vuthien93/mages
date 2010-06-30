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

import org.aksonov.tools.Log;

import android.os.Parcel;
import android.os.Parcelable;

public class Note implements GameEntity, Parcelable {
	public static final byte END_GAME_TYPE = 1;
	public static final byte MESSAGE_TYPE = 2;
	public static final byte PROPOSE_TYPE = 4;
	
	public static final byte TIME_LIMIT_REASON = 3;
	public static final byte GAME_OVER_REASON = 4;
	public static final byte END = 5;
	public static final byte RESIGN = 6;
	public static final byte WIN = 7;
	public static final byte TIE = 8;

	private static Stack<Note> pool = new Stack<Note>();
	public static final List<Note> emptyList = new ArrayList<Note>();

    public int describeContents() {
        return 0;
    }
	// attributes
	public String message = "";
	public byte type = 0;
	public byte reason = 0;
	public int arg1 = 0;
	public int arg2 = 0;
	public int who = 0;

	public static final Parcelable.Creator<Note> CREATOR = new Parcelable.Creator<Note>() {
		public Note createFromParcel(Parcel in) {
			return Note.create(in);
		}

		public Note[] newArray(int size) {
			return new Note[size];
		}
	};

	public void writeToParcel(Parcel parcel, int i) {
		Log.d("Notification", "We are writing to parcel here!");
		parcel.writeInt(arg1);
		parcel.writeInt(arg2);
		parcel.writeInt(who);
		parcel.writeByte(type);
		parcel.writeByte(reason);
		parcel.writeString(message);
		Note.dispose(this);
	}

	public void readFromParcel(Parcel parcel) {
		arg1 = parcel.readInt();
		arg2 = parcel.readInt();
		who = parcel.readInt();
		type = parcel.readByte();
		reason = parcel.readByte();
		message = parcel.readString();
	}

	// constructor
	private Note() {
	}

	public static Note create() {
		synchronized (pool) {
			return pool.size() > 0 ? new Note() /*pool.pop() */: new Note();
		}
	}

	private static Note create(Parcel in) {
		Note obj = Note.create();
		obj.readFromParcel(in);
		return obj;
	}

	public static Note createEndOfGame(byte reason, int who, int arg1, int arg2) {
		Note n = Note.create();
		n.type = END_GAME_TYPE;
		n.reason = reason;
		n.arg1 = arg1;
		n.arg2 = arg2;
		n.who = who;
		return n;
	}

	public static Note createProposition(byte what, int who) {
		Note n = Note.create();
		n.type = PROPOSE_TYPE;
		n.reason = what;
		n.who = who;
		return n;
	}

	public static Note createEndOfGame(byte reason, int who) {
		Note n = Note.create();
		n.type = END_GAME_TYPE;
		n.reason = reason;
		n.who = who;
		return n;
	}

	public static Note createMessage(int who, String message) {
		Note n = Note.create();
		n.type = MESSAGE_TYPE;
		n.who = who;
		n.message = message;
		return n;
	}

	public static void dispose(Note notification) {
		synchronized (pool) {
			pool.push(notification);
		}
	}

	public void dispose() {
		Note.dispose(this);
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}

	public byte getReason() {
		return reason;
	}

	public void setReason(byte reason) {
		this.reason = reason;
	}

	public int getArg1() {
		return arg1;
	}

	public void setArg1(int arg1) {
		this.arg1 = arg1;
	}

	public int getSender() {
		return who;
	}

	public void setSender(int who) {
		this.who = who;
	}

}
