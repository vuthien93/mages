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
package org.aksonov.mages.connection;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.aksonov.mages.Helper;
import org.aksonov.tools.Log;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.mega.gasp.moods.CustomTypes;

// TODO: Auto-generated Javadoc
/**
 * The Class CustomTypesRequestEntity.
 */
public class CustomTypesRequestEntity implements RequestEntity {
	
	/** The custom types. */
	private final CustomTypes customTypes;
	
	/** The data. */
	private final Object[] data;

	/**
	 * Instantiates a new custom types request entity.
	 * 
	 * @param customTypes the custom types
	 * @param data the data
	 */
	public CustomTypesRequestEntity(CustomTypes customTypes, Object[] data) {
		this.customTypes = customTypes;
		this.data = data;
	}

	/* (non-Javadoc)
	 * @see org.apache.commons.httpclient.methods.RequestEntity#getContentLength()
	 */
	@Override
	public long getContentLength() {
		return -1;
	}

	/* (non-Javadoc)
	 * @see org.apache.commons.httpclient.methods.RequestEntity#getContentType()
	 */
	@Override
	public String getContentType() {
		return "application/binary";
	}

	/* (non-Javadoc)
	 * @see org.apache.commons.httpclient.methods.RequestEntity#isRepeatable()
	 */
	@Override
	public boolean isRepeatable() {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.apache.commons.httpclient.methods.RequestEntity#writeRequest(java.io.OutputStream)
	 */
	@Override
	public void writeRequest(OutputStream stream) throws IOException {
		DataOutputStream dos = new DataOutputStream(stream);
		try {
			Helper.encode(data, dos, customTypes);
		} catch (Exception e) {
			Log.e("CustomTypesRequestEntity", e);
		}
		dos.flush();
	}

}
