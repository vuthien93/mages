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

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This servlet provide to the user to quit GASP.
 * The HTTP parameter is:
 * - sID the Session ID of the user
 * 
 * @author PELLERIN Romain (pellerin@cnam.fr) - MEGA Project 
 */

public class Quit extends HttpServlet {

	private Category cat;
    private PlatformImpl platform;
    private DataOutputStream dos; 

    public void init(){
        ServletContext app = getServletContext();
        platform = PlatformImpl.getPlatform();
        cat = Category.getInstance("QuitServlet");
    }
	
    
	public void doPost(HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException {
	}
	
	
	public void doGet(HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException {
		// get http request parameters for login 
		try{
		    short sID =  Short.parseShort(request.getParameter("sID"));
			platform.quit(Integer.parseInt(sID+""));
			response.setContentType("application/octet-stream");
			response.setContentLength(1);
			DataOutputStream dos = new DataOutputStream(response.getOutputStream());
			dos.writeBoolean(true);
			dos.close();
		}
		catch (Exception e){cat.info("cannot comply to received name request");}
	}

}
