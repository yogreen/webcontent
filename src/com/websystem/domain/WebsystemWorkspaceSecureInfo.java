package com.websystem.domain;

import java.io.Serializable;

public class WebsystemWorkspaceSecureInfo implements Serializable {

	/**
	 * version
	 */
	private static final long serialVersionUID = -1661669402181875978L;
	private String keystoreCommonKeyDir;
	private String keystoreDir;
	private String keystoreEntryAlias;
	private String keystoreEntryPassword;
	private String keystoreEntryPrincipalDir;
	private String keystoreFileName;
	private String keystorePrincipalIssuer;
	private String keystorePrincipalSubject;
	private String keystoreProtectedPassword;
	private String securePolicyDir;
	public String getKeystoreCommonKeyDir() {
		return keystoreCommonKeyDir;
	}
	public String getKeystoreDir() {
		return keystoreDir;
	}
	public String getKeystoreEntryAlias() {
		return keystoreEntryAlias;
	}
	public String getKeystoreEntryPassword() {
		return keystoreEntryPassword;
	}
	public String getKeystoreEntryPrincipalDir() {
		return keystoreEntryPrincipalDir;
	}
	public String getKeystoreFileName() {
		return keystoreFileName;
	}
	public String getKeystorePrincipalIssuer() {
		return keystorePrincipalIssuer;
	}
	public String getKeystorePrincipalSubject() {
		return keystorePrincipalSubject;
	}
	public String getKeystoreProtectedPassword() {
		return keystoreProtectedPassword;
	}
	public String getSecurePolicyDir() {
		return securePolicyDir;
	}
	public void setKeystoreCommonKeyDir(String keystoreCommonKeyDir) {
		this.keystoreCommonKeyDir = keystoreCommonKeyDir;
	}
	public void setKeystoreDir(String keystoreDir) {
		this.keystoreDir = keystoreDir;
	}
	public void setKeystoreEntryAlias(String keystoreEntryAlias) {
		this.keystoreEntryAlias = keystoreEntryAlias;
	}
	public void setKeystoreEntryPassword(String keystoreEntryPassword) {
		this.keystoreEntryPassword = keystoreEntryPassword;
	}
	public void setKeystoreEntryPrincipalDir(String keystoreEntryPrincipalDir) {
		this.keystoreEntryPrincipalDir = keystoreEntryPrincipalDir;
	}
	public void setKeystoreFileName(String keystoreFileName) {
		this.keystoreFileName = keystoreFileName;
	}
	public void setKeystorePrincipalIssuer(String keystorePrincipalIssuer) {
		this.keystorePrincipalIssuer = keystorePrincipalIssuer;
	}
	public void setKeystorePrincipalSubject(String keystorePrincipalSubject) {
		this.keystorePrincipalSubject = keystorePrincipalSubject;
	}
	public void setKeystoreProtectedPassword(String keystoreProtectedPassword) {
		this.keystoreProtectedPassword = keystoreProtectedPassword;
	}
	public void setSecurePolicyDir(String securePolicyDir) {
		this.securePolicyDir = securePolicyDir;
	}

}
