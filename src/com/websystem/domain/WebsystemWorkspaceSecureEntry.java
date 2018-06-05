package com.websystem.domain;

import java.io.Serializable;

public class WebsystemWorkspaceSecureEntry implements Serializable {

	/**
	 * version
	 */
	private static final long serialVersionUID = 2416503656351722756L;
	
	private String entry_alias;
	private String entry_password;
	private String entry_protected_password;
	public String getEntry_alias() {
		return entry_alias;
	}
	public void setEntry_alias(String entry_alias) {
		this.entry_alias = entry_alias;
	}
	public String getEntry_password() {
		return entry_password;
	}
	public void setEntry_password(String entry_password) {
		this.entry_password = entry_password;
	}
	public String getEntry_protected_password() {
		return entry_protected_password;
	}
	public void setEntry_protected_password(String entry_protected_password) {
		this.entry_protected_password = entry_protected_password;
	}

}
