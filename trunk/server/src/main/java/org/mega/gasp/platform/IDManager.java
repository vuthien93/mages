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


public interface IDManager {
	
	/**
	 * Generate an unique MasterApplication ID.
	 * 
	 * @return an MAID
	 */
	int generateMAID();
	
	
	/**
	 * Generate an unique ApplicationInstance ID .
	 * 
	 * @return an AIID
	 */
	int generateAIID();
	
	
	/**
	 * Generate an unique Session ID.
	 * 
	 * @return an SID
	 */
	int generateSID();
	
	
	/**
	 * Generate an unique ActorSession ID.
	 * 
	 * @return ASID
	 */
	int generateASID();
	
	
	/**
	 * Release a MasterApplication ID.
	 * 
	 * @param tempID the MAID
	 */
	void releaseMAID(int tempID);
	
	
	/**
	 * Release an ApplicationInstance ID.
	 * 
	 * @param tempID the AIID
	 */
	void releaseAIID(int tempID);
	
	
	/**
	 * Release a Session ID.
	 * 
	 * @param tempID the SID
	 */
	void releaseSID(int tempID);
	
	
	/**
	 * Release an ActorSession ID.
	 * 
	 * @param tempID the ASID
	 */
	void releaseASID(int tempID);
}
