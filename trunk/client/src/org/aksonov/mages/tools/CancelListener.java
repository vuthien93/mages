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

import android.content.DialogInterface;

// TODO: Auto-generated Javadoc
/**
 * The listener interface for receiving cancel events. The class that is
 * interested in processing a cancel event implements this interface, and the
 * object created with that class is registered with a component using the
 * component's <code>addCancelListener<code> method. When
 * the cancel event occurs, that object's appropriate
 * method is invoked.
 * 
 * @see CancelEvent
 */
public abstract class CancelListener implements

DialogInterface.OnCancelListener, DialogInterface.OnClickListener {
	
	/** The empty listener. */
	public static CancelListener emptyListener = new CancelListener() {
		public void onCancel() {

		}
	};

	/**
	 * On cancel.
	 */
	public abstract void onCancel();

	/* (non-Javadoc)
	 * @see android.content.DialogInterface$OnClickListener#onClick(android.content.DialogInterface, int)
	 */
	public void onClick(DialogInterface Dialog, int whichButton) {
		onCancel();
	}

	/* (non-Javadoc)
	 * @see android.content.DialogInterface$OnCancelListener#onCancel(android.content.DialogInterface)
	 */
	public void onCancel(DialogInterface Dialog) {
		onCancel();
	}
}
