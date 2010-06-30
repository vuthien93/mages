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
package org.aksonov.mages.view.chess;

import java.util.Map;

import org.aksonov.mages.Board;
import org.aksonov.mages.R;
import org.aksonov.mages.chess.ChessBoard;
import org.aksonov.mages.entities.Move;
import org.aksonov.mages.view.BoardView;
import org.aksonov.tools.Log;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;


// TODO: Auto-generated Javadoc
/**
 * View represents Chess GUI.
 * 
 * @author Pavel
 */
public class ChessView extends BoardView {

	/** The m bitmaps. */
	private Bitmap[][] mBitmaps = new Bitmap[2][7];
	
	/** The m white paint. */
	private Paint mWhitePaint;
	
	/** The m black paint. */
	private Paint mBlackPaint;
	
	/** The m red paint. */
	private Paint mRedPaint;
	
	/** The m focus paint. */
	private Paint mFocusPaint;
	
	/** The m figure. */
	private byte mFigure = 0;
	
	/** The m player. */
	private byte mPlayer = 0;
	
	/** The cell width. */
	private int cellWidth = 60;

	/** The m y. */
	private float mX = 0, mY = 0;
	
	/** The old x. */
	private byte oldX = 0;
	
	/** The old y. */
	private byte oldY = 0;
	
	/** The focus x. */
	private byte focusX = 3;
	
	/** The focus y. */
	private byte focusY = 3;
	
	/** The is show focus. */
	private boolean isShowFocus = false;
	
	/** The left. */
	private int left;
	
	/** The top. */
	private int top;
	
	/** The width. */
	private int width;
	
	/** The height. */
	private int height;

	/** The Constant TOUCH_TOLERANCE. */
	private static final float TOUCH_TOLERANCE = 4;

