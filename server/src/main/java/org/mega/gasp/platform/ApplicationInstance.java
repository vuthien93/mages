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

import org.mega.gasp.event.DataEvent;
import org.mega.gasp.listener.OnDataEvent;
import org.mega.gasp.listener.OnEndEvent;
import org.mega.gasp.listener.OnJoinEvent;
import org.mega.gasp.listener.OnQuitEvent;
import org.mega.gasp.listener.OnStartEvent;
import org.mega.gasp.moods.CustomTypes;
import org.mega.gasp.platform.utils.ApplicationInstanceInfos;
import org.mega.gasp.server.GASPServer;



/**
 * ApplicationInstance represent a game session of a specific application.
 * It contains a vector of ActorSession representing the actors want to play in.
 * This classes manage the in game events.
 *
 * 
 * @author PELLERIN Romain (pellerin@cnam.fr) - MEGA Project 
 * 
 */

public interface ApplicationInstance extends OnJoinEvent, OnStartEvent, OnEndEvent ,OnDataEvent, OnQuitEvent{

	/**
	 * Returns the ID of the ApplicationInstance.
	 * 
	 * @return the ApplicationInstanceID
	 */
	int getApplicationInstanceID();
	
	/**
	 * Returns the GASP Server instance attached to this game
	 * @return the GASP Server instance
	 */
	GASPServer getServer();
	
	
	/**
	 * Returns the minimum number of actors required to start the game session.
	 * 
	 * @return the minimum number of actors
	 */
	int getMinActors();
	
	
	/**
	 * Returns the maximum number of actors can play the game session.
	 * 
	 * @return the maximum number of actors
	 */
	int getMaxActors();
	
	
	/**
	 * Returns the ActorId of the ApplicationInstanceID owner.
	 * 
	 * @return the Actor ID
	 */
	int getOwnerAID();
	
	
	/**
	 * Returns the encoder of the application associated with the application instance.
	 * 
	 * @return the Actor ID
	 */
	CustomTypes getCustomTypes();
	
	
	/**
	 * Determine if the ApplicationInstance is public or not.
	 * 
	 * @return boolean 
	 */
	boolean isPublic();
	
	
	/**
	 * Determine if the ApplicationInstance is running or not.
	 * 
	 * @return boolean
	 */
	boolean isRunning();
	
	
	/**
	 * Determine if the Owner Master Application must destroy the application instance or not.
	 * 
	 * @return boolean
	 */
	boolean isDestroyable();
	
	/**
	 * Determine if the the application instance is joignable by a player or not.
	 * 
	 * @return boolean
	 */
	boolean isJoinable();
	
	
	/**
	 * Determine if the the application instance is startable by the player owner or not.
	 * 
	 * @return boolean
	 */
	boolean isStartable();
	
	
	/**
	 * Create a new ActorSession in the ApplicationInstance corresponding to 
	 * the user Session object informations.
	 * Returns the ActorSession ID.
	 * 
	 * @param aIID the ApplicationInstance
	 * @param session the Session object of the user
	 * @return the ActorSession ID
	 */
	int createNewActorSession(int aIID, Session session);
	

	/**
	 * Returns the ActorSession object corresponding to the ActorSession ID.
	 * 
	 * @param actorSessionID the ActorSession ID
	 * @return the ActorSession object
	 */
	ActorSession getActorSession(int actorSessionID);

	
	/**
	 * Remove the ActorSession object corresponding to the ActorSessionID.
	 * 
	 * @param actorSessionID the ActorSesion ID
	 * @return boolean representing the operation success
	 */
	boolean removeActorSession(int actorSessionID);

	
	/**
	 * Determine if the vector of ActorSession objects is empty or not.
	 * 
	 * @return boolean "empty or not"
	 */
	boolean isActorSessionEmpty();


	/**
	 * Returns the current number of ActorSession objects, e.g the number of actors ready
	 * to play this game session.
	 * 
	 * @return the number of actors
	 */
	int actorSessionSize();
	

	/**
	 * Determine if the ActorSession associated with the ActorSession ID is contained by
	 * this ApplicationInstance or not.
	 * 
	 * @param actorSessionID
	 * @return boolean "present or not"
	 */
	boolean containsActorSession(int actorSessionID);

	
	/**
	 * For Lobby service, this method provide informations of the ApplicationInstance.
	 * 
	 * @return an ApplicationInstanceInfos object
	 */
	ApplicationInstanceInfos getApplicationInstanceInfos();


	/**
	 * Returns an iterator on the actor sessions vector.
	 * 
	 * @return iterator on ActorSession vector
	 */
	Iterator enumerateActorSession();
	
	
	/**
	 * Specify the name of the Application Instance.
	 * 
	 * @param appInstanceName
	 */
	void setApplicationInstanceName(String appInstanceName);
	
	
	/**
	 * Add the actor to the actors vector.
	 * 
	 * @param actorSession the ActorSession object
	 * @return boolean of the operation success
	 */
	boolean addActorSession(ActorSession actorSession);
	


    /**
     * Send a dataEvent to a specific actorSession.
     * 
     * @param actorSessionID
     * @param dataEvent
     */
    void sendDataToActorSession(int actorSessionID, DataEvent dataEvent);

    
    
    /**
     * Send player info to the actor session ASID.
     * In case of mobile intempestive deconnection, the reconnected mobile
     * must know the players state of the AI.
     * 
     * @param aSID
     */
    void sendPlayersInfos(int aSID);

    
    /**
     * If the pseudo already exist in the AI modify it, else no modifications.
     * 
     * @param name
     * @return String the checked pseudo
     */
    String treatPseudo(String name);

    
    /**
     *  Raise a JoinEvent to all other players containing the new pseudo
     * @param actorSessionID
     */
    void raisePseudoModification(int actorSessionID, String pseudoname);

    
    /**
     *  Retrieve the applicationID from the master application instance.
     * @param actorSessionID
     */
    int retrieveApplicationID();

	/**
	 * Provides optional custom data associated with actor session
	 * @return object
	 */
	Object getCustomData();
	
	/**
	 * Sets optional custom data associated with actor session
	 * @param data
	 */
	void setCustomData(Object data);
}