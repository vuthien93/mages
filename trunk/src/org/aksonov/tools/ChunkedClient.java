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
package org.aksonov.tools;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;

/**
 * Simple chunked client since java connection classes doesn't correctly works with Android for comet-based protocols
 * @author Pavel
 *
 */
public class ChunkedClient {
	private static final String ENCODING = "ISO-8859-1";
	private static final String DELIMITER = "\r\n";
	private final URL url;
	private InputStream inputStream;
	private OutputStream outputStream;
	private DataInputStream dis;
	private DataOutputStream dos;
	private Socket socket;
	private StringBuffer headers = new StringBuffer();
	private boolean isOK = false;
	private String response = null;
	private final int timeout;
	
	public boolean isOK(){
		return isOK;
	}
	
	public String getResponse(){
		return response;
	}
	
	public DataInputStream getDataStream(){
		return dis;
	}

	public String readResponseHeaders() throws Exception {
		waitData();
		readHeaderLine();
		if (headers.toString().contains("200 OK")) {
			isOK = true;
			readHeaderLine();
			readHeaderLine();
			readHeaderLine();
			readHeaderLine();
		}
		response = headers.toString();
		return response;
	}

	public ChunkedClient(URL url, int timeout) throws IOException {
		this.url = url;
		this.timeout = timeout;
		initConnection();
		sendHeaders();
	}

	public ChunkedClient(URL url) throws IOException {
		this(url, Integer.MAX_VALUE);
	}

	private void initConnection() throws IOException {
		int port = url.getPort();
		port = (port < 0) ? url.getDefaultPort() : port;
		socket = new Socket(url.getHost(), port);
		socket.setSoTimeout(timeout);
		socket.setKeepAlive(true);
		inputStream = socket.getInputStream();
		outputStream = socket.getOutputStream();
		dis = new DataInputStream(inputStream);
		dos = new DataOutputStream(outputStream);
	}

	public void sendHeaders() throws IOException {
		String path = url.getPath() + "?" + url.getQuery();
		StringBuffer outputBuffer = new StringBuffer();
		outputBuffer.append("POST " + path + " HTTP/1.1" + DELIMITER);
		outputBuffer.append("Host: " + url.getHost() + DELIMITER);
		outputBuffer.append("User-Agent: CometTest" + DELIMITER);
		outputBuffer.append("Connection: keep-alive" + DELIMITER);
		outputBuffer.append("Content-Type: text/plain" + DELIMITER);
		outputBuffer.append("Transfer-Encoding: chunked" + DELIMITER);
		outputBuffer.append(DELIMITER);
		byte[] outputBytes = outputBuffer.toString().getBytes(ENCODING);
		outputStream.write(outputBytes);
		outputStream.flush();
	}

	public void readHeaderLine() throws IOException {

		int ch = 0;
		do {
			ch = dis.readByte();
			headers.append((char) ch);
		} while (ch != 13);
		headers.append((char) dis.readByte());
	}

	public void waitData() throws Exception {
		while (dis.available() == 0) {
			Thread.sleep(50);
		}
	}

	public void send(String chunkData) throws IOException {
		byte[] chunkBytes = chunkData.getBytes(ENCODING);
		String hexChunkLength = Integer.toHexString(chunkBytes.length);
		StringBuffer outputBuffer = new StringBuffer();
		outputBuffer.append(hexChunkLength);
		outputBuffer.append(DELIMITER);
		outputBuffer.append(chunkData);
		outputBuffer.append(DELIMITER);
		byte[] outputBytes = outputBuffer.toString().getBytes(ENCODING);
		outputStream.write(outputBytes);
		outputStream.flush();
	}

	public void sendShort(int data) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream d = new DataOutputStream(bos);
		d.writeShort(data);
		byte[] chunkBytes = bos.toByteArray();
		String hexChunkLength = Integer.toHexString(chunkBytes.length);
		StringBuffer outputBuffer = new StringBuffer();
		outputBuffer.append(hexChunkLength);
		outputBuffer.append(DELIMITER);
		outputBuffer.append(chunkBytes);
		outputBuffer.append(DELIMITER);
		byte[] outputBytes = outputBuffer.toString().getBytes(ENCODING);
		outputStream.write(outputBytes);
		outputStream.flush();
	}

	public void sendLastChunk() throws IOException {

		byte[] outputBytes = new String("0" + DELIMITER + DELIMITER)
				.getBytes(ENCODING);
		outputStream.write(outputBytes);
		outputStream.flush();
	}
}

