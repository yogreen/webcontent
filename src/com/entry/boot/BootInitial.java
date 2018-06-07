package com.entry.boot;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import com.websystem.accept.NotifyInstance;
import com.websystem.accept.monitor.SuperAcceptObserver;
import com.websystem.membership.MembershipAddress;
import com.websystem.membership.Reader;
import com.websystem.service.LocalService;
import com.websystem.service.boot.WebsystemBootInstance;
import com.websystem.service.instance.ActionServiceInstance;
import com.websystem.service.instance.LocalServiceInstance;
import com.websystem.service.instance.RegisterServiceInstance;
import com.websystem.service.spi.SuperRegisterService;
import com.websystem.service.spi.SuperService;
import com.websystem.task.ExecutorsFactory;
import com.websystem.task.WebsystemExecutorService;
import com.websystem.task.WebsystemThreadPoolParameter;
import com.websystem.workspace.util.WebsystemWorkspaceConfiguation;

class BootInitial {

	private WebsystemBootInstance bootinstance;
	private WebsystemWorkspaceConfiguation config;
	private Logger logger = Logger.getLogger(BootInitial.class
			.getSimpleName());
	private MembershipAddress maddr;
	private Reader reader = null;

	private List<String> uris = null;
	private static BootInitial boot = null;

	private BootInitial(WebsystemThreadPoolParameter parame, String subject) {
		this(parame, subject, false);
	}

	private BootInitial(WebsystemThreadPoolParameter parame, String subject,
			boolean isIPV6Support) {
		maddr = MembershipAddress.newInstance();
		config = WebsystemWorkspaceConfiguation.newInstance();
		bootinstance = new WebsystemBootInstance();
		uris = new ArrayList<String>();

		try {
			reader = Reader.newInstance(subject, isIPV6Support);
			inital(parame);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			bootLocal();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e.getMessage(),e);
		}

	}

	static BootInitial newInstance(WebsystemThreadPoolParameter parame, String subject,
			boolean isIPV6Support){
		if(boot==null){
			boot = new BootInitial(parame, subject, isIPV6Support);
		}
		return boot;
	}
	static BootInitial newInstance(WebsystemThreadPoolParameter parame, String subject){
		
		return newInstance(parame,subject,false);
	}
	void bindURI() {
		List<String> members = memberships();
		List<String> urs = uriList();
		String name = null;
		for (String tmp : urs) {
			if (tmp.contains("register")) {
				name = tmp;
			}
		}
		if (name != null) {
			SuperRegisterService rs = null;
			try {
				rs = (SuperRegisterService) Naming.lookup(name);
				for (int i = 0; i < members.size(); i++) {

					String key = members.get(i);
					String value = urs.get(i);
					int n = name.split("/").length;
					String servicename = name.split("/")[n - 1];
					rs.register(key, value);
					String k = key.split(":")[0];
					logger.info(String.format(
							"Bind key: %s to Service: %s completed.", k,
							servicename));

				}
			} catch (MalformedURLException | RemoteException
					| NotBoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
		}
	}

	void bootMemberships() throws MalformedObjectNameException,
			ClassNotFoundException {
		List<String> members = memberships();
		SuperService service = null;
		SuperRegisterService rs = null;
		for (String name : members) {
			String[] mems = parserOname(name);
			if (mems[2].contains("register")) {
				try {
					rs = new RegisterServiceInstance();
					bootinstance.startRegister(mems[0],
							Integer.parseInt(mems[1]), mems[2], rs);
					logger.info(String.format(
							"Service: %s start up. Instance of : %s", mems[2],
							rs.getClass().getSimpleName()));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new RuntimeException(e.getMessage(), e);
				}
			} else if (mems[2].contains("entry")) {
				try {
					service = new ActionServiceInstance();
					bootinstance.startAction(mems[0],
							Integer.parseInt(mems[1]), mems[2], service);
					logger.info(String.format(
							"Service: %s start up. Instance of : %s", mems[2],
							service.getClass().getSimpleName()));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new RuntimeException(e.getMessage(), e);
				}
			}
			uris.add("rmi://" + mems[0] + ":" + mems[1] + "/" + mems[2]);
		}
	}
	
	void bootLocal() throws Exception{
		LocalService service = new LocalServiceInstance();
		bootinstance.startAction("localhost",1099,"service@loader",service);
	}

	void createWorkspace() throws ClassNotFoundException, IOException {
		Path path = config.defaultWorkspace();
		logger.info(String.format("Workspace: %s", path));
		X509Certificate cert = config.loadCertificate();
		logger.info(String.format("Principal: %s", cert
				.getSubjectX500Principal().getName()));
	}

	private WebsystemExecutorService executorService(
			WebsystemThreadPoolParameter parame) {
		return ExecutorsFactory.executorFactory(parame);
	}

	private void inital(WebsystemThreadPoolParameter parame) throws IOException {
		try {
			createWorkspace();
			config.copyPolicy();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			throw new IOException(e.getMessage(), e);
		}
		WebsystemExecutorService service = executorService(parame);
		service.execute(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				reader.receive();
			}
		});

	}

	private List<String> memberships() {
		return maddr.localEntries();
	}

	NotifyInstance notifyInstance() {
		return this.reader.notifyInstance();
	}

	void openNotify(SuperAcceptObserver observer) {

		this.reader.openNotify(observer);

	}

	private String[] parserOname(String input)
			throws MalformedObjectNameException, ClassNotFoundException {
		String[] urimembers = new String[3];
		ObjectName oname = new ObjectName(input);
		String servicename = oname.getDomain();
		String classname = oname.getKeyProperty("classname");
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		Class<?> clazz = loader.loadClass(classname);
		if (clazz.getName().equals(RegisterServiceInstance.class.getName())) {
			servicename = servicename.replaceAll("entry", "register");
		}
		urimembers[2] = servicename;
		String host = oname.getKeyProperty("host");
		urimembers[0] = host;
		String port = oname.getKeyProperty("port");
		urimembers[1] = port;
		return urimembers;
	}

	Reader readerFactory(String subject, boolean isIPV6Support)
			throws IOException {
		Reader reader = Reader.newInstance(subject, isIPV6Support);
		return reader;
	}

	List<String> uriList() {
		return this.uris;
	}
}
