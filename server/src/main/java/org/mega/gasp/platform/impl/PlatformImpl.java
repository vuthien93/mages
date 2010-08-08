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

import javax.servlet.ServletContext;

import org.aksonov.mages.server.Log4Logger;
import org.aksonov.tools.Log;
import org.apache.log4j.Category;
import org.apache.log4j.PropertyConfigurator;
import org.mega.gasp.event.JoinEvent;
import org.mega.gasp.event.impl.JoinEventImpl;
import org.mega.gasp.platform.Actor;
import org.mega.gasp.platform.ActorSession;
import org.mega.gasp.platform.ApplicationInstance;
import org.mega.gasp.platform.DBManager;
import org.mega.gasp.platform.IDManager;
import org.mega.gasp.platform.Lobby;
import org.mega.gasp.platform.MasterApplicationInstance;
import org.mega.gasp.platform.Platform;
import org.mega.gasp.platform.Session;
import org.mega.gasp.platform.utils.PropertiesReader;

/**
 * Platform is the base of the GAming Services Platform(GASP), the container of
 * all GP representing objects. So it contains all MasterApplicationInstance
 * objects in a vector. It hold all of the service instances (Lobby,...) and
 * system instances (DB manager, ID manager, ...).
 * 
 * @author PELLERIN Romain (pellerin@cnam.fr) - MEGA Project
 */

public class PlatformImpl implements Platform {

	// attribute singleton is necessary to locate statically the platform
	// from the servlets
	// if the platform is machine independant to the servlets investigate
	// the approach of distributed objects (rmi,ldap,jndi)
	private static PlatformImpl singleton;
	private Lobby lobby;
	private DBManager dbManager;
	private IDManager idManager;
	private PropertiesReader propertiesReader;
	private Vector masterApplicationInstances;
	public Category cat; // logging with log4j
	protected String home_path;
	public boolean gaspDBOK = true;
	public boolean gaspInstanciationOK = true;
	protected int platformTimeout;

	private PlatformImpl(ServletContext context) {
		try {
			Log.setLogger(Log4Logger.instance);
			home_path = context.getRealPath("/")+ "WEB-INF/classes/";
			propertiesReader = new PropertiesReader(home_path);
			propertiesReader.checkLog4jLoggingPath();
			PropertyConfigurator.configure(home_path + "conf/log4j.properties");
			cat = Category.getInstance("PlatformImpl");
			cat.info("Configuration files directory: " + home_path + "conf/");
			masterApplicationInstances = new Vector();

			// instanciate the platform ID manager
			idManager = new IDManagerImpl();

			// load the platform timeout from conf file
			platformTimeout = propertiesReader.getPlatformTimeout();
			cat.info("Platform timeout set to " + platformTimeout + " ms");

			// enable DB connection
			dbManager = new DBManagerImpl(propertiesReader);
			if (dbManager.isDBConnectionOK())
				cat.info("Platform: environnement correctly instantiated");
			else {
				gaspDBOK = false;
				throw new Exception("connection to GASP DB failed");
			}

			// enable game services
			lobby = new LobbyImpl();

		} catch (Exception e) {
			gaspInstanciationOK = false;

			cat.fatal("Platform not correctly instantiated:" + e.getMessage());
		}
	}

	public synchronized static PlatformImpl getPlatform(ServletContext context) {
		if (singleton == null){
			singleton = new PlatformImpl(context);
		}
		return singleton;
	}

	public synchronized static PlatformImpl getPlatform() {
		return singleton;
	}

	/**
	 * Returns the MasterApplicationInstance associated with the application ID
	 * or if it is not already instantiates, initialize it.
	 * 
	 * @param applicationID
	 *            the application ID
	 * @return the MasterApplicationInstance object
	 */
	public synchronized MasterApplicationInstance getMasterApplicationInstance(
			int applicationID) {

		if (!this.containsMasterApplicationInstance(applicationID)) {
			MasterApplicationInstance masterApp = new MasterApplicationInstanceImpl(
					applicationID, platformTimeout);
			this.addMasterApplicationInstance(masterApp);
			return masterApp;
		} else {
			MasterApplicationInstance masterApplicationInstance = null;
			for (Iterator iter = enumerateMasterApplicationInstance(); iter
					.hasNext();) {
				masterApplicationInstance = (MasterApplicationInstance) iter
						.next();
				if (masterApplicationInstance.getApplicationID() == applicationID)
					return masterApplicationInstance;
			}
		}

		cat
				.fatal("Platform: no MasterApp found for this application, getMasterApplicationInstance("
						+ applicationID + ")");
		return null;
	}

