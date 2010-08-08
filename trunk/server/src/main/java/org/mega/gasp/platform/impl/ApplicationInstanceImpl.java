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

import java.util.Iterator;
import java.util.Vector;

import org.apache.log4j.Category;
import org.mega.gasp.event.*;
import org.mega.gasp.event.impl.*;
import org.mega.gasp.listener.*;
import org.mega.gasp.moods.CustomTypes;
import org.mega.gasp.platform.ActorSession;
import org.mega.gasp.platform.ApplicationInstance;
import org.mega.gasp.platform.MasterApplicationInstance;
import org.mega.gasp.platform.Session;
import org.mega.gasp.platform.utils.ApplicationInstanceInfos;
import org.mega.gasp.server.GASPServer;

/**
 * ApplicationInstance represent a game session of a specific application. It
 * contains a vector of ActorSession representing the actors want to play in.
 * This classes manage the in game events.
 * 
 * @author PELLERIN Romain (pellerin@cnam.fr) - MEGA Project
 */
public class ApplicationInstanceImpl implements ApplicationInstance,
		OnJoinEvent, OnStartEvent, OnEndEvent, OnDataEvent, OnQuitEvent {

	private Vector actorSessions;
	// private Iterator iterActorSessions;
	private int applicationInstanceID;
	private MasterApplicationInstance masterApp;
	private int ownerActorSessionID;
	private int minA;
	private int maxA;
	private String applicationInstanceName; // for later implementation of game
	// instance naming
	private boolean isPublicAI; // public or private
	private boolean isRunningAI; // running or waiting for players
	private Vector listeners;
	private CustomTypes customTypes;
	private GASPServer server;
	private Category cat; // logging with log4j
	private byte applicationModel;
	private Object customData = null;

	// this boolean is use to notify that the owner has left the AI
	// now the AI wait to all the actor sessions events vector in the
	// AI will be empty
	private boolean toDestroy;

	public ApplicationInstanceImpl(MasterApplicationInstance masterApplication,
			Session ownerSession, int minActors, int maxActors,
			String[] actors, String path, byte model) {
		cat = Category.getInstance("GASPLogging");
		actorSessions = new Vector();
		// iterActorSessions = actorSessions.iterator();
		masterApp = masterApplication;
		applicationInstanceID = PlatformImpl.getPlatform().getIDManager()
				.generateAIID();
		minA = minActors;
		maxA = maxActors;
		applicationModel = model;
		listeners = new Vector();
		if (actors.length == 0)
			isPublicAI = true;
		else {
			this.addActors(actors);
			isPublicAI = false;
		}
		isRunningAI = false;
		toDestroy = false;

		try {
			customTypes = (CustomTypes) Class.forName(path + ".CustomTypes")
					.newInstance();
			cat.debug("AI(" + applicationInstanceID
					+ "): linked CustomTypes instantiated >" + customTypes);
			server = (GASPServer) Class.forName(path + ".server.Server")
					.newInstance();
			cat.debug("AI(" + applicationInstanceID
					+ "): linked Game Server Logic instantiated >" + server);
			server.setOwnerAI(this);
			server.setMasterApp(masterApplication);
			listeners.addElement(server);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (Throwable t) {
			t.printStackTrace();
		}

		ownerActorSessionID = this.createNewActorSession(applicationInstanceID,
				ownerSession);
		cat.debug("AI(" + applicationInstanceID + "): owner ASID"
				+ ownerActorSessionID + ", creation ok");
	}

	/**
	 * Returns the server associated with the ApplicationInstance.
	 * 
	 * @return the MGP server
	 */
	public GASPServer getServer() {
		return server;
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
	public int getMinActors() {
		return minA;
	}

	/**
	 * Returns the maximum number of actors can play the game session.
	 * 
	 * @return the maximum number of actors
	 */
	public int getMaxActors() {
		return maxA;
	}

	/**
	 * Returns the ActorId of the ApplicationInstanceID owner.
	 * 
	 * @return the Actor ID
	 */
	public int getOwnerAID() {
		return ownerActorSessionID;
	}

	/**
	 * Returns the encoder of the application associated with the application
	 * instance.
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
	public boolean isPublic() {
		return isPublicAI;
	}

	/**
	 * Determine if the ApplicationInstance is running or not.
	 * 
	 * @return boolean
	 */
	public boolean isRunning() {
		return isRunningAI;
	}

	/**
	 * Determine if the owner master application can destroy or not the AI.
	 */
	public boolean isDestroyable() {
		return toDestroy;
	}

	/**
	 * Determine if the the application instance is joignable by a player or
	 * not, eg if: the AI is not destroyable the AI is not running the max of
	 * players is reached
	 * 
	 * @return boolean
	 */
	public boolean isJoinable() {
		switch (applicationModel) {
		case 0: // auto
			return (!this.isDestroyable());
					//&& (this.actorSessionSize() < this	.getMaxActors()));
		case 1: // manual
			return (!this.isDestroyable() && !this.isRunning() && (this
					.actorSessionSize() < this.getMaxActors()));

		case 3: // mapcycle
			return false;
		default:
			cat
					.debug("AI("
							+ applicationInstanceID
							+ "): bad model number, show your apps.properties > app.model (auto, manual, mapcycle)");
			return false;
		}
	}

	/**
	 * Determine if the the application instance is startable by the player
	 * owner or not.
	 * 
	 * @return boolean
	 */
	public boolean isStartable() {
		if (this.getMinActors() <= this.actorSessionSize())
			return true;
		return false;
	}

	/**
	 * Create a new ActorSession in the ApplicationInstance corresponding to the
	 * user Session object informations. Returns the ActorSession ID.
	 * 
	 * @param aIID
	 *            the ApplicationInstance
	 * @param session
	 *            the Session object of the user
	 * @return the ActorSession ID
	 */
	public int createNewActorSession(int aIID, Session session) {
		ActorSession actorSession = new ActorSessionImpl(aIID, session);
		cat.debug("AI(" + applicationInstanceID + "): actorSession n°"
				+ actorSession.getActorSessionID() + " created");
		actorSession.setPseudoName(masterApp.getActor(session.getActorID())
				.getPseudoName());
		this.addActorSession(actorSession);

		// raise event
		JoinEvent joinEv = new JoinEventImpl(actorSession.getActorSessionID(),
				actorSession.getPseudoName());
		onJoinEvent(joinEv);

		switch (applicationModel) {
		case 0:
			// auto
			// when a player join the aI, in this model, the game session
			// automatically start
			StartEvent startEv = new StartEventImpl(0);
			/*if (isRunning())
				actorSession.raiseEvent(startEv);
			else*/
				onStartEvent(startEv);
			break;
		case 1: // manual
		case 2: // mapcycle
		default:
			break;
		}

		sendPlayersInfos(actorSession.getActorSessionID());
		return actorSession.getActorSessionID();
	}

	/**
	 * This method provide the new player to know all of the players currently
	 * in the application instance.
	 * 
	 * @param aSID
	 *            the actor session associated to the player
	 */
	public void sendPlayersInfos(int aSID) {
		ActorSession actorSession = getActorSession(aSID);
		cat.debug("AI(" + applicationInstanceID + "): send to AS("
				+ actorSession.getActorSessionID()
				+ ") the current list of players");

		for (Iterator iter = actorSessions.iterator(); iter.hasNext();) {
			ActorSession aS = (ActorSession) iter.next();
			cat.debug("AI(" + applicationInstanceID + "): raise event to AS("
					+ aS.getActorSessionID() + ")");
			actorSession.raiseEvent(new JoinEventImpl(aS.getActorSessionID(),
					masterApp.getActor(aS.getActorID()).getPseudoName()));
		}
	}

	/**
	 * This method provide the new player to know all of the players currently
	 * in the application instance.
	 * 
	 * @param actorSession
	 *            the actor session associated to the player
	 */
	private void sendPlayersInfos(ActorSession actorSession) {
		cat.debug("AI(" + applicationInstanceID + "): send to AS("
				+ actorSession.getActorSessionID()
				+ ") the current list of players");

		for (Iterator iter = actorSessions.iterator(); iter.hasNext();) {
			ActorSession aS = (ActorSession) iter.next();
			cat.debug("AI(" + applicationInstanceID + "): raise event to AS("
					+ aS.getActorSessionID() + ")");
			actorSession.raiseEvent(new JoinEventImpl(aS.getActorSessionID(),
					aS.getPseudoName()));
		}
	}

	/**
	 * Returns the ActorSession object corresponding to the ActorSession ID.
	 * 
	 * @param actorSessionID
	 *            the ActorSession ID
	 * @return the ActorSession object
	 */
	public ActorSession getActorSession(int actorSessionID) {
		ActorSession actorSession = null;

		for (Iterator iter = actorSessions.iterator(); iter.hasNext();) {
			actorSession = (ActorSession) iter.next();
			int aSessionID = actorSession.getActorSessionID();
			if (aSessionID == actorSessionID)
				return actorSession;
		}

		return null;
	}

	/**
	 * Remove the ActorSession object corresponding to the ActorSessionID.
	 * 
	 * @param actorSessionID
	 *            the ActorSesion ID
	 * @return boolean representing the operation success
	 */
	public boolean removeActorSession(int actorSessionID) {
		ActorSession actorSession = null;
		int index = 0;

		for (Iterator iter2 = actorSessions.iterator(); iter2.hasNext();) {
			actorSession = (ActorSession) iter2.next();
			int aSessionID = actorSession.getActorSessionID();

			if (aSessionID == actorSessionID) {
				iter2.remove();
				PlatformImpl.getPlatform().getIDManager().releaseASID(
						actorSessionID);
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
	 * Returns the current number of ActorSession objects, e.g the number of
	 * actors ready to play this game session.
	 * 
	 * @return the number of actors
	 */
	public int actorSessionSize() {
		return actorSessions.size();
	}

	/**
	 * Determine if the ActorSession associated with the ActorSession ID is
	 * contained by this ApplicationInstance or not.
	 * 
	 * @param actorSessionID
	 * @return boolean "present or not"
	 */
	public boolean containsActorSession(int actorSessionID) {
		ActorSession actorSession = null;
		for (Iterator iter = actorSessions.iterator(); iter.hasNext();) {
			actorSession = (ActorSession) iter.next();
			if (actorSession.getActorSessionID() == actorSessionID)
				return true;
		}

		return false;
	}

	/**
	 * For Lobby service, this method provide informations of the
	 * ApplicationInstance.
	 * 
	 * @return an ApplicationInstanceInfos object
	 */
	public ApplicationInstanceInfos getApplicationInstanceInfos() {

		ApplicationInstanceInfos appInstanceInfos = new ApplicationInstanceInfos(
				this.getApplicationInstanceID(), minA, maxA, actorSessions,
				this.customData);

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
	 * Set the pool of actors required for a private Application Instance.
	 * 
	 * @param actors
	 *            the table of actorID
	 */
	private void addActors(String[] actors) {
		/** TODO: for private AI, to implement * */
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
	 * @param actorSession
	 *            the ActorSession object
	 * @return boolean of the operation success
	 */
	public boolean addActorSession(ActorSession actorSession) {
		if (containsActorSession(actorSession.getActorSessionID()))
			return false;
		else {
			actorSessions.insertElementAt(actorSession, actorSessions.size());
			return true;
		}
	}

	/** ****************************************************** */
	/** In game Events managment methods * */
	/** & implementation of the events superclasses * */
	/** ****************************************************** */

	/**
	 * When a JoinEvent arrives, notify to all listeners and to all other actor
	 * sessions
	 */
	public void onJoinEvent(JoinEvent e) {
		if (!toDestroy) {
			cat
					.debug("AI(" + applicationInstanceID
							+ "): JoinEvent received from ASID"
							+ e.getActorSessionID());
			notifyJoinToListeners(e);
			cat.debug("AI(" + applicationInstanceID
					+ "): notify to all listeners finished");
			notifyToAllOtherActorSession(e);
			cat.debug("AI(" + applicationInstanceID
					+ "): notify to all actor sessions finished");
		}

	}

	/**
	 * When a StartEvent arrives, if the actor session ID of the event is the
	 * ownerASID then notify to all listeners and to all other actor sessions
	 * else do nothing.
	 */
	public void onStartEvent(StartEvent e) {
		if (!toDestroy) {
			switch (applicationModel) {
			case 0: // auto
				cat.debug("AI(" + applicationInstanceID
						+ "): StartEvent received from server");
				isRunningAI = true;
				notifyStartToListeners(e);
				cat.debug("AI(" + applicationInstanceID
						+ "): notify to all listeners finished");
				notifyToAllOtherActorSession(e);
				cat.debug("AI(" + applicationInstanceID
						+ "): notify to all actor sessions finished");
				break;

			case 1: // manual
				cat.debug("AI(" + applicationInstanceID
						+ "): StartEvent received from ASID"
						+ e.getActorSessionID());
				if (e.getActorSessionID() == ownerActorSessionID) {
					isRunningAI = true;
					notifyStartToListeners(e);
					cat.debug("AI(" + applicationInstanceID
							+ "): notify to all listeners finished");
					notifyToAllOtherActorSession(e);
					cat.debug("AI(" + applicationInstanceID
							+ "): notify to all actor sessions finished");
				} else {
					cat
							.debug("AI("
									+ applicationInstanceID
									+ "): invalid StartEvent, ASID is not the owner of the AI");
				}
				break;

			case 2: // mapcycle
				break;
			default:
				break;
			}
		}
	}

	/**
	 * When a EndEvent arrives, if the actor session ID of the event is the
	 * ownerASID then notify to all listeners and to all other actor sessions
	 * else do nothing.
	 */
	public void onEndEvent(EndEvent e) {
		if (!toDestroy) {
			switch (applicationModel) {
			case 0: // auto
				cat.debug("AI(" + applicationInstanceID
						+ "): EndEvent received from server");
				notifyEndToListeners(e);
				cat.debug("AI(" + applicationInstanceID
						+ "): notify to all listeners finished");
				notifyToAllOtherActorSession(e);
				cat.debug("AI(" + applicationInstanceID
						+ "): notify to all actor sessions finished");
				isRunningAI = false;
				break;
			case 1: // manual
				cat.debug("AI(" + applicationInstanceID
						+ "): EndEvent received from ASID"
						+ e.getActorSessionID());
				if (e.getActorSessionID() == ownerActorSessionID) {
					notifyEndToListeners(e);
					cat.debug("AI(" + applicationInstanceID
							+ "): notify to all listeners finished");
					notifyToAllOtherActorSession(e);
					cat.debug("AI(" + applicationInstanceID
							+ "): notify to all actor sessions finished");
					isRunningAI = false;
				} else {
					cat
							.debug("AI("
									+ applicationInstanceID
									+ "): bad EndEvent, ASID is not the owner of the AI");
				}
				break;
			case 2: // mapcycle
			default:
				break;
			}
		}
	}

	/**
	 * When a QuitEvent arrives, then notify to all listeners and to all other
	 * actor sessions
	 */
	public void onQuitEvent(QuitEvent qe) {
		cat.debug("**** unlog quitEvent: removing actor session "
				+ qe.getActorSessionID());
		removeActorSession(qe.getActorSessionID());
		if (masterApp.getASIDAssociatedSession(qe.getActorSessionID()) != null) {
			masterApp.getASIDAssociatedSession(qe.getActorSessionID())
					.setActorSessionID(0);
		}

		if (!toDestroy) {
			notifyQuitToListeners(qe);
			cat.debug("AI(" + applicationInstanceID
					+ "): notify to all listeners finished");
			notifyToAllOtherActorSession(qe);
			cat.debug("AI(" + applicationInstanceID
					+ "): notify to all actor sessions finished");

			switch (applicationModel) {
			case 0: // automatic management
				if (actorSessions.isEmpty()) {
					toDestroy = true;
					cat.debug("AI(" + applicationInstanceID
							+ "): move the AI in destroyable mode");
					masterApp
							.removeApplicationInstance(applicationInstanceID/* ,iterMA */);
				}
				break;
			case 1: // manual management
				if (qe.getActorSessionID() == this.ownerActorSessionID) {
					toDestroy = true;
					cat.debug("AI(" + applicationInstanceID
							+ "): move the AI in destroyable mode");
				}
				if (actorSessions.isEmpty()) {
					toDestroy = true;
					cat.debug("AI(" + applicationInstanceID
							+ "): move the AI in destroyable mode");
					masterApp.removeApplicationInstance(applicationInstanceID);
				}
				break;
			case 2:
				/** TODO: future implementation */
				break;
			}

		}
	}

	/**
	 * When a DataEvent arrives, notify to all listeners.
	 */
	public void onDataEvent(DataEvent e) {
		if (!toDestroy) {
			cat
					.debug("AI(" + applicationInstanceID
							+ "): DataEvent received from ASID"
							+ e.getActorSessionID());
			notifyDataToListeners(e);
			cat.debug("AI(" + applicationInstanceID
					+ "): notify to all listeners finished");
		}
	}

	/**
	 * Provides to add a listener on events of application instance.
	 * 
	 * @param listener
	 *            the object to become a listener
	 */
	public void addListener(Object listener) {
		listeners.add(listener);
	}

	/**
	 * Put the event in event vectors of the ApplicationInstance ActorSessions.
	 * 
	 * @param e
	 *            the event
	 */
	private void notifyToAllOtherActorSession(Event e) {
		for (Iterator iter = actorSessions.iterator(); iter.hasNext();) {
			ActorSession actorSession = (ActorSession) iter.next();
			if (actorSession.getActorSessionID() != e.getActorSessionID())
				actorSession.raiseEvent(e);
		}
	}

	/**
	 * Raise the JoinEvent to all external listenners, in particular the game
	 * server logic thread.
	 * 
	 * @param e
	 *            the JoinEvent
	 */
	private void notifyJoinToListeners(JoinEvent e) {
		try {
			for (Iterator iter = listeners.iterator(); iter.hasNext();) {
				OnJoinEvent listenner = (OnJoinEvent) iter.next();
				listenner.onJoinEvent(e);
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}

	}

	/**
	 * Raise the EndEvent to all external listenners, in particular the game
	 * server logic thread.
	 * 
	 * @param e
	 *            the EndEvent
	 */
	private void notifyEndToListeners(EndEvent e) {
		for (Iterator iter = listeners.iterator(); iter.hasNext();) {
			OnEndEvent listenner = (OnEndEvent) iter.next();
			listenner.onEndEvent(e);
		}
	}

	/**
	 * Raise the StartEvent to all external listenners, in particular the game
	 * server logic thread.
	 * 
	 * @param e
	 *            the StartEvent
	 */
	private void notifyStartToListeners(StartEvent e) {
		for (Iterator iter = listeners.iterator(); iter.hasNext();) {
			OnStartEvent listenner = (OnStartEvent) iter.next();
			listenner.onStartEvent(e);
		}

	}

	/**
	 * Raise the QuitEvent to all external listenners, in particular the game
	 * server logic thread.
	 * 
	 * @param e
	 *            the QuitEvent
	 */
	private void notifyQuitToListeners(QuitEvent e) {
		for (Iterator iter = listeners.iterator(); iter.hasNext();) {
			OnQuitEvent listenner = (OnQuitEvent) iter.next();
			listenner.onQuitEvent(e);
		}

	}

	/**
	 * Raise the DataEvent to all external listenners, in particular the game
	 * server logic thread.
	 * 
	 * @param e
	 *            the DataEvent
	 */
	private void notifyDataToListeners(DataEvent e) {
		for (Iterator iter = listeners.iterator(); iter.hasNext();) {
			OnDataEvent listenner = (OnDataEvent) iter.next();
			listenner.onDataEvent(e);
		}

	}

	/**
	 * Send a dataEvent to a specific actorSession.
	 * 
	 * @param actorSessionID
	 * @param dataEvent
	 */
	public void sendDataToActorSession(int actorSessionID, DataEvent dataEvent) {
		if (!toDestroy) {
			ActorSession actorSession = this.getActorSession(actorSessionID);
			if (actorSession != null)
				actorSession.raiseEvent(dataEvent);
		}
	}

	/**
	 * If the pseudo already exist in the AI modify it, else no modifications.
	 * 
	 * @param name
	 * @return valid pseudo name
	 */
	public String treatPseudo(String name) {
		ActorSession actorSession = null;
		int i = 0;

		for (Iterator iter = actorSessions.iterator(); iter.hasNext();) {
			actorSession = (ActorSession) iter.next();
			if (actorSession.getPseudoName() == name)
				i++;
		}

		if (i > 0)
			name += "(" + i + ")";
		return name;
	}

	/**
	 * Raise a JoinEvent to all other players containing the new pseudo
	 * 
	 * @param actorSessionID
	 */
	public void raisePseudoModification(int actorSessionID, String pseudoname) {
		JoinEvent je = new JoinEventImpl(actorSessionID, pseudoname);
		onJoinEvent(je);
	}

	/**
	 * Retrieve the applicationID from the master application instance.
	 * 
	 * @param actorSessionID
	 */
	public int retrieveApplicationID() {
		return masterApp.getApplicationID();
	}

	@Override
	public Object getCustomData() {
		return customData;
	}

	@Override
	public void setCustomData(Object data) {
		customData = data;
	}

}
