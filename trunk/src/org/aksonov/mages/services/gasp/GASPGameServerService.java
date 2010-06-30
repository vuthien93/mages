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
package org.aksonov.mages.services.gasp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.aksonov.mages.services.IGameServer;
import org.aksonov.tools.ChunkedClient;
import org.aksonov.tools.Log;
import org.mega.gasp.moods.CustomTypes;

import android.app.Service;
import android.content.Intent;
import android.os.RemoteException;
import android.os.IBinder;

// TODO: Auto-generated Javadoc
/**
 * Android service returns GASPGameServer.
 * 
 * @author Pavel
 */
public class GASPGameServerService extends Service {
	
	/** The server. */
	private final GASPGameServer server = new GASPGameServer();
	
	/** The m binder. */
	private final IGameServer.Stub mBinder = server;
	
	/* (non-Javadoc)
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	
	public IBinder onBind(Intent intent) {
		if (IGASPServerConfiguration.class.getName().equals(intent.getAction())) {
			return mGASPBinder;
		}
		return mBinder;
	}

	/** The m gasp binder. */
	private final IGASPServerConfiguration.Stub mGASPBinder = new IGASPServerConfiguration.Stub() {
		
		public void setConnectInfo(int session, String host, int appId,
				String username, String password, boolean isComet) {
			server.setConnectInfo(session, host, appId, username, password, isComet);
		}

		
		public int createSession() throws RemoteException {
			return server.createSession();
		}
		

	};

	/**
	 * Servlet get request.
	 * 
	 * @param host
	 *            the host
	 * @param servletName
	 *            the servlet name
	 * @param formData
	 *            the form data
	 * 
	 * @return the data input stream
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static DataInputStream servletGetRequest(String host,
			String servletName, String formData) throws IOException {

		Log.d("GASP", "GASP.servletRequest " + host + "/" + servletName + "?"
				+ formData);
		HttpURLConnection connection = (HttpURLConnection) new URL(host + "/"
				+ servletName + "?" + formData).openConnection();
		return new DataInputStream(connection.getInputStream());
		// return new BufferedReader(new
		// InputStreamReader(connection.getInputStream()), 128);
		/*
		 * try { if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) { }
		 * else { Log.e("GASP", "servletGetRequest " + "Invalid code: " +
		 * connection.getResponseCode()); return null; } } finally { //
		 * connection.disconnect(); }
		 */
	}

	/**
	 * Send a request to the specified servlet with the http post parameters.
	 * Return the servlet response DataInputStream.
	 * 
	 * @param servletName
	 *            the name of the servlet
	 * @param formData
	 *            the HTTP post parameters
	 * @param host
	 *            the host
	 * @param data
	 *            the data
	 * @param customTypes
	 *            the custom types
	 * 
	 * @return the DataInputStream
	 * 
	 * @throws IOException *
	 * @throws Exception
	 *             the exception
	 */
	/**
	 * Send the data in the hashtable to the specified servlet.
	 * 
	 * @param servletName
	 *            the name of the servlet
	 * @return the DataInputStream of the servlet response
	 * @throws IOException
	 */
	public static DataInputStream servletSendDataRequest(String host,
			String servletName, String formData, Object[] data,
			CustomTypes customTypes) throws Exception {
		Log.d("GASP", "GASP.servletSendDataRequest " + host + "/" + servletName
				+ "?" + formData);
		HttpURLConnection connection = (HttpURLConnection) new URL(host + "/"
				+ servletName + "?" + formData).openConnection();
		connection.setDoInput(true);
		connection.setDoOutput(true);
		connection.setUseCaches(false);
		// connection.setReadTimeout(TIMEOUT);

		// connection.setRequestProperty("Content-Type",
		// "application/x-www-form-urlencoded");
		connection.setRequestProperty("Content-Type",
				"application/octet-stream");

		DataOutputStream dos = new DataOutputStream(connection
				.getOutputStream());

		org.aksonov.mages.Helper.encode(data, dos, customTypes);
		dos.flush();
		dos.close();
		return new DataInputStream(connection.getInputStream());
	}

	/**
	 * Send a request to the specified servlet with the http post parameters.
	 * Return the servlet response DataInputStream.
	 * 
	 * @param servletName
	 *            the name of the servlet
	 * @param host
	 *            the host
	 * 
	 * @return the DataInputStream
	 * 
	 * @throws IOException *
	 * @throws Exception
	 *             the exception
	 */
	/**
	 * Send the data in the hashtable to the specified servlet.
	 * 
	 * @param servletName
	 *            the name of the servlet
	 * @return the DataInputStream of the servlet response
	 * @throws IOException
	 */
	public static DataInputStream servletSendCometDataRequest(String host,
			String servletName) throws Exception {
		Log.d("GASPGameServer", "GASP.servletSendCometDataRequest " + host + "/"
				+ servletName);
		ChunkedClient client = new ChunkedClient(new URL(host + "/"
				+ servletName));
		client.sendHeaders();
		client.send("0");
		client.sendLastChunk();
		Log.d("GASPGameServer", "reading response headers");
		client.readResponseHeaders();
		if (client.isOK()) {
			Log.d("GASPGameServer", "return stream");
			return client.getDataStream();
		} else {
			Log.e("GASP", "Invalid response: " + client.getResponse());
			return null;
		}

		/*
		 * PrintWriter out = new PrintWriter(connection.getOutputStream(),
		 * true); out.print("test"); out.flush(); connection.connect();
		 */

		/*
		 * int code = connection.getResponseCode(); if (code ==
		 * HttpURLConnection.HTTP_OK) { Log .d("GASP",
		 * "GASP.servletSendCometDataRequest: closed everything"); return
		 * connection; } else { Log.e("GASP", "GASP " + "Invalid code: " +
		 * code); return null; }
		 */
	}

	/**
	 * Send a request to the specified servlet with the http post parameters.
	 * Return the servlet response DataInputStream.
	 * 
	 * @param servletName
	 *            the name of the servlet
	 * @param host
	 *            the host
	 * @param aSID
	 *            the a sid
	 * 
	 * @return the DataInputStream
	 * 
	 * @throws IOException *
	 * @throws Exception
	 *             the exception
	 */
	/**
	 * Send the data in the hashtable to the specified servlet.
	 * 
	 * @param servletName
	 *            the name of the servlet
	 * @return the DataInputStream of the servlet response
	 * @throws IOException
	 */
	public static HttpURLConnection servletGetCometDataResponse(String host,
			String servletName, int aSID) throws Exception {
		Log.d("GASP", "GASP.servletGetCometDataResponse " + host + "/"
				+ servletName);
		HttpURLConnection connection = (HttpURLConnection) new URL(host + "/"
				+ servletName + "?aSID=" + aSID).openConnection();
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Connection", "Keep-Alive");
		connection.setChunkedStreamingMode(1); // use default chunk length
		connection.setReadTimeout(0);
		connection.setDoInput(true);
		connection.setDoOutput(true);
		connection.connect();

		return connection;
	}

}
