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

import java.util.Hashtable;
import java.util.List;

import org.aksonov.mages.tools.AppHelper;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

// TODO: Auto-generated Javadoc
/**
 * Base activity all game activities should extend. It provides game name,
 * common graphical layouts
 * 
 * @author Pavel
 */
public abstract class Game extends ListActivity {
	
	/** The list. */
	private List<Hashtable<String, Object>> list;
	
	/** The intent. */
	private Intent intent;

	/**
	 * Returns internal used game name within Mages framework (like
	 * mages/chess).
	 * 
	 * @return internal used game name within Mages framework (like mages/chess)
	 */
	public abstract String getGameName();

	/**
	 * Returns.
	 * 
	 * @return the board layout
	 */
	public abstract int getBoardLayout();
	
	/**
	 * Gets the creates the game layout.
	 * 
	 * @return the creates the game layout
	 */
	public int getCreateGameLayout(){
		return R.layout.creategame;
	}
	
	/**
	 * Gets the join game layout.
	 * 
	 * @return the join game layout
	 */
	public int getJoinGameLayout(){
		return R.layout.joingame;
	}
	
	/**
	 * Gets the lobby layout.
	 * 
	 * @return the lobby layout
	 */
	public int getLobbyLayout(){
		return R.layout.lobby;
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		list = AppHelper.getServiceList(this,
				new Intent(AppHelper.SERVE_ACTION).setType(getGameName()));

        setListAdapter(new SimpleAdapter(this, list,
                android.R.layout.simple_list_item_1, new String[] { AppHelper.LABEL },
                new int[] { android.R.id.text1 }));
        
        setContentView(R.layout.game);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onActivityResult(int, int, java.lang.String, android.os.Bundle)
	 */
	
	protected void onActivityResult(int requestCode, int resultCode, Intent extras) {
		super.onActivityResult(requestCode, resultCode, extras);
		if (requestCode == 0) {
			if (resultCode > 0) {
				startActivity(AppHelper.createLobbyIntent(
						intent.getComponent(), resultCode, getGameName(),
						getBoardLayout()));
			}
		}
	}

	/* (non-Javadoc)
	 * @see android.app.ListActivity#onListItemClick(android.widget.ListView, android.view.View, int, long)
	 */
	
	protected void onListItemClick(ListView l, View v, int position, long id) {
		intent = (Intent)list.get(position).get(AppHelper.INTENT);
		if ((Boolean)list.get(position).get(AppHelper.HAS_CONFIG)) {
			this.startActivityForResult(AppHelper.createConfigurationIntent(intent
					.getComponent(), getGameName()), 0);
		} else {
			startActivity(AppHelper.createLobbyIntent(intent.getComponent(), 0,
					getGameName(), getBoardLayout()));
		}
	}

}
