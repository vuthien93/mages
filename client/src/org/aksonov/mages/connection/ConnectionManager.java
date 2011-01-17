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
package org.aksonov.mages.connection;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.aksonov.tools.Log;
import org.apache.http.HttpConnection;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;


// TODO: Auto-generated Javadoc
/**
 * The Class ConnectionManager.
 */
public class ConnectionManager {
	
	/** The Constant TIMEOUT. */
	private static final int TIMEOUT = 7000;
	
	/** The Constant shortReader. */
	private static final DataReader shortReader = new DataReader() {

		
		public Object read(DataInputStream stream) throws Exception {
			int k = stream.readShort();
			Log.d("read", k + "");
			return k;
		}

	};

	/** The Constant emptyReader. */
	public static final DataReader emptyReader = new DataReader() {

		
		public Object read(DataInputStream stream) throws Exception {
			return 0;
		}

	};
	
	/** The Constant longReader. */
	private static final DataReader longReader = new DataReader() {
		
		public Object read(DataInputStream stream) throws Exception {
			long k = stream.readLong();
			Log.d("read", k + "");
			return k;

		}

	};


	/**
	 * Read short.
	 * 
	 * @param host the host
	 * @param servlet the servlet
	 * @param formData the form data
	 * 
	 * @return the int
	 * 
	 * @throws Exception the exception
	 */
	public static int readShort(String host, String servlet, String formData) throws Exception {
        Log.i("ConnectionManager", "Host: " + host + ", URL:" + servlet + ", formData: " + formData);
		return (Integer) readGet(host, servlet, formData, shortReader);
	}


	/**
	 * Read short.
	 * 
	 * @param host the host
	 * @param servlet the servlet
	 * @param formData the form data
	 * 
	 * @return the int
	 * 
	 * @throws Exception the exception
	 */
	public static int readShort(String host, String servlet, String formData, CustomTypesRequestEntity requestEntity) throws Exception {
        Log.i("ConnectionManager", "Host: " + host + ", URL:" + servlet + ", POST formData: " + formData);
		return (Integer) readPost(host, servlet, formData, requestEntity, shortReader);
	}
	
	/**
	 * Read empty.
	 * 
	 * @param host the host
	 * @param servlet the servlet
	 * @param formData the form data
	 * 
	 * @return the int
	 * 
	 * @throws Exception the exception
	 */
	public static int readEmpty(String host, String servlet, String formData) throws Exception {
		return (Integer) readGet(host, servlet, formData, emptyReader);
	}

	/**
	 * Read long.
	 * 
	 * @param host the host
	 * @param servlet the servlet
	 * @param formData the form data
	 * 
	 * @return the long
	 * 
	 * @throws Exception the exception
	 */
	public static long readLong(String host, String servlet, String formData) throws Exception {
		return (Long) readGet(host, servlet, formData, longReader);
	}

	/**
	 * Read get.
	 * 
	 * @param host the host
	 * @param servlet the servlet
	 * @param formData the form data
	 * @param reader the reader
	 * 
	 * @return the object
	 * 
	 * @throws Exception the exception
	 */
	public static Object readGet(String host, String servlet, String formData,
			DataReader reader) throws Exception {

		Object result = null;
		URL url = null;
		          try {
		               url = new URL(host + "/" + servlet + "?" + formData);
		          } catch (MalformedURLException e) {
		               Log.e("error", e.getMessage());
		          }

		          if (url != null) {
		               try {
		                    HttpURLConnection urlConn = (HttpURLConnection) url
		                              .openConnection();
		    				DataInputStream stream = new DataInputStream(urlConn.getInputStream());
		                    result = reader.read(stream);
		    				
		    				stream.close();
		                    urlConn.disconnect();

		               } catch (IOException e) {
		                    Log.e("error", e.getMessage());
		               }	
		               
		          }
		          return result;
	}


	/**
	 * Read get.
	 * 
	 * @param host the host
	 * @param servlet the servlet
	 * @param formData the form data
	 * @param reader the reader
	 * 
	 * @return the object
	 * 
	 * @throws Exception the exception
	 */
	public static Object readPost(String host, String servlet, String formData, CustomTypesRequestEntity requestEntity,
			DataReader reader) throws Exception {

		Object result = null;
        HttpClient httpclient = new DefaultHttpClient();
        String url = host + "/" + servlet + "?" + formData;
        Log.i("ConnectionManager", "POST request to " + url);
       HttpPost httppost = new HttpPost(url);

                httppost.setEntity(requestEntity);

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
        HttpEntity entity = response.getEntity();

        if (entity != null){
            Log.e("ConnectionManager", "reading result from stream");
		    				DataInputStream stream = new DataInputStream(entity.getContent());
		                    result = reader.read(stream);
		    				
		    				stream.close();
            entity.consumeContent();
		          } else {
            Log.e("ConnectionManager", "entity is null!");
        }
		          return result;
	}	
}

	