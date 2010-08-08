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

import java.util.Vector;

import org.aksonov.mages.entities.PlayerInfo;
import org.mega.gasp.event.Event;

/**
 * ActorSession represent the link between a user (represented by instances of Session and Actor)
 * and an ApplicationInstance.
 * It receive and transmit all the events or message in an ApplicationInstance, in game it is the
 * communication link  between the client and the game session server.
 * 
 * @author PELLERIN Romain (pellerin@cnam.fr) - MEGA Project 
 */


public interface ActorSession {

	
	/**
	 * Returns the ApplicationInstance ID linked.
	 * 
	 * @return the ApplicationInstance ID
	 */
	int getApplicationInstanceID();


	/**
	 * Returns the Actor ID of the user.
	 * 
	 * @return the Actor ID
	 */
	int getActorID();
	

	/**
	 * Returns the Session ID of the user
	 * 
	 * @return the Session ID
	 */
	int getSessionID();

	
	/**
	 * Returns the ActorSession ID.
	 * 
	 * @return the ActorSession ID.
	 */
	int getActorSessionID();

	
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
	
	/**
	 * Return the events stored since the last request from the actor.
	 * 
	 * @return the stack of events
	 */
	//Vector getEvents();
	
	/**
	 * Return the new events 
	 * 
	 * @return the stack of events
	 */
	Vector getEvents();
	
	
	/**
	 * Put the event on the stack of the events must be read by the actor.
	 * 
	 * @param e the Event
	 */
	void raiseEvent(Event e);
	
	/**
	 * Returns is current session connected to the actor
	 * @return connected status
	 */
	boolean isConnected();
	
	/**
	 * Sets connected status
	 * @param connected connected status
	 */
	void setConnected(boolean connected);
	
	/**
	 * Determines is this actor session has new events to send
	 * @return
	 */
	boolean hasNewEvents();
}
