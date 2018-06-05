package com.websystem.test.accept;

import java.io.Serializable;

public class AcceptEntity implements Serializable {
	
	/**
	 * version
	 */
	private static final long serialVersionUID = -5085294661077030190L;
	public String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
