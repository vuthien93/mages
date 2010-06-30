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
package org.aksonov.mages.view.connect4;

import java.util.Map;

import org.aksonov.mages.Board;
import org.aksonov.mages.connect4.Connect4Board;
import org.aksonov.mages.entities.Move;
import org.aksonov.mages.view.BoardView;
import org.aksonov.tools.Log;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;

// TODO: Auto-generated Javadoc
/**
 * The Class Connect4View.
 */
public class Connect4View extends BoardView {
	
	/** The m white paint. */
	private Paint mWhitePaint;
	
	/** The m black paint. */
	private Paint mBlackPaint;
	
	/** The m gray paint. */
	private Paint mGrayPaint;
	
	/** The m focus paint1. */
	private Paint mFocusPaint1;
	
	/** The m focus paint2. */
	private Paint mFocusPaint2;
	
	/** The m brown paint. */
	private Paint mBrownPaint;
	
	/** The m red paint. */
	private Paint mRedPaint;
	
	/** The cell width. */
	private int cellWidth = 60;
	
	/** The width. */
	private int width;
	
	/** The height. */
	private int height;

	/** The focus x. */
	private byte focusX = 3;
	
	/** The is show focus. */
	private boolean isShowFocus = false;

	/** The Constant TOUCH_TOLERANCE. */
	private static final float TOUCH_TOLERANCE = 4;

	/**
	 * Instantiates a new connect4 view.
	 * 
	 * @param context
	 *            the context
	 * @param attrs
	 *            the attrs
	 * @param inflateParams
	 *            the inflate params
	 */
	public Connect4View(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize();
	}

	/**
	 * Instantiates a new connect4 view.
	 * 
	 * @param context
	 *            the context
	 */
	public Connect4View(Context context) {
		super(context);
		initialize();
	}

	/* (non-Javadoc)
	 * @see org.aksonov.mages.view.BoardView#createBoard()
	 */
	public Board createBoard() {
		return Connect4Board.create();
	}

	/**
	 * Initialize.
	 */
	private void initialize() {
		mWhitePaint = new Paint();
		mWhitePaint.setColor(Color.YELLOW);
		mWhitePaint.setStyle(Style.FILL_AND_STROKE);
		mWhitePaint.setStrokeWidth(3);

		mBlackPaint = new Paint();
		mBlackPaint.setColor(Color.BLACK);
		mBlackPaint.setStyle(Style.STROKE);
		mBlackPaint.setStrokeWidth(3);

		mRedPaint = new Paint();
		mRedPaint.setColor(Color.RED);
		mRedPaint.setStyle(Style.FILL);
		mRedPaint.setStrokeWidth(3);

		mBrownPaint = new Paint();
		mBrownPaint.setColor(Color.GRAY);
		mBrownPaint.setStyle(Style.FILL);

		mGrayPaint = new Paint();
		mGrayPaint.setColor(Color.GRAY);
		mGrayPaint.setStyle(Style.FILL);
		mGrayPaint.setStrokeWidth(3);

		mFocusPaint1 = new Paint();
		mFocusPaint1.setColor(Color.YELLOW);
		mFocusPaint1.setStyle(Style.STROKE);
		mFocusPaint1.setStrokeWidth(3);

		mFocusPaint2 = new Paint();
		mFocusPaint2.setColor(Color.RED);
		mFocusPaint2.setStyle(Style.STROKE);
		mFocusPaint2.setStrokeWidth(3);

		setFocusable(true);
		requestFocus();
		setClickable(true);
	}

	/**
	 * Draw figure.
	 * 
	 * @param canvas
	 *            the canvas
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 */
	private void drawFigure(Canvas canvas, int x, int y) {
		// Log.d("ChessView", "drawFigure x=" + x + ", y=" +y);
		Connect4Board board = (Connect4Board) this.board;

		canvas.drawCircle(cellWidth / 2 + x * cellWidth, cellWidth + cellWidth
				/ 2 + y * cellWidth, cellWidth / 2 - 2, mBlackPaint);

		if (board.map[x][y] == Connect4Board.WHITE) {
			canvas.drawCircle(cellWidth / 2 + x * cellWidth, cellWidth
					+ cellWidth / 2 + y * cellWidth, cellWidth / 2 - 5,
					mWhitePaint);

		} else if (board.map[x][y] == Connect4Board.BLACK) {
			canvas.drawCircle(cellWidth / 2 + x * cellWidth, cellWidth / 2
					+ cellWidth + y * cellWidth, cellWidth / 2 - 5, mRedPaint);
		} else {
			canvas.drawCircle(cellWidth / 2 + x * cellWidth, cellWidth / 2
					+ cellWidth + y * cellWidth, cellWidth / 2 - 5, mGrayPaint);
		}
	}

