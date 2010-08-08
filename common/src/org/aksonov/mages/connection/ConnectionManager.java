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

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.Hashtable;

import org.aksonov.tools.Log;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpConnection;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.HttpURL;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;

// TODO: Auto-generated Javadoc
/**
 * The Class ConnectionManager.
 */
public class ConnectionManager {
	
	/** The connection manager. */
	private static HttpConnectionManager connectionManager = new SimpleHttpConnectionManager();
	
	/** The hosts. */
	private static Hashtable<String, HostConfiguration> hosts = new Hashtable<String, HostConfiguration>();
	
	/** The Constant TIMEOUT. */
	private static final int TIMEOUT = 7000;
	
	/** The Constant shortReader. */
	private static final DataReader shortReader = new DataReader() {

		@Override
		public Object read(DataInputStream stream) throws Exception {
			int k = stream.readShort();
			Log.d("read", k + "");
			return k;
		}

	};

	/** The Constant emptyReader. */
	public static final DataReader emptyReader = new DataReader() {

		@Override
		public Object read(DataInputStream stream) throws Exception {
			return 0;
		}

	};
	
	/** The Constant longReader. */
	private static final DataReader longReader = new DataReader() {
		@Override
		public Object read(DataInputStream stream) throws Exception {
			long k = stream.readLong();
			Log.d("read", k + "");
			return k;

		}

	};

	/**
	 * Gets the connection.
	 * 
	 * @param host the host
	 * 
	 * @return the connection
	 */
	private static HttpConnection getConnection(String host) {
		try {
			synchronized (hosts) {
				if (!hosts.contains(host)) {
					HttpURL httpURL = new HttpURL(host);
					HostConfiguration hostConfig = new HostConfiguration();
					hostConfig.setHost(httpURL.getHost(), httpURL.getPort());
					hosts.put(host, hostConfig);
				}
			}
			HostConfiguration hostConfig = hosts.get(host);
			//Log.d("ConnectionManager", "Retrieving connection from the pool");
			HttpConnection connection = connectionManager
					.getConnectionWithTimeout(hostConfig, TIMEOUT);

			return connection;
		} catch (Exception e) {
			Log.e("ConnectionManager.getConnection", e);
			return null;
		}
	}

	/**
	 * Read short.
	 * 
	 * @param host the host
	 * @param servlet the servlet
	 * @param formData the form data
	 * 
	 * @return the int
	 * 
	 * @throws Exception the exception
	 */
	public static int readShort(String host, String servlet, String formData) throws Exception {
		return (Integer) readGet(host, servlet, formData, shortReader);
	}

	/**
	 * Read empty.
	 * 
	 * @param host the host
	 * @param servlet the servlet
	 * @param formData the form data
	 * 
	 * @return the int
	 * 
	 * @throws Exception the exception
	 */
	public static int readEmpty(String host, String servlet, String formData) throws Exception {
		return (Integer) readGet(host, servlet, formData, emptyReader);
	}

	/**
	 * Read short.
	 * 
	 * @param host the host
	 * @param servlet the servlet
	 * @param formData the form data
	 * @param entity the entity
	 * 
	 * @return the int
	 * 
	 * @throws Exception the exception
	 */
	public static int readShort(String host, String servlet, String formData,
			RequestEntity entity)  throws Exception {
		return (Integer) readPost(host, servlet, formData, entity, shortReader);
	}

	/**
	 * Read long.
	 * 
	 * @param host the host
	 * @param servlet the servlet
	 * @param formData the form data
	 * 
	 * @return the long
	 * 
	 * @throws Exception the exception
	 */
	public static long readLong(String host, String servlet, String formData) throws Exception {
		return (Long) readGet(host, servlet, formData, longReader);
	}

	/**
	 * Read post.
	 * 
	 * @param host the host
	 * @param servlet the servlet
	 * @param formData the form data
	 * @param entity the entity
	 * @param reader the reader
	 * 
	 * @return the object
	 * 
	 * @throws Exception the exception
	 */
	public static Object readPost(String host, String servlet, String formData,
			RequestEntity entity, DataReader reader) throws Exception {

		HttpConnection connection = getConnection(host);
		if (connection == null) {
			throw new IllegalArgumentException("Null connection");
		}
		synchronized (connection) {
			PostMethod postMethod = null;
			try {
				//Log.d("ConnectionManager", "Open connection");
				connection.open();
				//Log.d("ConnectionManager", "Set timeout");
				connection.setSocketTimeout(TIMEOUT);
				postMethod = new PostMethod(host + "/" + servlet
						+ "?" + formData);
				postMethod.setRequestEntity(entity);
				//postMethod.setRequestHeader("Transfer-encoding", "base64");
				//postMethod.setRequestHeader("Content-type", "application/octet-stream");
				postMethod.execute(new HttpState(), connection);
				InputStream response = postMethod.getResponseBodyAsStream();
				DataInputStream stream = new DataInputStream(response);
				Object data = reader.read(stream);
				return data;
			} catch (Exception e) {
				Log.e("ConnectionManager", e);
				throw e;
			} finally {
				if (postMethod != null) postMethod.releaseConnection();
				connection.close();
				connectionManager.releaseConnection(connection);
			}
		}
	}

	/**
	 * Read get.
	 * 
	 * @param host the host
	 * @param servlet the servlet
	 * @param formData the form data
	 * @param reader the reader
	 * 
	 * @return the object
	 * 
	 * @throws Exception the exception
	 */
	public static Object readGet(String host, String servlet, String formData,
			DataReader reader) throws Exception {
		HttpConnection connection = getConnection(host);
		if (connection == null) {
			throw new IllegalArgumentException("Null connection");
		}
		synchronized (connection) {
			GetMethod getMethod = null;
			try {
				Log.d("ConnectionManager", "Open connection");
				connection.open();
				connection.setSocketTimeout(TIMEOUT);
				String url = host + "/" + servlet + "?" + formData;
				Log.d("ConnectionManager", "GET " + url);
				getMethod = new GetMethod(url);
				//getMethod.setRequestHeader("Transfer-encoding", "base64");
				//getMethod.setRequestHeader("Content-type", "application/octet-stream");
				getMethod.execute(new HttpState(), connection);
				InputStream response = getMethod.getResponseBodyAsStream();
				DataInputStream stream = new DataInputStream(response);
				Object data = reader.read(stream);
				return data;
			} catch (Exception e) {
				Log.e("ConnectionManager", e);
				throw e;
			} finally {
				if (getMethod != null){
					getMethod.releaseConnection();
				}
				connection.close();
				connectionManager.releaseConnection(connection);
			}
		}
	}

}
