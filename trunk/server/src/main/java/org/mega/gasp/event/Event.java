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
package org.mega.gasp.event;

/**
 * Generic event.
 * 
 * @author PELLERIN Romain (pellerin@cnam.fr) - MEGA Project 
 */
public interface Event {
	/**
	 * Returns the code of the event.
	 * 
	 * @return the code of the event
	 */
	int getCode();
	
	/**
	 * Returns the id of the event.
	 * 
	 * @return the id of the event
	 */
	int getId();
	
	/**
	 * Sets the id of the event.
	 *
	 * @params id id of the event
	 */
	void setId(int id);
	
	
	/**
	 * Returns the ID of the ActorSession who raises the event.
	 * 
	 * @return the ActorSession ID
	 */
	int getActorSessionID();

}
