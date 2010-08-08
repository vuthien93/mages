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
package org.mega.gasp.platform.utils;

import java.util.List;
import java.util.Vector;

import org.mega.gasp.platform.ActorSession;

/**
 * This class is used to construct an object representing the current AIs State.
 * 
 * @author PELLERIN Romain (pellerin@cnam.fr) - MEGA Project
 */
public class ApplicationInstanceInfos {

	private final int appInstanceID;
	private final int nbP;
	private final int min;
	private final int max;
	private final Vector actorSessions;
	private final Object customData;

	public ApplicationInstanceInfos(int applicationInstanceID, int minOfActors,
			int maxOfActors, Vector actorSessions, Object data) {
		appInstanceID = applicationInstanceID;
		nbP = actorSessions.size();
		this.actorSessions = new Vector(actorSessions);
		min = minOfActors;
		max = maxOfActors;
		customData = data;
	}

	public int getApplicationInstanceID() {
		return appInstanceID;
	}

	public int getNbPlayers() {
		return nbP;
	}

	public Vector getActorSessions() {
		return actorSessions;
	}

	public int getMinActors() {
		return min;
	}

	public int getMaxActors() {
		return max;
	}
	
	public Object getCustomData(){
		return customData;
	}
}
