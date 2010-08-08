/***
 * GASP: a gaming services platform for mobile multiplayer games.
 * Copyright (C) 2004 CNAM/INT
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
 * Contact: gasp-team@objectweb.org
 *
 * Author: Romain Pellerin
 */
package org.mega.gasp.platform;


/**
 * Actor represents the link between an application and a user.
 * The Actor ID is generated at the first login in the application,
 * it is permanent and stored in the DataBase on server side, it is also
 * stored on client side.
 * This Actor ID is generated only if the user have the required rigths
 * to the application.
 * 
 * @author PELLERIN Romain (pellerin@cnam.fr) - MEGA Project 
 */

public interface Actor {

	/**
	* Returns the Actor ID.
	* 
	* @return the Actor ID
	*/
	int getActorID();
	
	
	/**
	 * Returns the Application ID associated.
	 * 
	 * @return the Application ID
	 */
	int getApplicationID();
	
	
	/**
	 * Returns the username of the actor.
	 * 
	 * @return username
	 */
	String getUsername();
	
	
	/**
	 * Returns the the password of the actor.
	 * 
	 * @return password
	 */
	String getPassword();
	
	
	/**
	 * Returns the current pseudo name of the user session.
	 * 
	 * @return the ActorSession ID
	 */
	String getPseudoName();


	/**
	 * Set the current pseudo name of the user session.
	 */
	void setPseudoName(String name);

	
	/**
	 * Determines if the pseudo has modified during the session.
	 */
	boolean isPseudoHasModified();
	
	
	int getRating();
	
	void setRating(int rating);
}
