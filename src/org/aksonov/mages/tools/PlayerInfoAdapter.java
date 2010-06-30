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

import org.aksonov.mages.entities.PlayerInfo;
import org.aksonov.tools.Log;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

// TODO: Auto-generated Javadoc
/**
 * The Class PlayerInfoAdapter.
 */
public class PlayerInfoAdapter extends BaseAdapter {
	
	/** The list. */
	private ArrayList<PlayerInfo> list = new ArrayList<PlayerInfo>();
	
	/** The m context. */
	private Context mContext;

	/**
	 * Instantiates a new player info adapter.
	 * 
	 * @param c
	 *            the c
	 */
	public PlayerInfoAdapter(Context c) {
		mContext = c;
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getCount()
	 */
	public int getCount() {
		return list.size();
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItem(int)
	 */
	public Object getItem(int position) {
		return list.get(position);
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItemId(int)
	 */
	public long getItemId(int position) {
		PlayerInfo info = list.get(position);
		return info.id;
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
		PlayerInfo info = list.get(position);
		
		TextView view = new TextView(mContext);
		view.setText(info.username + " " + info.rating);
		view.setPadding(2, 2, 2, 2);
		view.setTextColor(Color.WHITE);
		view.setTextSize(14);
		return view;
	}

	/**
	 * Update list.
	 * 
	 * @param l
	 *            the l
	 */
	public void updateList(List<PlayerInfo> l) {
		Log.d("PlayerInfoAdapter", "updateList"+l.size());
		this.list.clear();
		int size = l.size();
		for (int i = 0; i < size; i++) {
			this.list.add(l.get(i));
		}
		notifyDataSetChanged();
	}
	
	/**
	 * Gets the players.
	 * 
	 * @return the players
	 */
	public List<PlayerInfo> getPlayers(){
		return this.list;
	}
}
