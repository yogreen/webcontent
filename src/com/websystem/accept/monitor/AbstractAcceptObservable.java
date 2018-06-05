package com.websystem.accept.monitor;

import java.util.LinkedList;
import java.util.List;

public abstract class AbstractAcceptObservable{
	
	private LinkedList<SuperAcceptObserver> queue;
	protected AbstractAcceptObservable(){
		queue = new LinkedList<SuperAcceptObserver>();
	}
	protected void register(SuperAcceptObserver observer){
		queue.add(observer);
	}
	protected void remove(SuperAcceptObserver observer){
		queue.remove(observer);
	}
	protected void removeAll(){
		queue = new LinkedList<SuperAcceptObserver>();
	}
	
	public final List<SuperAcceptObserver> observers(){
		return queue;
	}
	public abstract <S>void notifies(SuperAcceptObserver observer,S source);
	
}
