package com.websystem.workspace.util;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.server.RMISocketFactory;
import java.util.logging.Logger;

public class WebsystemServiceSocketFactory extends RMISocketFactory {
	
	private Logger logger = Logger.getLogger(WebsystemServiceSocketFactory.class.getSimpleName());
	

	public WebsystemServiceSocketFactory() {
		super();
		// TODO Auto-generated constructor stub
		System.setProperty("java.net.preferIPv4Stack", "true");
	}

	@Override
	public Socket createSocket(String host, int port) throws IOException {
		// TODO Auto-generated method stub
		Socket socket = new Socket(host, port);
		logger.info(String.format("endpoint: .%d. is accepting.", port));
		return socket;
	}

	@Override
	public ServerSocket createServerSocket(int port) throws IOException {
		// TODO Auto-generated method stub
		ServerSocket server = null;
		try{
			server = new ServerSocket(port);
		}catch(Exception e){
			server = new ServerSocket(35557);
		}
		logger.info(String.format("endpoint: .%d. is up.", port));
		return server;
	}
	
}