	/**
	 * Add the created MasterApplication to the MasterApplications vector.
	 * 
	 * @param masterAppInstance
	 * @return boolean
	 */
	private boolean addMasterApplicationInstance(
			MasterApplicationInstance masterAppInstance) {
		if (this.containsMasterApplicationInstance(masterAppInstance
				.getApplicationID()))
			return false;
		else {
			masterApplicationInstances.add(masterAppInstance);
			return true;
		}
	}

	/**
	 * Remove the MasterApplicationInstance associated with the application ID.
	 * 
	 * @param applicationID
	 *            the application ID
	 * @return boolean of the operation success
	 */
	public boolean removeMasterApplicationInstance(int applicationID) {
		MasterApplicationInstance masterApplicationInstance = null;
		for (Iterator iter = enumerateMasterApplicationInstance(); iter
				.hasNext();) {
			masterApplicationInstance = (MasterApplicationInstance) iter.next();
			int appID = masterApplicationInstance.getApplicationID();
			int masterAppID = masterApplicationInstance
					.getMasterApplicationID();
			if (appID == applicationID) {
				iter.remove();
				this.getIDManager().releaseMAID(masterAppID);
				return true;
			}
		}
		cat
				.error("Platform: MasterApp not found, removeMasterApplicationInstance("
						+ applicationID + ")");
		return false;
	}

	/**
	 * Determine if the vector of MasterApplicationInstance is empty or not.
	 * 
	 * @return boolean
	 */
	public boolean isMasterApplicationInstanceEmpty() {
		return masterApplicationInstances.isEmpty();
	}

	/**
	 * Return the number of MasterApplicationInstance currently instantiated.
	 * 
	 * @return the number of MasterApplication instances.
	 */
	public int masterApplicationInstanceSize() {
		return masterApplicationInstances.size();
	}

	/**
	 * Determine if the MasterApplicationInstance of the application associated
	 * with the application ID is initialized or not.
	 * 
	 * @param applicationID
	 * @return boolean
	 */
	public boolean containsMasterApplicationInstance(int applicationID) {
		MasterApplicationInstance masterApplicationInstance = null;
		for (Iterator iter = enumerateMasterApplicationInstance(); iter
				.hasNext();) {
			masterApplicationInstance = (MasterApplicationInstance) iter.next();
			if (masterApplicationInstance.getApplicationID() == applicationID)
				return true;
		}
		return false;
	}

	/**
	 * Return the Lobby service.
	 * 
	 * @return the Lobby object
	 */
	public Lobby getLobby() {
		return lobby;
	}

	/**
	 * Return the platform ID manager .
	 * 
	 * @return the IDManager object
	 */
	public IDManager getIDManager() {
		return idManager;
	}

	/**
	 * Return the platform ID manager .
	 * 
	 * @return the IDManager object
	 */
	public PropertiesReader getPropertiesReader() {
		return propertiesReader;
	}

	/**
	 * Return the platform DB manager .
	 * 
	 * @return the DBManager object
	 */
	public DBManager getDBManager() {
		return dbManager;
	}

	/**
	 * Return the MasterApplicationInstance containing the sID.
	 * 
	 * @param sID
	 *            the session ID
	 * @return the MasterApplicationInstance object
	 */
	public MasterApplicationInstance getSessionOwner(int sID) {
		try {
			MasterApplicationInstance masterApplicationInstance = null;
			for (Iterator iter = enumerateMasterApplicationInstance(); iter
					.hasNext();) {
				masterApplicationInstance = (MasterApplicationInstance) iter
						.next();
				if (masterApplicationInstance.isSessionOwner(sID))
					return masterApplicationInstance;
			}
		} catch (Exception e) {
			cat.error("Platform: no owner MasterApp found, getSessionOwner("
					+ sID + ")");
		}
		return null;
	}

	/**
	 * Return the MasterApplicationInstance containing the aID.
	 * 
	 * @param aID
	 *            the actor ID
	 * @return the MasterApplicationInstance object
	 */
	public MasterApplicationInstance getActorOwner(int aID) {
		MasterApplicationInstance masterApplicationInstance = null;
		for (Iterator iter = enumerateMasterApplicationInstance(); iter
				.hasNext();) {
			masterApplicationInstance = (MasterApplicationInstance) iter.next();
			if (masterApplicationInstance.isActorOwner(aID))
				return masterApplicationInstance;
		}
		return null;
	}

