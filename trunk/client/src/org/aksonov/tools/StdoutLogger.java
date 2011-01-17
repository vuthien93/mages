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
package org.aksonov.tools;


public class StdoutLogger implements Logger {
	public static final Logger instance = new StdoutLogger();
	
	public StdoutLogger(){
		
	}

	
	public void d(String target, String message) {
		System.out.println("[DEBUG " + target + "] "  + message);
	}

	
	public void e(String target, String message) {
		System.out.println("[ERROR " + target + "] "  + message);
	}

	
	public void e(String target, Throwable error) {
		System.out.println("[ERROR " + target + "] "  + error.getMessage());
		error.printStackTrace();
	}

	
	public void i(String target, String message) {
		System.out.println("[INFO " + target + "] "  + message);
	}

	
	public void w(String target, String message) {
		System.out.println("[WARN " + target + "] "  + message);
	}
	
}
