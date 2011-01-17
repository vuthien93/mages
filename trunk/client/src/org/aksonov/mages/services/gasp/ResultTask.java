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
package org.aksonov.mages.services.gasp;

import org.aksonov.tools.Log;

// TODO: Auto-generated Javadoc
/**
 * The Class ResultTask.
 */
public abstract class ResultTask implements Runnable {
	
	/** The Constant TIMEOUT_FAILURE. */
	public final static int TIMEOUT_FAILURE = -1;
	
	/** The Constant PERMANENT_FAILURE. */
	public final static int PERMANENT_FAILURE = -200;
	
	/** The Constant SUCCESS. */
	public final static int SUCCESS = 100;
	
	/** The Constant CANCELLED. */
	public final static int CANCELLED = -300;

	/** The Constant TIMEOUT. */
	public static final int TIMEOUT = 10000;
	
	/** The Constant MAX_ATTEMPTS. */
	public static final int MAX_ATTEMPTS = 3;
	
	/** The Constant ATTEMPT_TIMEOUT. */
	public static final int ATTEMPT_TIMEOUT = 4000;
	
	/** The is stopped. */
	private boolean isStopped = false;

	/** The attempt. */
	private int attempt = 0;
	
	/** The max attempts. */
	private final int maxAttempts;

	/**
	 * Execute.
	 * 
	 * @return the int
	 */
	public abstract int execute();

	/**
	 * Instantiates a new result task.
	 */
	public ResultTask() {
		this(MAX_ATTEMPTS);
	}

	/**
	 * Instantiates a new result task.
	 * 
	 * @param maxAttempts
	 *            the max attempts
	 */
	public ResultTask(int maxAttempts) {
		this.maxAttempts = maxAttempts;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		long last = System.currentTimeMillis();
		attempt = 0;
		boolean isFinished = false;
		Log.d(this.getClass().getName(), " Task starting");
		while (attempt < maxAttempts) {
			attempt++;
			if (System.currentTimeMillis() - last > TIMEOUT)
				break;
			int res = execute();
				if (res >= 0 && !isFinished) {
					isFinished = true;
					break;
				} else if (!isFinished){
					isFinished = true;
					break;
				}
		}
	}

	/**
	 * Start.
	 */
	public void start() {
		new Thread(this).start();
	}

}
