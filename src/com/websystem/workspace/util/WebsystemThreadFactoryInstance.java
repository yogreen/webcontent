package com.websystem.workspace.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class WebsystemThreadFactoryInstance implements ThreadFactory {

	private boolean beDaemon;
	private int maxPriory;
	private String name;
	private long stackSize;
	private ThreadGroup threadGroup;
	public WebsystemThreadFactoryInstance(String name, int priory,
			boolean beDaemon) {
		this(null, name, 0, priory, beDaemon);
	}
	public WebsystemThreadFactoryInstance(ThreadGroup group, String name,
			int priory, boolean beDaemon) {
		this(group, name, 0, priory, beDaemon);
	}

	public WebsystemThreadFactoryInstance(ThreadGroup group, String name,
			long stackSize, int priory, boolean beDaemon) {
		this.name = name;
		threadGroup = group;
		this.stackSize = stackSize;
		this.beDaemon = beDaemon;
		maxPriory = priory;
	}

	@Override
	public Thread newThread(Runnable run) {
		// TODO Auto-generated method stub
		Thread th = new Thread(threadGroup, run, name, this.stackSize);
		ThreadGroup group = th.getThreadGroup();
		if (group != null) {
			th.setName(group.getName() + "_" + th.getName() + "@" + th.getId());
		} else {
			th.setName(th.getName() + "@" + th.getId());
		}
		if (this.beDaemon) {
			th.setDaemon(true);
		}
		th.setUncaughtExceptionHandler(new ThreadRuntimeExceptionHandler());
		th.setPriority(maxPriory);
		return th;
	}

	public void cancel(Thread th) {
		th.interrupt();
	}
	public void cancel(Thread th, long timelen, TimeUnit unit) {
		try {
			unit.sleep(timelen);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Thread.currentThread().interrupt();
		} finally {

			th.interrupt();
		}
	}

}
