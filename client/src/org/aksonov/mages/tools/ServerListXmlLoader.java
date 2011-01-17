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
package org.aksonov.mages.tools;

import java.util.ArrayList;
import java.util.List;

import org.aksonov.tools.Log;
import org.xmlpull.v1.XmlPullParser;

import android.content.Context;



// TODO: Auto-generated Javadoc
/**
 * The Class ServerListXmlLoader.
 */
public class ServerListXmlLoader {
	
	/** The context. */
	private final Context context;

	/**
	 * Instantiates a new server list xml loader.
	 * 
	 * @param context
	 *            the context
	 */
	public ServerListXmlLoader(Context context) {
		this.context = context;
	}

	/**
	 * Load.
	 * 
	 * @param resource
	 *            the resource
	 * 
	 * @return the list< server data>
	 */
	public List<ServerData> load(int resource) {
		List<ServerData> list = new ArrayList<ServerData>();
		XmlPullParser xpp = context.getResources().getXml(resource);
		try {
			int eventType = xpp.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				if ((eventType == XmlPullParser.START_TAG)
						&& xpp.getName().equals("server")) {
					String uri = xpp.getAttributeValue(null, "uri");
					String type = xpp.getAttributeValue(null, "type");
					if (uri == null || uri.equals("")) {
						Log.w("ServerListXmlLoader",
										"Server uri value should not be null and non-empty");
						eventType = xpp.next();
						continue;
					}
					int appId;
					try {
						appId = Integer.parseInt(xpp.getAttributeValue(null,
								"app_id"));
					} catch (Exception e) {
						Log.w("ServerListXmlLoader",
								"Cannot obtain app_id attribute for the server: "
										+ e.getMessage());
						eventType = xpp.next();
						continue;
					}
					list.add(new ServerData(uri, appId, type));
				}
				eventType = xpp.next();
			}
		} catch (Exception e) {
			Log.w("ServerListXmlLoader", e.getMessage());
		}
		return list;

	}

}
