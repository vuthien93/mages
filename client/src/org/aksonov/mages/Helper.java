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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Hashtable;

import org.aksonov.tools.Log;
import org.mega.gasp.moods.CustomTypes;

// TODO: Auto-generated Javadoc
/**
 * The Class Helper.
 */
public class Helper {
	
	/**
	 * Sleep.
	 * 
	 * @param time the time
	 */
	public static void sleep(long time) {
		try {
			Thread.sleep(time);
		} catch (Exception e) {
			Log.e("Helper.sleep", e);
		}
	}

	/**
	 * Pad.
	 * 
	 * @param c the c
	 * 
	 * @return the string
	 */
	public static String pad(int c) {
		if (c >= 10)
			return String.valueOf(c);
		else
			return "0" + String.valueOf(c);
	}

	/**
	 * Format time.
	 * 
	 * @param time the time
	 * @param infinite the infinite
	 * 
	 * @return the char sequence
	 */
	public static CharSequence formatTime(int time, CharSequence infinite) {
		if (time == 0) {
			return infinite;
		} else {
			return formatTime(getHour(time), getMinute(time), getSecond(time));
		}
	}

	/**
	 * Gets the hour.
	 * 
	 * @param time the time
	 * 
	 * @return the hour
	 */
	public static int getHour(long time) {
		return (int) (time / (60 * 60 * 1000));
	}

	/**
	 * Gets the minute.
	 * 
	 * @param time the time
	 * 
	 * @return the minute
	 */
	public static int getMinute(long time) {
		return (int) ((time - getTime(getHour(time), 0)) / (60 * 1000));
	}

	/**
	 * Gets the second.
	 * 
	 * @param time the time
	 * 
	 * @return the second
	 */
	public static int getSecond(long time) {
		return (int) ((time - getTime(getHour(time), getMinute(time), 0)) / 1000);
	}

	/**
	 * Gets the time.
	 * 
	 * @param hour the hour
	 * @param minute the minute
	 * 
	 * @return the time
	 */
	public static int getTime(int hour, int minute) {
		return getTime(hour, minute, 0);
	}

	/**
	 * Gets the time.
	 * 
	 * @param hour the hour
	 * @param minute the minute
	 * @param second the second
	 * 
	 * @return the time
	 */
	public static int getTime(int hour, int minute, int second) {
		return (hour * 60 * 60 + minute * 60 + second) * 1000;
	}

	/**
	 * Format time.
	 * 
	 * @param hour the hour
	 * @param minute the minute
	 * 
	 * @return the char sequence
	 */
	public static CharSequence formatTime(int hour, int minute) {
		return new StringBuilder().append(Helper.pad(hour)).append(":").append(
				Helper.pad(minute));
	}

	/**
	 * Format time.
	 * 
	 * @param hour the hour
	 * @param minute the minute
	 * @param second the second
	 * 
	 * @return the char sequence
	 */
	public static CharSequence formatTime(int hour, int minute, int second) {
		StringBuilder sb = new StringBuilder();
		if (hour > 0)
			sb.append(hour).append("h").append(Helper.pad(minute)).append("m");
		else 
			sb.append(pad(minute)).append(":").append(pad(second));
		return sb.toString();

	}

	/**
	 * Format time.
	 * 
	 * @param time the time
	 * 
	 * @return the char sequence
	 */
	public static CharSequence formatTime(long time) {
		return formatTime(getHour(time), getMinute(time), getSecond(time));
//		return formatTime(getHour(time), getMinute(time), getSecond(time));
	}

	/**
	 * Convert to array.
	 * 
	 * @param data the data
	 * 
	 * @return the object[]
	 */
	private static Object[] convertToArray(Hashtable data) {
		Object[] result = new Object[data.size()];
		for (int i = 0; i < data.size(); i++) {
			result[i] = data.get(i + "");
		}
		return result;
	}

	/**
	 * Convert to hashtable.
	 * 
	 * @param data the data
	 * 
	 * @return the hashtable
	 */
	private static Hashtable convertToHashtable(Object[] data) {
		Hashtable h = new Hashtable();
		for (int i = 0; i < data.length; i++) {
			h.put(i + "", data[i]);
		}
		return h;
	}

	/**
	 * Decode.
	 * 
	 * @param dis the dis
	 * @param types the types
	 * 
	 * @return the object[]
	 * 
	 * @throws Exception the exception
	 */
	public static Object[] decode(DataInputStream dis, CustomTypes types)
			throws Exception {
		if (types instanceof MoodsSerializer) {
			return ((MoodsSerializer) types).decodeObjects(dis);
		} else {
			return convertToArray(types.decodeData(dis));
		}
	}

	/**
	 * Encode.
	 * 
	 * @param data the data
	 * @param dos the dos
	 * @param types the types
	 * 
	 * @throws Exception the exception
	 */
	public static void encode(Object[] data, DataOutputStream dos,
			CustomTypes types) throws Exception {
		if (types instanceof MoodsSerializer) {
			((MoodsSerializer) types).encodeObjects(data, dos);
		} else {
			types.encodeData(convertToHashtable(data), dos);
		}
	}

}
