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
package org.aksonov.mages.chess;

import java.util.List;
import java.util.Stack;

import org.aksonov.mages.BaseBoard;
import org.aksonov.mages.entities.Move;
import org.aksonov.tools.Log;

public class ChessBoard extends BaseBoard {
	private static final byte MAX_VALUE = 20;

	public static final byte WHITE = 0;
	public static final byte BLACK = 1;
	public static final byte PONE = 1;
	public static final byte KNIGHT = 2;
	public static final byte CASTLE = 3;
	public static final byte BISHOP = 4;
	public static final byte QUEEN = 5;
	public static final byte KING = 6;
	private static Stack<ChessBoard> pool = new Stack<ChessBoard>();
	private boolean[] isLeftRockAllowed;
	private boolean[] isRightRockAllowed;

	private byte[][] figures = new byte[2][16];
	private byte[][] figuresX = new byte[2][16];
	private byte[][] figuresY = new byte[2][16];
	private byte[] figuresSize = new byte[2];

	public static final int OLD_X = 0;
	public static final int OLD_Y = 1;
	public static final int NEW_X = 2;
	public static final int NEW_Y = 3;
	public static final int FIGURE = 4;
	public static final int OPERATION = 5;
	public static final int FIGURE2 = 6;

	public static final int MATE_OPERATION = 1;
	public static final int EAT_OPERATION = 2;
	public static final int CHECK_OPERATION = 3;
	public static final int PAT_OPERATION = 4;
	public static final int RIGHT_ROCK = 5;
	public static final int LEFT_ROCK = 6;

	private boolean isGameOver = false;

	public Move createMove(byte player, byte figure, byte oldX, byte oldY,
			byte newX, byte newY) {
		Move move = Move.create();
		move.data = new byte[7];
		move.id = moves.size();
		move.player = player;
		move.data[OLD_X] = oldX;
		move.data[OLD_Y] = oldY;
		move.data[NEW_X] = newX;
		move.data[NEW_Y] = newY;
		move.data[FIGURE] = figure;
		return move;
	}

	
	public byte getCurrentPlayer() {
		return this.player;
	}

	public boolean makeMove(Move move) {
		Log.d(this.toString(), "Making move: " + move + " , map: "
				+ map[move.data[OLD_X]][move.data[OLD_Y]]);
		byte p = getPlayer(map[move.data[OLD_X]][move.data[OLD_Y]]);
		if (move.player != p) {
			Log.d("ChessBoard", "Incorrect color of figure! Player is "
					+ move.player + ", but figure:" + p);
			return false;
		}
		if (isGameOver) {
			Log.d("ChessBoard", "Game is over, no moves are allowed!");
			return false;
		}
		if (moveFigure(move, false)) {
			Log.d("ChessBoard", "Move " + move + " added");
			if (checkCheck(this.player)) {
				move.data[OPERATION] = CHECK_OPERATION;
			}
			this.player = (byte) (1 - this.player);
			Log.d("ChessBoard", "Switching player to " + this.player);
			moves.add(move);

			boolean canMove = canMove(this.player);
			if (move.data[OPERATION] == CHECK_OPERATION && !canMove) {
				Log.d("ChessBoard", "Mate!");
				isGameOver = true;
				move.data[OPERATION] = MATE_OPERATION;
				scores[1 - this.player] = 100;
				scores[this.player] = 0;
			} else if (move.data[OPERATION] != CHECK_OPERATION && !canMove) {
				move.data[OPERATION] = PAT_OPERATION;
				isGameOver = true;
			}

			return true;
		} else {
			return false;
		}
	}

	private boolean canMove(byte player) {
		for (int i = 0; i < figuresSize[player]; i++) {
			for (byte x = 0; x < 8; x++) {
				for (byte y = 0; y < 8; y++) {
					byte oldX = figuresX[player][i];
					byte oldY = figuresY[player][i];

					if (isValid(oldX, oldY, x, y, figures[player][i], player)) {
						Move move = createMove(player, figures[player][i],
								oldX, oldY, x, y);
						if (moveFigure(move, false)) {
							Log.d("ChessBoard", "Trying to make move: " + move);
							undoMove(move);
							return true;
						} else {
						}
						move.dispose();
					}
				}
			}
		}
		return false;
	}

