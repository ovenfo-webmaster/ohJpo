/*
 * PPO v1.7.0
 * Author		: Oscar Huertas
 * Date			: 2018-07-11
 * Description	: Util class to map string to json, read the body to json
 * */
package ohSolutions.ohJpo.dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import ohSolutions.ohJpo.bean.FileReq;

public class JpoUtil {
	
	// string to object Map<String, Object>
	public static Map<String, Object> bsGetMap(String data) throws IOException {
		return new Gson().fromJson(data, new TypeToken<Map<String, Object>>(){}.getType());
	}
	
	// request to string
	public static String getBody(HttpServletRequest request) throws IOException {
	   	StringBuilder buffer = new StringBuilder();
	    BufferedReader reader = request.getReader();
	    String line;
	    while ((line = reader.readLine()) != null) {
	        buffer.append(line);
	    }
	    return buffer.toString();
	}
	
	public static Properties getProperties() throws Exception {
		return getProperties(null);
	}
	
	public static Properties getProperties(String propertiesFile) throws Exception {
		
		Properties defaultProps = new Properties();

		String fileName = (propertiesFile != null && propertiesFile.length() != 0)?propertiesFile:"application.properties";
		
		// 1. Reading file from system dir
		boolean isLoaded = false;
		
			try { 
				defaultProps.load(new FileInputStream(System.getProperty("user.dir")+File.separator+fileName));
				isLoaded = true;
			} catch (Exception ex){}
		
		// 2. Reading file from environment JPO_HOME
		if(!isLoaded) {
			String home = System.getenv(Jpo.ENV_JPO_HOME);
			if(home != null && home.length()>0) {
				try { 
					defaultProps.load(new FileInputStream(home+File.separator+fileName));
					isLoaded = true;
				} catch (Exception ex){}
			}
		}
		
		// 3. Reading local file
		if(!isLoaded) {
			try {
				defaultProps.load(new FileInputStream("./"+fileName));
				isLoaded = true;
			} catch (Exception ex){}
		}

		// 4. Reading from resource
		if(!isLoaded) {
			try {
				InputStream file = Jpo.class.getResourceAsStream("/"+fileName);
				defaultProps.load(file);
				isLoaded = true;
			} catch (Exception ex){}
		}
		
		if(!isLoaded) {
			throw new Exception("ohSolutions.Jpo - error reading properties");
		}

		return defaultProps;
		
	}
	
	public static Properties getProperties(String propertiesFile, String properties_folder) throws Exception {
		Properties defaultProps = new Properties();
		defaultProps.load(new FileInputStream(properties_folder+File.separator+((propertiesFile != null)?propertiesFile:"application.properties")));
		return defaultProps;
	}
	
	public static String getPropertie(String propertie) throws Exception { // Return the propertie of the Master
		return getProperties().getProperty(propertie);
	}
	
	public static String getPropertie(String from, String propertie) throws Exception { // Return the propertie of the Master
		return getProperties("application"+((from!=null)?from:"")+".properties").getProperty(propertie);
	}
	
	public static String getPropertieProyect(String propertie, String properties_folder) throws Exception {
		return getPropertieProyect(null, propertie, properties_folder);
	}
	
	public static String getPropertieProyect(String from, String propertie, String properties_folder) throws Exception { // Return the propertie of the Master
		if(properties_folder == null) {
			return getPropertie(from, propertie);
		} else {
			Properties defaultProps = getProperties("application"+((from!=null)?from:"")+".properties", properties_folder);
			return defaultProps.getProperty(propertie);
		}
	}
		
	public static void createFile(String source, String file, InputStream content) throws Exception {
		
		createFolder(source);
		OutputStream outputStream = null;

		try {
			outputStream =  new FileOutputStream(new File(source+File.separator+file));

			int read = 0;
			byte[] bytes = new byte[1024];

			while ((read = content.read(bytes)) != -1) {
				outputStream.write(bytes, 0, read);
			}
			outputStream.flush();
			outputStream.close();
			
		} catch (IOException e) {
			e.printStackTrace();
			throw new Exception("ohSolutions.JpoUtil - Error uploading File "+file+" in "+source);
		} finally {
			if (content != null) {
				try {
					content.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}
		
	}
	
	public static boolean deleteFile(String folder, String name) throws Exception {
		
    	try{
    		File file = new File(folder+File.separator+name);
    		if(file.exists()) {
    			if(file.delete()){
        			return true;
        		} else {
        			return false;
        		}
    		} else {
    			return true;
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
		 
    	return false;
		
	}
	
	public static void createFolder(String ruta) throws Exception{
			
    	try{
    		File theDir = new File(ruta);
    		if (!theDir.exists()) {
    	        theDir.mkdirs();
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    		throw new Exception("ohSolutions.JpoUtil - Error creating folder");
    	}
			
	}
	
	public static FileReq getFile(HttpServletRequest request, String name) throws IOException, ServletException {
		
		Part filePart = request.getPart(name);

		FileReq fileReq = null;
		
		if(filePart.getSubmittedFileName() != null) {
			fileReq = new FileReq();
			fileReq.setFile(filePart.getInputStream());
			fileReq.setName(Paths.get(filePart.getSubmittedFileName()).getFileName().toString());
			fileReq.setSize(filePart.getSize());
			String[] parts = fileReq.getName().split("\\.");
			fileReq.setFormat(parts[parts.length-1]);
		}

		return fileReq;
	}
	
	public static long getFileRequestSize(HttpServletRequest request, String name) throws IOException, ServletException {
		return request.getPart(name).getSize();
	}

}