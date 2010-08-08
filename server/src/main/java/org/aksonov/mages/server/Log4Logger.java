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
package org.aksonov.mages.server;

import java.util.Hashtable;

import org.aksonov.tools.Logger;
import org.apache.log4j.Category;

// TODO: Auto-generated Javadoc
/**
 * The Class Log4Logger.
 */
public class Log4Logger implements Logger {
	
	/** The Constant instance. */
	public static final Log4Logger instance = new Log4Logger();
	
	/**
	 * Instantiates a new log4 logger.
	 */
	private Log4Logger(){
		
	}

	/* (non-Javadoc)
	 * @see org.aksonov.tools.Logger#d(java.lang.String, java.lang.String)
	 */
	@Override
	public void d(String target, String message) {
		Category.getInstance(target).debug(message);
	}

	/* (non-Javadoc)
	 * @see org.aksonov.tools.Logger#e(java.lang.String, java.lang.String)
	 */
	@Override
	public void e(String target, String error) {
		Category.getInstance(target).error(error);
	}

	/* (non-Javadoc)
	 * @see org.aksonov.tools.Logger#e(java.lang.String, java.lang.Throwable)
	 */
	@Override
	public void e(String target, Throwable error) {
		Category.getInstance(target).error(error.getMessage(), error);
	}

	/* (non-Javadoc)
	 * @see org.aksonov.tools.Logger#i(java.lang.String, java.lang.String)
	 */
	@Override
	public void i(String target, String message) {
		Category.getInstance(target).info(message);
		
	}

	/* (non-Javadoc)
	 * @see org.aksonov.tools.Logger#w(java.lang.String, java.lang.String)
	 */
	@Override
	public void w(String target, String message) {
		Category.getInstance(target).warn(message);
		
	}

}