	private ChessBoard() {
		super(8, 8);
	}

	private void init() {
		scores = new int[] { 50, 50 };
		moves.clear();
		isGameOver = false;
		scores[0] = 50;
		scores[1] = 50;
		figuresSize[0] = 0;
		figuresSize[1] = 0;
		isRightRockAllowed = new boolean[] { true, true };
		isLeftRockAllowed = new boolean[] { true, true };

		for (byte i = 0; i < 8; i++)
			for (byte j = 0; j < 8; j++)
				map[i][j] = 0;

		createFigure(ChessBoard.BLACK, ChessBoard.KING, (byte) 4, (byte) 0);
		createFigure(ChessBoard.WHITE, ChessBoard.KING, (byte) 4, (byte) 7);

		for (byte i = 0; i < 8; i++)
			createFigure(ChessBoard.BLACK, ChessBoard.PONE, i, (byte) 1);
		createFigure(ChessBoard.BLACK, ChessBoard.CASTLE, (byte) 0, (byte) 0);
		createFigure(ChessBoard.BLACK, ChessBoard.KNIGHT, (byte) 1, (byte) 0);
		createFigure(ChessBoard.BLACK, ChessBoard.BISHOP, (byte) 2, (byte) 0);
		createFigure(ChessBoard.BLACK, ChessBoard.QUEEN, (byte) 3, (byte) 0);
		createFigure(ChessBoard.BLACK, ChessBoard.BISHOP, (byte) 5, (byte) 0);
		createFigure(ChessBoard.BLACK, ChessBoard.KNIGHT, (byte) 6, (byte) 0);
		createFigure(ChessBoard.BLACK, ChessBoard.CASTLE, (byte) 7, (byte) 0);

		for (byte i = 0; i < 8; i++)
			createFigure(ChessBoard.WHITE, ChessBoard.PONE, i, (byte) 6);
		createFigure(ChessBoard.WHITE, ChessBoard.CASTLE, (byte) 0, (byte) 7);
		createFigure(ChessBoard.WHITE, ChessBoard.KNIGHT, (byte) 1, (byte) 7);
		createFigure(ChessBoard.WHITE, ChessBoard.BISHOP, (byte) 2, (byte) 7);
		createFigure(ChessBoard.WHITE, ChessBoard.QUEEN, (byte) 3, (byte) 7);
		createFigure(ChessBoard.WHITE, ChessBoard.BISHOP, (byte) 5, (byte) 7);
		createFigure(ChessBoard.WHITE, ChessBoard.KNIGHT, (byte) 6, (byte) 7);
		createFigure(ChessBoard.WHITE, ChessBoard.CASTLE, (byte) 7, (byte) 7);

		player = ChessBoard.WHITE;
	}

	public static ChessBoard create() {
		synchronized (pool) {
			ChessBoard board = pool.size() > 0 ? new ChessBoard()/* pool.pop() */
			: new ChessBoard();
			board.init();
			return board;
		}
	}

	public void dispose() {
		synchronized (pool) {
			pool.push(this);
		}
	}

	public void createFigure(byte player, byte figure, byte x, byte y) {
		map[x][y] = ChessBoard.getCell(player, figure);
		if (figuresSize[player] == 0 && figure != KING) {
			throw new IllegalArgumentException(
					"King should be first created figure!");
		}
		figuresX[player][figuresSize[player]] = x;
		figuresY[player][figuresSize[player]] = y;
		figures[player][figuresSize[player]++] = figure;
	}

	private boolean checkCheck(byte player) {
		return !safeKing(player, figuresX[player][0], figuresY[player][0]);
	}