	/* (non-Javadoc)
	 * @see android.view.View#onTrackballEvent(android.view.MotionEvent)
	 */
	
	public boolean onTrackballEvent(MotionEvent event) {
		super.onTrackballEvent(event);
		return onTouchEvent(event);
	}

	/* (non-Javadoc)
	 * @see android.view.View#onTouchEvent(android.view.MotionEvent)
	 */
	
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		if (readonly){
			return super.onTouchEvent(event);
		}
		if (board.getCurrentPlayer() != base){
			Log.d("Connec4View", "Not your move now");
			return super.onTouchEvent(event);
		}
		isShowFocus = true;
		float x = event.getX();
		float y = event.getY();

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			focusX = (byte)((float)x / cellWidth);
			invalidate();
			break;
		case MotionEvent.ACTION_MOVE:
			focusX = (byte)((float)x / cellWidth);
			invalidate();
			break;
		case MotionEvent.ACTION_UP:
			mark(focusX);
			invalidate();
			break;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see android.view.View#onKeyDown(int, android.view.KeyEvent)
	 */
	
	public boolean onKeyDown(int keycode, KeyEvent event) {
		if (readonly){
			return super.onKeyDown(keycode, event);
		}
		if (board.getCurrentPlayer() != base){
			Log.d("Connec4View", "Not your move now");
			return super.onKeyDown(keycode, event);
		}
		isShowFocus = true;
		if (keycode == KeyEvent.KEYCODE_DPAD_LEFT && focusX > 0) {
			focusX--;
		} else if (keycode == KeyEvent.KEYCODE_DPAD_UP && focusX > 0) {
			focusX--;
		} else if (keycode == KeyEvent.KEYCODE_DPAD_DOWN && focusX < 7) {
			focusX++;
		} else if (keycode == KeyEvent.KEYCODE_DPAD_RIGHT && focusX < 7) {
			focusX++;
		} else if ((keycode == KeyEvent.KEYCODE_DPAD_CENTER || keycode == KeyEvent.KEYCODE_ENTER)) {
			mark(focusX);
		} else {
			return super.onKeyDown(keycode, event);
		}
		invalidate();
		return true;
	}

	/**
	 * Mark.
	 * 
	 * @param x
	 *            the x
	 */
	private void mark(byte x) {
		Move move = ((Connect4Board)board).createMove(board.getCurrentPlayer(), x);
		Log.d("Connect4View", "Creating move " + move + " current player " + board.getCurrentPlayer());
		makeMove(move);
	}

	/* (non-Javadoc)
	 * @see android.view.View#onDraw(android.graphics.Canvas)
	 */
	
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawRect(new Rect(0, 0, width, height), mBrownPaint);
		for (int i = 0; i < 8; i++)
			for (int j = 0; j < 6; j++) {
				drawFigure(canvas, i, j);
			}

		if (isShowFocus) {
			for (int y = 0; y < 6; y++) {
				canvas.drawCircle(cellWidth / 2 + focusX * cellWidth, cellWidth
						+ cellWidth / 2 + y * cellWidth, cellWidth / 2 - 2,
						board.getCurrentPlayer() == 0 ? mFocusPaint1 : mFocusPaint2);
			}

		}
	}

	/* (non-Javadoc)
	 * @see android.view.View#onSizeChanged(int, int, int, int)
	 */
	
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		this.width = w;
		this.height = h;

		int min = Math.min(width, height);
		float scale = (float) min / ((float) BOARD_WIDTH);
		cellWidth = (int) (scale * ((float) BOARD_WIDTH / 8));
	}
}
