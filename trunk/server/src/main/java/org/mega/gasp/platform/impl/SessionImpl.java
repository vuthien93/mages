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

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.aksonov.mages.entities.PlayerInfo;
import org.aksonov.tools.Log;
import org.mega.gasp.platform.Actor;
import org.mega.gasp.platform.ApplicationInstance;
import org.mega.gasp.platform.MasterApplicationInstance;
import org.mega.gasp.platform.Session;

/**
 * Session represent the connection of the user in the application. So it is
 * instantiate immedialtly after the login of the user. The Session ID is used
 * for identification of the user on a request.
 * 
 * @author PELLERIN Romain (pellerin@cnam.fr) - MEGA Project
 */

public class SessionImpl implements Session {

	public static final String PLAYERS = "players";
	public static final String INVITATIONS = "invitations";
	public static final String GAMES = "games";
	
	private int sessionID;
	private int actorID;
	private int actorSessionID;
	private String pseudo;
	private long timeout;
	private MasterApplicationInstance masterApp;
	private List invitations = java.util.Collections.synchronizedList(new ArrayList());

	private long lastLobbyTime = 0;

	public void setMasterApp(MasterApplicationInstance masterApp) {
		this.masterApp = masterApp;
		lastLobbyTime = 0;
	}

	public boolean hasNewEvents() {
		return (masterApp.getLastAIListChangedTime() > lastLobbyTime || invitations.size() > 0 );
	}
	
	public void putInvitation(PlayerInfo info){
		synchronized (invitations){
			Log.d("SessionImpl", "putInvitation info=" + info);
			invitations.add(info);
		}
	}

	public Map getEvents() {
		Hashtable result = new Hashtable();

		if (masterApp.getLastAIListChangedTime() > lastLobbyTime) {
			lastLobbyTime = masterApp.getLastAIListChangedTime();

			Hashtable h = new Hashtable();
			int i = 0;
			// lobby list
			for (Iterator iter = masterApp.enumerateApplicationInstance(); iter
					.hasNext();) {

				ApplicationInstance appInstance = (ApplicationInstance) iter
						.next();
				if (appInstance.isJoinable()
						&& appInstance.getCustomData() != null) {
					Log.d("SessionImpl", "writing app instance: "
							+ appInstance.getApplicationInstanceID());
					h.put(i + "", appInstance.getCustomData());
					i++;
				}
			}
			result.put(GAMES, h);
			Hashtable pl = new Hashtable();
			i = 0;
			for (Iterator iter = masterApp.enumerateSession(); iter.hasNext();) {

				Session session = (Session) iter.next();
				Actor actor = masterApp.getActor(session.getActorID());

				PlayerInfo info = PlayerInfo.create();
				info.id = actor.getActorID();
				info.rating = actor.getRating();
				info.username = actor.getUsername();
				info.arg1 = session.getActorSessionID();
				info.arg2 = session.getSessionID();
				
				pl.put(i + "", info);
				i++;
			}
			result.put(PLAYERS, pl);
		}
		if (invitations.size() > 0){
			Hashtable inv = new Hashtable();
			synchronized (invitations){
				for (int i=0;i<invitations.size();i++){
					inv.put(i+"", invitations.get(i));
				}
				invitations.clear();
			}
			result.put(INVITATIONS, inv);
		}
		return result;

	}

	public SessionImpl(int aID, long _timeout) {
		actorID = aID;
		sessionID = PlatformImpl.getPlatform().getIDManager().generateSID();
		actorSessionID = 0;
		timeout = _timeout;
	}

	/**
	 * Returns the Session ID of the Session object.
	 * 
	 * @return the Session ID
	 */
	public int getSessionID() {
		return sessionID;
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
	 * Returns the ActorSession ID if the user have already choiced a game
	 * session.
	 * 
	 * @return the ActorSession ID
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
	 * Sets the ActorSession ID after game session choose by the user.
	 * 
	 * @param aSID
	 *            the ActorSession ID
	 */
	public void setActorSessionID(int aSID) {
		actorSessionID = aSID;
	}

	/**
	 * Set the current pseudo name of the user session.
	 */
	public void setPseudoName(String name) {
		pseudo = name;
	}

	/**
	 * Returns last time infos received for the user associated with the
	 * actorSession.
	 * 
	 * @return long the application timeout
	 */
	public long getTimeout() {
		return timeout;
	}

	/**
	 * Save last time infos received for the user associated with the
	 * actorSession.
	 */
	public void setTimeout(long _timeout) {
		timeout = _timeout;
	}

	/**
	 * Save last time infos received for the user associated with the
	 * actorSession.
	 */
	public void refreshTimeout() {
		setTimeout(System.currentTimeMillis());
	}

}
