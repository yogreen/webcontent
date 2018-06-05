package com.websystem.event;

public interface EventAction<T> {
	
	void action(EventSource<T> source);

}
