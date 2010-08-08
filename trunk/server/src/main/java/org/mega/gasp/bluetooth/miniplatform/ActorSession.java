
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

import java.util.Vector;

import org.mega.gasp.event.Event;


/**
 * ActorSession represent the link between a user (represented by instances of Session and Actor)
 * and an ApplicationInstance.
 * It receive and transmit all the events or message in an ApplicationInstance, in game it is the
 * communication link  between the client and the game session server.
 * 
 * @author PELLERIN Romain (pellerin@cnam.fr) - MEGA Project  
 */


public class ActorSession {

	private int aIID;
	private int aID;
	private int aSID;
	private Vector events; 
	// storage of events must be send to the user represented by the actor session.

	
	public ActorSession(int applicationInstanceID, int actorID, int actorSessionID){
		aIID = applicationInstanceID;
		aID = actorID;
		aSID = actorSessionID;
	}

	
	/**
	 * Returns the ApplicationInstance ID linked.
	 * 
	 * @return the ApplicationInstance ID
	 */
	public int getApplicationInstanceID() {
		return aIID;
	}
	
	
	/**
	 * Returns the Actor ID of the user.
	 * 
	 * @return the Actor ID
	 */
	public int getActorID() {
		return aID;
	}
	
	
	/**
	 * Returns the ActorSession ID.
	 * 
	 * @return the ActorSession ID.
	 */
	public int getActorSessionID() {
		return aSID;
	}

	
	/**
	 * Return the events stored since the last request from the actor.
	 * 
	 * @return the stack of events
	 */
	public synchronized Vector getEvents(){
        Vector v = new Vector(events);
        events.removeAllElements();
	    return purgeEvents(v);
	}
	
	
	/**
	 * Put the event on the stack of the events must be read by the actor.
	 * 
	 * @param e the Event
	 */
	public void raiseEvent(Event e){
	    events.insertElementAt(e,events.size());
	}
	

    /**
     * Purge the deprecated events.
     * 
     * @param v the copy of the events vector
     */
    private Vector purgeEvents(Vector v) {
       return v; 
    }
	
	
}
