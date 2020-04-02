package ohSolutions.ohJpo.bean;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;

public class FileReq {
	
	private InputStream file;
	private String name;
	private long size;
	private String format;
	
	public InputStream getFile() {
		return file;
	}
	public void setFile(InputStream file) {
		this.file = file;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}
	
	public String getFileString() throws IOException {
		StringWriter writer = new StringWriter();
		IOUtils.copy(this.file, writer, "UTF-8");
		return writer.toString();
	}
	
}