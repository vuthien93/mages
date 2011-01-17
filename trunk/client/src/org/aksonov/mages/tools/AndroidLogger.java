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

import org.aksonov.tools.Logger;


// TODO: Auto-generated Javadoc
/**
 * The Class AndroidLogger.
 */
public class AndroidLogger implements Logger {
	
	/** The Constant instance. */
	public static final AndroidLogger instance = new AndroidLogger();
	
	/**
	 * Instantiates a new android logger.
	 */
	private AndroidLogger(){
		
	}

	/* (non-Javadoc)
	 * @see org.aksonov.tools.Logger#d(java.lang.String, java.lang.String)
	 */
	
	public void d(String target, String message) {
		android.util.Log.d(target, message);
	}

	/* (non-Javadoc)
	 * @see org.aksonov.tools.Logger#e(java.lang.String, java.lang.String)
	 */
	
	public void e(String target, String message) {
		android.util.Log.e(target, message);
	}

	/* (non-Javadoc)
	 * @see org.aksonov.tools.Logger#e(java.lang.String, java.lang.Throwable)
	 */
	
	public void e(String target, Throwable e) {
		android.util.Log.e(target, android.util.Log.getStackTraceString(e));
	}

	/* (non-Javadoc)
	 * @see org.aksonov.tools.Logger#i(java.lang.String, java.lang.String)
	 */
	
	public void i(String target, String message) {
		android.util.Log.i(target, message);
	}

	/* (non-Javadoc)
	 * @see org.aksonov.tools.Logger#w(java.lang.String, java.lang.String)
	 */
	
	public void w(String target, String message) {
		android.util.Log.w(target, message);
	}
	
}
