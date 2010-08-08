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

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.aksonov.mages.entities.PlayerInfo;
import org.apache.log4j.Category;
import org.mega.gasp.event.DataEvent;
import org.mega.gasp.event.impl.DataEventImpl;
import org.mega.gasp.moods.CustomTypes;
import org.mega.gasp.platform.ApplicationInstance;
import org.mega.gasp.platform.impl.PlatformImpl;

/**
 * This servlet provide to the user to join an ApplicationInstance. The HTTP
 * parameters are : - sID the Session ID of the user - aIID the
 * ApplicationInstanceID
 * 
 * @author PELLERIN Romain (pellerin@cnam.fr) - MEGA Project
 */
public class Join extends HttpServlet {

	private int aSID;
	private Category cat;
	private PlatformImpl platform;

	public void init() {
		ServletContext app = getServletContext();
		platform = PlatformImpl.getPlatform();
		cat = Category.getInstance("JoinServlet");
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		try {
			// get http request parameters for login
			short sID = Short.parseShort(request.getParameter("sID"));
			String applicationInstanceID = request.getParameter("aIID");
			cat.debug("JoinServlet: called");

			if (applicationInstanceID==null || applicationInstanceID.equals("")) {
				// Join Randomly an Application Instance
				cat.debug("JoinServlet: join random");
				aSID = PlatformImpl.getPlatform().joinAIRnd(sID);
			} else {
				// Join the specific Application Instance
				cat
						.debug("JoinServlet: join AI(" + applicationInstanceID
								+ ")");
				short aIID = Short.parseShort(applicationInstanceID);
				aSID = PlatformImpl.getPlatform().joinAI(sID, aIID);
			}

			if (aSID != 0) {
				// reading custom data (if any)
				boolean getCustomData = request.getParameter("custom_data") != null
						&& request.getParameter("custom_data").equals("1");

				if (getCustomData) {
					cat.debug("Reading custom data for Create");
					DataInputStream dis = new DataInputStream(request
							.getInputStream());

					ApplicationInstance appIns = platform
							.getActorSessionOwner(aSID);
					CustomTypes customTypes = appIns.getCustomTypes();

					Hashtable h = customTypes.decodeData(dis);

					if (h.size() != 0 && h.get("0") instanceof PlayerInfo) {
						cat
								.debug("Received data from client, number of objects: "
										+ h.size());
						PlayerInfo info = (PlayerInfo) h.get("0");
						if (appIns.getServer().couldJoin(info)) {
							DataEvent de = new DataEventImpl(aSID, h);
							appIns.onDataEvent(de);
						} else {
							aSID = 0;
							cat.debug("Cannot be join because position is busy!");
						}
					} else {
						aSID = 0;
						cat.debug("PlayerInfo is absent");
					}

					dis.close();
				}
			}

			// write the short sID in the http response
			DataOutputStream dos = new DataOutputStream(response
					.getOutputStream());
			response.setContentType("application/octet-stream");
			response.setContentLength(2);
			dos.writeShort(aSID);
			dos.close();
		} catch (Exception e) {
			e.printStackTrace();
			cat.info("cannot comply to received join request " + e);
		}
	}
}
