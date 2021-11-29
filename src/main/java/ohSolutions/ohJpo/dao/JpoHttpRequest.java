package ohSolutions.ohJpo.dao;

import java.util.HashMap;
import java.util.Map;

public class JpoHttpRequest {
	
	private String dsOauth2;
	private String propertiesFile;
	private String remoteAddr;
	
	private Map<String, String> headers;
	
	public JpoHttpRequest() {
		headers = new HashMap<String, String>();
	}
	
	public String getDsOauth2() {
		return dsOauth2;
	}
	public void setDsOauth2(String dsOauth2) {
		this.dsOauth2 = dsOauth2;
	}
	public String getPropertiesFile() {
		return propertiesFile;
	}
	public void setPropertiesFile(String propertiesFile) {
		this.propertiesFile = propertiesFile;
	}
	public String getRemoteAddr() {
		return remoteAddr;
	}
	public void setRemoteAddr(String remoteAddr) {
		this.remoteAddr = remoteAddr;
	}
	public void setHeader(String variable, String value) {
		this.headers.put(variable,  value);
	}
	public String getHeader(String variable) {
		return this.headers.get(variable);
	}
	
}
