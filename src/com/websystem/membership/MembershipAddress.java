package com.websystem.membership;

import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import com.websystem.util.NetWorkToolkit;
import com.websystem.workspace.util.WebsystemWorkspaceConfiguation;
import com.websystem.xml.bean.Entry;
import com.websystem.xml.bean.HostType;
import com.websystem.xml.bean.Naming;
import com.websystem.xml.bean.Servers;
import com.websystem.xml.bean.Websystem;

public class MembershipAddress {
	private static MembershipAddress maddr = null;
	public static MembershipAddress newInstance(){
		if(maddr==null){
			maddr = new MembershipAddress();
		}
		return maddr;
	}
	private WebsystemWorkspaceConfiguation config = null;
	private List<String> locals;
	private Map<String, List<String>> memberships;
	private NetWorkToolkit nkit = null;
	
	private MembershipAddress() {
		config = WebsystemWorkspaceConfiguation.newInstance();
		nkit = NetWorkToolkit.builder.newInstance();

		try {
			memberships = entryMembership();
		} catch (MalformedObjectNameException | ClassNotFoundException
				| IOException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e.getMessage(), e);
		}
		try {
			locals = locals();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	private Map<String, List<String>> entryMembership()
			throws ClassNotFoundException, IOException,
			MalformedObjectNameException {
		Map<String, List<String>> membership = new LinkedHashMap<String, List<String>>();
		Websystem web = null;
		web = config.queryXMLBean();
		String subject = null;
		String entrykey = null;
		String namingkey = null;
		String domain = null;
		String host = null;
		String port = null;
		List<String> entrys = new ArrayList<String>();
		List<String> namings = new ArrayList<String>();
		List<Servers> serverlist = web.getServers();
		ObjectName oname = null;
		for (Servers tmp : serverlist) {
			subject = tmp.getSubject();
			entrykey = subject + "@entry";
			List<Entry> entrylist = tmp.getEntry();
			for (Entry en : entrylist) {
				List<HostType> hosts = en.getHost();
				String classname = en.getName();
				for (HostType ht : hosts) {
					boolean ismaster = ht.isMaster();
					if (ismaster) {
						domain = "Master@" + subject + "_" + "entry" + "_"
								+ ht.getName();
					} else {
						domain = subject + "_" + "entry" + "_" + ht.getName();
					}
					host = ht.getValue();
					port = ht.getPort() + "";
					oname = new ObjectName(domain + ":" + "host=" + host
							+ ",port=" + port + ",classname=" + classname);
					entrys.add(oname.getCanonicalName());
				}
			}
			membership.put(entrykey, entrys);
		}
		for (Servers tmp : serverlist) {
			subject = tmp.getSubject();
			namingkey = subject + "@naming";
			List<Naming> entrylist = tmp.getNaming();
			for (Naming en : entrylist) {
				List<HostType> hosts = en.getHost();
				for (HostType ht : hosts) {
					boolean ismaster = ht.isMaster();
					if (ismaster) {
						domain = "Master@" + subject + "_" + "naming" + "_"
								+ ht.getName();
					} else {
						domain = subject + "_" + "naming" + "_" + ht.getName();
					}
					host = ht.getValue();
					port = ht.getPort() + "";
					oname = new ObjectName(domain + ":" + "host=" + host
							+ ",port=" + port);
					namings.add(oname.getCanonicalName());
				}
			}
			membership.put(namingkey, namings);
		}

		return membership;
	}
	public List<String> entryMembership(String subject) {
		List<String> lines = Collections.emptyList();
		String key = subject + "@entry";
		for (Map.Entry<String, List<String>> en : this.memberships.entrySet()) {
			String ekey = en.getKey();
			if (ekey.contains(key)) {
				lines = en.getValue();
				break;
			}
		}
		return lines;
	}

	public List<String> localEntries() {
		List<String> lines = new ArrayList<String>();
		for (Map.Entry<String, List<String>> en : this.memberships.entrySet()) {
			List<String> tmps = en.getValue();
			for (int i = 0; i < locals.size(); i++) {
				String tmp = locals.get(i);
				for (int j = 0; j < tmps.size(); j++) {
					if (tmps.get(j).contains(tmp)
							&& tmps.get(j).contains("_entry")) {
						lines.add(tmps.get(j));
					}
				}
			}
		}
		return lines;
	}
	public List<String> localNamings() {
		List<String> lines = new ArrayList<String>();
		for (Map.Entry<String, List<String>> en : this.memberships.entrySet()) {
			List<String> tmps = en.getValue();
			for (int i = 0; i < locals.size(); i++) {
				String tmp = locals.get(i);
				for (int j = 0; j < tmps.size(); j++) {
					if (tmps.get(j).contains(tmp)
							&& tmps.get(j).contains("_naming")) {
						lines.add(tmps.get(j));
					}
				}
			}
		}
		return lines;
	}
	private List<String> locals() throws SocketException {
		String hname = nkit.getFirstNonLocalhostNetworkInterface().getFirst()
				.getName();
		List<String> locals = nkit.ipView().get(hname);
		return locals;
	}
	public String masterEnrty(String subject) {
		String key = "Master@" + subject + "_entry";
		String line = null;
		List<String> lines = this.memberships.get(subject + "@entry");
		for (String tmp : lines) {
			if (tmp.contains(key)) {
				line = tmp;
				break;
			}
		}
		return line;
	}

	public String masterNaming(String subject) {
		String key = "Master@" + subject + "_naming";
		String line = null;
		List<String> lines = this.memberships.get(subject + "@naming");
		for (String tmp : lines) {
			if (tmp.contains(key)) {
				line = tmp;
				break;
			}
		}
		return line;
	}
	public List<String> namingMembership(String subject) {
		List<String> lines = Collections.emptyList();
		String key = subject + "@naming";
		for (Map.Entry<String, List<String>> en : this.memberships.entrySet()) {
			String ekey = en.getKey();
			if (ekey.contains(key)) {
				lines = en.getValue();
				break;
			}
		}
		return lines;
	}
	public String[] subjects(){
		Set<String> set = this.memberships.keySet();
		String[] subjects = new String[set.size()];
		set.toArray(subjects);
		for(int i=0;i<subjects.length;i++){
			String subject = subjects[i];
			if(subject.contains("@")){
				subject = subject.split("@")[0];
			}
			subjects[i] = subject;
		}
		return subjects;
	}
}
