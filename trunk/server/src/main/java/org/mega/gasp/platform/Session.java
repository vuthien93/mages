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

import java.util.Map;

import org.aksonov.mages.entities.PlayerInfo;

/**
 * Session represent the connection of the user in the application.
 * So it is instantiate immedialtly after the login of the user.
 * The Session ID is used for identification of the user on a request.
 * 
 * @author PELLERIN Romain (pellerin@cnam.fr) - MEGA Project 
 * 
 */


public interface Session {

	/**
	 * Returns the Session ID of the Session object.
	 * 
	 * @return the Session ID
	 */
	int getSessionID();
	
	
	/**
	 * Returns the Actor ID of the user.
	 * 
	 * @return the Actor ID
	 */
	int getActorID();
	
	
	/**
	 * Returns the ActorSession ID if the user have already choiced a game session.
	 * 
	 * @return the ActorSession ID
	 */
	int getActorSessionID();

	
	/**
	 * Sets the ActorSession ID after game session choose by the user.
	 * @param aSID the ActorSession ID
	 */
	void setActorSessionID(int aSID);
	
	void setMasterApp(MasterApplicationInstance masterApp);
	
	boolean hasNewEvents();
	
	Map getEvents();
	void putInvitation(PlayerInfo info);
	 /**
     * Returns last time infos received for the user associated with the actorSession.
     * 
     * @return long the application timeout
     */
    long getTimeout();

    
    /**
     * Save last time infos received for the user associated with the actorSession.
     */
    void setTimeout(long _timeout);
    
    
    /**
     * Save last time infos received for the user associated with the actorSession.
     */
    void refreshTimeout();

}
