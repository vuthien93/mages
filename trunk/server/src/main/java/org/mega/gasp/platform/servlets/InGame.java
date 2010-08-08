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
package org.mega.gasp.platform.servlets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.aksonov.tools.Log;
import org.apache.log4j.Category;
import org.mega.gasp.event.DataEvent;
import org.mega.gasp.event.EndEvent;
import org.mega.gasp.event.Event;
import org.mega.gasp.event.JoinEvent;
import org.mega.gasp.event.QuitEvent;
import org.mega.gasp.event.StartEvent;
import org.mega.gasp.event.impl.DataEventImpl;
import org.mega.gasp.event.impl.QuitEventImpl;
import org.mega.gasp.moods.CustomTypes;
import org.mega.gasp.platform.ActorSession;
import org.mega.gasp.platform.ApplicationInstance;
import org.mega.gasp.platform.MasterApplicationInstance;
import org.mega.gasp.platform.Session;
import org.mega.gasp.platform.impl.PlatformImpl;

/**
 * This servlet provide to send in game datas to GASP. The input stream is a
 * byte[] contains (sid,hashtable).
 * 
 * @author PELLERIN Romain (pellerin@cnam.fr) - MEGA Project
 */
public class InGame extends HttpServlet {

	private Category cat;

	private PlatformImpl platform;

	public void init() {
		ServletContext app = getServletContext();
		platform = PlatformImpl.getPlatform();
		cat = Category.getInstance("InGameServlet");
		cat.debug("Log4J InGame Category instanciated");
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		DataInputStream dis = new DataInputStream(request.getInputStream());
		DataOutputStream dos = null;
		short aSID = 0;

		try {
			aSID = dis.readShort();
			cat.debug("Servlet InGame: aSID=" + aSID);

			// take the platform, the AI , the MAI and the Session associated to
			// ASID
			ApplicationInstance appIns = platform.getActorSessionOwner(aSID);
			ActorSession aSession = appIns.getActorSession(aSID);
			int sID = aSession.getSessionID();

			MasterApplicationInstance masterApp = platform.getSessionOwner(sID);
			Session session = masterApp.getSession(sID);
			CustomTypes customTypes = appIns.getCustomTypes();

			Hashtable h = customTypes.decodeData(dis);

			if (h.size() != 0) {
				cat.debug("Received data from client, number of objects: "
						+ h.size());
				DataEvent de = new DataEventImpl(aSID, h);
				appIns.onDataEvent(de);
			}

			dis.close();

			dos = new DataOutputStream(response.getOutputStream());

			cat
					.info("---------------- START ENCODING ON HTTP REQUEST DOS ----------------");
			// send events to client
			int i = encodeEvents(appIns.getActorSession(aSID).getEvents(), dos,
					customTypes);

			// destroy the actor session linked to aSID
			// and if all actor sessions are destroyed calling the owner master
			// app
			// to destroy the application instance.
			if (appIns.isDestroyable()) {
				QuitEventImpl qe = new QuitEventImpl(aSID);
				appIns.onQuitEvent(qe);
				if (appIns.isActorSessionEmpty())
					masterApp.removeApplicationInstance(appIns
							.getApplicationInstanceID());
			}
			cat
					.info("---------------- END of ENCODING ON HTTP REQUEST DOS ----------------");
			cat.debug("InGameServlet: encoding events to client ok, nb events="
					+ i);

		} catch (Exception e) {
			cat.error("cannot comply to received in game request, cause: "
					+ e.getMessage());
			e.printStackTrace();
		} finally {
			if (dis != null)
				try {
					dis.close();
				} catch (Exception e) {
				}
			if (dos != null)
				try {
					dos.close();
				} catch (Exception e) {
				}
		}
	}

	/**
	 * @param events
	 * @throws Exception
	 */
	private synchronized static int encodeEvents(Vector events, DataOutputStream dos,
			CustomTypes customTypes) throws Exception {
		dos.writeByte(events.size());
		for (Iterator iter = events.iterator(); iter.hasNext();) {
			Event e = (Event) iter.next();
			encodeEvent(e, dos, customTypes);
		}
		return events.size();

	}

	public synchronized static void encodeEvent(Event e, DataOutputStream dos,
			CustomTypes customTypes) throws Exception {
		int code = e.getCode();
		Log.d("InGame", "Writing event: " + e + " ASID= " + e.getActorSessionID() + ", id:" + e.getId());
		dos.writeByte(code);
		dos.writeInt(e.getId());
		switch (code) {
		case 1: // JoinEvent
			JoinEvent je = (JoinEvent) e;
			dos.writeShort(je.getActorSessionID());
			dos.writeUTF(je.getUsername());
			break;
		case 2: // StartEvent
			StartEvent se = (StartEvent) e;
			dos.writeShort(se.getActorSessionID());
			break;
		case 3:
			EndEvent ee = (EndEvent) e;
			dos.writeShort(ee.getActorSessionID());
			break;
		case 4:
			QuitEvent qe = (QuitEvent) e;
			dos.writeShort(qe.getActorSessionID());
			break;
		case 5:
			DataEvent de = (DataEvent) e;
			dos.writeShort(de.getActorSessionID());
			customTypes.encodeData(de.getData(), dos);
			break;
		default:
			break;
		}
	}
	
}
