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

/**
 * @author PELLERIN Romain (pellerin@cnam.fr) - MEGA Project  
 */
public class ApplicationInstanceInfos {
	
	private int appInstanceID;
	private int nbP;
	private Vector pList;

	
	public ApplicationInstanceInfos(int applicationInstanceID, int nbPlayers, Vector playerList){
		appInstanceID= applicationInstanceID;
		nbP= nbPlayers;
		pList = new Vector(playerList);
	}
	
	public int getApplicationInstanceID(){return appInstanceID;}
	
	public int getNbPlayers(){return nbP;}
	
	public Vector getPlayerList(){return pList;}
}
