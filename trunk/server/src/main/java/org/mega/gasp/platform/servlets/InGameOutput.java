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

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.aksonov.tools.Log;
import org.apache.log4j.Category;
import org.mega.gasp.event.Event;
import org.mega.gasp.event.impl.QuitEventImpl;
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
public class InGameOutput extends HttpServlet {
	class EventInfo {
		public final Event event;
		public final CustomTypes customTypes;
		public final DataOutputStream dos;

		public EventInfo(Event e, CustomTypes customTypes, DataOutputStream dos) {
			this.event = e;
			this.customTypes = customTypes;
			this.dos = dos;
		}

		public String toString() {
			return event.toString();
		}
	}

	private Category cat;
	private PlatformImpl platform;

	public void init() throws ServletException {
		ServletContext app = getServletContext();
		platform = PlatformImpl.getPlatform(getServletContext());
		cat = Category.getInstance("InGameOutputServlet");
		cat.debug("123 Log4J InGameOutput Category instanciated");
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		request.getInputStream().close();
		DataOutputStream dos = new DataOutputStream(response.getOutputStream());
		String s = request.getParameter("aSID");
		
		if (s == null || s.trim().equals("")) {
			throw new IllegalArgumentException("aSID parameter is not defined");
		}
		// get actor session id
		int aSID = Integer.parseInt(s);
		s = request.getParameter("id");

		cat.debug("Servlet InGameOutput: aSID=" + aSID);

		// take the platform, the AI , the MAI and the Session associated to
		// ASID
		ApplicationInstance appIns = platform.getActorSessionOwner(aSID);
		if (appIns == null) {
			dos.close();
			return;
		}
		ActorSessionImpl aSession = (ActorSessionImpl)appIns.getActorSession(aSID);
		int sID = aSession.getSessionID();

		MasterApplicationInstance masterApp = platform.getSessionOwner(sID);
		Session session = masterApp.getSession(sID);
		CustomTypes customTypes = appIns.getCustomTypes();
		aSession.setConnected(true);

		try {
			long last = System.currentTimeMillis();
			while (aSession.isConnected()) {
				if (appIns.isDestroyable()) {
					QuitEventImpl qe = new QuitEventImpl(aSID);
					appIns.onQuitEvent(qe);
					if (appIns.isActorSessionEmpty())
						masterApp.removeApplicationInstance(appIns
								.getApplicationInstanceID());
					break;
				}
				if (aSession.hasNewEvents()) {
					Vector events = aSession.getEvents();
					for (int i = 0; i < events.size(); i++) {
						sendEvent(new EventInfo((Event) events.get(i), appIns
								.getCustomTypes(), dos));
						//aSession.confirmId(((Event)events.get(i)).getId());
					}
				}
				Thread.sleep(250);

			}
		} catch (Exception e) {
			cat.error("Error during inGameOutput:");
			e.printStackTrace();
		} finally {
			dos.close();
		}
	}

	private void sendEvent(EventInfo info) {
		try {
			Log.d("InGameOutput", "Sending event : " + info);
			if (info.event.getActorSessionID() == 0) {
				Log.d("InGameOutput", "Ignore event, ASID is not set!");
			} else {
				InGame.encodeEvent(info.event, info.dos, info.customTypes);
				info.dos.flush();
				Thread.sleep(50);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
