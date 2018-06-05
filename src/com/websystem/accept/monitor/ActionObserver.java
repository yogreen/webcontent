package com.websystem.accept.monitor;

public interface ActionObserver{
	
	<S>void update(AbstractAcceptObservable able,S source);

}
