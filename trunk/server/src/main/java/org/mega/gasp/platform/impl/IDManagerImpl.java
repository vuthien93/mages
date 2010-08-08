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

import org.apache.log4j.Category;
import org.mega.gasp.platform.IDManager;

import java.util.Iterator;
import java.util.Vector;

/**
 * IDManager provide a management of the temporary IDs: the MasterApplicationID(MAID), ApplicationInstanceID(AIID),
 * SessionID (SID) and ActorSessionID (ASID).
 * These ID must be unique on the platform.
 * For all type of IDs, there is a vector of released ID sort by ID and a meter to insure unicity.
 * A max is set to limit the size of the IDs to improve the length of the message communications, in this
 * version of the platform the type short is used.
 * The algorithm to generate a new ID: the meter is incremented for a new ID attribution if the vector of released
 * ID is empty, else the first ID of the vector is attributed and removed from the vector.
 * The algorithm to release an ID: the meter is decremented if the ID is the max attributed ID and a purge of
 * the vector is done, else the ID is released in the vector and insert at the right place (growing order).
 * 
 * @author PELLERIN Romain (pellerin@cnam.fr) - MEGA Project  
 */


public class IDManagerImpl implements IDManager {
	
	// short are the type choosed for our mobile communication
	// to improve the message length and in the same way the
	// communication speed
	// short enable to host 32767 users simultanely
	// it is possible too use int type for communication but it
	// decrease performance but enable to host more users
	private int max = 32767; 
	
	private Vector maxFreeIDs; 
	
	private Vector freeASID;
	private Vector freeSID;
	private Vector freeAIID;
	private Vector freeMAID;
	private Category cat;
	
	private final int MAID = 0;
	private final int AIID = 1;
	private final int SID = 2;
	private final int ASID = 3;

	public IDManagerImpl(){
		freeMAID = new Vector();
		freeAIID = new Vector();
		freeSID  = new Vector();
		freeASID = new Vector();
		
		cat = Category.getInstance("GASPLogging");
		
		// vector: {currentMaxFreeMAID, currentMaxFreeAIID, currentMaxFreeSID, currentMaxFreeASID}
		maxFreeIDs = new Vector();
		for(int i=0; i<4; i++) 
		    //init vector at 1 because id=0 is the neutral element for errors
			maxFreeIDs.add(new Integer(1));
	}
	
	/**
	 * Returns the max free ID for ID type.
	 * 
	 * @param idType
	 * @return int
	 */
	private int getMaxFreeID(int idType){
	    return ((Integer) maxFreeIDs.get(idType)).intValue();
	}
	
	
	/**
	 * Sets the max free ID to value of ID type.
	 * 
	 * @param idType
	 * @param value
	 */
	private void setMaxFreeID(int idType, int value){
	    maxFreeIDs.setElementAt(new Integer(value), idType);
	}
	
	
	/**
	 * Increments the max free id of ID type.
	 * 
	 * @param idType
	 */
	private void incrementMaxFreeID(int idType){
	    maxFreeIDs.setElementAt(new Integer(getMaxFreeID(idType)+1), idType);
	}
	
	
	/**
	 * Decrements the max free id of ID type.
	 * 
	 * @param idType
	 */
	private void decrementMaxFreeID(int idType){
	    maxFreeIDs.setElementAt(new Integer(getMaxFreeID(idType)-1), idType);
	}
	
	
	/**
	 * Generate an unique MasterApplication ID.
	 * 
	 * @return an MAID
	 */
	public int generateMAID() {
		return generateID(freeMAID,MAID);
	}
	
	
	/**
	 * Generate an unique ApplicationInstance ID .
	 * 
	 * @return an AIID
	 */
	public int generateAIID() {
		return generateID(freeAIID,AIID);
	}
	
	
	/**
	 * Generate an unique Session ID.
	 * 
	 * @return an SID
	 */
	public int generateSID() {
		return generateID(freeSID,SID);
	}
	
	
	/**
	 * Generate an unique ActorSession ID.
	 * 
	 * @return ASID
	 */
	public int generateASID() {
		return generateID(freeASID,ASID);
	}
	
	
	/**
	 * Release a MasterApplication ID.
	 * 
	 * @param tempID the MAID
	 */
	public void releaseMAID(int tempID) { 
		releaseID(freeMAID,MAID,tempID);
	}
	

	/**
	 * Release an ApplicationInstance ID.
	 * 
	 * @param tempID the AIID
	 */
	public void releaseAIID(int tempID) {  
		releaseID(freeAIID,AIID,tempID);
	}


	/**
	 * Release a Session ID.
	 * 
	 * @param tempID the SID
	 */
	public void releaseSID(int tempID) {  
		releaseID(freeSID,SID,tempID);
	}


	/**
	 * Release an ActorSession ID.
	 * 
	 * @param tempID the ASID
	 */
	public void releaseASID(int tempID) {  
		releaseID(freeASID,ASID,tempID);
	}
	
	
	/**
	 * Generate a unique ID. 
	 * 
	 * @param releasedIdVector the corresponding vector
	 * @param index the index of the max free IDs table 
	 * @return the generated ID
	 */
	private synchronized int generateID(Vector releasedIdVector, int index){
		if (releasedIdVector.isEmpty()){
		    int id = getMaxFreeID(index);
			if (id<max){
				incrementMaxFreeID(index);
			    return id;
			}
			else return 0;
		}
		else{
			Integer generatedID = (Integer) releasedIdVector.firstElement();
			releasedIdVector.remove(generatedID);
			return generatedID.intValue() ; 
		}
	}
	
	
	/**
	 * @param releasedIdVector
	 * @param index the index of the max free IDs table
	 * @param tempID
	 */
	private synchronized void releaseID(Vector releasedIdVector, int index, int tempID) {  
		int maxFreeID = getMaxFreeID(index);
		if (tempID == maxFreeID-1){// tempID is the highest SID delivered
		    decrementMaxFreeID(index);
		    purgeVector(releasedIdVector, index);
		}	
		else indexID(releasedIdVector,tempID);
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
			else index++;
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
	private void purgeVector(Vector releasedIdVector, int index) {
		while((!releasedIdVector.isEmpty())&&((Integer) releasedIdVector.lastElement()).intValue() == getMaxFreeID(index)-1){
			releasedIdVector.removeElementAt(releasedIdVector.size()-1);
			decrementMaxFreeID(index);
		}
	}
}
