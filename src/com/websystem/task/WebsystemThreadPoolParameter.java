package com.websystem.task;

import java.io.Serializable;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class WebsystemThreadPoolParameter implements Serializable {

	/**
	 * version
	 */
	private static final long serialVersionUID = 2006531521689070610L;
	private long awaitTimeOut;
	private TimeUnit awaitUnit;
	private int coreThreadnum;
	private RejectedExecutionHandler handler;
	private long keepAliveTimeLength;
	private int maxThreadnum;
	private ThreadFactory threadFactory;
	private TimeUnit unit;
	private BlockingQueue<Runnable> workQueue;

	public WebsystemThreadPoolParameter(int coreThreadnum, int maxThreadnum,
			long keepAliveTimeLength, TimeUnit unit,
			BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory,
			RejectedExecutionHandler handler) {
		super();
		this.coreThreadnum = coreThreadnum;
		this.maxThreadnum = maxThreadnum;
		this.keepAliveTimeLength = keepAliveTimeLength;
		this.unit = unit;
		this.workQueue = workQueue;
		this.threadFactory = threadFactory;
		this.handler = handler;
	}

	public long getAwaitTimeOut() {
		return awaitTimeOut;
	}

	public TimeUnit getAwaitUnit() {
		return awaitUnit;
	}

	public int getCoreThreadnum() {
		return coreThreadnum;
	}

	public RejectedExecutionHandler getHandler() {
		return handler;
	}

	public long getKeepAliveTimeLength() {
		return keepAliveTimeLength;
	}

	public int getMaxThreadnum() {
		return maxThreadnum;
	}
	public ThreadFactory getThreadFactory() {
		return threadFactory;
	}
	public TimeUnit getUnit() {
		return unit;
	}
	public BlockingQueue<Runnable> getWorkQueue() {
		return workQueue;
	}
	public void setAwaitTimeOut(long awaitTimeOut) {
		this.awaitTimeOut = awaitTimeOut;
	}
	public void setAwaitUnit(TimeUnit awaitUnit) {
		this.awaitUnit = awaitUnit;
	}
	public void setCoreThreadnum(int coreThreadnum) {
		this.coreThreadnum = coreThreadnum;
	}
	public void setHandler(RejectedExecutionHandler handler) {
		this.handler = handler;
	}
	public void setKeepAliveTimeLength(long keepAliveTimeLength) {
		this.keepAliveTimeLength = keepAliveTimeLength;
	}
	public void setMaxThreadnum(int maxThreadnum) {
		this.maxThreadnum = maxThreadnum;
	}
	public void setThreadFactory(ThreadFactory threadFactory) {
		this.threadFactory = threadFactory;
	}
	public void setUnit(TimeUnit unit) {
		this.unit = unit;
	}
	public void setWorkQueue(BlockingQueue<Runnable> workQueue) {
		this.workQueue = workQueue;
	}

}
