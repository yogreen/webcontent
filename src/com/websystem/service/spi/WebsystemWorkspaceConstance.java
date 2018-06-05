package com.websystem.service.spi;

import com.websystem.security.WebsystemSecurityConstance;

public interface WebsystemWorkspaceConstance extends WebsystemSecurityConstance {
	String WEBSYS_RSA_PRIVATE_KEY = "local_RSA_prv";
	String WEBSYS_RSA_PUBLIC_KEY = "local_RSA_pub";
	String WEBSYS_WORKSPACE_DIR_KEY = "workspace_base_dir";
	String WEBSYS_WORKSPACE_ENTRY_ITEM = "META-INF/configs/websys_workspace_config.properties";
	String WEBSYS_WORKSPACE_MASTER_KEY = "isMaster";
	String WEBSYS_WORKSPACE_ONAME_CLASSNAME_KEY = "type";
	String WEBSYS_WORKSPACE_ONAME_DATE_KEY = "date";
	String WEBSYS_WORKSPACE_ONAME_IDENTIFIER_KEY = "identifier";
	String WEBSYS_WORKSPACE_ONAME_SOURCE_ADDRKEY = "source_addr";
	String WEBSYS_WORKSPACE_ONAME_SOURCE_PORTKEY = "source_port";
	String WEBSYS_WORKSPACE_ONAME_TARGET_ADDR_KEY = "target_addr";
	String WEBSYS_WORKSPACE_ONAME_TARGET_PORT_KEY = "target_port";
	String WEBSYS_WORKSPACE_ONAME_VERSION_KEY = "version";
	String WEBSYS_WORKSPACE_POLICY_DIR_KEY = "policy_dir";
	String WEBSYS_WORKSPACE_POLICY_FILE_NAME = "websys_policy.pol";
	String WEBSYS_WORKSPACE_POLICY_SYSTEM_KEY = "java.security.policy";
	String WEBSYS_WORKSPACE_SCHEMA_FILE_NAME = "schema.xsd";
	String WEBSYS_WORKSPACE_SERVICE_NAMING_LOCAL_KEY = "naming_local";
	String WEBSYS_WORKSPACE_SERVICE_SCHEME = "rmi://";
	String WEBSYS_WORKSPACE_URI_HOST_KEY = "host";
	String WEBSYS_WORKSPACE_URI_MULTICAST_ADDRESS_KEY = "multicast_address";
	String WEBSYS_WORKSPACE_URI_MULTICAST_PORT_KEY = "multicast_port";
	String WEBSYS_WORKSPACE_URI_PORT_KEY = "port";
	String WEBSYS_WORKSPACE_XML_DIR_KEY = "xml_dir";
	String WEBSYS_WORKSPACE_XML_FILE_NAME = "websystem.xml";
	String WEBSYS_X509CERT_KEY = "local_cert_x509";
	String WEBSYS_LOG4J_PROPERTY_KEY= "log4j_config_file";

}
