package org.aksonov.mages.services;

import org.aksonov.mages.services.IGameServer;
import org.aksonov.mages.services.IPlayerGameListener;
import org.aksonov.mages.services.IPlayerLobbyListener;
import org.aksonov.mages.entities.GameSettings; 
import org.aksonov.mages.entities.PlayerInfo; 
import org.aksonov.mages.entities.Move; 
import org.aksonov.mages.entities.Note; 
import org.aksonov.mages.entities.Custom; 

interface IGamePlayer {
	int getState();
	void  setState(int state);
	
	void onGameList(in List<GameSettings> gameList);
	void onPlayerList(in List<PlayerInfo> playerList);
	
	void onInvitation(in PlayerInfo info, int playerId);
	void onStart(in GameSettings settings, int playerId);
	void onData(int playerId, in Custom data);
	void onMove(int playerId, in Move move);
	void onNote(int playerId, in Note data);
	void onJoin(in PlayerInfo info);
	void onQuit(int playerId);
	void onLogin(in PlayerInfo info);
	void onEnd(int playerId);
	void onError(int attempt, int code);
	void dispose();
	
	void setServer(IGameServer server);
	IGameServer getServer();
	
	PlayerInfo getInfo();
	void setInfo(in PlayerInfo info);
	
	void registerLobbyListener(IPlayerLobbyListener listener);
	void unregisterLobbyListener(IPlayerLobbyListener listener);
	
	void registerGameListener(IPlayerGameListener listener);
	void unregisterGameListener(IPlayerGameListener listener);

	//void registerLoginListener(IPlayerLoginListener listener);
	//void unregisterLoginListener(IPlayerLoginListener listener);
}