	private boolean moveFigure(Move move, boolean isRock) {
		for (int i=0;i<8;i++)
			java.util.Arrays.fill(map[i], (byte)0);
		for (int i=0;i<2;i++){
			for (int j=0;j<figuresSize[i];j++)
				map[figuresX[i][j]][figuresY[i][j]] = getCell((byte)i, figures[i][j]);
		}
		
		if (move.player != this.player) {
			Log.d("ChessBoard", "Current player " + this.player
					+ " is not player of move: " + move.player);
			return false;
		}
		byte oldX = move.data[OLD_X];
		byte oldY = move.data[OLD_Y];
		byte x = move.data[NEW_X];
		byte y = move.data[NEW_Y];

		if (oldX < 0 || oldY < 0 || x < 0 || y < 0 || x >= map.length
				|| oldX >= map.length || oldY >= map[0].length
				|| y >= map[0].length) {
			Log.e("Board", "Out of bounds ");
			return false;
		}

		byte player = move.player;
		byte figure = move.data[FIGURE];
		if (figure == 0) {
			// Log.e("Board", "No figure at " + oldX + ", " + oldY);
			return false;
		}

		if (!isRock && !isValid(oldX, oldY, x, y, figure, player)) {
			// Log.d("ChessBoard", "Invalid move: " + move);
			return false;
		}

		if (isLeftRock(oldX, oldY, x, y, figure, player)) {
			moveFigure(createMove(move.player, figure, (byte) 0, oldY,
					(byte) (oldX - 1), oldY), true);
			moveFigure(createMove(move.player, figure, oldX, oldY,
					(byte) (oldX - 2), oldY), true);
			this.player = (byte) (1 - this.player);
			Log
					.d("ChessBoard", "Left Rock: Switching player to "
							+ this.player);
			move.data[OPERATION] = LEFT_ROCK;
			return true;
		}

		if (isRightRock(oldX, oldY, x, y, figure, player)) {
			moveFigure(createMove(move.player, figure, (byte) 7, oldY,
					(byte) (oldX + 1), oldY), true);
			moveFigure(createMove(move.player, figure, oldX, oldY,
					(byte) (oldX + 2), oldY), true);
			move.data[OPERATION] = RIGHT_ROCK;
			this.player = (byte) (1 - this.player);
			Log.d("ChessBoard", "Right Rock: Switching player to "
					+ this.player);
			return true;
		}
		// change coordinates of the figure
		int found = -1;
		for (int i = 0; i < figuresSize[player]; i++) {
			if (figures[player][i] == figure && figuresX[player][i] == oldX
					&& figuresY[player][i] == oldY) {
				found = i;
				break;
			}
		}
		figuresX[player][found] = x;
		figuresY[player][found] = y;

		if (getFigure(map[x][y]) > 0) {
			move.data[OPERATION] = EAT_OPERATION;
			move.data[FIGURE2] = getFigure(x, y);
			removeFigure(x, y);
		}
		map[oldX][oldY] = 0;
		map[x][y] = getCell(player, figure);

		// check king safety
		if (!safeKing(player, figuresX[player][0], figuresY[player][0])) {
			// rollback
			Log.d("ChessBoard", "Cannot make move - check!");
			undoMove(move);
			return false;
		}

		if (figure == PONE && player == WHITE && y == 0) {
			figures[player][found] = QUEEN;
			map[x][y] = getCell(player, QUEEN);
		}

		if (figure == PONE && player == BLACK && y == 7) {
			figures[player][found] = QUEEN;
			map[x][y] = getCell(player, QUEEN);
		}

		if (figure == CASTLE && oldX == 0) {
			isLeftRockAllowed[player] = false;
		}
		if (figure == CASTLE && oldX == 7) {
			isRightRockAllowed[player] = false;
		}
		if (figure == KING) {
			isRightRockAllowed[player] = false;
			isLeftRockAllowed[player] = false;
		}
		return true;
	}

