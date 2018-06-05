package com.websystem.membership;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ProtocolFamily;
import java.net.StandardProtocolFamily;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

import com.websystem.accept.NetPackage;
import com.websystem.util.PlatformUtil;

public class Sender {
	private static Sender sender = null;
	public static Sender newInstance(String subject, boolean isIPV6Support)
			throws IOException {
		if (sender == null) {
			sender = new Sender(subject, isIPV6Support);
		}
		return sender;
	}
	private DatagramChannel channel;

	private MulticastAddress maddr;
	
	private String subject;

	private Sender(String subject, boolean isIPV6Support) throws IOException {
		maddr = MulticastAddress.newInstance(subject);
		ProtocolFamily family = null;
		if (isIPV6Support) {
			family = StandardProtocolFamily.INET6;
		} else {
			family = StandardProtocolFamily.INET;
			System.setProperty("java.net.preferIPv4Stack", "true");
		}
		channel = DatagramChannel.open(family);
		this.subject = subject;

	}

	public void send(NetPackage pack) throws IOException {
		if (pack == null) {
			throw new RuntimeException("parameter \"pack\" must not be null");
		}
		pack.setSubject(this.subject);
		byte[] codes = PlatformUtil.objectMarshal(pack);
		ByteBuffer buff = ByteBuffer.wrap(codes);
		channel.bind(new InetSocketAddress(maddr.local(), 0));
		channel.setOption(StandardSocketOptions.IP_MULTICAST_IF,
				maddr.hardWare());
		buff.flip();
		buff.compact();
		channel.send(buff,
				new InetSocketAddress(maddr.group(), maddr.getPort()));
		channel.close();

	}

}
