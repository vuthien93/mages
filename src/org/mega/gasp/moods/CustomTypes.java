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
package org.mega.gasp.moods;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Hashtable;

/**
 * When the game programmer wants to use his custom types classes,
 * first he must describe his custom types in the types.xml with the
 * correct syntax. 
 * Then he must run the custom types generator in org.mega.moods package
 * with the command line: MoodsGenerator types.xml packageName
 * The generator generate a file CustomTypes.java that is the encoder/decoder
 * of the game custom types.
 * 
 * 
 * @author PELLERIN Romain (pellerin@cnam.fr) - MEGA Project 
 */
public interface CustomTypes {
    
    /**
     * Encode the hashtable of game custom types objects on the
     * given data output stream.
     * 
     * @param h
     * @param dos
     */
    void encodeData(Hashtable h, DataOutputStream dos) throws Exception;
   
    
    /**
     * Decode the game custom types objects read in the data input
     * stream and returns the associated objects hashtable.
     * 
     * @param dis
     * @return the  game custom objects hashtable
     */
    Hashtable decodeData(DataInputStream dis) throws Exception;

}
