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
package org.mega.gasp.bluetooth.miniplatform;

import java.util.Iterator;
import java.util.Vector;

import org.mega.gasp.event.*;
import org.mega.gasp.event.impl.*;
import org.mega.gasp.listener.*;
import org.mega.gasp.moods.CustomTypes;




/**
 * ApplicationInstance represent a game session of a specific application.
 * It contains a vector of ActorSession representing the actors want to play in.
 * This classes manage the in game events.
 * 
 * @author PELLERIN Romain (pellerin@cnam.fr) - MEGA Project  
 */
public class ApplicationInstance implements OnJoinEvent, OnStartEvent, OnEndEvent ,OnDataEvent, OnQuitEvent {

	private Vector actorSessions;
	private int applicationInstanceID;
	private int ownerActorSessionID;
	private int minA;
	private int maxA;
	private String applicationInstanceName; // for later implementation of game instance naming
	private boolean isPublicAI; // public or private  
	private boolean isRunningAI; // running or waiting for players  
	private Vector listenners;
    private CustomTypes customTypes;
    private IDManager idManager;
	

	public ApplicationInstance(int actorID, int minActors, int maxActors, String[] actors, CustomTypes encoder){
		actorSessions = new Vector();
		ownerActorSessionID = this.createNewActorSession(applicationInstanceID,actorID);
		idManager = new IDManager();
		applicationInstanceID = 1; // eugeniuz: multiple application instance in a BT area?
		minA = minActors;
		maxA = maxActors;
		if (actors.length == 0) isPublicAI = true;
		else{
			this.addActors(actors);
			isPublicAI = false;
		}
		isRunningAI = false;
		customTypes = encoder;
	}
	
	
	/**
	 * Returns the ID of the ApplicationInstance.
	 * 
	 * @return the ApplicationInstanceID
	 */
	public int getApplicationInstanceID() {
		return applicationInstanceID;
	}


	/**
	 * Returns the minimum number of actors required to start the game session.
	 * 
	 * @return the minimum number of actors
	 */
	public int getMinActors(){return minA;}
	
	
	/**
	 * Returns the maximum number of actors can play the game session.
	 * 
	 * @return the maximum number of actors
	 */
	public int getMaxActors(){return maxA;}
	
	
	/**
	 * Returns the ActorId of the ApplicationInstanceID owner.
	 * 
	 * @return the Actor ID
	 */
	public int getOwnerAID(){return ownerActorSessionID;}

	
	/**
	 * Returns the encoder of the application associated with the application instance.
	 * 
	 * @return the Actor ID
	 */
    public CustomTypes getCustomTypes() {
        return customTypes;
    }
    
	
	/**
	 * Determine if the ApplicationInstance is public or not.
	 * 
	 * @return boolean 
	 */
	public boolean isPublic(){
	    return isPublicAI;
	}
	
	
	public boolean isRunning() {
	    return isRunningAI;
	}
	
