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

import org.aksonov.mages.Helper;
import org.aksonov.mages.R;
import org.aksonov.mages.entities.GameSettings;
import org.aksonov.mages.entities.PlayerInfo;
import org.aksonov.tools.Log;

import android.content.Context;
import android.graphics.Color;
import android.text.Layout.Alignment;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

// TODO: Auto-generated Javadoc
/**
 * The Class GameSettingsAdapter.
 */
public class GameSettingsAdapter extends BaseAdapter {
	
	/** The m a is. */
	private ArrayList<GameSettings> mAIs = new ArrayList<GameSettings>();
	
	/** The m context. */
	private Context mContext;

	/**
	 * Instantiates a new game settings adapter.
	 * 
	 * @param c
	 *            the c
	 */
	public GameSettingsAdapter(Context c) {
		mContext = c;
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getCount()
	 */
	public int getCount() {
		return mAIs.size();
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItem(int)
	 */
	public Object getItem(int position) {
		return mAIs.get(position);
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItemId(int)
	 */
	public long getItemId(int position) {
		GameSettings game = mAIs.get(position);
		return game.id;
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
		GameSettings game = mAIs.get(position);
		PlayerInfo[] players = new PlayerInfo[game.maxActors];
		for (int i = 0; i < game.maxActors; i++)
			players[i] = null;

		// Log.d("getView", "Players number: " + game.players.size());
		for (int i = 0; i < game.players.size(); i++) {
			// Log.d("getView", "Player color is: " +
			// game.players.get(i).player);
			if (game.players.get(i).player >= 0
					&& game.players.get(i).player < game.maxActors) {
				players[game.players.get(i).player] = game.players.get(i);
			}
		}

		LinearLayout layout = new LinearLayout(mContext);

		TextView number = new TextView(mContext);
		number.setText(game.id + "");
		number.setTextColor(Color.WHITE);
		//number.setTextSize(16);
		layout.addView(number, new LayoutParams(20, LayoutParams.WRAP_CONTENT));

		for (int i = 0; i < game.maxActors; i++) {
			TextView color = new TextView(mContext);
			//color.setTextSize(16);
			color.setTextColor(Color.WHITE);
			color.setPadding(2, 2, 2, 2);
			if (players[i] == null) {
				color.setText("?");
				color.setGravity(Gravity.CENTER);
				layout.addView(color, new LayoutParams(60,
						color.getHeight() + 20));
			} else {
				color.setText(players[i].username);
				color.setGravity(Gravity.CENTER);
				layout.addView(color, new LayoutParams(60,
						LayoutParams.WRAP_CONTENT));
			}
		}

		TextView view = new TextView(mContext);
		CharSequence infinite = mContext.getText(R.string.infinite);
		StringBuilder sb = new StringBuilder();
		CharSequence timePerGame = Helper
				.formatTime(game.timePerGame, infinite);

		CharSequence timePerMove = Helper
				.formatTime(game.timePerMove, infinite);
		sb.append(game.rated ? mContext.getText(R.string.rated) : mContext.getText(R.string.unrated));
		sb.append(", ");

		if (timePerGame.equals(infinite)) {
			sb.append(infinite);
		} else {
			sb.append(timePerGame);
			sb.append("/");
			sb.append(timePerMove);
		}
		view.setText(sb.toString());
		view.setTextColor(Color.WHITE);
		//view.setTextSize(16);

		layout.addView(view, new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));
		return layout;
	}

	/**
	 * Update list.
	 * 
	 * @param list
	 *            the list
	 */
	public void updateList(List<GameSettings> list) {
		Log.w("GameSettingsAdapter", "updateList: number of instances: "
				+ list.size());
		for (int i = 0; i < mAIs.size(); i++) {
			mAIs.get(i).dispose();
		}
		mAIs.clear();
		int size = list.size();
		for (int i = 0; i < size; i++) {
			mAIs.add(list.get(i));
		}
		notifyDataSetChanged();
	}
	
	/**
	 * Gets the list.
	 * 
	 * @return the list
	 */
	public List<GameSettings> getList(){
		return mAIs;
	}

}
