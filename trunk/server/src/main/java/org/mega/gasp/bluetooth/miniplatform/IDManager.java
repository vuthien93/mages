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

import java.util.Iterator;
import java.util.Vector;

/**
 * IDManager provide a management of the temporary IDs: ActorSessionID (ASID).
 * These ID must be unique on the platform.
 * There is a vector of released ASID sort by ASID and a meter to insure unicity.
 * A max is set to limit the size of the IDs to improve the length of the message communications, in this
 * version of the platform the type short is used.
 * The algorithm to generate a new ID: the meter is incremented for a new ID attribution if the vector of released
 * ID is empty, else the first ID of the vector is attributed and removed from the vector.
 * The algorithm to release an ID: the meter is decremented if the ID is the max attributed ID and a purge of
 * the vector is done, else the ID is released in the vector and insert at the right place (growing order).
 * 
 * @author PELLERIN Romain (pellerin@cnam.fr) - MEGA Project  
 */


public class IDManager{
	
	// short are the type choosed for our mobile communication
	// to improve the message length and in the same way the
	// communication speed
	// short enable to host 32767 users simultanely
	// it is possible too use int type for communication but it
	// decrease performance but enable to host more users
	private int max = 32767; 
	
	private int maxFreeID;
	
	private Vector freeASID;


	public IDManager(){
		freeASID = new Vector();
		maxFreeID = 1; // 0 is the neutral element
	}
	
	
	/**
	 * Generate an unique ActorSession ID.
	 * 
	 * @return ASID
	 */
	public synchronized int generateASID() {
	    if (freeASID.isEmpty()){
			int id = maxFreeID;
			if (id<max){
				maxFreeID = id+1;
				return id;
			}
			else return 0;
		}
		else{
			Integer generatedID = (Integer) freeASID.firstElement();
			freeASID.remove(generatedID);
			return generatedID.intValue() ; 
		}
	}
	
	
	/**
	 * Release an ActorSession ID.
	 * 
	 * @param tempID the ASID
	 */
	public synchronized void releaseASID(int tempID) {  
		if (tempID == maxFreeID-1){// tempID is the highest SID delivered
			maxFreeID = maxFreeID-1;
			purgeVector(freeASID);
		}	
		else{
			indexID(freeASID,tempID);
		}
	}

	/**
	 * Place the ID at his place in the vector, increase order.
	 * 
	 * @param releasedIdVector the vector of released ID
	 * @param tempID the tempID to place
	 */
	private void indexID(Vector releasedIdVector, int tempID) {
		int index = 0;
		for(Iterator iter = releasedIdVector.iterator(); iter.hasNext();){
			Integer id = (Integer) iter.next();
			if (tempID < id.intValue()){
				break;
			}
			index++;
		}
		releasedIdVector.insertElementAt(new Integer(tempID),index);
	}
	

	/**
	 * Purge the vector with the alogorithm above:
	 * -> if the released ID is the max used ID then search in the vector
	 * releasedID-1, if it is in remove it, set to max Free ID and replay the algorithm.
	 * 
	 * @param releasedIdVector
	 * @param index
	 */
	private void purgeVector(Vector releasedIdVector) {
		while(((Integer) releasedIdVector.lastElement()).intValue() == maxFreeID-1){
			releasedIdVector.removeElementAt(releasedIdVector.size()-1);
			maxFreeID = maxFreeID-1;
		}
	}
	
}
