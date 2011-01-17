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
import android.view.Menu;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

// TODO: Auto-generated Javadoc
/**
 * Start activity of the Mages Engine - displays list of installed games.
 * 
 * @author Pavel
 */
public class Main extends ListActivity {
	//private ActivityIconAdapter mAdapter;
	/** The list. */
	private List<Hashtable<String, Object>> list;

	/**
	 * Called when the activity is first created.
	 */
	
	protected void onDestroy() {
		super.onDestroy();
	}

	/**
	 * Creates and fills list with installed games.
	 * 
	 * @param icicle
	 *            the icicle
	 */
	
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		list = AppHelper.getActivityList(this, new Intent(AppHelper.GAME_ACTION));
		setContentView(R.layout.main);
		
        setListAdapter(new SimpleAdapter(this, list,
                android.R.layout.simple_list_item_1, new String[] { AppHelper.LABEL },
                new int[] { android.R.id.text1 }));
	}

	/**
	 * Starts choosen activity.
	 * 
	 * @param l
	 *            the l
	 * @param v
	 *            the v
	 * @param position
	 *            the position
	 * @param id
	 *            the id
	 */
	
	protected void onListItemClick(ListView l, View v, int position, long id) {
		startActivity((Intent)list.get(position).get(AppHelper.INTENT));
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		return true;
	}

}
