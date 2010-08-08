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
package org.mega.gasp.event.impl;

import java.util.Hashtable;

import org.aksonov.tools.Log;
import org.mega.gasp.event.DataEvent;

/**
 * @author PELLERIN Romain (pellerin@cnam.fr) - MEGA Project 
 */
public class DataEventImpl extends EventImpl implements DataEvent{
	
	private Hashtable data;
	

	public DataEventImpl(int actorSessionID, Hashtable dataTable){
		if (actorSessionID == 0){
			Log.d("DataEventImpl", "DOESN'T accept 0 actor session id" );
			throw new IllegalArgumentException("DOESN'T accept 0 actor session id");
		}
		if (dataTable.size() == 0 ){
			Log.d("DataEventImpl", "DOESN'T accept empty hashtable" );
			throw new IllegalArgumentException("DOESN'T accept empty data");
		}
		aSID = actorSessionID;
		data = dataTable;
		code = 5;
	}
	
	/**
	 * Returns the datas containing in the DataEvent
	 * 
	 * @return the hastable of datas
	 */
	public Hashtable getData() {
		return data;
	}
	
	public String toString(){
		String res = "DATAEVENT, id=" + this.getId() + ", size=" + data.size();
		for (int i=0;i<data.size();i++){
			res += "object:"+data.get(i+"")+".";
		}
		return res;
	}

}
