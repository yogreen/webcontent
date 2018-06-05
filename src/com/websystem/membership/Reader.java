package com.websystem.membership;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ProtocolFamily;
import java.net.SocketAddress;
import java.net.StandardProtocolFamily;
import java.net.StandardSocketOptions;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.LoggerFactory;

import com.websystem.accept.NetPackage;
import com.websystem.accept.NotifyInstance;
import com.websystem.accept.monitor.SuperAcceptObserver;
import com.websystem.domain.ActionStateEnum;
import com.websystem.service.spi.SuperRegisterService;
import com.websystem.util.NetWorkToolkit;
import com.websystem.util.PlatformUtil;
import com.websystem.workspace.util.WebsystemWorkspaceConfiguation;

public class Reader {

	private final static int BUFF_CAPACITY = 4096;
	private static Reader reader = null;
	public static Reader newInstance(String subject, boolean isIPV6Surpport)
			throws IOException {
		if (reader == null) {
			reader = new Reader(subject, isIPV6Surpport);
		}
		return reader;
	}
	private DatagramChannel channel;
	private Logger logger = Logger.getLogger(Reader.class.getSimpleName());
	private org.slf4j.Logger slogger = LoggerFactory.getLogger("storage");
	private MulticastAddress maddr;
	private LinkedHashSet<String> membershipSet;
	private NotifyInstance notify;
	private SuperAcceptObserver observer;
	private Lock lock;
	private WebsystemWorkspaceConfiguation config;
	private ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<String>(1);
	private NetWorkToolkit kit = null;

	private ByteBuffer readerBuff;
	private Reader(String subject, boolean isIPV6Support) throws IOException {
		super();
		maddr = MulticastAddress.newInstance(subject);
		readerBuff = ByteBuffer.allocate(BUFF_CAPACITY);
		ProtocolFamily family = null;
		if (isIPV6Support) {
			family = StandardProtocolFamily.INET6;
		} else {
			family = StandardProtocolFamily.INET;
			System.setProperty("java.net.preferIPv4Stack", "true");
		}
		channel = DatagramChannel.open(family);
		channel.setOption(StandardSocketOptions.SO_REUSEADDR, true).bind(
				new InetSocketAddress(maddr.getPort()));
		membershipSet = new LinkedHashSet<String>();
		lock = new ReentrantLock();
		notify = new NotifyInstance();
		config = WebsystemWorkspaceConfiguation.newInstance();
		kit = NetWorkToolkit.builder.newInstance();
		try {
			loggerInitial();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	void loggerInitial() throws IOException {
		String logp = config.logPath().toString();
		System.setProperty("log4j_path", logp);
		PropertyConfigurator.configure(config.log4jConfigFile());

	}
	void action(ByteBuffer receive) throws ClassNotFoundException, IOException,
			URISyntaxException {

		lock.lock();
		try {
			byte[] codes = receive.array();
			NetPackage npack = (NetPackage) PlatformUtil.objectUnMarshal(codes);
			String uri = npack.getRegisterServiceURI();
			boolean offer = false;
			if (uri != null && !"".equals(uri.trim())) {

				offer = queue.offer(uri);
			}
			String header = npack.getHeaderSource();
			ObjectName oname = null;
			try {
				oname = new ObjectName(header);
			} catch (MalformedObjectNameException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
			String addr = oname.getDomain().split("@")[0].split("_")[1];
			boolean flag = this.membershipSet.contains(addr);
			if (!flag) {
				logger.info(String.format("Welcome, Principal: %s!!!",
						oname.getKeyProperty("principal")));
			}
			slogger.info(String.format(header));

			ActionStateEnum state = npack.getActionState();
			if (state != null) {

				switch (state) {
					case UPDATE :
						this.membershipSet.add(addr);
						break;
					case UPDATE_DONE :
						this.membershipSet.add(addr);
						if (npack.getHeaderSource().equals(
								npack.packageHeader())
								&& offer) {
							Map<String, String> members = npack.getMembers();
							uri = queue.poll();
							String filter = null;
							for(Map.Entry<String, String> en:members.entrySet()){
								filter = en.getValue();
								if(filter.equals(uri)){
									continue;
								}
								if(filter!=null){
									break;
								}
							}
							filter = filter.split(":")[1];
							if(filter.contains("/")){
								filter = filter.split("/")[0];
							}
							String local = kit.getFirstNonLocalhostInetAddress().getHostAddress();
							if(filter.equals(local)){
								
								try {
									SuperRegisterService service = (SuperRegisterService) Naming
											.lookup(uri);
									for (Map.Entry<String, String> en : members
											.entrySet()) {
										service.register(en.getKey(), en.getValue());
									}
								} catch (NotBoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}

						return;
					default :
						break;
				}

			}
			if (notify != null && observer != null) {
				if (!flag) {

					logger.info(String.format("monitor: %s start up.", observer
							.getClass().getSimpleName()));
				}
				notify.notifies(observer, npack);

			}
		} finally {
			lock.unlock();
		}

	}
	protected void buffResize(int remaining) {
		if (readerBuff.remaining() < remaining) {
			ByteBuffer b = ByteBuffer.allocate(readerBuff.capacity() * 2);
			readerBuff.flip();
			b.put(readerBuff);
			readerBuff = b;
		}
	}

	public Set<String> getMembershipSet() {
		return membershipSet;
	}

	public void openNotify(SuperAcceptObserver observer) {
		lock.lock();
		try {
			if (observer != null) {

				this.observer = observer;

			}

		} finally {
			lock.unlock();
		}
	}
	public NotifyInstance notifyInstance() {
		return this.notify;
	}
	public void receive() {
		try {
			if (membershipSet.isEmpty()) {
				channel.join(maddr.group(), maddr.hardWare());
			} else {

				Iterator<String> itera = membershipSet.iterator();
				while (itera.hasNext()) {
					String tmp = itera.next();

					InetAddress source = InetAddress.getByName(tmp);
					channel.join(maddr.group(), maddr.hardWare(), source);
				}
			}
			Selector sel = Selector.open();
			channel.configureBlocking(false);
			channel.register(sel, SelectionKey.OP_READ);
			while (true) {
				int n = sel.select();
				if (n > 0) {
					Iterator<SelectionKey> iter = sel.selectedKeys().iterator();
					while (iter.hasNext()) {
						SelectionKey skey = iter.next();
						iter.remove();
						DatagramChannel dc = (DatagramChannel) skey.channel();
						buffResize(BUFF_CAPACITY / 20);
						SocketAddress saddr = dc.receive(readerBuff);
						if (saddr != null) {
							readerBuff.flip();
							readerBuff.clear();
							action(readerBuff);
							readerBuff.rewind();
							readerBuff.limit(readerBuff.capacity());
						}

					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