	/**
	 * Log a user in the platform, authentified by actorID, username and
	 * password. It consists of creating a new Session in the correct
	 * MasterApplicationInstance and return the session ID.
	 * 
	 * @param aID
	 *            the Actor ID attributed at the first login
	 * @param username
	 * @param password
	 * 
	 * @return the SessionID of the session created or 0 if the authentification
	 *         informations are not valid
	 */
	public int login(int aID, String username, String password) {
		try {
			int sID = alreadyLogged(aID);
			if (sID == 0) { // user not already logged check auth
				Actor actor = dbManager.getActorIfAuthentificationOK(aID,
						username, password);
				if (actor != null) { // auth ok
					MasterApplicationInstance masterApp = this
							.getMasterApplicationInstance(actor
									.getApplicationID());
					masterApp.addActor(actor);
					sID = masterApp.createNewSession(aID);
				} else {
					cat.error("Platform: bad authentification, login(" + aID
							+ "," + username + "," + password + ")");
				}
			} else {
				MasterApplicationInstance masterApp = this.getSessionOwner(sID);
				((SessionImpl) masterApp.getSession(sID))
						.setMasterApp(masterApp);
			}

			return sID;
		} catch (Exception e) {
			cat.error("error during login request, invalid infos "
					+ e.getClass().toString() + e.getMessage());
		}
		return 0;
	}

	/**
	 * Return the sID of the user requiring to log in if he's already logged,
	 * else 0.
	 * 
	 * @param aID
	 *            the actor ID of the user
	 * @return the session ID
	 */
	private int alreadyLogged(int aID) {
		MasterApplicationInstance masterApp = this.getActorOwner(aID);
		if (masterApp != null) { // the Actor is already instantiated then an
									// Session too
			Session session = masterApp.getAIDAssociatedSession(aID);

			// if no session is found, it means session is expired after timeout
			if (session == null) {
				return 0;
			}
			return session.getSessionID();
		}
		return 0;
	}

