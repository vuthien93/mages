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
import java.io.PrintWriter;
import java.util.Iterator;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.mega.gasp.platform.*;
import org.mega.gasp.platform.impl.PlatformImpl;

/**
 * This servlet make an HTML representation of the current
 * GASP state.
 * 
 * @author PELLERIN Romain (pellerin@cnam.fr) - MEGA Project 
 */

public class PlatformState extends HttpServlet {
	
    private PlatformImpl platform;
    private String param;
    private String value;
    
    public void init(){
        ServletContext app = getServletContext();
        platform = PlatformImpl.getPlatform(getServletContext());
    }
    
    public void doGet(HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException {

		int size = platform.masterApplicationInstanceSize();
		
		/**** html platform state rendering ******/
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		int numberOfUsers = 0;
		for(Iterator iter1 = platform.enumerateMasterApplicationInstance();iter1.hasNext();){
			MasterApplicationInstance masterApp = (MasterApplicationInstance) iter1.next();
			numberOfUsers += masterApp.actorSize();
		}
		
		out.println("<html>");
		out.println("<head><title>GASP State HTML Viewer</title></head>");
		out.println("<body bgcolor=\"#E0E0E0 \" >");
		out.println("<font size=\"1\"face=\"Verdana\">");
		out.println("<center>");
		
		out.println("<table bgcolor=\"#8470FF\" height=\"95%\" width=\"68%\" cellspacing=10>");
		
		out.println("<tr height=\"30\">");
		out.println("<td align=\"left\" bgcolor=\"#585858 \" colspan=2>");
		out.println("<font size=\"3\" color=\"white\"><b>GASP State Viewer  v1.0</b></font>");
		out.println("</td>");
		out.println("</tr>");
		
		out.println("<tr>");
		out.println("<td align= center>");
		
		out.println("<table bgcolor=\"white\" border=0  width=\"68%\" cellspacing=0>");
		out.println("<th bgcolor=\"#FFCC33\" colspan=2>");
		out.println("<font size=\"2\" color=\"blue\">Platform</font>");
		out.println("</th>");
		
		out.println("<tr>");
		out.println("<td align=\"left\">");
		out.println("<font size=\"2\">!@!@Platform instanciation</font>");
		out.println("</td>");
		out.println("<td align=\"right\">");
		out.println("<font size=\"2\">"+(platform.gaspInstanciationOK ? "ok" : "nok see logs")+"</font>");
		out.println("</td>");
		out.println("</tr>");
		out.println("<tr>");
		
		out.println("<td align=\"left\">");
		out.println("<font size=\"2\">DB connection</font>");
		out.println("</td>");
		out.println("<td align=\"right\">");
		out.println("<font size=\"2\">"+(platform.gaspDBOK ? "ok" : "nok see logs")+"</font>");
		out.println("</td>");
		out.println("</tr>");
		
		out.println("<tr>");
		out.println("<td align=\"left\">");
		out.println("<font size=\"2\">Current number of Master Applications</font>");
		out.println("</td>");
		out.println("<td align=\"right\">");
		out.println("<font size=\"2\">"+size+"</font>");
		out.println("</td>");
		out.println("</tr>");
		
		out.println("<tr>");
		out.println("<td align=\"left\">");
		out.println("<font size=\"2\">Current total number of Users</font>");
		out.println("</td>");
		out.println("<td align=\"right\">");
		out.println("<font size=\"2\">"+numberOfUsers+"</font>");
		out.println("</td>");
		out.println("</tr>");
		
		out.println("</table>");
		
		out.println("</td>");
		out.println("</tr>");
		
		for(Iterator iter1 = platform.enumerateMasterApplicationInstance();iter1.hasNext();){
			MasterApplicationInstance masterApp = (MasterApplicationInstance) iter1.next();
			
			out.println("<tr>");
			out.println("<td align= center>");
			
			out.println("<table bgcolor=\"white\" border=0 width=\"68%\" cellspacing=0>");
			out.println("<th bgcolor=\"#FFCC33\" colspan=2>");
			out.println("<font size=\"2\" color=\"blue\">Master Application Instance</font>");
			out.println("</th>");
			
			out.println("<tr>");
			out.println("<td align=\"left\">");
			out.println("<font size=\"2\">MAID</font>");
			out.println("</td>");
			out.println("<td align=\"right\">");
			out.println("<font size=\"2\">"+masterApp.getMasterApplicationID()+"</font>");
			out.println("</td>");
			out.println("</tr>");
			
			out.println("<tr>");
			out.println("<td align=\"left\">");
			out.println("<font size=\"2\">Application ID</font>");
			out.println("</td>");
			out.println("<td align=\"right\">");
			out.println("<font size=\"2\">"+masterApp.getApplicationID()+"</font>");
			out.println("</td>");
			out.println("</tr>");
			
			String model;
			switch (masterApp.getApplicationModel()){
				case 0: 
				    model="auto";
				    break;
				case 1:
				    model="manual";
				    break;
				case 2:
				    model="mapcycle";
				default: 
				    model="unknow";
				    break;
			}
			
			out.println("<tr>");
			out.println("<td align=\"left\">");
			out.println("<font size=\"2\">Application Model</font>");
			out.println("</td>");
			out.println("<td align=\"right\">");
			out.println("<font size=\"2\">"+model+"</font>");
			out.println("</td>");
			out.println("</tr>");
			
			out.println("<tr>");
			out.println("<td align=\"left\">");
			out.println("<font size=\"2\">Number of Application Instances</font>");
			out.println("</td>");
			out.println("<td align=\"right\">");
			out.println("<font size=\"2\">"+masterApp.applicationInstanceSize()+"</font>");
			out.println("</td>");
			out.println("</tr>");

			out.println("<tr>");
			out.println("<td align=\"left\">");
			out.println("<font size=\"2\">Number of Users</font>");
			out.println("</td>");
			out.println("<td align=\"right\">");
			out.println("<font size=\"2\">"+masterApp.sessionSize()+"</font>");
			out.println("</td>");
			out.println("</tr>");
			
			out.println("<tr>");
			out.println("<td align=\"left\">");
			out.println("<font size=\"2\">Users IDs infos (aID/sID)</font>");
			out.println("</td>");
			out.println("<td align=\"right\">");
			for(Iterator iter2 = masterApp.enumerateSession(); iter2.hasNext();){
				Session session = (Session) iter2.next();
				out.println("<font size=\"2\">"+"("+session.getActorID()+"/"+session.getSessionID()+")"+"</font>");
			}
			out.println("</td>");
			out.println("</tr>");
			
			
			for(Iterator iter3 = masterApp.enumerateApplicationInstance(); iter3.hasNext();){
			    out.println("<tr>");
			    out.println("<td align=\"left\" bgcolor=\"#FFCC33\" colspan=2>");
				out.println("<font size=\"2\" color=\"blue\"> <b> Application Instance </b></font>");
				out.println("</td>");
				out.println("</tr>");
				
			    ApplicationInstance appIns = (ApplicationInstance) iter3.next();
			    
			    out.println("<tr>");
				out.println("<td align=\"left\">");
				out.println("<font size=\"2\">AIID</font>");
				out.println("</td>");
				out.println("<td align=\"right\">");
				out.println("<font size=\"2\">"+appIns.getApplicationInstanceID()+"</font>");
				out.println("</td>");
				out.println("</tr>");
				
				out.println("<tr>");
				out.println("<td align=\"left\">");
				out.println("<font size=\"2\">Number of players</font>");
				out.println("</td>");
				out.println("<td align=\"right\">");
				out.println("<font size=\"2\">"+appIns.actorSessionSize()+"</font>");
				out.println("</td>");
				out.println("</tr>");
				
				out.println("<tr>");
				out.println("<td align=\"left\">");
				out.println("<font size=\"2\">Owner ASID</font>");
				out.println("</td>");
				out.println("<td align=\"right\">");
				out.println("<font size=\"2\">"+appIns.getOwnerAID()+"</font>");
				out.println("</td>");
				out.println("</tr>");
				
				out.println("<tr>");
				out.println("<td align=\"left\">");
				out.println("<font size=\"2\">Minimum starting number of players</font>");
				out.println("</td>");
				out.println("<td align=\"right\">");
				out.println("<font size=\"2\">"+appIns.getMinActors()+"</font>");
				out.println("</td>");
				out.println("</tr>");
				
				out.println("<tr>");
				out.println("<td align=\"left\">");
				out.println("<font size=\"2\">Maximum number of players</font>");
				out.println("</td>");
				out.println("<td align=\"right\">");
				out.println("<font size=\"2\">"+appIns.getMaxActors()+"</font>");
				out.println("</td>");
				out.println("</tr>");
				
				out.println("<tr>");
				out.println("<td align=\"left\">");
				out.println("<font size=\"2\">Status</font>");
				out.println("</td>");
				out.println("<td align=\"right\">");
				
				String status;
				if (appIns.isDestroyable()) status = "[Destroy Mode]";
				else if (appIns.isRunning()){
				    status = "[In Game]";
				    if (appIns.isJoinable()) status+="[full:n]";
				    else status+="[full:y]";
				}
				else{
				    status = "[Waiting Room]";
				    if (appIns.isJoinable()) status+="[full:n]";
				    else status+="[full:y]";
				    if (appIns.actorSessionSize()< appIns.getMinActors()) status+= " [startable: n]";
				    else status += " [startable: y]";
				}
				
				out.println("<font size=\"2\">"+status+"</font>");
				out.println("</td>");
				out.println("</tr>");
				
				boolean first = true;
				for(Iterator iter4 = appIns.enumerateActorSession(); iter4.hasNext();){
				   
				    ActorSession actorSession = (ActorSession) iter4.next();
				    
				    out.println("<tr>");
					out.println("<td align=\"left\">");
					if (first){
					    out.println("<font size=\"2\">Player (aID/sID/aSID)</font>");
					    first = false;
					}
					else  out.println("<font size=\"2\">Player</font>");
					
					out.println("</td>");
					out.println("<td align=\"right\">");
					out.println("<font size=\"2\">"+actorSession.getPseudoName());
					out.print(" ("+actorSession.getActorID()+"/"+actorSession.getSessionID()+"/"+actorSession.getActorSessionID()+") "+"</font>");
					out.println("</td>");
					out.println("</tr>");
					out.println();
				}
			}
			
			out.println("</table>");
			
		}
		
		out.println("<tr height=\"30\">");
		out.println("<td align=\"right\" bgcolor=\"#585858\" colspan=2>");
		out.println("<font size=\"2\" color=\"white\"><b>PELLERIN Romain - MEGA Project - 2004</b></font>");
		out.println("</td>");
		out.println("</tr>");
		
		out.println("</table>");
		out.println("</center>");
		out.println("</font>");
		out.println("</body>");
		out.println("</html>");
		out.close();

	}
	
	
	public void doPost(HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException {}
}
