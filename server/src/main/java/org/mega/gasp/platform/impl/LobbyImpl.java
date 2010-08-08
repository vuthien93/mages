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
import org.mega.gasp.platform.ApplicationInstance;
import org.mega.gasp.platform.Lobby;
import org.mega.gasp.platform.MasterApplicationInstance;


/**
 * Lobby provide informations to the users on the current state of the Gaming Platform.
 * 
 * @author PELLERIN Romain (pellerin@cnam.fr) - MEGA Project 
 */


public class LobbyImpl implements Lobby {
	
    private Category cat;
    
	public LobbyImpl(){}
	
	
	/**
	 * Returns a list of the current active ApplicationInstances associated to a specific application
	 * with the ActorSession contains in fact a stream : (ApplicationInstanceID (username,ActorID)*)*
	 * 
	 * @param sID the application ID
	 * @return vector
	 */
	public Vector getApplicationInstancesInfos(int sID) {
	    cat = Category.getInstance("GASPLogging");
		Vector applicationInstancesInfos = new Vector();
		cat.debug("getApplicationInstanceInfos " + sID);
		MasterApplicationInstance masterApp =  PlatformImpl.getPlatform().getSessionOwner(sID);
		if (masterApp != null){
			for(Iterator iter = masterApp.enumerateApplicationInstance(); iter.hasNext();){
				ApplicationInstance appInstance = (ApplicationInstance) iter.next();
				cat.debug("appInstance " + appInstance.getApplicationInstanceID() + " " + appInstance.isJoinable());
				if (appInstance.isJoinable())
					cat.debug("appInstance added ");
				    applicationInstancesInfos.add(appInstance.getApplicationInstanceInfos());
			}
		} else {
			cat.debug("masterApp is null!");
		}
		
		return applicationInstancesInfos;
	}
	
	/**
	 * Returns last modified time of lobby
	 * @param sID the application ID
	 * @return time (in milliseconds)
	 */
	public long getLobbyModifiedTime(int sID){
	    cat = Category.getInstance("GASPLogging");
		MasterApplicationInstance masterApp =  PlatformImpl.getPlatform().getSessionOwner(sID);
		cat.debug("getLobbyModifiedTime " + sID + ", MasterApp: " + masterApp);
		if (masterApp == null){
			return -1;
		}
		long res =  masterApp.getLastAIListChangedTime();
		cat.debug("getLobbyModifiedTime returns " + res);
		return res;
	}
	
	

}
