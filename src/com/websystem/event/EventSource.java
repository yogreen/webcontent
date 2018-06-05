package com.websystem.event;

public class EventSource<T> {
	
	private T source;
	private String id;

	public EventSource(T source) {
		super();
		this.source = source;
	}

	public T getSource() {
		return source;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		if(id==null||"".equals(id.trim())){
			throw new IllegalStateException("id can't be null.");
		}
		this.id = id;
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return getId().hashCode();
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if(obj==null){
			return false;
		}
		if(this==obj){
			return true;
		}
		if(obj instanceof EventSource){
			EventSource<T> other= (EventSource<T>) obj;
			return getId()==other.getId();
		}
		return false;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return String.format("\n id=%s, %s", getId(),this.toString());
	}
	
	

}
