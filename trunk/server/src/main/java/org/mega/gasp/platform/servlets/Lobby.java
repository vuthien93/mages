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

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Vector;
import java.util.Hashtable;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Category;
import org.mega.gasp.moods.CustomTypes;
import org.mega.gasp.platform.ActorSession;
import org.mega.gasp.platform.MasterApplicationInstance;
import org.mega.gasp.platform.impl.PlatformImpl;
import org.mega.gasp.platform.utils.ApplicationInstanceInfos;

/**
 * This servlet provide to get Lobby informations about current
 * ApplicationInstance of the Application. The HTTP parameter is : - sID the
 * Session ID of the user
 * 
 * @author PELLERIN Romain (pellerin@cnam.fr) - MEGA Project
 */
public class Lobby extends HttpServlet {

	private Category cat;
	private PlatformImpl platform;

	public void init() {
		ServletContext app = getServletContext();
		platform = PlatformImpl.getPlatform();
		cat = Category.getInstance("LobbyServlet");
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		String checkTime = request.getParameter("check_time");
		String sID = request.getParameter("sID");
		response.setContentType("application/octet-stream");
		try {
			DataOutputStream dos = new DataOutputStream(response
					.getOutputStream());

			// return last modified time of lobby list
			if (checkTime != null && checkTime.equals("1")) {
				response.setContentLength(8);
				int sid = Integer.parseInt(sID);
				dos.writeLong(platform.getLobby().getLobbyModifiedTime(sid));
			} else {
				boolean getCustomData = request.getParameter("custom_data") != null
						&& request.getParameter("custom_data").equals("1");

				MasterApplicationInstance masterApp = platform
						.getSessionOwner(Integer.parseInt(sID));

				// requiring the lobby infos to the platform for the application
				// associated with the sID
				Vector lobbyInfos = platform.getLobby()
						.getApplicationInstancesInfos(Integer.parseInt(sID));

				// push in the DataOutputStream the number of application
				// instances
				dos.writeShort(lobbyInfos.size());

				for (Iterator iter = lobbyInfos.iterator(); iter.hasNext();) {
					ApplicationInstanceInfos appInstanceInfos = (ApplicationInstanceInfos) iter
							.next();

					CustomTypes customTypes = getCustomData ? masterApp
							.getApplicationInstance(
									appInstanceInfos.getApplicationInstanceID())
							.getCustomTypes()
							: null;

					// push the aIID of the current application instance
					dos.writeShort(appInstanceInfos.getApplicationInstanceID());

					// push the minimum starting number of actors
					dos.writeShort(appInstanceInfos.getMinActors());

					// push the number of actors currently connected in
					dos.writeShort(appInstanceInfos.getNbPlayers());

					// push the maximum number of actors
					dos.writeShort(appInstanceInfos.getMaxActors());

					// encode custom data for actor session
					if (getCustomData) {
						cat
								.debug("Writing custom data for application instance"
										+ appInstanceInfos.getCustomData());
						Hashtable h = new Hashtable();
						if (appInstanceInfos.getCustomData() != null)
							h.put("0", appInstanceInfos.getCustomData());
						customTypes.encodeData(h, dos);
					}
					/*

					for (Iterator iter2 = appInstanceInfos.getActorSessions()
							.iterator(); iter2.hasNext();) {
						ActorSession actorSession = (ActorSession) iter2.next();

						String username = actorSession.getPseudoName();
						Integer actorID = actorSession.getActorID();

						// push the username and the actor ID of the current
						// actor
						dos.writeUTF(username);
						dos.writeShort(actorID.intValue());

						// encode custom data for actor session
						if (getCustomData) {
							cat.debug("Writing custom actor data "
									+ actorSession.getPlayerInfo());
							Hashtable h = new Hashtable();
							if (actorSession.getPlayerInfo() != null)
								h.put("0", actorSession.getPlayerInfo());
							customTypes.encodeData(h, dos);
						}

					}*/
				}
			}
			dos.close();
		} catch (Exception e) {
			e.printStackTrace();
			cat.error("cannot comply to received lobby request "
					+ e.getMessage());
		}
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
	}
}
