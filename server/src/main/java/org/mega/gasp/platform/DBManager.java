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
 * DBManager contains all the methods requiring an acces to the GASP DB.
 * 
 * @author PELLERIN Romain (pellerin@cnam.fr) - MEGA Project 
 */


public interface DBManager {
	
	/**
	 * Generate a DB persistant Actor ID. 
	 * 
	 * @return the actor ID
	 */ 
	int generateActorID();
	
	
	/**
	 * Verify if the connection to the GASP DB is ok.
	 * 	
	 * @return boolean
	 */
	boolean isDBConnectionOK();
	
	
	/**
	 * Verify if the login authentification of an Actor is valid 
	 * then return the authentified actor or null.
	 * 
	 * @param aID the actor ID
	 * @param username 
	 * @param password
	 * @return the Actor instance object
	 */
	Actor getActorIfAuthentificationOK(int aID, String username, String password);

	
	/**
	 * Register a new actor for the double (uid,appID) if the user have the appropriate rights.
	 * 
	 * @param uid
	 * @param appID
	 * @param username
	 * @param password
	 * @return the Actor ID
	 */
	int registerNewActor(String uid, int appID, String username, String password);


    /**
     * Saves in the DB the last used pseudo.
     * 
     * @param actorID
     * @param pseudoName
     */
    void saveLastUsedPseudo(int actorID, String pseudoName);
    
    void saveRating(int actorID, int rating);
    
}
