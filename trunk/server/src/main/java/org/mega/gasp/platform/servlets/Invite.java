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
import org.mega.gasp.moods.CustomTypes;
import org.mega.gasp.platform.Session;
import org.mega.gasp.platform.impl.MasterApplicationInstanceImpl;
import org.mega.gasp.platform.impl.PlatformImpl;

/**
 * This servlet provide to the user to Invite an ApplicationInstance. The HTTP
 * parameters are : - sID the Session ID of the user - aIID the
 * ApplicationInstanceID
 * 
 * @author PELLERIN Romain (pellerin@cnam.fr) - MEGA Project
 */
public class Invite extends HttpServlet {

	private int aSID;
	private Category cat;
	private PlatformImpl platform;

	public void init() {
		ServletContext app = getServletContext();
		platform = PlatformImpl.getPlatform();
		cat = Category.getInstance("InviteServlet");
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		try {
			// get http request parameters for login
			short sID = Short.parseShort(request.getParameter("sID"));
			cat.debug("InviteServlet: called");

			// Invite the specific Application Instance
			cat.debug("InviteServlet: Invite AI(" + sID + " )");

			DataInputStream dis = new DataInputStream(request.getInputStream());
			MasterApplicationInstanceImpl masterApp = (MasterApplicationInstanceImpl) platform
					.getSessionOwner(sID);
			cat
					.debug("Reading custom data for Invite, masterApp= "
							+ masterApp);

			CustomTypes customTypes = masterApp.customTypes;

			Hashtable h = customTypes.decodeData(dis);

			if (h.size() != 0 && h.get("0") instanceof PlayerInfo) {
				cat.debug("Received data from client, number of objects: "
						+ h.size());
				PlayerInfo info = (PlayerInfo) h.get("0");
				int invitedSID = info.arg2;
				Session invitedSession = masterApp.getSession(invitedSID);
				cat.debug("Invited session = " + invitedSID + " "
						+ invitedSession);
				if (invitedSession != null) {
					invitedSession.putInvitation(info);
					invitedSession.setMasterApp(masterApp);
					aSID = 1;
				} else {
					aSID = 0;
					cat.debug("Cannot be Invite because invited session doesn't exist!");
				}
			} else {
				aSID = 0;
				cat.debug("PlayerInfo is absent");
			}

			dis.close();

			// write the short sID in the http response
			DataOutputStream dos = new DataOutputStream(response
					.getOutputStream());
			response.setContentType("application/octet-stream");
			response.setContentLength(2);
			dos.writeShort(aSID);
			dos.close();
		} catch (Exception e) {
			e.printStackTrace();
			cat.info("cannot comply to received Invite request " + e);
		}
	}
}
