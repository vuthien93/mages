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

// import java.util.Iterator;
import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Vector;

import org.aksonov.tools.Log;
import org.apache.log4j.Category;
import org.mega.gasp.event.Event;
import org.mega.gasp.platform.ActorSession;
import org.mega.gasp.platform.Session;

/**
 * ActorSession represent the link between a user (represented by instances of
 * Session and Actor) and an ApplicationInstance. It receive and transmit all
 * the events or message in an ApplicationInstance, in game it is the
 * communication link between the client and the game session server.
 * 
 * @author PELLERIN Romain (pellerin@cnam.fr) - MEGA Project
 */

public class ActorSessionImpl implements ActorSession {
	private boolean connected = false;
	private int applicationInstanceID;
	private int actorID;
	private int sessionID;
	private int actorSessionID;
	private String pseudo;

	private long lastConfirmedTime = -1;
	private long lastRequestTime = -1;

	private SortedMap events = Collections.synchronizedSortedMap(new TreeMap<Integer,Event>());
	// private SortedMap events = Collections.synchronizedSortedMap(new
	// TreeMap());
	private Category cat;

	private int lastConfirmedId = -1;
	private int lastSentId = -1;
	private int max = 0;

	public void confirmId(int id) {
		Log.d("ActorSessionImpl", "Confirmed id : " + id);
		
		if (lastConfirmedId < id) {
			lastConfirmedTime = System.currentTimeMillis();
			lastConfirmedId = id;
		}
		synchronized (events){
			events.remove(id);
		}
	}

	// storage of events must be send to the user represented by the actor
	// session.
	private boolean pseudoModified;

	public ActorSessionImpl(int aIID, Session session) {
		applicationInstanceID = aIID;
		actorID = session.getActorID();
		sessionID = session.getSessionID();
		cat = Category.getInstance("GASPLogging");
		actorSessionID = PlatformImpl.getPlatform().getIDManager()
				.generateASID();
		session.setActorSessionID(actorSessionID);
	}

	/**
	 * Returns the ApplicationInstance ID linked.
	 * 
	 * @return the ApplicationInstance ID
	 */
	public int getApplicationInstanceID() {
		return applicationInstanceID;
	}

	/**
	 * Returns the Actor ID of the user.
	 * 
	 * @return the Actor ID
	 */
	public int getActorID() {
		return actorID;
	}

	/**
	 * Returns the Session ID of the user
	 * 
	 * @return the Session ID
	 */
	public int getSessionID() {
		return sessionID;
	}

	/**
	 * Returns the ActorSession ID.
	 * 
	 * @return the ActorSession ID.
	 */
	public int getActorSessionID() {
		return actorSessionID;
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

	/**
	 * Return the events stored since the last request from the actor.
	 * 
	 * @return the stack of events
	 */
	public Vector getEvents() {
		cat.debug("AS(" + actorSessionID + "): calls getEvents(), from : "
				+ getLastId());

		synchronized (events) {
			Vector v =new Vector(events
						.subMap(getLastId() + 1, max).values());
			lastSentId = max - 1;	
			cat.debug("AS(" + actorSessionID + "): size = " + v.size());

			return v;
		}
	}
	
	private int getLastId(){
		long time = System.currentTimeMillis();
		if (time - lastConfirmedTime > 3000 && time - lastRequestTime > 3000) {
			lastRequestTime = time;
			return lastConfirmedId;
		} else {
			return lastSentId;
		}
		
	}

	/**
	 * Put the event on the stack of the events must be read by the actor.
	 * 
	 * @param e
	 *            the Event
	 */
	public void raiseEvent(Event e) {
		synchronized (events) {
			e.setId(max++);
			events.put(e.getId(), e);
			cat.debug("AS(" + actorSessionID + "): event arrived code "
					+ e.getCode() + ", id of the event is  " + e.getId()
					+ " event is " + e + " currentId= " + max + " " + " " + hasNewEvents());
		}
	}

	public boolean isConnected() {
		return connected;
	}

	public void setConnected(boolean connected) {
		this.connected = connected;
	}

	public boolean hasNewEvents() {
		synchronized (events){
			boolean result = getLastId() < max - 1;
		//	cat.debug("AS(" + actorSessionID + ") hasNewEvents= " + result +" lastConfirmedId:" + lastConfirmedId + " Max: " + max 
		//			+" last send id: " + lastSentId);
			return result;
		}
	}

}
