package org.aksonov.mages.services;

import org.aksonov.mages.entities.PlayerInfo; 

interface ILocalServerConfiguration {
	int createSession();
	void setPlayerInfo(int session, in PlayerInfo player);
}