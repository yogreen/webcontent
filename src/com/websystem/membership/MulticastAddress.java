package com.websystem.membership;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.List;

import com.websystem.util.NetWorkToolkit;
import com.websystem.util.Pair;
import com.websystem.workspace.util.WebsystemWorkspaceConfiguation;
import com.websystem.xml.bean.Multicast;
import com.websystem.xml.bean.Servers;
import com.websystem.xml.bean.Websystem;

class MulticastAddress {

	private static MulticastAddress maddr = null;
	static MulticastAddress newInstance(String subject) {
		if (maddr == null) {
			maddr = new MulticastAddress(subject);
		}
		return maddr;
	}

	private WebsystemWorkspaceConfiguation config = WebsystemWorkspaceConfiguation
			.newInstance();
	private String host;
	private NetWorkToolkit nkit = NetWorkToolkit.builder.newInstance();

	private int port;

	private MulticastAddress(String subject) {
		Pair<String, Integer> pair = null;
		try {
			pair = multicastPair(subject);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e.getMessage(), e);
		}
		host = pair.getFirst();
		port = pair.getSecond();
	}

	int getPort() {
		return port;
	}

	InetAddress group() throws IOException {
		InetAddress addr = InetAddress.getByName(host);
		return addr;
	}
	
	InetAddress local() throws SocketException{
		return nkit.getFirstNonLocalhostInetAddress();
	}

	NetworkInterface hardWare() throws SocketException {
		NetworkInterface interf = nkit.getFirstNonLocalhostNetworkInterface()
				.getFirst();
		return interf;
	}

	private Pair<String, Integer> multicastPair(String subject) throws IOException {
		if(subject==null||"".equals(subject.trim())){
			throw new RuntimeException("parameter \"subject\" must not be null or empty.");
		}
		Pair<String, Integer> pair = null;
		Websystem web = null;
		try {
			web = config.queryXMLBean();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			throw new IOException(e.getMessage(), e);
		}
		List<Servers> serverlist = web.getServers();
		Servers current = null;
		for (Servers tmp : serverlist) {
			String name = tmp.getSubject();
			if (name.equals(subject)) {
				current = tmp;
				break;
			}
		}
		if (current == null) {
			throw new IOException(String.format("Subject: %s is not found",
					subject));
		}
		Multicast mul = current.getMulticast();
		pair = new Pair<String, Integer>(mul.getIp(), mul.getPort());
		return pair;
	}

}
