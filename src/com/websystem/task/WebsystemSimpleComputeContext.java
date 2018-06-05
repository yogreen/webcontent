package com.websystem.task;

import java.io.Serializable;
import java.util.concurrent.ThreadFactory;

import com.websystem.service.spi.WebsystemComputable;
import com.websystem.workspace.util.WebsystemThreadFactoryInstance;

public class WebsystemSimpleComputeContext<A extends Serializable, V> implements Serializable {

	/**
	 * version
	 */
	private static final long serialVersionUID = -3075413425017711281L;
	private WebsystemComputable<A, V> cable;
	private ThreadFactory tf = null;

	public WebsystemSimpleComputeContext(WebsystemComputable<A, V> compable) {
		this(compable, new WebsystemThreadFactoryInstance("compute",Thread.NORM_PRIORITY,false));
	}
	public WebsystemSimpleComputeContext(WebsystemComputable<A, V> compable,
			ThreadFactory threadFactory) {
		super();
		this.cable = compable;
		this.tf = threadFactory;
	}

	public V compute(A aply) {
		V value = null;
		if (aply != null) {
			Memoization<A, V> mem = null;
			if (this.tf == null) {

				mem = new Memoization<A, V>(cable);
			} else {
				mem = new Memoization<A, V>(cable, this.tf);
			}
			try {
				value = mem.compute(aply);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				Thread.currentThread().interrupt();
				e.printStackTrace();
			}
		}
		return value;

	}

}
