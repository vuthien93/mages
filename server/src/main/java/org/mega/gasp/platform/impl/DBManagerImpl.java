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
package org.mega.gasp.platform.impl;

import java.sql.*;
//import java.sql.DriverManager;

import org.apache.log4j.Category;
import org.mega.gasp.platform.Actor;
import org.mega.gasp.platform.DBManager;
import org.mega.gasp.platform.utils.PropertiesReader;


/**
 * DBManager contains all the methods requiring an acces to the GASP DB.
 * 
 * @author PELLERIN Romain (pellerin@cnam.fr) - MEGA Project 
 */


public class DBManagerImpl implements DBManager {
	
    private Category cat;
    private Connection connection;

	private String dbUrl;

	private boolean isDBConnectivityOK = false;
	
	public DBManagerImpl(PropertiesReader propertiesReader){
	    cat = Category.getInstance("GASPLogging");	    
	    dbUrl = propertiesReader.getDBUrl();
	    try {
			cat.info("DB url:"+dbUrl);

            connection =
                     DriverManager.getConnection(
                                 dbUrl,"root", "");

            //test of the connection
			isDBConnectivityOK = true;
			
			cat.info("Database connection ok");
	    }
		catch (Exception e){
		    e.printStackTrace();
		    cat.debug("Error loading the database "+dbUrl+", message:"+ e.getMessage()+ ", cause:"+e.getCause());
		}

	}
	
	/**
	 * Verify if the connection to the GASP DB is ok.
	 * 	
	 * @return boolean
	 */
	public boolean isDBConnectionOK(){ 
	    return isDBConnectivityOK;
	}

	/**
	 * Open a DB connection.
	 */
	public synchronized void openDBConnection(){ 
	    try {
                connection = DriverManager.getConnection(
                                 dbUrl,"root", "");
        } catch (Exception e) {
            e.printStackTrace();
            cat.debug("Error opening DB connection:"+e.getMessage());
        }
	}
	
	/**
	 * Close the DB connection.
	 */
	public synchronized void closeDBConnection(){ 
	    try {
	        if (connection != null && !connection.isClosed()) connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            cat.debug("Error closing DB connection");
        }
	}
	
	/**
	 * Generate a DB persistant Actor ID. 
	 * 
	 * @return the actor ID
	 */ 
	public synchronized int generateActorID() {
	    Statement statement = null;
	    int minFreeActorID = 1;
		try {
		    openDBConnection();
		    
			statement = (Statement) connection.createStatement();
			
			// search the lowest free actorID in the table actors
			ResultSet result = statement.executeQuery("SELECT actorID FROM actors ORDER BY actorID;");
			while (result.next()){
				int sInt = result.getInt("actorID");
				if (sInt == minFreeActorID) minFreeActorID++;
				if (sInt > minFreeActorID) return Short.parseShort(""+minFreeActorID);
			}

			cat.info("generateActodId() return " + minFreeActorID);
			return minFreeActorID;
		} 
		catch (SQLException e) {e.printStackTrace();}
		finally{
		    if (statement!=null) try {statement.close();} catch (SQLException e1) { e1.printStackTrace();}
		    closeDBConnection();
		}
		return 0;
	}
	
	
	/**
	 * Verify if the login authentification of an Actor is valid 
	 * then return the authentified actor or null.
	 * 
	 * @param aID the actor ID
	 * @param username 
	 * @param password
	 * @return the Actor instance object
	 */
	public Actor getActorIfAuthentificationOK(int aID, String username,String password) {
	    Statement statement = null;

	    try{
		    openDBConnection();
		    
			statement = (Statement) connection.createStatement();
			
			// hash the password
			ResultSet result1 = statement.executeQuery("SELECT PASSWORD('"+password+"');");
			result1.first();
			String passwordHashed = result1.getString(1);
			
			// get the actor n-uple matching the aID
			String query = "SELECT * FROM actors WHERE actorID='"+aID+"';";
			ResultSet result2 = statement.executeQuery(query);
			
			// test if the login/pass matching
			if (result2.first()){
				String user = result2.getString("username");
				String pwd = result2.getString("password");
				String pseudo = result2.getString("lastPseudo");
				if (username.equals(user)&&passwordHashed.equals(pwd)){
					Actor actor = new ActorImpl(aID,result2.getInt("appID"),user,pwd,pseudo,result2.getInt("rating"));
					return actor;
				}
			}		
		}
		catch (SQLException e) {e.printStackTrace();}
		finally{
		    if (statement!=null) try {statement.close();} catch (SQLException e1) { e1.printStackTrace();}
		    closeDBConnection();
		}
		return null;
	}
	
