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
package org.aksonov.mages.connect4;

import java.util.Stack;

import org.aksonov.mages.BaseBoard;
import org.aksonov.mages.entities.Move;
import org.aksonov.tools.Log;

public class Connect4Board extends BaseBoard {
	public static final byte WHITE = 0;
	public static final byte BLACK = 1;
	private static Stack<Connect4Board> pool = new Stack<Connect4Board>();
	public static final int X = 0;
	public static final int Y = 1;
	private int[] sizes;
	private boolean isGameOver = false;

	public Move createMove(byte player, byte x, byte y) {
		Move move = Move.create();
		move.data = new byte[2];
		move.id = moves.size();
		move.player = player;
		move.data[X] = x;
		move.data[Y] = y;
		return move;
	}

	
	public byte getCurrentPlayer() {
		return this.player;
	}
	
	public Move createMove(byte player, byte x){
		Move move = Move.create();
		move.id = moves.size();
		move.player = player;
		move.data = new byte[1];
		move.data[0] = x;
		return move;
	}

	public boolean makeMove(Move move) {
		if (isGameOver){
			Log.d("Connect4Board", "Game is over, no moves are allowed");
			return false;
		}
		if (move.player != player){
			Log.d("Connect4Board", "Move player is not current player");
			return false;
		}
			
		Log.d("Connect4Board", "Making move: " + move);
		if (move.data[0] >= 0 && move.data[0] <8 && sizes[move.data[0]] <6) {
			Log.d("Connect5Board", "Move " + move + " added");
			moves.add(move);
			sizes[move.data[0]]++;
			map[move.data[0]][6 - sizes[move.data[0]]] = move.player;
			checkOver();
			player = (byte)(1 - player);
			Log.d("Connect4Board", "Switching to player: " + player);
			return true;
		} else {
			return false;
		}
	}
	
	private void checkOver(){
		for (int i=0;i<8;i++){
			for (int j=0;j<6;j++){
				int result = diagUp(i,j);
				if (result >= 0){
					Log.d("diagUp", i+" " +j);
					isGameOver = true;
					scores[0] = result == WHITE? 100 : 0;
					scores[1] = result == WHITE ? 0 : 100;
					return;
				}
				result = diagDown(i,j);
				if (result >= 0){
					isGameOver = true;
					scores[0] = result == WHITE? 100 : 0;
					scores[1] = result == WHITE ? 0 : 100;
					return;
				}
				result = horiz(i,j);
				if (result >= 0){
					Log.d("horiz", i+" " +j);
					isGameOver = true;
					scores[0] = result == WHITE? 100 : 0;
					scores[1] = result == WHITE ? 0 : 100;
					return;
				}
				result = vert(i,j);
				if (result >= 0){
					Log.d("vert", i+" " +j);
					isGameOver = true;
					scores[0] = result == WHITE? 100 : 0;
					scores[1] = result == WHITE ? 0 : 100;
					return;
				}
			}
		}
	}
	
	private boolean outX(int x){
		return x<0 || x>=8;
	}
	
	private boolean outY(int y){
		return y<0 || y>=6;
	}
	
	private int diagUp(int x, int y){
		int color = -1;
		for (int i=0;i<4;i++){
			if (outX(x+i) || outY(y-i)) return -1;
			if (color>=0 && map[x+i][y-i]!=color) return -1;
			if (map[x+i][y-i]==-1) return -1; 
			color = map[x+i][y-i];
		}
		return color;
	}

	private int diagDown(int x, int y){
		int color = -1;
		for (int i=0;i<4;i++){
			if (outX(x+i) || outY(y+i)) return -1;
			if (color>=0 && map[x+i][y+i]!=color) return -1;
			if (map[x+i][y+i]==-1) return -1; 
			color = map[x+i][y+i];
		}
		return color;
	}

	private int horiz(int x, int y){
		int color = -1;
		for (int i=0;i<4;i++){
			if (outX(x+i) || outY(y)) return -1;
			if (color>=0 && map[x+i][y]!=color) return -1;
			if (map[x+i][y]==-1) return -1; 
			color = map[x+i][y];
		}
		return color;
	}

	private int vert(int x, int y){
		int color = -1;
		for (int i=0;i<4;i++){
			if (outX(x) || outY(y+i)) return -1;
			if (color>=0 && map[x][y+i]!=color) return -1;
			if (map[x][y+i]==-1) return -1; 
			color = map[x][y+i];
		}
		return color;
	}

	private Connect4Board() {
		super(8, 6);
	}

	private void init() {
		scores = new int[]{50, 50};
		moves.clear();
		isGameOver = false;
		player = 0;
		isGameOver = false;
		scores[0] = 50;
		scores[1] = 50;
		for (int i = 0; i < 8; i++)
			for (int j = 0; j < 6; j++)
				map[i][j] = -1;
		
		sizes = new int[8];
		for (int i=0;i<8;i++){
			sizes[i]=0;
		}
	}

	public static Connect4Board create() {
		synchronized (pool) {
			Connect4Board board = pool.size() > 0 ? pool.pop()
					: new Connect4Board();
			board.init();
			return board;
		}
	}

	public void dispose() {
		synchronized (pool) {
			pool.push(this);
		}
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

	
	public boolean isGameOver() {
		return isGameOver;
	}

	
	public void undoLastMove() {
		
	}

}
