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
import org.mega.gasp.platform.Actor;
import org.mega.gasp.platform.ActorSession;
import org.mega.gasp.platform.ApplicationInstance;
import org.mega.gasp.platform.MasterApplicationInstance;
import org.mega.gasp.platform.impl.PlatformImpl;

/**
 * Perform the change of pseudoname in the AI.
 * 
 * @author PELLERIN Romain (pellerin@cnam.fr) - MEGA Project 
 */
public class Name extends HttpServlet {
	private Category cat;
    private PlatformImpl platform;

    public void init(){
        ServletContext app = getServletContext();
        platform = PlatformImpl.getPlatform();
        cat = Category.getInstance("NameServlet");
		cat.debug("EndAI: called");
    }
    
    public void doGet(HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException {
	}
	
	public void doPost(HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException {
		DataOutputStream dos = null;
	    
		try{
		    //get http request parameters for login 
		    short aSID =  Short.parseShort(request.getParameter("aSID"));
			String name = request.getParameter("name");
	
			ApplicationInstance appIns = platform.getActorSessionOwner(aSID);
			ActorSession aS = appIns.getActorSession(aSID);
			MasterApplicationInstance masterApp = platform.getSessionOwner(aS.getSessionID());
			Actor actor = masterApp.getActor(aS.getActorID());
			
			//treat the new pseudoname
			aS.setPseudoName(appIns.treatPseudo(name));
			actor.setPseudoName(aS.getPseudoName());
			platform.getDBManager().saveLastUsedPseudo(actor.getActorID(),actor.getPseudoName());

			//raise the new pseudo to all other players
			appIns.raisePseudoModification(aS.getActorSessionID(), aS.getPseudoName());
			
			if (!name.equals(aS.getPseudoName())) aS.getPseudoName();
			cat.debug("Name: aSID n°"+aSID+ " renamed to "+aS.getPseudoName());

			//send to the player his new pseudoname correctly formed
			dos = new DataOutputStream(response.getOutputStream());
		    dos.writeUTF(aS.getPseudoName());
		} catch (Exception e1) {
            cat.info("cannot comply to received name request");
        }
        finally{
            if (dos!=null) try{dos.close();} catch (Exception e){}
        }
	}
}
