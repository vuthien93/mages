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


import java.util.Iterator;

import org.mega.gasp.platform.utils.PropertiesReader;



/**
 * Platform is the base of the GAming Services Platform (GASP), the container of all GP representing objects.
 * So it contains all MasterApplicationInstance objects in a vector.
 * It hold all of the service instances (Lobby,...) and system instances (DB manager, ID manager, ...).
 * 
 * 
 * @author PELLERIN Romain (pellerin@cnam.fr) - MEGA Project 
 * 
 */

public interface Platform {

	/**
	 * Returns the MasterApplicationInstance associated with the application ID or if it is not
	 * already instantiates, initialize it.
	 * 
	 * @param applicationID the application ID
	 * @return the MasterApplicationInstance object
	 */
	MasterApplicationInstance getMasterApplicationInstance(int applicationID);


	/**
	 * Remove the MasterApplicationInstance associated with the application ID.
	 * 
	 * @param applicationID the application ID
	 * @return boolean of the operation success
	 */
	boolean removeMasterApplicationInstance(int applicationID);


	/**
	 * Determine if the vector of MasterApplicationInstance is empty or not.
	 * 
	 * @return boolean
	 */
	boolean isMasterApplicationInstanceEmpty();

	
	/**
	 * Return the number of MasterApplicationInstance currently instantiated.
	 * 
	 * @return the number of MasterApplication instances.
	 **/
	int masterApplicationInstanceSize();
	
	
	/**
	 * Returns an iterator on the MasterApplicationInstance vector.
	 * 
	 * @return iterator
	 */
	Iterator enumerateMasterApplicationInstance();


	/**
	 * Determine if the MasterApplicationInstance of the application associated with the
	 * application ID is initialized or not.
	 * 
	 * @param applicationID
	 * @return boolean
	 */
	boolean containsMasterApplicationInstance(int applicationID);

	
	/**
	 * Return the Lobby service.
	 * 
	 * @return the Lobby object
	 */
	Lobby getLobby();
	
	
	/**
	 * Return the platform ID manager .
	 * 
	 * @return the IDManager object
	 */
	IDManager getIDManager();
	
	
	/**
	 * Return the platform DB manager .
	 * 
	 * @return the DBManager object
	 */
	DBManager getDBManager();
	
	
	/**
	 * Return the platform DB manager .
	 * 
	 * @return the DBManager object
	 */
	PropertiesReader getPropertiesReader();
	
	/**
	 * Return the MasterApplicationInstance containing the sID.
	 * 
	 * @param sID the session ID
	 * @return the MasterApplicationInstance object
	 */
	public MasterApplicationInstance getSessionOwner(int sID);
	
	
	/**
	 * Return the ApplicationInstance containing the aSID.
	 * 
	 * @param aSID the actor session ID
	 * @return the MasterApplicationInstance object
	 */
	public ApplicationInstance getActorSessionOwner(int aSID);
	
	
	/**
	 * Return the MasterApplicationInstance containing the aID.
	 * 
	 * @param aID the actor ID
	 * @return the MasterApplicationInstance object
	 */
	public MasterApplicationInstance getActorOwner(int aID);
	
	
	/**
	 * Log a user in the platform, authentified by actorID, username and password.
	 * It consists of creating a new Session in the correct MasterApplicationInstance
	 * and return the session ID.
	 * 
	 * @param aID the Actor ID attributed at the first login
	 * @param username
	 * @param password
	 * 
	 * @return the SessionID of the session created or 0 if the authentification informations
	 * are not valid
	 */
	int login(int aID, String username, String password);
	
	
	
	/**
	 * Provide to a user with a valid session ID to join a specific ApplicationInstance of
	 * the application open by the user. 
	 * Then create un ActorSession in the ApplicationInstance and return the ActorSessionID.
	 * 
	 * @param sID the Session ID
	 * @param aIID the ApplicationInstance ID
	 * @return the ActorSession ID
	 */
	int joinAI(int sID, int aIID);
	
	
	/**
	 * Provide to a user with a valid session ID to join a random ApplicationInstance
	 * corresponded to tbe application opened by the user.
	 * 
	 * @param sID the Session ID
	 * @return the ActorSession ID
	 */
	int joinAIRnd(int sID);
	
	
	/**
	 * Provide to a user with a valid session ID to create an ApplicationInstance with a minimum
	 * start number of actors and a maximum number of actors.
	 * The ApplicationInstance status is:
	 * "public": if the table of actors is empty.
	 * "private": if the table of actors contains some Actor ID.
	 * Returns the ID of the ApplicationInstance created.
	 * 
	 * @param sID the Session ID
	 * @param minActors minimum number of actors for starting
	 * @param maxActors maximum number of actors
	 * @param actors a table of Actor ID
	 * @return the ApplicationInstance ID
	 */
	int createAI(int sID, int minActors, int maxActors, String[] actors);
	
	
	/**
	 * This method is called when a user quit the application.
	 * ActorSession, Session, Actor instances corresponding to the user are removed.
	 * 
	 * @param sID the Session ID
	 */
	void quit(int sID);
	
}


	
	