	/**
	 * Instantiates a new chess view.
	 * 
	 * @param context
	 *            the context
	 * @param attrs
	 *            the attrs
	 * @param inflateParams
	 *            the inflate params
	 */
	public ChessView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize();
	}

	/**
	 * Instantiates a new chess view.
	 * 
	 * @param context
	 *            the context
	 */
	public ChessView(Context context) {
		super(context);
		initialize();
	}

	/* (non-Javadoc)
	 * @see org.aksonov.mages.view.BoardView#createBoard()
	 */
	public Board createBoard() {
		return ChessBoard.create();
	}

	/* (non-Javadoc)
	 * @see android.view.View#onSizeChanged(int, int, int, int)
	 */
	
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		this.width = w;
		this.height = h;
		fillBitmaps();

	}

	/**
	 * Initialize.
	 */
	private void initialize() {
		mWhitePaint = new Paint();
		mWhitePaint.setColor(Color.WHITE);
		mBlackPaint = new Paint();
		mBlackPaint.setColor(Color.GRAY);

		mRedPaint = new Paint();
		mRedPaint.setColor(Color.BLUE);
		mRedPaint.setStyle(Style.STROKE);
		mRedPaint.setStrokeWidth(3);

		mFocusPaint = new Paint();
		mFocusPaint.setColor(Color.RED);
		mFocusPaint.setStyle(Style.STROKE);
		mFocusPaint.setStrokeWidth(3);

		setFocusable(true);
		requestFocus();
		setClickable(true);

	}

	/**
	 * Fill bitmaps.
	 */
	private void fillBitmaps() {
		Matrix matrix = new Matrix();
		int min = Math.min(width, height);
		float scale = (float) min / ((float) BOARD_WIDTH);
		matrix.setScale(scale, scale);
		cellWidth = (int) (scale * ((float) BOARD_WIDTH / 8));
		for (byte player = 0; player <= 1; player++)
			for (byte figure = 1; figure <= 6; figure++) {
				Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
						getImageResource(player, figure));
				mBitmaps[player][figure] = Bitmap.createBitmap(bitmap, 0, 0,
						bitmap.getWidth(), bitmap.getHeight(), matrix, true);
			}

	}

	/**
	 * Gets the image resource.
	 * 
	 * @param player
	 *            the player
	 * @param figure
	 *            the figure
	 * 
	 * @return the image resource
	 */
	private static int getImageResource(byte player, byte figure) {

		if (player == ChessBoard.BLACK && figure == ChessBoard.PONE)
			return R.drawable.black_pone;
		if (player == ChessBoard.BLACK && figure == ChessBoard.KNIGHT)
			return R.drawable.black_knight;
		if (player == ChessBoard.BLACK && figure == ChessBoard.CASTLE)
			return R.drawable.black_castle;
		if (player == ChessBoard.BLACK && figure == ChessBoard.BISHOP)
			return R.drawable.black_bishop;
		if (player == ChessBoard.BLACK && figure == ChessBoard.QUEEN)
			return R.drawable.black_queen;
		if (player == ChessBoard.BLACK && figure == ChessBoard.KING)
			return R.drawable.black_king;
		if (player == ChessBoard.WHITE && figure == ChessBoard.PONE)
			return R.drawable.white_pone;
		if (player == ChessBoard.WHITE && figure == ChessBoard.KNIGHT)
			return R.drawable.white_knight;
		if (player == ChessBoard.WHITE && figure == ChessBoard.CASTLE)
			return R.drawable.white_castle;
		if (player == ChessBoard.WHITE && figure == ChessBoard.BISHOP)
			return R.drawable.white_bishop;
		if (player == ChessBoard.WHITE && figure == ChessBoard.QUEEN)
			return R.drawable.white_queen;
		if (player == ChessBoard.WHITE && figure == ChessBoard.KING)
			return R.drawable.white_king;
		return 0;
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
		if (this.base == ChessBoard.WHITE) {
			Rect rect = new Rect(x * cellWidth, y * cellWidth, (x + 1)
					* cellWidth, (y + 1) * cellWidth);
			canvas.drawRect(rect, (x + y) % 2 == 1 ? mBlackPaint : mWhitePaint);
			if (((ChessBoard) board).getFigure(x, y) != 0
					&& (mFigure == 0 || oldX != x || oldY != y)) {
				canvas
						.drawBitmap(mBitmaps[((ChessBoard) board).getPlayer(x,
								y)][((ChessBoard) board).getFigure(x, y)], x
								* cellWidth + 0, y * cellWidth + 0, null);
			}

			if (mFigure != 0) {
				canvas.drawRect(new Rect(oldX * cellWidth, oldY * cellWidth,
						(oldX + 1) * cellWidth, (oldY + 1) * cellWidth),
						mRedPaint);
			}

			if (isShowLastMove && board != null && board.getMoves().size() > 0) {
				Move move = board.getMoves().get(board.getMoves().size() - 1);
				int moveX = move.data[ChessBoard.NEW_X];
				int moveY = move.data[ChessBoard.NEW_Y];

				canvas.drawRect(new Rect(moveX * cellWidth, moveY * cellWidth,
						(moveX + 1) * cellWidth, (moveY + 1) * cellWidth),
						mRedPaint);
			}

			if (isShowFocus) {
				canvas.drawRect(new Rect(focusX * cellWidth,
						focusY * cellWidth, (focusX + 1) * cellWidth,
						(focusY + 1) * cellWidth), mFocusPaint);
			}

		} else {
			Rect rect = new Rect(8 * cellWidth - (x + 1) * cellWidth, 8
					* cellWidth - (y + 1) * cellWidth, 8 * cellWidth - x
					* cellWidth, 8 * cellWidth - y * cellWidth);
			canvas.drawRect(rect, (x + y) % 2 == 1 ? mBlackPaint : mWhitePaint);
			if (((ChessBoard) board).getFigure(x, y) != 0
					&& (mFigure == 0 || oldX != x || oldY != y)) {
				canvas
						.drawBitmap(mBitmaps[((ChessBoard) board).getPlayer(x,
								y)][((ChessBoard) board).getFigure(x, y)], 8
								* cellWidth - (x + 1) * cellWidth + 0, 8
								* cellWidth - (y + 1) * cellWidth + 0, null);
			}
			if (mFigure != 0) {
				canvas.drawRect(new Rect(
						8 * cellWidth - (oldX + 1) * cellWidth, 8 * cellWidth
								- (oldY + 1) * cellWidth, 8 * cellWidth - oldX
								* cellWidth, 8 * cellWidth - oldY * cellWidth),
						mRedPaint);
			}
			if (isShowLastMove && board != null && board.getMoves().size() > 0) {
				Move move = board.getMoves().get(board.getMoves().size() - 1);
				int moveX = move.data[ChessBoard.NEW_X];
				int moveY = move.data[ChessBoard.NEW_Y];

				canvas.drawRect(new Rect(8 * cellWidth - (moveX + 1)
						* cellWidth, 8 * cellWidth - (moveY + 1) * cellWidth, 8
						* cellWidth - moveX * cellWidth, 8 * cellWidth - moveY
						* cellWidth), mRedPaint);
			}
			if (isShowFocus) {
				canvas.drawRect(new Rect(8 * cellWidth - (focusX + 1)
						* cellWidth, 8 * cellWidth - (focusY + 1) * cellWidth,
						8 * cellWidth - focusX * cellWidth, 8 * cellWidth
								- focusY * cellWidth), mFocusPaint);
			}
		}
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
	 * @param player
	 *            the player
	 * @param figure
	 *            the figure
	 */
	private void drawFigure(Canvas canvas, float x, float y, int player,
			int figure) {
		canvas.drawBitmap(mBitmaps[player][figure], x, y, null);
	}

	/**
	 * Touch_start.
	 * 
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 */
	private void touch_start(float x, float y) {
		if (x < 0 || y < 0 || readonly)
			return;
		byte i = (byte) (x / cellWidth);
		byte j = (byte) (y / cellWidth);
		if (this.base == ChessBoard.BLACK) {
			i = (byte) (7 - i);
			j = (byte) (7 - j);
		}
		if (((ChessBoard) board).getPlayer(i, j) != this.base){
			Log.d("ChessView", "It is not your figure!");
			return;
		}
			
		if (i >= 0
				&& j >= 0
				&& i < 8
				&& j < 8
				&& ((ChessBoard) board).getPlayer(i, j) == board
						.getCurrentPlayer()
				&& ((ChessBoard) board).getFigure(i, j) != 0) {
			mFigure = ((ChessBoard) board).getFigure(i, j);
			mPlayer = ((ChessBoard) board).getPlayer(i, j);
			mX = x;
			mY = y;
			oldX = i;
			oldY = j;
		}
	}

	/**
	 * Touch_move.
	 * 
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 */
	private void touch_move(float x, float y) {
		float dx = Math.abs(x - mX);
		float dy = Math.abs(y - mY);
		if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
			mX = x;
			mY = y;
		}
	}

	/**
	 * Touch_up.
	 * 
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 */
	private void touch_up(float x, float y) {
		if (x < 0 || y < 0)
			return;
		byte i = (byte) (x / cellWidth);
		byte j = (byte) (y / cellWidth);

		if (this.base == ChessBoard.BLACK) {
			i = (byte) (7 - i);
			j = (byte) (7 - j);
		}

		if (mFigure == 0 || mPlayer == -1)
			return;

		Log.e("ChessView.makeMove", "Making move: ");
		makeMove(((ChessBoard) board).createMove(mPlayer, mFigure, oldX, oldY,
				i, j));
		mX = 0;
		mY = 0;
		mFigure = 0;
		mPlayer = -1;
	}

	/* (non-Javadoc)
	 * @see android.view.View#onTrackballEvent(android.view.MotionEvent)
	 */
	
	public boolean onTrackballEvent(MotionEvent event) {
		super.onTrackballEvent(event);
		return onTouchEvent(event);
	}

	/* (non-Javadoc)
	 * @see android.view.View#onKeyDown(int, android.view.KeyEvent)
	 */
	
	public boolean onKeyDown(int keycode, KeyEvent event) {
		isShowFocus = true;
		if (keycode == KeyEvent.KEYCODE_DPAD_UP && focusY > 0) {
			focusY--;
		} else if (keycode == KeyEvent.KEYCODE_DPAD_DOWN && focusY < 7) {
			focusY++;
		} else if (keycode == KeyEvent.KEYCODE_DPAD_LEFT && focusX > 0) {
			focusX--;
		} else if (keycode == KeyEvent.KEYCODE_DPAD_RIGHT && focusX < 7) {
			focusX++;
		} else if ((keycode == KeyEvent.KEYCODE_DPAD_CENTER || keycode == KeyEvent.KEYCODE_ENTER)
				&& mFigure == 0) {
			touch_start(focusX * cellWidth, focusY * cellWidth);
		} else if ((keycode == KeyEvent.KEYCODE_DPAD_CENTER || keycode == KeyEvent.KEYCODE_ENTER)
				&& mFigure != 0) {
			touch_up(focusX * cellWidth, focusY * cellWidth);
		} else {
			return super.onKeyDown(keycode, event);
		}
		touch_move(focusX * cellWidth + cellWidth/2, focusY * cellWidth + cellWidth/2);
		invalidate();
		return true;
	}

	/* (non-Javadoc)
	 * @see android.view.View#onTouchEvent(android.view.MotionEvent)
	 */
	
	public boolean onTouchEvent(MotionEvent event) {
		isShowFocus = false;
		super.onTouchEvent(event);
		float x = event.getX();
		float y = event.getY();

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			touch_start(x, y);
			invalidate();
			break;
		case MotionEvent.ACTION_MOVE:
			touch_move(x, y);
			invalidate();
			break;
		case MotionEvent.ACTION_UP:
			touch_up(x, y);
			invalidate();
			break;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see android.view.View#onDraw(android.graphics.Canvas)
	 */
	
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// canvas.drawColor(Color.BLACK);
		for (int i = 0; i < 8; i++)
			for (int j = 0; j < 8; j++) {
				drawFigure(canvas, i, j);
			}
		if (mFigure != 0) {
			drawFigure(canvas, mX - cellWidth / 2, mY - cellWidth / 2, mPlayer,
					mFigure);
		}
	}

}