	/**
	 * Provide to a user with a valid session ID to join a specific
	 * ApplicationInstance of the application open by the user. Then create un
	 * ActorSession in the ApplicationInstance and return the ActorSessionID.
	 * 
	 * @param sID
	 *            the Session ID
	 * @param aIID
	 *            the ApplicationInstance ID
	 * @return the ActorSession ID
	 */
	public synchronized int joinAI(int sID, int aIID) {
		try {
			MasterApplicationInstance masterApp = this.getSessionOwner(sID);
			Session session;
			if (masterApp != null) {
				session = masterApp.getSession(sID);
				session.refreshTimeout();
				cat.debug("!!JOIN AI: " + session.getActorSessionID());

				if (session.getActorSessionID() == 0) { // there is no already
														// an actorSession
														// linked to that
														// session
					ApplicationInstance applicationInstance = masterApp
							.getApplicationInstance(aIID);
					masterApp.updateLastAIListChangedTime();

					cat.debug("!!JOIN AI: applicationInstance "
							+ applicationInstance);
					if (applicationInstance != null) {
						cat.debug("!!JOIN AI: applicationInstance.isJoinable "
								+ applicationInstance.isJoinable());
						if (applicationInstance.isJoinable()) {
							int aSID = applicationInstance
									.createNewActorSession(applicationInstance
											.getApplicationInstanceID(),
											session);
							cat.debug("!!Creation actor session ID for app ID "
									+ aIID);
							cat.debug("Number players : "
									+ applicationInstance.actorSessionSize());
							return aSID;
						}
					}
				} else {
					return rejoinAI(masterApp, session.getActorSessionID(),
							aIID);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			cat.error("Platform: error during joining, joinAI(" + sID + ","
					+ aIID + ")");
		}
		return 0;
	}

	/**
	 * Rejoin the linked AI after a deconnection.
	 * 
	 * @param masterApp
	 * @param sid
	 */
	private int rejoinAI(MasterApplicationInstance masterApp, int aSID, int aIID) {
		masterApp.getActorSessionAI(aSID).sendPlayersInfos(aSID);
		ApplicationInstance applicationInstance = masterApp
				.getApplicationInstance(aIID);
		ActorSession actorSession = applicationInstance.getActorSession(aSID);
		// raise event
		JoinEvent joinEv = new JoinEventImpl(actorSession.getActorSessionID(),
				actorSession.getPseudoName());
		applicationInstance.onJoinEvent(joinEv);
		return aSID;
	}

	public Iterator enumerateMasterApplicationInstance() {
		return masterApplicationInstances.iterator();
	}

	/**
	 * Provide to a user with a valid session ID to join a random
	 * ApplicationInstance corresponded to tbe application opened by the user.
	 * 
	 * @param sID
	 *            the Session ID
	 * @return the ActorSession ID
	 */
	public synchronized int joinAIRnd(int sID) {
		try {
			MasterApplicationInstance masterApp = this.getSessionOwner(sID);
			int aIIDRnd = masterApp.getAnAIID();
			return this.joinAI(sID, aIIDRnd);
		} catch (Exception e) {
			cat.info("error during joinAIRnd request, invalid sID");
		}
		return 0;
	}

	/**
	 * Provide to a user with a valid session ID to create an
	 * ApplicationInstance with a minimum start number of actors and a maximum
	 * number of actors. The ApplicationInstance status is: "public": if the
	 * table of actors is empty. "private": if the table of actors contains some
	 * Actor ID. Returns the ID of the ApplicationInstance created.
	 * 
	 * @param sID
	 *            the Session ID
	 * @param minActors
	 *            minimum number of actors for starting
	 * @param maxActors
	 *            maximum number of actors
	 * @param actors
	 *            a table of Actor ID
	 * @return the ApplicationInstance ID
	 */
	public int createAI(int sID, int minActors, int maxActors, String[] actors) {
		try {
			MasterApplicationInstance masterApp = this.getSessionOwner(sID);

			cat.debug("Platform: createAI call by sid n°" + sID);
			cat.debug("Platform: master app>" + masterApp);
			if (masterApp != null) {
				Session session = masterApp.getSession(sID);
				cat.debug("Platform: session >" + session);
				if (session.getActorSessionID() == 0) {
					cat.debug("Platform: OK sid n°" + sID
							+ " no currently in an AI, creating a new AI");
					return masterApp.createApplicationInstance(session,
							minActors, maxActors, actors);
				} else {
					return session.getActorSessionID();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			cat.error("Platform: error " + e.getMessage()
					+ " during creation, createrAI(" + sID + "," + minActors
					+ "," + maxActors + ")");
		}
		return 0;
	}

	/**
	 * This method is called when a user quit the application. ActorSession,
	 * Session, Actor instances corresponding to the user are removed.
	 * 
	 * @param sID
	 *            the Session ID
	 */
	public void quit(int sID) {
		try {
			MasterApplicationInstance masterApp = this.getSessionOwner(sID);
			masterApp.unlog(sID);
		} catch (Exception e) {
			cat.info("error during platform quit request, invalid sID");
		}
	}

	/**
	 * Return the MasterApplication hosts the Session linked to the ActorSession
	 * ID.
	 * 
	 * @param aSID
	 *            the ActorSessionID
	 * @return the MasterApplication
	 */
	private MasterApplicationInstance getLinkedSessionOwner(int aSID) {
		MasterApplicationInstance masterApplicationInstance = null;
		for (Iterator iter = enumerateMasterApplicationInstance(); iter
				.hasNext();) {
			masterApplicationInstance = (MasterApplicationInstance) iter.next();
			if (masterApplicationInstance.getActorSessionAI(aSID) != null)
				return masterApplicationInstance;
		}
		cat
				.error("Platform: no MasterApp found with this ASID, getLinkedSessionOwner("
						+ aSID + ")");
		return null;
	}

	/**
	 * Return the ApplicationInstance containing the aSID.
	 * 
	 * @param aSID
	 *            the actor session ID
	 * @return the MasterApplicationInstance object
	 */
	public ApplicationInstance getActorSessionOwner(int aSID) {
		try {
			MasterApplicationInstance masterApp = this
					.getLinkedSessionOwner(aSID);
			if (masterApp == null) {
				return null;
			}
			masterApp.getASIDAssociatedSession(aSID).refreshTimeout();
			return masterApp.getActorSessionAI(aSID);
		} catch (Exception e) {
			e.printStackTrace();
			cat.info("error during getting actor session for aSID(" + aSID
					+ ")");
		}
		return null;
	}

}
