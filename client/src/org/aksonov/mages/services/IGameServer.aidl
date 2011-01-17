package org.aksonov.mages.services;

import org.aksonov.mages.services.IGamePlayer;
import org.aksonov.mages.entities.PlayerInfo;
import org.aksonov.mages.entities.GameSettings; 
import org.aksonov.mages.entities.PlayerInfo; 
import org.aksonov.mages.entities.Move; 
import org.aksonov.mages.entities.Note; 
import org.aksonov.mages.entities.Custom; 

interface IGameServer {
	boolean isConfigured(int session);
	void connect(int session, IGamePlayer player);
	IGamePlayer getPlayer(int session);
	void disconnect(int session);
	
	void createGame(int session, in GameSettings settings);
	void joinGame(int gameId, in PlayerInfo info);
	
	void requestGameList(int playerId);
	void requestPlayerList(int playerId);
	
	void startGame(int gameId, int playerId);
	void quitGame(int gameId, int playerId);
	void endGame(int gameId, int playerId);
	
	void sendData(int gameId, int playerId, in Custom data);
	void sendMove(int gameId, int playerId, in Move move);
	void sendNote(int gameId, int playerId, in Note note);
	void sendInvitation(int session, in PlayerInfo info);
}