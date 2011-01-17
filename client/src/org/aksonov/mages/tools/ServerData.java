/***
 * Mages: Multiplayer Game Engine for mobile devices
 * Copyright (C) 2008 aksonov
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
 * Contact: aksonov dot gmail dot com
 *
 * Author: Pavlo Aksonov
 */
package org.aksonov.mages.tools;

// TODO: Auto-generated Javadoc
/**
 * The Class ServerData.
 */
public class ServerData {
	
	/** The uri. */
	private final String uri;
	
	/** The app id. */
	private final int appId;
	
	/** The type. */
	private final String type;
	
	/**
	 * Gets the uri.
	 * 
	 * @return the uri
	 */
	public String getUri(){
		return uri;
	}
	
	/**
	 * Gets the app id.
	 * 
	 * @return the app id
	 */
	public int getAppId(){
		return appId;
	}
	
	/**
	 * Instantiates a new server data.
	 * 
	 * @param uri
	 *            the uri
	 * @param appId
	 *            the app id
	 * @param type
	 *            the type
	 */
	public ServerData(String uri, int appId, String type){
		this.appId = appId;
		this.uri = uri;
		this.type = type;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	
	public boolean equals(Object o) {
		if (o == null || !(o instanceof ServerData)){
			return false;
		}
		ServerData od = (ServerData)o;
		return od.appId == this.appId && od.uri == this.uri;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	
	public int hashCode() {
		return this.appId + this.uri.hashCode();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	
	public String toString() {
		return this.appId + this.uri + ", type="+type;
	}
	
	/**
	 * Gets the type.
	 * 
	 * @return the type
	 */
	public String getType(){
		return type;
	}
	

}
