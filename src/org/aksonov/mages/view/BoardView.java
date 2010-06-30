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
package org.aksonov.mages.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.aksonov.mages.Board;
import org.aksonov.mages.InGame;
import org.aksonov.mages.entities.Move;
import org.aksonov.tools.Log;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

// TODO: Auto-generated Javadoc
/**
 * Base view for all board-based and strategy games provides measurement and
 * back/forwad game history feature.
 * 
 * @author Pavel
 */
public abstract class BoardView extends View {
	
	/** The base. */
	public byte base = 0;
	
	/** The board. */
	public Board board;
	
	/** The readonly. */
	public boolean readonly;
	
	/** The is show last move. */
	public boolean isShowLastMove = true;

	/** The handler. */
	protected Handler handler = null;
	
	/** The moves. */
	private List<Move> moves = new ArrayList<Move>();
	
	/** The current move. */
	private int currentMove = 0;

	/**
	 * Sets following moves to the board.
	 * 
	 * @param moves
	 *            the moves
	 */
	public void setMoves(List<Move> moves) {
		this.moves.clear();
		currentMove = 0;
		if (board != null) {
			board.dispose();
		}
		Log.d("BoardView", "Setup initial moves!" + moves.size());
		board = createBoard();
		for (int i = 0; i < moves.size(); i++) {
			Move move = moves.get(i);
			if (board.makeMove(move)) {
				this.moves.add(move);
			}
			currentMove = this.moves.size();

		}
		Log.d("BoardView", "Setup complete");
		invalidate();
	}

	/** Preffered board width. */
	protected static final int BOARD_WIDTH = 480;

	/**
	 * Constructor.
	 * 
	 * @param context
	 *            Context
	 * @param attrs
	 *            attribute
	 * @param inflateParams
	 *            inflate parameters
	 */
	public BoardView(Context context, AttributeSet attrs) {
		super(context, attrs);
		for (int i = 0; i < attrs.getAttributeCount(); i++) {
			Log.d("BoardView", "Attribute name: " + attrs.getAttributeName(i));
			if (attrs.getAttributeName(i).equals("player")) {
				base = (byte) attrs.getAttributeIntValue(i, 0);
			}
		}
		init();
	}

	/**
	 * Instantiates a new board view.
	 * 
	 * @param context
	 *            the context
	 */
	public BoardView(Context context) {
		super(context);
		init();
	}

	/**
	 * Inits the.
	 */
	private void init() {
		board = createBoard();
	}

	/**
	 * Sets event handler (used to send moves).
	 * 
	 * @param handler
	 *            the handler
	 */
	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	/**
	 * Receives moves from the server.
	 * 
	 * @param move
	 *            Move
	 */
	public void onMove(Move move) {
		if (move.id >= moves.size() && board.makeMove(move)) {
			synchronized (moves) {
				moves.add(move);
				currentMove = moves.size();
			}
		}
		invalidate();
	}

	/**
	 * Makes given move.
	 * 
	 * @param move
	 *            the move
	 */
	public void makeMove(Move move) {
		if (readonly) {
			Log.d("BoardView", "Board in read-only mode");
			return;
		}
		try {
			if (currentMove < moves.size()) {
				for (int i = currentMove; i < moves.size(); i++) {
					board.makeMove(moves.get(i));
				}
			}
			boolean result = board.makeMove(move);
			if (result) {
				moves.add(move);
				currentMove = moves.size();
				handler.sendMessage(handler.obtainMessage(InGame.SEND, move));

			} else {
				Log.d("ChessView.makeMove", "Move " + move + " is not valid");
			}
		} catch (Exception e) {
			Log.e("BoardView", e);
		}
	}

	/**
	 * Abstract method, descedants should implement it and return concrete game
	 * logic represented by Board instance.
	 * 
	 * @return the board
	 */
	public abstract Board createBoard();

	/**
	 * Undo last move.
	 */
	public void undoLastMove() {
		synchronized (moves) {
			if (currentMove > 0) {
				currentMove--;
				board = createBoard();
				for (int i = 0; i < currentMove; i++) {
					board.makeMove(moves.get(i));
				}
				invalidate();
			}
		}
	}

	/**
	 * Redo last move.
	 */
	public void redoLastMove() {
		synchronized (moves) {
			if (currentMove < moves.size()) {
				board.makeMove(moves.get(currentMove));
				currentMove++;
				invalidate();
			}
		}

	}

	/**
	 * Dispose.
	 */
	public void dispose() {
		if (board != null) {
			board.dispose();
		}
	}

	/* (non-Javadoc)
	 * @see android.view.View#onMeasure(int, int)
	 */
	
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = Math.min(getMeasuredSize(widthMeasureSpec),
				getMeasuredSize(heightMeasureSpec));
		setMeasuredDimension(width, width);
	}

	/**
	 * Gets the measured size.
	 * 
	 * @param measureSpec
	 *            the measure spec
	 * 
	 * @return the measured size
	 */
	private int getMeasuredSize(int measureSpec) {
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		if (specMode == MeasureSpec.EXACTLY) {
			return specSize;
		} else if (specMode == MeasureSpec.AT_MOST) {
			return Math.min(specSize, BOARD_WIDTH - 80);
		} else {
			return BOARD_WIDTH - 80;
		}

	}
}