	/**
	 * Register a new actor for the double (uid,appID) if the user have the appropriate rights.
	 * 
	 * @param uid
	 * @param appID
	 * @param username
	 * @param password
	 * @return the Actor ID
	 */
	public synchronized int registerNewActor(String uid, int appID, String username, String password) {
		Statement statement = null;
	    int actorID = 0;
	    
		try {
		    openDBConnection();
			statement = (Statement) connection.createStatement();
			
			int aIDRegistered = this.userAlreadyRegistered(statement, uid, appID, username, password);
			if (aIDRegistered!=0) return aIDRegistered; else 
			if (this.isUserAllowed(statement,uid,appID)){

				// generate an actorID
				actorID = this.generateActorID();
				
				// hash the password
				ResultSet result1 = statement.executeQuery("SELECT PASSWORD('"+password+"');");
				result1.first();
				String pwdHashed = result1.getString(1);
				
				// insert the new actor n-uple;
				String query = "INSERT INTO actors VALUES " +
					"("+ actorID +","+ uid +","+ appID +",'"+ username +"','"+ pwdHashed +"','" + username+ "', 1200);";
				statement.execute(query);
				return actorID;
			}
		} 
		catch (SQLException e) {
            cat.error("excepton", e);
            e.printStackTrace();
        } finally{
		    if (statement!=null) try {statement.close();} catch (SQLException e1) { e1.printStackTrace();}
		    closeDBConnection();
		}
		return 0;
	}

	/**
	 * Determine by search the table rights if the user have already an actorID.
	 * 
	 * @param statement the Statement of DB
	 * @param uid the User ID
	 * @param appID the Application ID
	 * @return boolean
	 */
	private int userAlreadyRegistered(Statement statement, String uid, int appID, String username, String password) {
	    try{
	    	// aksonov
			String query = "SELECT * FROM actors WHERE userID='"+ uid +"' AND appID='"+ appID +"'"+
			" AND username='"+username+"' AND password=PASSWORD('"+password+"')";
			ResultSet result = statement.executeQuery(query);
			if (result.first()) return result.getInt("actorID");
			else {
				query = "SELECT * FROM actors WHERE userID='"+ uid +"' AND appID='"+ appID +"'"+
				" AND username='"+username+"'";
				result = statement.executeQuery(query);
				if (result.first()) return -1; else return 0;
			}
		}
		catch (SQLException e) {e.printStackTrace();}
		return 0;
	}
	
	
	/**
	 * Determine by search the table rights if the user have the rigths
	 * to play the application appID.
	 * 
	 * @param statement the Statement of DB
	 * @param uid the User ID
	 * @param appID the Application ID
	 * @return boolean
	 */
	private boolean isUserAllowed(Statement statement, String uid, int appID) {
	    try{
			String query = "SELECT * FROM rights WHERE userID='"+ uid +"' AND appID='"+ appID +"'";
			cat.info("QUERY: " + query);
			ResultSet result = statement.executeQuery(query);
			if (result.first()) return true;
		}
		catch (SQLException e) {e.printStackTrace();}
		return false;
	}

	/**
     * Saves in the DB the last used pseudo.
     * 
     * @param actorID
     * @param pseudoName
     */
    public void saveLastUsedPseudo(int actorID, String pseudoName) {
        Statement statement = null;
        try{
            openDBConnection();
            
            statement = (Statement) connection.createStatement();
			String query = "UPDATE actors SET lastPseudo='"+pseudoName+"' WHERE actorID='"+ actorID +"'";
			statement.execute(query);
		}
        catch (SQLException e) {e.printStackTrace();}
        finally{
            if (statement!=null) try {statement.close();} catch (SQLException e1) { e1.printStackTrace();}
            closeDBConnection();
        }
    }

	/**
     * Saves in the DB the rating of actor
     * 
     * @param actorID
     * @param rating
     */
    public void saveRating(int actorID, int rating) {
        Statement statement = null;
        try{
            openDBConnection();
            
            statement = (Statement) connection.createStatement();
			String query = "UPDATE actors SET rating='"+rating+"' WHERE actorID='"+ actorID +"'";
			statement.execute(query);
		}
        catch (SQLException e) {e.printStackTrace();}
        finally{
            if (statement!=null) try {statement.close();} catch (SQLException e1) { e1.printStackTrace();}
            closeDBConnection();
        }
    }
    
    
}
