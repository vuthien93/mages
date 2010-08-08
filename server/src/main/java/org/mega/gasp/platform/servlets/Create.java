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

import org.apache.log4j.Category;
import org.mega.gasp.event.DataEvent;
import org.mega.gasp.event.impl.DataEventImpl;
import org.mega.gasp.moods.CustomTypes;
import org.mega.gasp.platform.ActorSession;
import org.mega.gasp.platform.ApplicationInstance;
import org.mega.gasp.platform.MasterApplicationInstance;
import org.mega.gasp.platform.Session;
import org.mega.gasp.platform.impl.PlatformImpl;

/**
 * This servlet provide to create an ApplicationInstance to the user. The HTTP
 * parameters are : - sID the Session ID of the user - minActors the minimum
 * actors required to start the game - maxActors the maximum actors can join the
 * ApplicationInstance - actors the Actor IDs of the actors for a private game
 * or nothing
 * 
 * @author PELLERIN Romain (pellerin@cnam.fr) - MEGA Project
 */
public class Create extends HttpServlet {

	private int aSID;
	private PlatformImpl platform;
	private Category cat;

	public void init() {
		ServletContext app = getServletContext();
		platform = PlatformImpl.getPlatform(getServletContext());
		cat = Category.getInstance("CreateServlet");
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		try {
			// get http request parameters for login
			short sID = Short.parseShort(request.getParameter("sID"));
			int minActors = Integer.parseInt(request.getParameter("minActors"));
			int maxActors = Integer.parseInt(request.getParameter("maxActors"));
			int aIID = 0;

			if (minActors > 0 && minActors <= maxActors) {
				String[] actors = request.getParameterValues("actors");

				// create an application instance owned by the sid represented
				// actor
				// with necessited parameters minActors and maxActors
				// if the table actors is empty then the application instance
				// is private statued else public
				aSID = platform.createAI(Integer.parseInt(sID + ""), minActors,
						maxActors, actors);

				ApplicationInstance appIns = platform.getActorSessionOwner(aSID);
				aIID = appIns.getApplicationInstanceID();
				
				// reading custom data (if any)
				boolean getCustomData = request.getParameter("custom_data") != null
						&& request.getParameter("custom_data").equals("1");

				if (getCustomData) {
					cat.debug("Reading custom data for Create");
					DataInputStream dis = new DataInputStream(request
							.getInputStream());

					CustomTypes customTypes = appIns.getCustomTypes();

					Hashtable h = customTypes.decodeData(dis);

					if (h.size() != 0) {
						cat.debug("Received data from client, number of objects: "
								+ h.size());
						DataEvent de = new DataEventImpl(aSID, h);
						appIns.onDataEvent(de);
					}
					
					dis.close();
				}

				// write the short sID in the http response
			} else {
				cat
						.debug("invalid minActors and maxActors parameters in create request ("
								+ minActors + "," + maxActors + ")");
				aSID = 0;
			}
			DataOutputStream dos = new DataOutputStream(response.getOutputStream());
			response.setContentType("octet/stream");
			response.setContentLength(2);
			dos.writeShort(aSID);
			//dos.writeInt(aIID);
			dos.close();
		} catch (Exception e) {
			e.printStackTrace();
			cat.info("cannot comply to received create request "
					+ e.getMessage());
		} 
	}

}
