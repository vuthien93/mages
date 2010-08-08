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
import org.mega.gasp.event.impl.QuitEventImpl;
import org.mega.gasp.moods.CustomTypes;
import org.mega.gasp.platform.Actor;
import org.mega.gasp.platform.ApplicationInstance;
import org.mega.gasp.platform.MasterApplicationInstance;
import org.mega.gasp.platform.Session;
import org.mega.gasp.platform.utils.PropertiesReader;

/**
 * MasterApplicationInstance represents the container of all ApplicationInstance
 * of a specific application. So a MasterApplicationInstance contains a vector
 * of ApplicationInstance. It contains also a vector of Session objects,
 * representing the Actors currently using the Application. Finally it contains
 * a vector of the Actor objects, representing a link User-Application.
 * 
 * @author PELLERIN Romain (pellerin@cnam.fr) - MEGA Project
 */

public class MasterApplicationInstanceImpl implements
		MasterApplicationInstance, Runnable {

	private Vector applicationInstances;
	private Vector sessions;
	private Vector actors;
	private int applicationID;
	private int masterApplicationID;
	private String applicationPath;
	private Category cat; // logging via log4j
	private byte applicationModel;
	// 0: auto -> starts, stops watching the number of actors
	// 1: manual -> the owner of the aI starts, stops the aI
	// 2: mapcycle
	private int platformTimeout;
	private int applicationTimeout;
	private final int timeoutCheckingDeltaTime = 30000;
	private Thread timeoutThread = new Thread(this);
	private long lastAIListChanged = 0;
	public CustomTypes customTypes;

	public MasterApplicationInstanceImpl(int appID, int _platformTimeout) {
		cat = Category.getInstance("GASPLogging");
		cat.debug("MasterApp Instantiation for appID>" + appID);
		platformTimeout = _platformTimeout;
		applicationInstances = new Vector();
		applicationID = appID;
		sessions = new Vector();
		actors = new Vector();
		masterApplicationID = PlatformImpl.getPlatform().getIDManager()
				.generateMAID();
		PropertiesReader propReader = PlatformImpl.getPlatform()
				.getPropertiesReader();
		applicationPath = propReader.getApplicationPackage(appID);
		cat.debug("avant getApplicationModel");
		applicationModel = propReader.getApplicationModel(appID);
		applicationTimeout = propReader.getApplicationTimeout(appID);
		timeoutThread.start();

		try {
			customTypes = (CustomTypes) Class.forName(
					applicationPath + ".CustomTypes").newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			cat.error("Cannot create custom types: " + applicationPath
					+ ".CustomTypes", e);
			return;
		}
		if (customTypes == null) {
			cat.error("Cannot create custom types: " + applicationPath
					+ ".CustomTypes");
		} else {
			cat.debug("MasterApp(app" + applicationID + "): instantiation ok");
		}
	}

	/**
	 * Returns the ID of the Master Application Instance.
	 * 
	 * @return MasterApplicationID
	 */
	public int getMasterApplicationID() {
		return masterApplicationID;
	}

	/**
	 * Returns the ID of the application managed by the Master Application
	 * Instance.
	 * 
	 * @return MasterApplicationID
	 */
	public int getApplicationID() {
		return applicationID;
	}

	/**
	 * Returns the ID of the application managed by the Master Application
	 * Instance.
	 * 
	 * @return MasterApplicationID
	 */
	public byte getApplicationModel() {
		return applicationModel;
	}

	/**
	 * Create an Application Instance.
	 * 
	 * @param ownerSession
	 *            the Session object representing the actor who create the
	 *            Application Instance.
	 * @param minActors
	 *            represents the minimum number of actors required to start the
	 *            Application Instance.
	 * @param maxActors
	 *            represents the maximum number of actors can play this
	 *            Application Instance.
	 * @param actors
	 *            if the Application Instance is private this vector contains
	 *            the required actors else if it is a public Application
	 *            Instance this vector is empty.
	 * 
	 * @return applicationInstanceID the ID of the Application Instance created.
	 */
	public int createApplicationInstance(Session ownerSession, int minActors,
			int maxActors, String[] actors) {
		lastAIListChanged = System.currentTimeMillis();
		ApplicationInstance aI = new ApplicationInstanceImpl(this,
				ownerSession, minActors, maxActors, actors, applicationPath,
				applicationModel);
		applicationInstances.add(aI);
		return aI.getOwnerAID();
	}

	/**
	 * Returns the required ApplicationInstance object.
	 * 
	 * @param applicationInstanceID
	 *            the ID of the required Application Instance
	 * 
	 * @return applicationInstance the object representing the Application
	 *         Instance
	 */
	public ApplicationInstance getApplicationInstance(int applicationInstanceID) {
		ApplicationInstance applicationInstance = null;
		for (Iterator iter = enumerateApplicationInstance(); iter.hasNext();) {
			applicationInstance = (ApplicationInstance) iter.next();
			if (applicationInstance.getApplicationInstanceID() == applicationInstanceID)
				return applicationInstance;
		}
		return null;
	}

	/**
	 * Remove the required Application Instance.
	 * 
	 * @param applicationInstanceID
	 * 
	 * @return boolean telling us if the remove was correctly done or not.
	 */
	public synchronized boolean removeApplicationInstance(
			int applicationInstanceID/* , Iterator iter */) {
		ApplicationInstance applicationInstance = null;
		for (Iterator iter2 = enumerateApplicationInstance(); iter2.hasNext();) {
			applicationInstance = (ApplicationInstance) iter2.next();
			int appInstID = applicationInstance.getApplicationInstanceID();
			if (appInstID == applicationInstanceID) {
				lastAIListChanged = System.currentTimeMillis();
				iter2.remove();
				cat.debug("Application instance (" + applicationID
						+ ") removed");
				PlatformImpl.getPlatform().getIDManager().releaseAIID(
						applicationInstanceID);
				return true;
			}
		}

		return false;
	}

	/**
	 * Determine if the vector of ApplicationInstance objects is currently empty
	 * or not.
	 * 
	 * @return boolean telling us if the vector is empty or not.
	 */
	public boolean isApplicationInstanceEmpty() {
		return applicationInstances.isEmpty();
	}

	/**
	 * Returns an ApplicationInstance vector iterator.
	 * 
	 * @return iterator
	 */
	public Iterator enumerateApplicationInstance() {
		return applicationInstances.iterator();
	}

	/**
	 * Determine if the Application Instance associated with
	 * applicationInstanceID is hold by this Master Application.
	 * 
	 * @param applicationInstanceID
	 * 
	 * @return boolean
	 */
	public boolean containsApplicationInstance(int applicationInstanceID) {
		ApplicationInstance applicationInstance = null;
		for (Iterator iter = enumerateApplicationInstance(); iter.hasNext();) {
			applicationInstance = (ApplicationInstance) iter.next();
			if (applicationInstance.getApplicationInstanceID() == applicationInstanceID)
				return true;
		}
		return false;
	}

	/**
	 * Returns the current size of the vector of ApplicationInstance objects.
	 * 
	 * @return nbApplicationInstance
	 */
	public int applicationInstanceSize() {
		return applicationInstances.size();
	}

	/**
	 * Returns a random ApplicationInstance currently not started.
	 * 
	 * @return the ID of an ApplicationInstance object
	 */
	public int getAnAIID() {
		ApplicationInstance appI = null;
		for (Iterator iter = enumerateApplicationInstance(); iter.hasNext();) {
			appI = (ApplicationInstance) iter.next();
			if (appI.isJoinable()) {
				return appI.getApplicationInstanceID();
			}
		}
		return 0;
	}

	/**
	 * Return the session associated to the Actor Session ID.
	 * 
	 * @param aSID
	 *            the ActorSession ID
	 * @return the Session object associated
	 */
	public Session getASIDAssociatedSession(int aSID) {
		Session session = null;
		for (Iterator iter = sessions.iterator(); iter.hasNext();) {
			session = (Session) iter.next();
			if (session.getActorSessionID() == aSID) {
				session.refreshTimeout();
				return session;
			}
		}
		return null;
	}

	/**
	 * Return the session associated to the aID.
	 * 
	 * @param aID
	 * @return the Session object associated
	 */
	public Session getAIDAssociatedSession(int aID) {
		Session session = null;
		for (Iterator iter = sessions.iterator(); iter.hasNext();) {
			session = (Session) iter.next();
			if (session.getActorID() == aID)
				return session;
		}
		return null;
	}

	/**
	 * Unlog the user represented by this ActorSession ID , delete Actor,
	 * Session, ActorSession objects linked to this user, and also the
	 * ApplicationInstance would created by this user.
	 * 
	 * @param sID
	 *            the Session ID
	 */
	public void unlog(int sID) {
		Session session = getSession(sID);

		int aSID = session.getActorSessionID();

		if (aSID != 0) {
			// send a QuitEvent to the AI of the player
			ApplicationInstance appI = getActorSessionAI(aSID);
			QuitEventImpl qe = new QuitEventImpl(aSID);
			appI.onQuitEvent(qe);

		}

		// remove Session and Actor of the user
		removeSession(sID);
		removeActor(session.getActorID());

	}

	/**
	 * Returns the ApplicationInstance object containing the ActorSession linked
	 * to the Session.
	 * 
	 * @param aSID
	 *            the session ID
	 * @return the ApplicationInstance object
	 */
	public ApplicationInstance getActorSessionAI(int aSID) {
		ApplicationInstance appI = null;
		for (Iterator iter = enumerateApplicationInstance(); iter.hasNext();) {
			appI = (ApplicationInstance) iter.next();
			if (appI.containsActorSession(aSID)) {
				return appI;
			}
		}
		return null;
	}

	/** *************************************************** */
	/** gestion des sessions associées à l'application * */
	/** *************************************************** */

	/**
	 * Create a new session associated with the actor ID.
	 * 
	 * @param aID
	 *            the actor ID
	 * 
	 * @return the ID of the session created
	 */
	public int createNewSession(int aID) {
		Session session = new SessionImpl(aID, System.currentTimeMillis());
		session.setMasterApp(this);
		sessions.add(session);
		lastAIListChanged = System.currentTimeMillis();
		cat.debug("MasterApp(app" + applicationID + "): login of actor n°"
				+ aID + ", session n°" + session.getSessionID() + " created");
		return session.getSessionID();
	}

	/**
	 * Returns the corresponding session
	 * 
	 * @param sID
	 *            the session ID
	 * 
	 * @return the Session object
	 */
	public Session getSession(int sID) {
		Session session = null;
		for (Iterator iter = sessions.iterator(); iter.hasNext();) {
			session = (Session) iter.next();
			if (session.getSessionID() == sID) {
				session.refreshTimeout();
				return session;
			}
		}
		return null;
	}

	/**
	 * Remove the corresponding Session object.
	 * 
	 * @param sID
	 * 
	 * @return boolean
	 */
	public boolean removeSession(int sID) {
		Session session = null;
		for (Iterator iter = sessions.iterator(); iter.hasNext();) {
			session = (Session) iter.next();
			if (session.getSessionID() == sID) {
				session.setMasterApp(null);
				iter.remove();
				PlatformImpl.getPlatform().getIDManager().releaseSID(sID);
				return true;
			}
		}
		return false;
	}

	/**
	 * Determine if the MasterApplicationInstance is the owner of the session
	 * associated with the SessionID.
	 * 
	 * @param sID
	 *            the Session ID
	 * 
	 * @return boolean
	 */
	public boolean isSessionOwner(int sID) {
		if (this.getSession(sID) != null)
			return true;
		return false;
	}

	/**
	 * Return the current number of Session objects in the Session vector.
	 * 
	 * @return the number of sessions
	 */
	public int sessionSize() {
		return sessions.size();
	}

	/**
	 * Return the current number of Session objects in the Session vector.
	 * 
	 * @return iterator on Session vector
	 */
	public Iterator enumerateSession() {
		return sessions.iterator();
	}

	/** *************************************************** */
	/** gestion des actors associées à l'application * */
	/** *************************************************** */

	/**
	 * Add the Actor object actor to the vector of actors in the Master
	 * Application.
	 * 
	 * @param actor
	 *            the Actor object to add
	 */
	public void addActor(Actor actor) {
		actors.add(actor);
	}

	/**
	 * Return the Actor object associated with the actorID.
	 * 
	 * @param aID
	 *            the actor ID
	 * 
	 * @return the Actor object
	 */
	public Actor getActor(int aID) {
		Actor actor = null;
		for (Iterator iter = actors.iterator(); iter.hasNext();) {
			actor = (Actor) iter.next();
			if (actor.getActorID() == aID)
				return actor;
		}
		return null;
	}

	/**
	 * Remove the Actor object associated with the actorID.
	 * 
	 * @param aID
	 *            the actor ID
	 * @return boolean representing the success of the operation
	 */
	public boolean removeActor(int aID) {
		Actor actor = null;
		for (Iterator iter = actors.iterator(); iter.hasNext();) {
			actor = (Actor) iter.next();
			if (actor.getActorID() == aID) {
				iter.remove();
				return true;
			}
		}
		return false;
	}

	/**
	 * Determine if the Master Application is the owner of the Actor object
	 * associated with the actorID.
	 * 
	 * @param aID
	 *            the actor ID
	 * 
	 * @return boolean
	 */
	public boolean isActorOwner(int aID) {
		if (this.getActor(aID) != null)
			return true;
		return false;
	}

	/**
	 * Return the current number of Actor objects in the Actor vector.
	 * 
	 * @return the number of actors
	 */
	public int actorSize() {
		return actors.size();
	}

	/**
	 * Return the current number of Session objects in the Session vector.
	 * 
	 * @return iterator on Actor vector
	 */
	public Iterator enumerateActor() {
		return actors.iterator();
	}

	/** ** pas sûr que ces fonctions servent encore + fonctions de debuggage *** */

	/**
	 * Remove the ActorSession associciated with the session.
	 * 
	 * @param session
	 * 
	 * @return boolean representing the operation success
	 */
	/*
	 * public boolean removeAssociatedActorSession(Session session) {
	 * ApplicationInstance appI= null; for (Iterator iter =
	 * enumerateApplicationInstance(); iter.hasNext();){ appI =
	 * (ApplicationInstance) iter.next(); if
	 * (appI.containsActorSession(session.getActorSessionID())){
	 * appI.removeActorSession(session.getActorSessionID());
	 * session.setActorSessionID(0); return true; } } return false; }
	 */

	public Vector getSessions() {
		return sessions;
	}

	public Vector getActors() {
		return actors;
	}

	public boolean addApplicationInstance(
			ApplicationInstance applicationInstance) {
		if (this.containsApplicationInstance(applicationInstance
				.getApplicationInstanceID()))
			return false;
		else {
			lastAIListChanged = System.currentTimeMillis();
			applicationInstances.add(applicationInstance);
			return true;
		}
	}

	public long getLastAIListChangedTime() {
		return lastAIListChanged;
	}

	public void updateLastAIListChangedTime() {
		lastAIListChanged = System.currentTimeMillis();
	}

	/** ************************************** */
	/** ******** TIME OUT THREADING ********** */
	/** ************************************** */

	public void run() {
		while (true) {
			if (sessions.size() > 0) {
				cat.debug("check for timeouts");
				checkForTimeouts();
			}

			try {
				Thread.sleep(timeoutCheckingDeltaTime);
			} catch (InterruptedException e) {
				cat.fatal("Timeout threading error: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	/**
	 * Command the applicationInstances to treat the eventuals timeouts on their
	 * actor sessions.
	 */
	private synchronized void checkForTimeouts() {
		for (Iterator iter = sessions.iterator(); iter.hasNext();) {
			Session session = (Session) iter.next();

			// player connected to an AI
			if (session.getActorSessionID() > 0) {
				if ((System.currentTimeMillis() - session.getTimeout()) > applicationTimeout) {
					ApplicationInstance aI = this.getActorSessionAI(session
							.getActorSessionID());

					// AI list could be changed
					lastAIListChanged = System.currentTimeMillis();

					if (aI != null)
						aI.onQuitEvent(new QuitEventImpl(session
								.getActorSessionID()));
					cat.debug("ActorSession with aSID("
							+ session.getActorSessionID()
							+ ") timeouted, quitAI forced");
				}
			}

			// player connected to the lobby room
			if ((System.currentTimeMillis() - session.getTimeout()) > platformTimeout) {
				session.setMasterApp(null);
				iter.remove();
				removeActor(session.getSessionID());

				// AI list could be changed
				lastAIListChanged = System.currentTimeMillis();

				cat.debug("Session with sID(" + session.getSessionID()
						+ ") timeouted, logout forced");
			}
		}
	}
}
