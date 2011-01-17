package org.aksonov.mages.services;

import org.aksonov.mages.services.IGamePlayer;
import org.aksonov.mages.services.IGameServer;
import org.aksonov.mages.entities.PlayerInfo; 

interface IGamePlayerFactory {
	int createSession();
	boolean isConfigured(int session);
	void exportPlayers(int session, int serverSession, IGameServer server);
	void disconnect(int session);
}