	public void removeFigure(byte x, byte y) {
		byte figure = getFigure(map[x][y]);
		byte player = getPlayer(map[x][y]);
		if (figure == 0) {
			throw new IllegalArgumentException("No figures at position " + x
					+ ", " + y);
		}
		if (figuresSize[player] < 1) {
			throw new IllegalArgumentException("No figures for player "
					+ player);
		}
		// swap figure with last figure and decrease length
		byte last = figures[player][figuresSize[player] - 1];
		byte lastX = figuresX[player][figuresSize[player] - 1];
		byte lastY = figuresY[player][figuresSize[player] - 1];
		int found = 0;
		for (int i = 0; i < figuresSize[player]; i++) {
			if (figures[player][i] == figure) {
				found = i;
				break;
			}
		}
		figures[player][found] = last;
		figuresX[player][found] = lastX;
		figuresY[player][found] = lastY;
		figuresSize[player]--;
		map[x][y] = 0;
		Log
				.d("ChessBoard", "Removing map[" + x + ", " + y + "] = "
						+ map[x][y]);
	}

	public boolean check(int x1, int y1, int x2, int y2) {
		int length = Math.max(Math.abs(x1 - x2), Math.abs(y1 - y2));
		int dx = (int) Math.signum(x2 - x1);
		int dy = (int) Math.signum(y2 - y1);

		for (int i = 1; i < length; i++) {
			if (map[x1 + i * dx][y1 + i * dy] != 0)
				return false;
		}
		return true;
	}

	private boolean safeKing(int player, int x, int y) {
		if (getFigure(map[x][y]) != 0
				&& (getPlayer(map[x][y]) != player || getFigure(map[x][y]) != KING))
			return false;

		byte opp = (byte) (1 - player);
		for (int i = 0; i < figuresSize[opp]; i++) {
			if (isValid(figuresX[opp][i], figuresY[opp][i], x, y,
					figures[opp][i], opp)) {
				return false;
			}
		}
		return true;
	}

	private boolean isLeftRock(int oldX, int oldY, int x, int y, int figure,
			int player) {
		if (figure == KING && player == WHITE && oldX == 4 && oldY == 7
				&& x == 2 && y == 7 && safeKing(player, 1, 7)
				&& safeKing(player, 2, 7) && safeKing(player, 3, 7)
				&& safeKing(player, 4, 7) && isLeftRockAllowed[player]) {
			return true;
		}
		if (figure == KING && player == BLACK && oldX == 4 && oldY == 0
				&& x == 2 && y == 0 && safeKing(player, 1, 0)
				&& safeKing(player, 2, 0) && safeKing(player, 3, 0)
				&& safeKing(player, 4, 0) && isLeftRockAllowed[player]) {
			return true;
		}
		return false;
	}

	private boolean isRightRock(int oldX, int oldY, int x, int y, int figure,
			int player) {
		if (figure == KING && player == WHITE && oldX == 4 && oldY == 7
				&& x == 6 && y == 7 && safeKing(player, 6, 7)
				&& safeKing(player, 5, 7) && safeKing(player, 4, 7)
				&& isRightRockAllowed[player]) {
			return true;
		}
		if (figure == KING && player == BLACK && oldX == 4 && oldY == 0
				&& x == 6 && y == 0 && safeKing(player, 6, 0)
				&& safeKing(player, 5, 0) && safeKing(player, 4, 0)
				&& isRightRockAllowed[player]) {
			return true;
		}
		return false;
	}

