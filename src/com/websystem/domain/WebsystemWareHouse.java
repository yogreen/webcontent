package com.websystem.domain;

import java.io.Serializable;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.List;

import com.websystem.util.Pair;

public class WebsystemWareHouse implements Serializable {

	/**
	 * version
	 */
	private static final long serialVersionUID = 4863331328791613674L;
	private Pair<List<String>, List<byte[]>> classes_depends;
	private Pair<String, byte[]> common_depends;
	private Date date;
	private String id;
	private Pair<String, byte[]> jar_depends;
	private String name;
	private Pair<String, byte[]> processes;
	private byte[] result;

	private X509Certificate sourceCertificate;
	
	public Pair<List<String>, List<byte[]>> getClasses_depends() {
		return classes_depends;
	}
	public Pair<String, byte[]> getCommon_depends() {
		return common_depends;
	}

	public Date getDate() {
		return date;
	}

	public String getId() {
		return id;
	}
	public Pair<String, byte[]> getJar_depends() {
		return jar_depends;
	}

	public String getName() {
		return name;
	}

	public Pair<String, byte[]> getProcesses() {
		return processes;
	}

	public byte[] getResult() {
		return result;
	}

	public X509Certificate getSourceCertificate() {
		return sourceCertificate;
	}
	public void setClasses_depends(Pair<List<String>, List<byte[]>> classes_depends) {
		this.classes_depends = classes_depends;
	}

	public void setCommon_depends(Pair<String, byte[]> common_depends) {
		this.common_depends = common_depends;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setJar_depends(Pair<String, byte[]> jar_depends) {
		this.jar_depends = jar_depends;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setProcesses(Pair<String, byte[]> processes) {
		this.processes = processes;
	}
	public void setResult(byte[] result) {
		this.result = result;
	}
	public void setSourceCertificate(X509Certificate sourceCertificate) {
		this.sourceCertificate = sourceCertificate;
	}

}
