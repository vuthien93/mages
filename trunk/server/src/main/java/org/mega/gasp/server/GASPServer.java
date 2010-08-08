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
package org.mega.gasp.server;

import org.aksonov.mages.entities.PlayerInfo;
import org.mega.gasp.event.DataEvent;
import org.mega.gasp.event.EndEvent;
import org.mega.gasp.event.JoinEvent;
import org.mega.gasp.event.QuitEvent;
import org.mega.gasp.event.StartEvent;
import org.mega.gasp.listener.OnDataEvent;
import org.mega.gasp.listener.OnEndEvent;
import org.mega.gasp.listener.OnJoinEvent;
import org.mega.gasp.listener.OnQuitEvent;
import org.mega.gasp.listener.OnStartEvent;
import org.mega.gasp.platform.ApplicationInstance;
import org.mega.gasp.platform.MasterApplicationInstance;

/**
 * A game server logic running on GASP must implement this class GASPServer.
 * 
 * @author PELLERIN Romain (pellerin@cnam.fr) - MEGA Project
 */
public abstract class GASPServer implements OnDataEvent, OnEndEvent,
		OnJoinEvent, OnQuitEvent, OnStartEvent {

	protected ApplicationInstance appIns;
	protected MasterApplicationInstance masterApp;

	protected GASPServer() {
	}

	/**
	 * Link the application instance to the game server logic.
	 * 
	 * @param applicationInstance
	 */
	public void setOwnerAI(ApplicationInstance applicationInstance) {
		appIns = applicationInstance;
	}

	/**
	 * Link the master application instance to the game server logic.
	 * 
	 * @param applicationInstance
	 */
	public void setMasterApp(MasterApplicationInstance masterApplication) {
		masterApp = masterApplication;
	}

	/**
	 * Send to the linked applicationInstance a DataEvent for a specific
	 * actorSession.
	 * 
	 * @param actorSessionID
	 * @param dataEvent
	 *            the DataEvent to send
	 */
	protected void sendDataTo(int actorSessionID, DataEvent dataEvent) {
		appIns.sendDataToActorSession(actorSessionID, dataEvent);
	}

	/**
	 * Retrieve the actor ID linked to the actor session ID. (persistency)
	 * Provide the storage of player game informations.
	 * 
	 * @param actorSessionID
	 * @param dataEvent
	 *            the DataEvent to send
	 */
	protected int retrieveActorID(int actorSessionID) {
		return appIns.getActorSession(actorSessionID).getActorID();
	}

	/**
	 * Retrieve the application ID. (persistency) Provide the storage of player
	 * game informations.
	 */
	protected int retrieveApplicationID() {
		return appIns.retrieveApplicationID();
	}

	/**
	 * Send to the linked applicationInstance a DataEvent for a specific
	 * actorSession.
	 * 
	 * @param actorSessionID
	 * @param dataEvent
	 *            the DataEvent to send
	 */
	/**
	 * TODO: à virer protected void forceQuitEvent(int actorSessionID){ if
	 * (appIns.getActorSession(actorSessionID)!=null) appIns.onQuitEvent(new
	 * QuitEventImpl(actorSessionID)); }
	 */

	/**
	 * Method called when a JoinEvent is received from the platform.
	 * 
	 * @param je
	 *            a JoinEvent
	 */
	abstract public void onJoinEvent(JoinEvent je);

	/**
	 * Method called when a StartEvent is received from the platform.
	 * 
	 * @param se
	 *            a StartEvent
	 */
	abstract public void onStartEvent(StartEvent se);

	/**
	 * Method called when a EndEvent is received from the platform.
	 * 
	 * @param ee
	 *            a EndEvent
	 */
	abstract public void onEndEvent(EndEvent ee);

	/**
	 * Method called when a QuitEvent is received from the platform.
	 * 
	 * @param qe
	 *            a QuitEvent
	 */
	abstract public void onQuitEvent(QuitEvent qe);

	/**
	 * Method called when a DataEvent is received from the platform.
	 * 
	 * @param de
	 *            a DataEvent
	 */
	abstract public void onDataEvent(DataEvent de);
	
	abstract public boolean couldJoin(PlayerInfo info);

}