	/**
	 * Create a new ActorSession in the ApplicationInstance corresponding to 
	 * the user actorID.
	 * Returns the ActorSession ID.
	 * 
	 * @param aIID the ApplicationInstance
	 * @param actorID the actorID of the user
	 * @return the ActorSession ID
	 */
	public int createNewActorSession(int aIID, int actorID) {
		ActorSession actorSession = new ActorSession(aIID,actorID,idManager.generateASID());
		this.addActorSession(actorSession);
		JoinEvent joinEv = new JoinEventImpl(actorSession.getActorSessionID(),"A IMPLEMENTER:username");
		onJoinEvent(joinEv);
		return actorSession.getActorSessionID();
	}
	
	
	/**
	 * Returns the ActorSession object corresponding to the ActorSession ID.
	 * 
	 * @param actorSessionID the ActorSession ID
	 * @return the ActorSession object
	 */
	public ActorSession getActorSession(int actorSessionID) {
		ActorSession actorSession= null;
		for (Iterator iter = enumerateActorSession(); iter.hasNext();){
			actorSession = (ActorSession) iter.next();
			int aSessionID = actorSession.getActorSessionID();
			if ( aSessionID == actorSessionID){
				return actorSession;
			}

		}
		return null;
	}
	
	
	/**
	 * Remove the ActorSession object corresponding to the ActorSessionID.
	 * 
	 * @param actorSessionID the ActorSesion ID
	 * @return boolean representing the operation success
	 */
	public boolean removeActorSession(int actorSessionID) {
		ActorSession actorSession= null;
		int index = 0;
		for (Iterator iter = enumerateActorSession(); iter.hasNext();){
			actorSession = (ActorSession) iter.next();
			int aSessionID = actorSession.getActorSessionID();
			if ( aSessionID == actorSessionID){
				actorSessions.remove(index);
				QuitEvent quitEv = new QuitEventImpl(actorSessionID);
				onQuitEvent(quitEv);
				idManager.releaseASID(actorSessionID);
				return true;
			}
			index++;
		}
		return false;
	}
	
	
	/**
	 * Determine if the vector of ActorSession objects is empty or not.
	 * 
	 * @return boolean "empty or not"
	 */
	public boolean isActorSessionEmpty() {
		return actorSessions.isEmpty();
	}
	
	
	/**
	 * Returns the current number of ActorSession objects, e.g the number of actors ready
	 * to play this game session.
	 * 
	 * @return the number of actors
	 */
	public int actorSessionSize() {
		return actorSessions.size();
	}
	
	
	/**
	 * Determine if the ActorSession associated with the ActorSession ID is contained by
	 * this ApplicationInstance or not.
	 * 
	 * @param actorSessionID
	 * @return boolean "present or not"
	 */
	public boolean containsActorSession(int actorSessionID) {
		ActorSession actorSession = null;
		for (Iterator iter = enumerateActorSession(); iter.hasNext();){
			actorSession = (ActorSession) iter.next();
			if (actorSession.getActorSessionID() == actorSessionID)
				return true;
		}
		return false;
	}
	
	
	/**
	 * For Lobby service, this method provide informations of the ApplicationInstance.
	 * 
	 * @return an ApplicationInstanceInfos object
	 */
	public ApplicationInstanceInfos getApplicationInstanceInfos() {
		Vector playerList = new Vector();
		int nbPlayer = this.actorSessionSize();
		
		ActorSession actorSession = null;
		for(Iterator iter = this.enumerateActorSession(); iter.hasNext();){
			actorSession = (ActorSession) iter.next();
			playerList.add("pseudo");
			playerList.add(new Integer(actorSession.getActorID()));
		}
		
		ApplicationInstanceInfos appInstanceInfos = 
			new ApplicationInstanceInfos(
				this.getApplicationInstanceID(),
				nbPlayer,
				playerList
			);
		
		return appInstanceInfos;
	}
	
	
	/**
	 * Returns the name of the ApplicationInstance.
	 * 
	 * @return the name of the AI
	 */
	public String getApplicationInstanceName() {
		return applicationInstanceName;
	}
	
	
	/**
	 * Returns an iterator on the actor sessions vector.
	 * 
	 * @return iterator on ActorSession vector
	 */
	public Iterator enumerateActorSession() {
		return actorSessions.iterator();
	}
	
	
	/**
	 * Set the pool of actors required for a private Application
	 * Instance.
	 * 
	 * @param actors the table of actorID
	 */
	private void addActors(String[] actors) {
		//a implementer
	}
	
	
	/**
	 * Specify the name of the Application Instance.
	 * 
	 * @param appInstanceName
	 */
	public void setApplicationInstanceName(String appInstanceName) {
		applicationInstanceName = appInstanceName;
	}

	
	/**
	 * Add the actor to the actors vector.
	 * 
	 * @param actorSession the ActorSession object
	 * @return boolean of the operation success
	 */
	public boolean addActorSession(ActorSession actorSession){
		if (this.containsActorSession(actorSession.getActorSessionID()))
			return false;
		else{
			actorSessions.add(actorSession);
			return true;
		}
	}

	
	/**
	 * Start the game managed by the ApplicationInstance.
	 * 
	 * @param aSID the ActorSession ID
	 * @return boolean of the operation success
	 */
	public boolean start(int aSID) {
        if (ownerActorSessionID == aSID){
            StartEvent startEv = new StartEventImpl(aSID);
            notifyToAllOtherActorSession(startEv);
            notifyStartToListenners(startEv);
            isRunningAI = true;
        }
        return false;
    }
	
	
	/**
	 * End the game managed by the ApplicationInstance.
	 * 
	 * @param aSID the ActorSession ID
	 * @return boolean of the operation success
	 */
	public boolean end(int aSID) {
        if (ownerActorSessionID == aSID){
            EndEvent endEv = new EndEventImpl(aSID);
            notifyToAllOtherActorSession(endEv);
            notifyEndToListenners(endEv);
            isRunningAI = false;
        }
        return false;
    }
	
	
	/*********************************************************/
	/** 	In game Events managment methods		 	    **/
	/**     & implementation of the events superclasses     **/
	/*********************************************************/
	
	
    public void onJoinEvent(JoinEvent e) {
        notifyToAllOtherActorSession(e);
        notifyJoinToListenners(e);
    }


