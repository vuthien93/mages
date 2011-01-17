package org.aksonov.mages.services;

import org.aksonov.mages.entities.GameSettings;
import org.aksonov.mages.entities.PlayerInfo;

interface IPlayerLobbyListener {
	void onError(int attempt, int code);
	void onGameList(in List<GameSettings> gameList);
	void onPlayerList(in List<PlayerInfo> playerList);
	void onInvitation(in PlayerInfo info, int playerId);
	void onJoin(in PlayerInfo info);
	void onLogin(in PlayerInfo info);
} 