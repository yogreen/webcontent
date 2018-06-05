package com.websystem.accept;

import java.io.Serializable;
import java.net.SocketException;
import java.util.Date;
import java.util.LinkedList;
import java.util.Map;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import com.websystem.domain.ActionStateEnum;
import com.websystem.util.NetWorkToolkit;
import com.websystem.util.PlatformUtil;

public class NetPackage implements Serializable {

	/**
	 * version
	 */
	private static final long serialVersionUID = 628419644604871875L;

	private ActionStateEnum actionState;
	private String additionalMessage;
	private String headerSource;
	private String id;
	private Map<String, String> members;
	private String registerServiceURI;
	private String subject;
	private String x509Certificatecodes;
	private LinkedList<Map<String,byte[]>> contentqueue = new LinkedList<Map<String,byte[]>>();
	public NetPackage() {
	}
	public ActionStateEnum getActionState() {
		return actionState;
	}
	public String getAdditionalMessage() {
		return additionalMessage;
	}

	public String getHeaderSource() {
		return headerSource;
	}
	public String getId() {
		return id;
	}

	public Map<String, String> getMembers() {
		return members;
	}
	public String getRegisterServiceURI() {
		return registerServiceURI;
	}
	public String getSubject() {
		return subject;
	}
	public String packageHeader() throws SocketException {
		NetWorkToolkit kit = NetWorkToolkit.builder.newInstance();
		String localaddr = kit.getFirstNonLocalhostInetAddress()
				.getHostAddress();
		String hardware = kit.getFirstNonLocalhostNetworkInterface().getFirst()
				.getName();
		localaddr = hardware + "_" + localaddr;
		hardware = kit.macView().get(hardware).get(0);
		hardware = localaddr + "@" + hardware;
		localaddr = "principal=" + System.getProperty("user.name");
		String date = "date=" + "\"" + PlatformUtil.formatDate(new Date())
				+ "\"";
		ObjectName oname = null;
		try {
			oname = new ObjectName("\"" + hardware + "\"" + ":" + localaddr
					+ "," + date);
		} catch (MalformedObjectNameException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e.getMessage(), e);
		}
		return oname.getCanonicalName();
	}

	public void setActionState(ActionStateEnum actionState) {
		this.actionState = actionState;
	}
	public void setAdditionalMessage(String additionalMessage) {
		this.additionalMessage = additionalMessage;
	}
	public void setHeaderSource(String headerSource) {
		this.headerSource = headerSource;
	}
	public void setId(String id) {
		this.id = id;
	}
	public void setMembers(Map<String, String> members) {
		this.members = members;
	}
	public void setRegisterServiceURI(String registerServiceURI) {
		this.registerServiceURI = registerServiceURI;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public void setContents(Map<String,byte[]> classses,Map<String,byte[]> jars,Map<String,byte[]> commons){
		this.contentqueue.offer(classses);
		this.contentqueue.offer(jars);
		this.contentqueue.offer(commons);
	}
	public LinkedList<Map<String,byte[]>> getContents(){
		return this.contentqueue;
	}
	public String getX509Certificatecodes() {
		return x509Certificatecodes;
	}
	public void setX509Certificatecodes(String x509Certificatecodes) {
		this.x509Certificatecodes = x509Certificatecodes;
	}
	

}
