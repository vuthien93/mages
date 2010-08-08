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
import java.util.Hashtable;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Category;
import org.mega.gasp.moods.CustomTypes;
import org.mega.gasp.platform.Session;
import org.mega.gasp.platform.impl.MasterApplicationInstanceImpl;
import org.mega.gasp.platform.impl.PlatformImpl;
import org.mega.gasp.platform.impl.SessionImpl;

/**
 * This servlet provide to get LobbyOutput informations about current
 * ApplicationInstance of the Application. The HTTP parameter is : - sID the
 * Session ID of the user
 * 
 * @author PELLERIN Romain (pellerin@cnam.fr) - MEGA Project
 */
public class LobbyOutput extends HttpServlet {

	private Category cat;
	private PlatformImpl platform;

	public void init() {
		ServletContext app = getServletContext();
		platform = PlatformImpl.getPlatform(getServletContext());
		cat = Category.getInstance("LobbyOutputServlet");
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		String checkTime = request.getParameter("check_time");
		String sID = request.getParameter("sID");
		response.setContentType("application/octet-stream");
		try {
			DataOutputStream dos = new DataOutputStream(response
					.getOutputStream());
			int sid = Integer.parseInt(sID);

			MasterApplicationInstanceImpl masterApp = (MasterApplicationInstanceImpl) platform
					.getSessionOwner(sid);
			SessionImpl session = (SessionImpl) masterApp.getSession(sid);
			CustomTypes customTypes = masterApp.customTypes;
			if (session.hasNewEvents()) {
				Map events = session.getEvents();
				if (events.containsKey(SessionImpl.INVITATIONS)) {
					dos.writeByte(3);
					Hashtable invitations = (Hashtable) events
							.get(SessionImpl.INVITATIONS);
					cat.debug("Writing invitations : " + invitations.size());
					customTypes.encodeData(invitations, dos);
				}
				if (events.containsKey(SessionImpl.GAMES)) {
					dos.writeByte(1);
					Hashtable games = (Hashtable) events.get(SessionImpl.GAMES);
					cat.debug("Writing game list : " + games.size());
					customTypes.encodeData(games, dos);

				}
				;
				if (events.containsKey(SessionImpl.PLAYERS)) {
					dos.writeByte(2);
					Hashtable players = (Hashtable) events
							.get(SessionImpl.PLAYERS);
					cat.debug("Writing player list : " + players.size());
					customTypes.encodeData(players, dos);
				}
			} 
			cat.debug("Writing 0 (end of response)");
			dos.writeByte(0);
			dos.flush();
			dos.close();
		} catch (Exception e) {
			e.printStackTrace();
			cat.error("cannot comply to received lobby request "
					+ e.getMessage(), e);
		}
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
	}
}