    public void onStartEvent(StartEvent e) {
        notifyToAllOtherActorSession(e);
        notifyStartToListenners(e);
    }


    public void onEndEvent(EndEvent e) {
        notifyToAllOtherActorSession(e);
        notifyEndToListenners(e);
    }


    public void onDataEvent(DataEvent e) {
        notifyDataToListenners(e);
    }


    public void onQuitEvent(QuitEvent e) {
        notifyToAllOtherActorSession(e);
        notifyQuitToListenners(e);
    }
    
    
    public void addListenner(Object listenner){
        listenners.add(listenner);
    }
    

    /**
     * Put the event in event vectors of the ApplicationInstance 
     * ActorSessions.
     * 
     * @param e the event
     */
    private void notifyToAllOtherActorSession(Event e) {
        for(Iterator iter = actorSessions.iterator(); iter.hasNext();){
            ActorSession actorSession = (ActorSession) iter.next();
            if (actorSession.getActorSessionID()!= e.getActorSessionID()) actorSession.raiseEvent(e); 
        }
    }
    
    
    /**
     * Raise the JoinEvent to all external listenners, in particular 
     * the game logic threads. 
     * 
     * @param e the JoinEvent
     */
    private void notifyJoinToListenners(JoinEvent e) {
        for(Iterator iter = listenners.iterator(); iter.hasNext();){
            OnJoinEvent listenner = (OnJoinEvent) iter.next();
            listenner.onJoinEvent(e);
        }
    }
    
    
    /**
     * Raise the EndEvent to all external listenners, in particular 
     * the game logic threads. 
     * 
     * @param e the EndEvent
     */
    private void notifyEndToListenners(EndEvent e) {
        for(Iterator iter = listenners.iterator(); iter.hasNext();){
            OnEndEvent listenner = (OnEndEvent) iter.next();
            listenner.onEndEvent(e);
        }
        
    }
    
    
    /**
     * Raise the StartEvent to all external listenners, in particular 
     * the game logic threads. 
     * 
     * @param e the StartEvent
     */
    private void notifyStartToListenners(StartEvent e) {
        for(Iterator iter = listenners.iterator(); iter.hasNext();){
            OnStartEvent listenner = (OnStartEvent) iter.next();
            listenner.onStartEvent(e);
        }
        
    }
    
    
    /**
     * Raise the QuitEvent to all external listenners, in particular
     * the game logic threads. 
     * 
     * @param e the QuitEvent
     */
    private void notifyQuitToListenners(QuitEvent e) {
        for(Iterator iter = listenners.iterator(); iter.hasNext();){
            OnQuitEvent listenner = (OnQuitEvent) iter.next();
            listenner.onQuitEvent(e);
        }
        
    }
    
    
    /**
     * Raise the DataEvent to all external listenners, in particular
     * the game logic threads. 
     * 
     * @param e the DataEvent
     */
    private void notifyDataToListenners(DataEvent e) {
        for(Iterator iter = listenners.iterator(); iter.hasNext();){
           OnDataEvent listenner = (OnDataEvent) iter.next();
           listenner.onDataEvent(e);
        }
        
    }


    

}
