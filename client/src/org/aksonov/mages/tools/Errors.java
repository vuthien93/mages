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
package org.aksonov.mages.tools;

import org.aksonov.mages.R;

// TODO: Auto-generated Javadoc
/**
 * The Class Errors.
 */
public class Errors {
	
	/** The Constant FIRST_LOGIN_FAILED_ERROR. */
	public static final int FIRST_LOGIN_FAILED_ERROR = -11;
	
	/** The Constant LOGIN_FAILED_ERROR. */
	public static final int LOGIN_FAILED_ERROR = -12;
	
	/** The Constant NETWORK_ERROR. */
	public static final int NETWORK_ERROR = -13;
	
	/** The Constant CANNOT_RETRIEVE_GAMELIST_ERROR. */
	public static final int CANNOT_RETRIEVE_GAMELIST_ERROR = -14;
	
	/** The Constant CREATE_GAME_FAILED_ERROR. */
	public static final int CREATE_GAME_FAILED_ERROR = -15;
	
	/** The Constant JOIN_GAME_FAILED_ERROR. */
	public static final int JOIN_GAME_FAILED_ERROR = -16;
	
	/** The Constant RECEIVE_GAME_FAILED_ERROR. */
	public static final int RECEIVE_GAME_FAILED_ERROR = -17;
	
	/** The Constant SEND_GAME_FAILED_ERROR. */
	public static final int SEND_GAME_FAILED_ERROR = -18;
	
	/** The Constant INVALID_MOVE. */
	public static final int INVALID_MOVE = -101;
	
	/** The Constant NOT_SUPPORTED. */
	public static final int NOT_SUPPORTED = -19;
	
	/** The Constant JOIN_BUSY_ERROR. */
	public static final int JOIN_BUSY_ERROR = -20;
	
	/** The Constant INVITE_FAILED_ERROR. */
	public static final int INVITE_FAILED_ERROR = -21;

	/**
	 * Instantiates a new errors.
	 */
	private Errors() {

	}

	/**
	 * Gets the message id.
	 * 
	 * @param code
	 *            the code
	 * 
	 * @return the message id
	 */
	public static int getMessageId(int code) {
		switch (code) {
		case FIRST_LOGIN_FAILED_ERROR:
			return R.string.error_first_login_failed;
		case LOGIN_FAILED_ERROR:
			return R.string.error_login_failed;
		case NETWORK_ERROR:
			return R.string.error_network_error;
		case NOT_SUPPORTED:
			return R.string.error_unsupported;
		case JOIN_GAME_FAILED_ERROR:
			return R.string.error_join;
		case CREATE_GAME_FAILED_ERROR:
			return R.string.error_create;
		case CANNOT_RETRIEVE_GAMELIST_ERROR:
			return R.string.error_list;
		case INVALID_MOVE:
			return R.string.error_incorrect_move;
		case SEND_GAME_FAILED_ERROR:
			return R.string.error_send;
		case RECEIVE_GAME_FAILED_ERROR:
			return R.string.error_receive;
		case JOIN_BUSY_ERROR:
			return R.string.error_join;
		case INVITE_FAILED_ERROR:
			return R.string.error_invite;
		}
		return R.string.error_unknown;
	}
}
