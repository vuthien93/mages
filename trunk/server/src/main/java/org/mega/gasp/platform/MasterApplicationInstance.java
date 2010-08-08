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
import java.util.Vector;



/**
 * MasterApplicationInstance represents the container of all ApplicationInstance of a specific application.
 * So a MasterApplicationInstance contains a vector of ApplicationInstance.
 * It contains also a vector of Session objects, representing the Actors currently using the Application.
 * Finally it contains a vector of the Actor objects, representing a link User-Application.
 * 
 * @author PELLERIN Romain (pellerin@cnam.fr) - MEGA Project 
 * 
 * 
 */

public interface MasterApplicationInstance {

	/**
	 * Returns the ID of the Master Application Instance.
	 * 
	 * @return MasterApplicationID 
	 */
	int getMasterApplicationID();
	

	/**
	 * Returns the ID of the application managed by the Master Application Instance.
	 * 
	 * @return MasterApplicationID 
	 */
	int getApplicationID();
	

    /**
     * Returns the model choosed for the application.
     */
    byte getApplicationModel();
    
	
	/**
	 * Create an Application Instance.
	 * 
	 * @param ownerSession the Session object representing the actor who create the Application Instance.
	 * @param minActors represents the minimum number of actors required to start the Application Instance.
	 * @param maxActors represents the maximum number of actors can play this Application Instance.
	 * @param actors if the Application Instance is private this vector contains the required actors
	 * else if it is a public Application Instance this vector is empty.
	 * 
	 * @return applicationInstanceID the ID of the Application Instance created.
	 */	
	int createApplicationInstance(Session ownerSession, int minActors, int maxActors, String[] actors);
	
	
	/**
	 * Returns the required ApplicationInstance object.
	 * 
	 * @param applicationInstanceID the ID of the required Application Instance
	 * 
	 * @return applicationInstance the object representing the Application Instance
	 */
	ApplicationInstance getApplicationInstance(int applicationInstanceID);


	/**
	 * Remove the required Application Instance.
	 * 
	 * @param applicationInstanceID
	 * 
	 * @return boolean telling us if the remove was correctly done or not.
	 */
	boolean removeApplicationInstance(int applicationInstanceID);
	
	public long getLastAIListChangedTime();
	
	public void updateLastAIListChangedTime();
	

	/**
	 * Determine if the vector of ApplicationInstance objects is currently empty or not.
	 * 
	 * @return boolean telling us if the vector is empty or not.
	 */
	boolean isApplicationInstanceEmpty();
	
	
	/**
	 * Returns the current size of the vector of ApplicationInstance objects.
	 * 
	 * @return nbApplicationInstance
	 */	
	int applicationInstanceSize();
	
	
	/**
	 * Returns an ApplicationInstance vector iterator.
	 * 
	 * @return iterator
	 */
	Iterator enumerateApplicationInstance();
	
	
	/**
	 * Determine if the Application Instance associated with applicationInstanceID is hold by this
	 * Master Application.
	 * 
	 * @param applicationInstanceID
	 * 
	 * @return boolean
	 */
	boolean containsApplicationInstance(int applicationInstanceID);


	/**
	 * Returns a random ApplicationInstance currently not started.
	 * 
	 * @return the ID of an ApplicationInstance object
	 */
	int getAnAIID();
	
	
	/**
	 * The user associated to the sID wants to quit the ApplicationInstance
	 * he has joined.
	 * This method remove the ActorSession associated and update his
	 * Session informations.
	 * 
	 * @param aSID
	 */
	//void quitCurrentAI(int aSID);
	
	
	/**
	 * Unlog the user represented by this session , delete Actor, Session, ActorSession objects
	 * linked to this user, and also the ApplicationInstance would created by this user.
	 * 
	 * @param sid
	 */
	void unlog(int sid);
	
	
	/**
	 * Returns the ApplicationInstance object containing the ActorSession linked to the Session.
	 * 
	 * @param aSID the ActorSession ID
	 * @return the ApplicationInstance object
	 */
	ApplicationInstance getActorSessionAI(int aSID);
	
	
	/**
	 * Return the session associated to the aID.
	 * 
	 * @param aID
	 * @return the Session object associated
	 */
	Session getAIDAssociatedSession(int aID);
	
	
	/**
	 * Return the session associated to the aSID.
	 * 
	 * @param aSID
	 * @return the Session object associated
	 */
	Session getASIDAssociatedSession(int aSID);
	
	
	/************* Sessions managment *****************************/
	
	
	/**
	 * Create a new session associated with the actor ID.
	 * 
	 * @param aID the actor ID
	 * 
	 * @return the ID of the session created
	 */
	int createNewSession(int aID);
	
	
	/**
	 * Returns the corresponding session
	 * 
	 * @param sID the session ID
	 * 
	 * @return the Session object
	 */
	Session getSession(int sID);
	
	
	/**
	 * Remove the corresponding Session object.
	 * 
	 * @param sID
	 * 
	 * @return boolean
	 */
	boolean removeSession(int sID);

	
	/**
	 * Determine if the MasterApplicationInstance is the owner of the session associated with the SessionID.
	 * 
	 * @param sID the Session ID
	 * 
	 * @return boolean
	 */
	boolean isSessionOwner(int sID);
	
	
	/**
	 * Return the current number of Session objects in the Session vector. 
	 * 
	 * @return the number of sessions
	 */
	int sessionSize();

	
	/**
	 * Return the current number of Session objects in the Session vector. 
	 * 
	 * @return iterator on Session vector
	 */
	Iterator enumerateSession();
	
	
	
	/************* Actors managment *****************************/
	
	/**
	 * Add the Actor object actor to the vector of actors in the Master Application.
	 * 
	 * @param actor the Actor object to add
	 */
	void addActor(Actor actor);
	
	
	/**
	 * Return the Actor object associated with the actorID.
	 * 
	 * @param aID the actor ID 
	 * 
	 * @return the Actor object
	 */
	Actor getActor(int aID);
	
	
	/**
	 * Remove the Actor object associated with the actorID.
	 * 
	 * @param aID the actor ID
	 * @return boolean representing the success of the operation
	 */
	boolean removeActor(int aID);
	
	
	/**
	 * Determine if the Master Application is the owner of the Actor object associated with the actorID.
	 * 
	 * @param aID the actor ID
	 * 
	 * @return boolean 
	 */
	boolean isActorOwner(int aID);

	
	/**
	 * Return the current number of Actor objects in the Actor vector.
	 * 
	 * @return the number of actors
	 */
	int actorSize();
	
	
	/**
	 * Return the current number of Session objects in the Session vector. 
	 * 
	 * @return iterator on Actor vector 
	 */
	Iterator enumerateActor();
	
	
	
	/**** pas sûr que ces fonctions servent encore + fonctions de debuggage ****/
	Vector getSessions();
	Vector getActors();

}
