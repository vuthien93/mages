/***
 * Mages: Multiplayer Game Engine for mobile devices
 * Copyright (C) 2008 aksonov
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
 * Contact: aksonov dot gmail dot com
 *
 * Author: Pavlo Aksonov
 */
package org.aksonov.mages;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Hashtable;

// TODO: Auto-generated Javadoc
/**
 * The Interface MoodsSerializer.
 */
public interface MoodsSerializer {
    
    /**
     * Encode the hashtable of game custom types objects on the
     * given data output stream.
     * 
     * @param dos the dos
     * @param data the data
     * 
     * @throws Exception the exception
     */
    void encodeObjects(Object[] data, DataOutputStream dos) throws Exception;
   
    
    /**
     * Decode the game custom types objects read in the data input
     * stream and returns the associated objects hashtable.
     * 
     * @param dis the dis
     * 
     * @return the  game custom objects hashtable
     * 
     * @throws Exception the exception
     */
    Object[] decodeObjects(DataInputStream dis) throws Exception;
}
