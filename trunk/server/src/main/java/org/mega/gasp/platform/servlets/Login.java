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

import org.apache.log4j.Category;
import org.mega.gasp.platform.impl.PlatformImpl;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This servlet provide to the user to log in the Gaming Platform. The HTTP
 * parameters are : - aID the Actor ID received during the first login -
 * username the username choosed during first login - password the password
 * choosed during first login - version the version of GP OMA model (optional)
 * 
 * @author PELLERIN Romain (pellerin@cnam.fr) - MEGA Project
 */

public class Login extends HttpServlet {

	private PlatformImpl platform;
	private Category cat;

	public void init() {
		ServletContext app = getServletContext();
		platform = PlatformImpl.getPlatform(getServletContext());
		cat = Category.getInstance("LoginServlet");
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		// get http request parameters for login
		try {
			short aID = Short.parseShort(request.getParameter("aID"));
			String username = request.getParameter("username");
			String password = request.getParameter("password");
			String version = request.getParameter("version");

			// login, return a short representing the sessionID resulting from
			// the session instanciation
			// or the short "0" if the authentification informations are not
			// valid
			int sID = platform.login(aID, username, password);

			/*
			 * response.setContentType("text/html"); PrintWriter out =
			 * response.getWriter(); out.println(sID); out.flush(); out.close();
			 */
			response.setContentType("application/octet-stream");
			response.setContentLength(2);
			DataOutputStream dos = new DataOutputStream(response
					.getOutputStream());
			dos.writeShort(sID);
			cat.debug("Writing response for username: " + username + ", aID="
					+ aID + ", result=" + sID);
			dos.close();
		} catch (Exception e) {
			cat.info("cannot comply to received login request");
		}
	}
}
