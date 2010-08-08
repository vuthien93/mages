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

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Category;
import org.mega.gasp.platform.impl.PlatformImpl;

/**
 * This servlet is call the first time the user log in with the Application.
 * The HTTP parameters are :
 * - uid the UserID of the operator
 * - appID the ApplicationID hard coded in the application.
 * - username the choosed username
 * - password the choosed password
 * 
 * @author PELLERIN Romain (pellerin@cnam.fr) - MEGA Project 
 */
public class FirstLogin extends HttpServlet {
    private PlatformImpl platform;
    private Category cat;

    public void init(){
        ServletContext app = getServletContext();
        platform = PlatformImpl.getPlatform(getServletContext());
        cat = Category.getInstance("FirstLoginServlet");
    }
    
	public void doPost(HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException {}
	
	
	public void doGet(HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException {
		cat.debug("FirstLogin, do post!");
	    try{
		    // get http request parameters for login 
			String uid = request.getParameter("uid");
			short appID = Short.parseShort(((String) request.getParameter("appID")));
			String username = request.getParameter("username");
			String password = request.getParameter("password");
				
			// registration of the user with his choice of username and password
			// return a short aID, permanent actorID associated with the userID and applicationID
			// or the short "0" if the user have not the appropriate rights
			int aID = platform.getDBManager().registerNewActor(uid,appID,username,password);
			if (aID == -1) aID = 0;
			
			/*response.setContentType("text/html");
			PrintWriter out = response.getWriter();
			out.println(aID);
			out.flush();
			out.close();*/
			response.setContentType("application/octet-stream");
			response.setContentLength(2);
			DataOutputStream dos = new DataOutputStream(response.getOutputStream());
			cat.debug("Writing response for username: " + username +", result="+aID);
			dos.writeShort(aID);
			dos.close();	    }
	    catch (Exception e){
	    	e.printStackTrace();
	        cat.info("cannot complied to received first login request ");
	    }
	    finally{
	        //if (dos!=null) try{dos.close();} catch (Exception e){}
	    }
		
	}
}
