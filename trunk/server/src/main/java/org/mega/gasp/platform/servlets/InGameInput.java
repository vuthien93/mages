package org.mega.gasp.platform.servlets;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Hashtable;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.CometEvent;
import org.apache.catalina.CometProcessor;
import org.apache.log4j.Category;
import org.mega.gasp.event.DataEvent;
import org.mega.gasp.event.impl.DataEventImpl;
import org.mega.gasp.moods.CustomTypes;
import org.mega.gasp.platform.ActorSession;
import org.mega.gasp.platform.ApplicationInstance;
import org.mega.gasp.platform.MasterApplicationInstance;
import org.mega.gasp.platform.Session;
import org.mega.gasp.platform.impl.ActorSessionImpl;
import org.mega.gasp.platform.impl.PlatformImpl;

/**
 * Helper class to implement Comet functionality.
 */
public class InGameInput extends HttpServlet implements CometProcessor {
	private Category cat;
	private PlatformImpl platform;

	public void init() throws ServletException {
		ServletContext app = getServletContext();
		platform = PlatformImpl.getPlatform(getServletContext());
		cat = Category.getInstance("InGameServlet");
		cat.debug("Log4J InGame Category instanciated");
	}

	public void destroy() {
	}

	/**
	 * Process the given Comet event.
	 * 
	 * @param event
	 *            The Comet event that will be processed
	 * @throws IOException
	 * @throws ServletException
	 */
	public void event(CometEvent event) throws IOException, ServletException {

		if (event.getEventType() == CometEvent.EventType.ERROR) {
			end(event);
		} else if (event.getEventType() == CometEvent.EventType.END) {
			end(event);
		} else if (event.getEventType() == CometEvent.EventType.BEGIN) {
			begin(event);
		} else if (event.getEventType() == CometEvent.EventType.READ) {
			read(event);
		}
	}

	protected void begin(CometEvent event) throws IOException, ServletException {
		cat.debug("InGameInput servlet begin()");
		//event.getHttpServletRequest().setAttribute(
		//		"org.apache.tomcat.comet.timeout", new Integer(30 * 1000));
	}

	protected void read(CometEvent event) throws IOException, ServletException {
		final HttpServletRequest request = event.getHttpServletRequest();
		final HttpServletResponse response = event.getHttpServletResponse();

		String s = request.getParameter("aSID");
		cat.debug("InGameInput servlet read() aSID = " + s);
		if (s == null || s.trim().equals("")) {
			throw new IllegalArgumentException("aSID parameter is not defined");
		}
		// get actor session id
		int aSID = Integer.parseInt(s);
		s = request.getParameter("id");

		// take the platform, the AI , the MAI and the Session associated to
		// ASID
		final ApplicationInstance appIns = platform.getActorSessionOwner(aSID);
		if (appIns == null) {
			end(event);
			return;
		}
		final DataInputStream dis = new DataInputStream(request
				.getInputStream());
		ActorSessionImpl aSession = (ActorSessionImpl) appIns
				.getActorSession(aSID);
		CustomTypes customTypes = appIns.getCustomTypes();
		int sID = aSession.getSessionID();

		MasterApplicationInstance masterApp = platform.getSessionOwner(sID);
		Session session = masterApp.getSession(sID);

		if (dis.available() > 0) {
			try {
				byte signal = dis.readByte();
				if (signal == 1) {
					Hashtable h = customTypes.decodeData(dis);

					if (h.size() != 0) {
						cat
								.debug("Received data from client, number of objects: "
										+ h.size());
						DataEvent de = new DataEventImpl(aSID, h);
						appIns.onDataEvent(de);
					}
				} else if (signal == 2) {
					int size = dis.readInt();
					for (int i = 0; i < size; i++) {
						int confirmedId = dis.readInt();
						cat.debug("ASID: " + aSID + " Received confirmed ID from client: "
								+ confirmedId );
						aSession.confirmId(confirmedId);
					}
				}
				Thread.sleep(250);
			} catch (Exception e) {
				cat.error("Error during InGameInput: " + e.getMessage(), e);
			}
		}

	}

	protected void end(CometEvent event) throws IOException, ServletException {
		cat.debug("InGameInput end()");
		final HttpServletRequest request = event.getHttpServletRequest();
		final HttpServletResponse response = event.getHttpServletResponse();
		String s = request.getParameter("aSID");
		System.out.println("aSID = " + s);
		if (s == null || s.trim().equals("")) {
			throw new IllegalArgumentException("aSID parameter is not defined");
		}
		// get actor session id
		int aSID = Integer.parseInt(s);
		final ApplicationInstance appIns = platform.getActorSessionOwner(aSID);
		if (appIns != null) {
			ActorSession aSession = appIns.getActorSession(aSID);
			if (aSession != null)
				aSession.setConnected(false);
		}
		event.close();

	}

	protected void service(HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {
		// Compatibility method: equivalent method using the regular connection
		// model
		PrintWriter writer = response.getWriter();
		writer
				.println("<!doctype html public \"-//w3c//dtd html 4.0 transitional//en\">");
		writer
				.println("<html><head><title>GASP</title></head><body bgcolor=\"#FFFFFF\">");
		writer.println("This servlet supports only Comet processing");
		writer.println("</body></html>");
	}

}
