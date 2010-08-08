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

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Category;
import org.mega.gasp.event.EndEvent;
import org.mega.gasp.event.impl.EndEventImpl;
import org.mega.gasp.platform.ApplicationInstance;
import org.mega.gasp.platform.impl.PlatformImpl;

/**
 * This servlet provides to the owner of the ApplicationInstance to stop the
 * game session.
 * 
 * @author PELLERIN Romain (pellerin@cnam.fr) - MEGA Project
 */
public class EndAI extends HttpServlet {
	private Category cat;
	private PlatformImpl platform;

	public void init() {
		ServletContext app = getServletContext();
		platform = PlatformImpl.getPlatform();
		cat = Category.getInstance("PlatformImpl");
		cat.debug("EndAI: called");
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		try {
			// get http request parameters for login
			short aSID = Short.parseShort(request.getParameter("aSID"));
			EndEvent ee = new EndEventImpl(aSID);

			ApplicationInstance appIns = platform.getActorSessionOwner(aSID);
			if (appIns != null) {
				cat.debug("EndAI: send EndEvent from aSID n°" + aSID + "to AI("
						+ appIns.getApplicationInstanceID() + ")");
				appIns.onEndEvent(ee);
			}
		} catch (Exception e) {
			e.printStackTrace();
			cat.info("cannot comply to received end AI request");
		}
	}
}
