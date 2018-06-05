package com.websystem.accept;

import java.util.List;

import com.websystem.accept.monitor.AbstractAcceptObservable;
import com.websystem.accept.monitor.SuperAcceptObserver;

public class NotifyInstance extends AbstractAcceptObservable{
	
	public NotifyInstance() {
		super();
	}
	
	@Override
	public void register(SuperAcceptObserver observer) {
		// TODO Auto-generated method stub
		super.register(observer);
	}

	@Override
	public void remove(SuperAcceptObserver observer) {
		// TODO Auto-generated method stub
		super.remove(observer);
	}

	@Override
	public void removeAll() {
		// TODO Auto-generated method stub
		super.removeAll();
	}

	@Override
	public <S> void notifies(SuperAcceptObserver observer,S source) {
		// TODO Auto-generated method stub
		if(observer==null||source==null){
			return;
		}
		List<SuperAcceptObserver> observers = super.observers();
		if(!observers.contains(observer)){
			throw new IllegalStateException(String.format("no Observer be found: %s", observer.getClass().getName()));
		}
		observer.update(this, source);
	}
	

}
