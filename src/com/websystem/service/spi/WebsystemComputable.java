package com.websystem.service.spi;

import java.io.Serializable;

public interface WebsystemComputable<I extends Serializable, R> extends Computable<I,R>,Serializable{
	
}