	private boolean isValid(int oldX, int oldY, int x, int y, int figure,
			int player) {

		if (oldX == x && oldY == y) {
			return false;
		}

		boolean result = false;

		if (!(x >= 0 && y >= 0 && x < 8 && y < 8 && figure != 0)) {
			Log.d("ChessBoard", "Out of bounds or no figure to check, figure: "
					+ figure);
			return false;
		}
		if (map[x][y] > 0 && getPlayer(map[x][y]) == player) {
			return false;
		}
		if (figure == KNIGHT) {
			return ((Math.abs(x - oldX) == 1 && Math.abs(y - oldY) == 2) || (Math
					.abs(x - oldX) == 2 && Math.abs(y - oldY) == 1));
		}
		if (x - oldX != 0 && y - oldY != 0
				&& Math.abs(x - oldX) != Math.abs(y - oldY))
			return false;

		if (figure == PONE) {
			if (x == oldX && map[x][y] == 0) {
				if (player == BLACK
						&& (y - oldY == 1 || (y - oldY == 2 && y == 3 && map[x][2] == 0)))
					result = true;
				if (player == WHITE
						&& (y - oldY == -1 || (y - oldY == -2 && y == 4 && map[x][5] == 0)))
					result = true;
			} else if (player == BLACK && y - oldY == 1
					&& Math.abs(x - oldX) == 1 && map[x][y] > 0) {
				result = true;
			} else if (player == WHITE && y - oldY == -1
					&& Math.abs(x - oldX) == 1 && map[x][y] > 0) {
				result = true;
			} else {
				return false;
			}
		}
		if (figure == CASTLE) {
			if (x != oldX && y != oldY)
				return false;
			result = check(oldX, oldY, x, y);
		}

		if (figure == BISHOP) {
			if (x == oldX || y == oldY)
				return false;
			result = check(oldX, oldY, x, y);
		}

		if (figure == QUEEN) {
			result = check(oldX, oldY, x, y);
		}

		if (figure == KING) {
			if (isLeftRock(oldX, oldY, x, y, figure, player)) {
				return true;
			} else if (isRightRock(oldX, oldY, x, y, figure, player)) {
				return true;
			} else if (Math.abs(x - oldX) > 1 || Math.abs(y - oldY) > 1)
				return false;
			else {
				return true;// safeKing(player, x, y);
			}
		}

		return result;
	}

	private static byte getFigure(byte cell) {
		return (byte) (cell % MAX_VALUE);
	}

	private static byte getPlayer(byte cell) {
		return (byte) (cell / MAX_VALUE);
	}

	private static byte getCell(byte player, byte figure) {
		return (byte) (player * MAX_VALUE + figure);
	}

	public byte getPlayer(int x, int y) {
		return getPlayer(map[x][y]);
	}

	public byte getFigure(int x, int y) {
		return getFigure(map[x][y]);
	}

	
	public boolean isGameOver() {
		return isGameOver;
	}

	private void undoMove(Move move) {
		map[move.data[OLD_X]][move.data[OLD_Y]] = getCell(move.player,
				move.data[FIGURE]);

		byte found = -1;
		for (byte i = 0; i < figuresSize[move.player]; i++) {
			if (figures[move.player][i] == move.data[FIGURE]
					&& figuresX[move.player][i] == move.data[NEW_X]
					&& figuresY[move.player][i] == move.data[NEW_Y]) {
				found = i;
				break;
			}
		}
		figuresX[move.player][found] = move.data[OLD_X];
		figuresY[move.player][found] = move.data[OLD_Y];

		if (move.data[OPERATION] == EAT_OPERATION) {
			Log.d("undoMove", "undo eat operation " + move.data[FIGURE2]);
			map[move.data[NEW_X]][move.data[NEW_Y]] = getCell(
					(byte) (1 - move.player), move.data[FIGURE2]);
			int index = figuresSize[1 - move.player];
			figuresX[1 - move.player][index] = move.data[NEW_X];
			figuresY[1 - move.player][index] = move.data[NEW_Y];
			figures[1 - move.player][index] = move.data[FIGURE2];
			figuresSize[1 - move.player]++;
		} else if (move.data[OPERATION] == LEFT_ROCK) {
			isLeftRockAllowed[move.player] = true;
			map[move.data[NEW_X]][move.data[NEW_Y]] = 0;
		} else if (move.data[OPERATION] == RIGHT_ROCK) {
			isRightRockAllowed[move.player] = true;
			map[move.data[NEW_X]][move.data[NEW_Y]] = 0;
		} else {
			map[move.data[NEW_X]][move.data[NEW_Y]] = 0;
		}
	}

	public void undoLastMove() {
		if (moves.size() == 0)
			return;
		Move lastMove = moves.get(moves.size() - 1);
		undoMove(lastMove);
		moves.remove(lastMove);
		player = (byte) (1 - player);
	}

}
