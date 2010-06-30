package org.aksonov.mages.services.gasp;

interface IGASPServerConfiguration {
	int createSession();
	void setConnectInfo(int session, String host, int appId,
			String username, String password, boolean isComet);
}