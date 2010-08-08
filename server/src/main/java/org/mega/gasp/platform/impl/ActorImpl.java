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
package org.mega.gasp.platform.impl;

import org.mega.gasp.platform.Actor;


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


public class ActorImpl implements Actor{

	private int actorID;
	private int appID;
	private String name;
	private String pwd;
	private String pseudo;
	private int rating;
	private boolean pseudoModified;
	
	
	public ActorImpl(int aID, int applicationID, String username, String password, String lastUsedPseudo, int rating){
		actorID = aID;
		appID = applicationID;
		name = username;
		pwd = password;
		this.rating = rating;
		
		pseudo = lastUsedPseudo;
		pseudoModified = false;
	}
	
	/**
	 * Returns the rating
	 */
	public int getRating(){
		return rating;
	}
	
	
	public void setRating(int rating){
		this.rating = rating;
	}
	/**
	 * Returns the Actor ID.
	 * 
	 * @return the Actor ID
	 */
	public int getActorID() {
		return actorID;
	}
	
	
	/**
	 * Returns the Application ID associated.
	 * 
	 * @return the Application ID
	 */
	public int getApplicationID() {
		return appID;
	}

	
	/**
	 * Returns the username of the actor.
	 * 
	 * @return username
	 */
	public String getUsername(){
		return name;
	}

	
	/**
	 * Returns the the password of the actor.
	 * 
	 * @return password
	 */
	public String getPassword() {
		return pwd;
	}
	
	
	/**
	 * Returns the current pseudo name of the user session.
	 * 
	 * @return the ActorSession ID
	 */
	public String getPseudoName() {
		return pseudo;
	}
	
	
	/**
	 * Set the current pseudo name of the user session.
	 */
	public void setPseudoName(String name) {
		pseudo = name;
		pseudoModified = true;
	}
	
	
	/**
	 * Tells if the pseudo has modified during the session.
	 */
	public boolean isPseudoHasModified() {
		return pseudoModified;
	}
}
