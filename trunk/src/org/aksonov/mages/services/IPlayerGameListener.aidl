package org.aksonov.mages.services;

import org.aksonov.mages.entities.Custom;
import org.aksonov.mages.entities.Move;
import org.aksonov.mages.entities.Note;
import org.aksonov.mages.entities.PlayerInfo;
import org.aksonov.mages.entities.GameSettings;

interface IPlayerGameListener {
	void onStart(in GameSettings settings, int playerId);
	void onData(int playerId, in Custom data);
	void onMove(int playerId, in Move move);
	void onNote(int playerId, in Note data);
	void onJoin(in PlayerInfo info);
	void onQuit(int playerId);
	void onEnd(int playerId);
	void onError(int attempt, int code);
	void onPlayerList(in List<PlayerInfo> playerList);
}