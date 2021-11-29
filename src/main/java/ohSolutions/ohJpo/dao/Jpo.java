/*
 * PPO v1.8.8
 * Author		: Oscar Huertas
 * Date			: 2017-09-27
 * Update date	: 2018-07-11 - v1.7.0
 * Update date	: 2019-03-13 - v1.8.7
 * Description	: Main class that read the request params to instance a table or procedure for mapping
 * Description	: Adding log4j2
 * */
package ohSolutions.ohJpo.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
//import java.util.Optional;
import java.util.Properties;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

//import com.microsoft.azure.functions.HttpRequestMessage;

public class Jpo {

	final static Logger logger = LogManager.getLogger(Jpo.class);

	public static String ENV_JPO_HOME = "JPO_HOME";
	
	public static int ENTRADA = 1;
	public static int INPUT = 1;
	public static int ENTSAL = 2;
	public static int INOUTPUT = 2;
	public static int SALIDA = 3;
	public static int OUTPUT = 3;

	public static int CADENA = 1;
	public static int STRING = 1;
	public static int ENTERO = 2;
	public static int INTEGER = 2;
	public static int DECIMAL = 3;
	public static int TEXTO = 4;
	public static int TEXT = 4;
	public static int CARACTER = 5;
	public static int CHARACTER = 5;
	public static int RESULTADO = 6;
	public static int RESULT = 6;
	public static int FECHA = 7;
	public static int DATE = 7;
	public static int FECHAHORA = 8;
	public static int DATETIME = 8;
	public static int XML = 9;
	public static int BIGINTEGER = 10;
	
	public static String TYPE_SQLITE = "SQLITE";
	public static String TYPE_DB2 = "DB2";
	public static String TYPE_POSTGRESQL = "POSTGRESQL";
	public static String TYPE_SQLAZURE = "SQLAZURE";
	public static String TYPE_SQLSERVER = "SQLSERVER";
	public static String TYPE_MYSQL = "MYSQL";
	
	//public static int XML = 9;

	private String dataSource;
	private String type = "";		// SQLITE | POSTGRESQL | SQLSERVER
	private String url;	        	// SQLITE | ´D:\\MapeoFuente\\config\\´ | DEMAS BDS | localhost:5432
    private String db;		    	// SQLITE | ´NOMBRE´.db | DEMAS BDS ´NOMBRE´
    private String jdbc = null;	    // URL + DB , ex jdbc:sqlserver://10.10.10.37;databaseName=MILLENIUMDEV
    private String driver = null;	// URL + DB , ex jdbc:sqlserver://10.10.10.37;databaseName=MILLENIUMDEV
    private String scheme = "";
    private String username;		// SQLITE | ´VACIO´ | DEMAS BDS REQUERIDO
	private String password;	 	// SQLITE | ´VACIO´ | DEMAS BDS REQUERIDO
	
	/* ESQUEMA DEFECTO - Vacio no se adiciona*/

	private LinkedHashMap<String,Object> fields = new LinkedHashMap<String,Object>();
	private LinkedHashMap<String,Object> wheres = new LinkedHashMap<String,Object>();
	private LinkedHashMap<String,Object> multiple = new LinkedHashMap<String,Object>();
	
	public int hashConection;
	
	private JpoHttpRequest jpoRequest;
	
	public String getSourceInfo() {
		return type+"|"+url+"|"+db+"|"+scheme+"|"+username+"|"+password;
	}
	
	public LinkedHashMap<String, Object> getFields() {
		return fields;
	}

	public void setFields(LinkedHashMap<String, Object> fields) {
		this.fields = fields;
	}

	public LinkedHashMap<String, Object> getWheres() {
		return wheres;
	}

	public void setWheres(LinkedHashMap<String, Object> wheres) {
		this.wheres = wheres;
	}

	public LinkedHashMap<String, Object> getMultiple() {
		return multiple;
	}

	public void setMultiple(LinkedHashMap<String, Object> multiple) {
		this.multiple = multiple;
	}

	private Connection c = null;
	private boolean isAutocommit = false;

    public Jpo() throws Exception {
    	setConfigDB(null);
    	//isAutocommit = false;
    	//getConexion(false);
    }

    public Jpo(boolean autoCommit) throws Exception {
    	setConfigDB(null);
    	isAutocommit = autoCommit;
    	//getConexion(autoCommit);
    }

    public Jpo(Map<String, String> configDB) throws Exception {
    	setConfigDB(configDB);
    	//isAutocommit = false;
    	//getConexion(false);
    }

    public Jpo(HttpServletRequest request) throws Exception {
    	setConfigDB(null);
    	instanciarJpoRequest(request);
    	//getConexion(false);
    }
    /*
    public Jpo(Map<String, String> configDB, HttpRequestMessage<Optional<String>> request) throws Exception {
    	setConfigDB(configDB);
    	getConexion(false);
    	instanciarJpoRequest(request);
    }
    */
    public Jpo(HttpServletRequest request, String source) throws Exception {
    	setConfigDB(null, source, null);
    	instanciarJpoRequest(request);
    	//getConexion(false);
    }
    
    public Jpo(HttpServletRequest request, String source, String propertiesFile) throws Exception {
    	setConfigDB(null, source, propertiesFile);
    	instanciarJpoRequest(request);
    	//getConexion(false);
    }
    
    public Jpo(Jpo parentJpo, String source, String propertiesFile) throws Exception {
    	setConfigDB(null, source, propertiesFile);
    	instanciarJpoParent(parentJpo);
    	//getConexion(false);
    }
    
	public Jpo(String source) throws Exception {
    	setConfigDB(null, source, null);
    	//getConexion(false);
    }
    
    public Jpo(String source, String propertieFile) throws Exception {
    	setConfigDB(null, source, propertieFile);
    	//getConexion(false);
    }
    
    public Jpo(String source, String propertieFile, String properties_folder) throws Exception {
    	setConfigDB(null, source, propertieFile);
    	readProp(source, propertieFile, properties_folder);
    	//getConexion(false);
    }
    
    public Jpo(String body, String source, String propertieFile, String properties_folder) throws Exception {
    	setConfigDB(null, source, propertieFile);
    	readProp(source, propertieFile, properties_folder);
    	JpoString(body);
    	//getConexion(false);
    }
    
    public Jpo(Object body) throws Exception {
    	setConfigDB(null);
    	instanciarJpoRequest(body);
    	//getConexion(false);
    }
    
    public void JpoString(String body) throws Exception {
    	//setConfigDB(null);
		if(body != null && body.length()>0) {
			JSONObject prefixes = new JSONObject(body);
			loadDataFromBody(prefixes);
		}
    	//getConexion(false);
    }

    public Jpo(HttpServletRequest request,boolean autoCommit) throws Exception {
    	setConfigDB(null);
    	//getConexion(autoCommit);
    	instanciarJpoRequest(request);
    	isAutocommit = autoCommit;
    }

    public Jpo(HttpServletRequest request,boolean autoCommit, Map<String, String> configDB) throws Exception {
    	setConfigDB(configDB);
    	//getConexion(autoCommit);
    	instanciarJpoRequest(request);
    	isAutocommit = autoCommit;
    }
    
    public Jpo(Object body,boolean autoCommit) throws Exception {
    	setConfigDB(null);
    	instanciarJpoRequest(body);
    	//getConexion(autoCommit);
    	isAutocommit = autoCommit;
    }

    public Jpo(Object body,boolean autoCommit, Map<String, String> configDB) throws Exception {
    	setConfigDB(configDB);
    	instanciarJpoRequest(body);
    	//getConexion(autoCommit);
    	isAutocommit = autoCommit;
    }
    
    private void setConfigDB(Map<String, String> campos) throws Exception {
    	setConfigDB(campos, null, null);
    }
    
    private void setConfigDB(Map<String, String> campos, String source, String propertiesFile) throws Exception {

    	if(campos == null) {

            readProp(source, propertiesFile);

        } else {

			if(campos.get("type") != null) {
				this.type = campos.get("type");
			}
			if(campos.get("url") != null) {
				this.url = campos.get("url");
			}
            if(campos.get("db") != null) {
                this.db = campos.get("db");
            }
            if(campos.get("scheme") != null) {
                this.scheme = campos.get("scheme");
            }
            if(campos.get("username") != null) {
				this.username = campos.get("username");
			}
			if(campos.get("password") != null) {
				this.password = campos.get("password");
			}

		}
    	
    	this.hashConection = (this.type+this.url+this.db+this.scheme+this.username+this.password).hashCode();
    	
    }

	private String getURL(String tipo2) {
        if(this.jdbc != null){
            return this.jdbc;
        }
		if(type.equals(Jpo.TYPE_SQLITE)) {
			return "jdbc:sqlite:"+url+"\\\\"+ db;
		} else if(type.equals(Jpo.TYPE_POSTGRESQL)) {
			return "jdbc:postgresql://"+url+"/"+ db;
		} else if(type.equals(Jpo.TYPE_SQLSERVER)) {
			return "jdbc:sqlserver://"+url+";databaseName="+ db +";";
		} else if(type.equals(Jpo.TYPE_SQLAZURE)) {
			return "jdbc:sqlserver://"+url+":1433;database="+db+";user="+username+";password="+password+";encrypt=true;hostNameInCertificate=*.database.windows.net;loginTimeout=30;";
		} else if(type.equals(Jpo.TYPE_MYSQL)) {
			return "jdbc:mysql://"+url+"/"+ db;
		} else {
			return "";
		}
	}

	private String getNombreClase(){
        if(this.driver != null){
            return this.driver;
        }
		if(type.equals(Jpo.TYPE_SQLITE)) {
			return "org.sqlite.JDBC";
		} else if(type.equals(Jpo.TYPE_POSTGRESQL)) {
			return "org.postgresql.Driver";
		} else if(type.equals(Jpo.TYPE_SQLSERVER)) {
			return "com.microsoft.sqlserver.jdbc.SQLServerDriver";
		} else if(type.equals(Jpo.TYPE_SQLAZURE)) {
			return "com.microsoft.sqlserver.jdbc.SQLServerDriver";
		} else if(type.equals(Jpo.TYPE_MYSQL)) {
			return "com.mysql.jdbc.Driver";
		} else {
			return "";
		}
	}
	
	public Connection getConexion() throws Exception {
		//System.out.println("getConexion");
		//System.out.println(c);
		if(c == null) {
			getConexion(isAutocommit);
		}
		return c;
	}

	public Connection getConexion(boolean autoCommit) throws Exception {

		try {
			//System.out.println("conectando "+this.getSourceInfo()+" | "+this.hashConection);
			Class.forName(getNombreClase());
			if(type.equals(Jpo.TYPE_SQLITE)){
				c = DriverManager.getConnection(getURL(type));
			}
			if(type.equals(Jpo.TYPE_DB2) || type.equals(Jpo.TYPE_SQLSERVER) || type.equals(Jpo.TYPE_POSTGRESQL) || type.equals(Jpo.TYPE_MYSQL) || this.type.equals("")){
				c = DriverManager.getConnection(getURL(type), username, password);
			}
			if(type.equals(Jpo.TYPE_SQLAZURE)){
				c = DriverManager.getConnection(getURL(type));
			}
			
            if(this.jdbc != null){
            	logger.debug("Conectado a '"+ this.jdbc +"' correctamente");
            } else {
            	logger.debug("Conectado a '"+ db +"' correctamente en : "+url);
            }

			if(type.equals(Jpo.TYPE_POSTGRESQL) || type.equals(Jpo.TYPE_MYSQL)){
				c.setAutoCommit(false);
			} else {
				c.setAutoCommit(autoCommit);
			}
		} catch ( Exception e ) {
			logger.error("type+\"|\"+url+\"|\"+db+\"|\"+scheme+\"|\"+username+\"|\"+password");
			logger.error(this.getSourceInfo());
			e.printStackTrace();
			throw new Exception("ohSolutions.Jpo - Error connecting datasource "+ this.dataSource);
		}
		return c;
	}

	public void autoCommit(boolean autoCommit) throws SQLException{
		if(c!=null){
			c.setAutoCommit(autoCommit);
		}
	}

	private void setSource(Properties defaultProps, String prefix) {
		String _url 		= prefix+".url";
		String _type 		= prefix+".type";
		String _db 			= prefix+".db";
		String _scheme 		= prefix+".scheme";
		String _username 	= prefix+".username";
		String _password 	= prefix+".password";
		this.url = this.getEnvValue(_url) != null ? this.getEnvValue(_url) : defaultProps.getProperty(_url);
        this.type = this.getEnvValue(_type) != null ? this.getEnvValue(_type) : defaultProps.getProperty(_type);
        this.db = this.getEnvValue(_db) != null ? this.getEnvValue(_db) : defaultProps.getProperty(_db);
        this.scheme = this.getEnvValue(_scheme) != null ? this.getEnvValue(_scheme) : defaultProps.getProperty(_scheme);
        if(this.scheme == null){
            this.scheme = "";
        }
        this.username = this.getEnvValue(_username) != null ? this.getEnvValue(_username) : defaultProps.getProperty(_username);
        this.password = this.getEnvValue(_password) != null ? this.getEnvValue(_password) : defaultProps.getProperty(_password);
	}
	
	private String getEnvValue(String value) {
		return System.getenv().get(value.replace(".", "_").toUpperCase());
	}
	
	private void readProp(String source, String propertiesFile) throws Exception {
		readProp(source, propertiesFile, null);
	}
	
	private void readProp(String source, String propertiesFile, String properties_folder) throws Exception {
		Properties defaultProps;
		
		if(properties_folder == null) {
			defaultProps = JpoUtil.getProperties(propertiesFile);
		} else {
			defaultProps = JpoUtil.getProperties(propertiesFile, properties_folder);
		}
		
		String defaultSource = defaultProps.getProperty("jpo.ds.source");
		if(source != null && source.length()>0) { // Si cargan desde clase un source jpo.{source}. | @JpoClass( source = "dsprod" )
			this.dataSource = source;
			setSource(defaultProps, "jpo."+source);
		} else if(defaultSource != null) { // Si tiene un source mapeado desde el mism properties | jpo.ds.source = dsprod
			this.dataSource = defaultSource;
			this.jdbc = this.getEnvValue(defaultSource+".url") != null ? this.getEnvValue(defaultSource+".url") : defaultProps.getProperty(defaultSource+".url");
            this.driver = this.getEnvValue(defaultSource+".driverClassName") != null ? this.getEnvValue(defaultSource+".driverClassName") : defaultProps.getProperty(defaultSource+".driverClassName");
            this.username = this.getEnvValue(defaultSource+".username") != null ? this.getEnvValue(defaultSource+".username") : defaultProps.getProperty(defaultSource+".username");
            this.password = this.getEnvValue(defaultSource+".password") != null ? this.getEnvValue(defaultSource+".password") : defaultProps.getProperty(defaultSource+".password");
		} else { // Configuración basica del los sources
			this.dataSource = "ds";
			setSource(defaultProps, "jpo.ds");
		}
	}
	
	private void instanciarJpoRequest(Object body) throws Exception {
		if(body != null) {
			loadDataFromBody(body);
		}
	}
	
	private void instanciarJpoRequest(HttpServletRequest request) throws Exception {

		if(request != null) {

			if(request.getHeader("jpoNoMappingBody") == null || (request.getHeader("jpoNoMappingBody") != null && request.getHeader("jpoNoMappingBody").equals("false"))) {
				String body = JpoUtil.getBody(request);
				if(body != null && body.length()>0) {
					JSONObject prefixes = new JSONObject(body);
					loadDataFromBody(prefixes);
				}
			}
			
			if(request.getHeader("jpoNoMappingBody") != null && request.getHeader("jpoNoMappingBody").equals("true")) {
				String jpoData = request.getParameter("jpoData");
				if(jpoData != null && jpoData.length()>0) {
					JSONObject prefixes = new JSONObject(jpoData);
					loadDataFromBody(prefixes);
				}
			}
			
			loadDataFromParams(request);
			
		}

	}
	
    private void instanciarJpoParent(Jpo parentJpo) {
    	this.setFields(parentJpo.getFields());
    	this.setWheres(parentJpo.getWheres());
    	this.setMultiple(parentJpo.getMultiple());
	}
	/*
	private void instanciarJpoRequest(HttpRequestMessage<Optional<String>> request) throws Exception {
		if(request != null) {			
			if(request.getHeaders().get("jponomappingbody") == null || (request.getHeaders().get("jponomappingbody") != null && request.getHeaders().get("jponomappingbody").equals("false"))) {
				String body = request.getBody().get();
				if(body != null && body.length()>0) {
					JSONObject prefixes = new JSONObject(body);
					loadDataFromBody(prefixes);
				}
			}
			if(request.getHeaders().get("jponomappingbody") != null && request.getHeaders().get("jponomappingbody").equals("true")) {
		        String body = request.getQueryParameters().get("jpoData");
		        if(body != null && body.length()>0) {
		            JSONObject prefixes = new JSONObject(body);
		            loadDataFromBody(prefixes);
		        }
	        }
			//loadDataFromParams(request);
		}

	}
    */
		private void loadDataFromBody(Object body) {
			
			JSONObject prefixes = (JSONObject) body;
			
			Iterator<String> prefixes_keys = prefixes.keys();
	
			while(prefixes_keys.hasNext() ) {
				String prefix_key = prefixes_keys.next();
				
			    if (prefixes.get(prefix_key) instanceof JSONObject) {
			    
					JSONObject data = (JSONObject) prefixes.get(prefix_key);
					
			    	if(data.has("F")) { // Has fields
			    		
			    		JSONObject fields = (JSONObject) data.get("F");
			    		Iterator<String> fields_leys = fields.keys();
			    		
			    		while(fields_leys.hasNext() ) {
						    String field_key = (String)fields_leys.next();
						    addField(prefix_key, field_key, "" + fields.get(field_key));
			    		}
			    		
			    	}
			    	
			    	if(data.has("M")) { // Has multiple fields
			    		
			    		JSONObject rows = (JSONObject) data.get("M");
			    		Iterator<String> rows_keys = rows.keys();
			    		
			    		while(rows_keys.hasNext() ) {
			    			
			    			String row_key = (String)rows_keys.next();
			    			
			    			if (rows.get(row_key) instanceof JSONObject) {
			    				
					    		JSONObject fields = (JSONObject) rows.get(row_key);
					    		Iterator<String> fields_leys = fields.keys();
					    		
					    		while(fields_leys.hasNext() ) {
								    String field_key = (String)fields_leys.next();
								    addFieldMultiple(prefix_key, row_key, field_key, "" + fields.get(field_key));
					    		}
			    				
			    			}
			    			
			    		}
			    		
			    	}
			    	
			    	// {"IPR":{"W":{"proyect_id":"1"}}}
			    	if(data.has("W")) { // Has wheres
			    		
			    		JSONObject wheres = (JSONObject) data.get("W");
			    		Iterator<String> wheres_leys = wheres.keys();
			    		
			    		while(wheres_leys.hasNext() ) {
						    String where_key = (String)wheres_leys.next();
						    addWhere(prefix_key, "W", where_key, "" + wheres.get(where_key));
			    		}
			    		
			    	}
			    	
			    	if(data.has("I")) { // Has where in
			    		
			    		JSONObject wheres = (JSONObject) data.get("I");
			    		Iterator<String> wheres_leys = wheres.keys();
			    		
			    		while(wheres_leys.hasNext() ) {
						    String where_key = (String)wheres_leys.next();
						    addWhere(prefix_key, "I", where_key, "" + wheres.get(where_key));
			    		}
			    		
			    	}
			    	
			    }
			}
			
		}

		@SuppressWarnings("rawtypes")
		private void loadDataFromParams(HttpServletRequest request) throws Exception {

			try {
				
				Enumeration keys = request.getParameterNames();
				if(keys != null) {
					while(keys.hasMoreElements()){
						
						String llave = (String)keys.nextElement();
		
						//String value = new String(request.getParameter(llave).getBytes("ISO-8859-1"), "UTF-8"); // USAR PARA CONSULTAS GET
						String value = (String) request.getParameter(llave); // USAR PARA CONSULTAS POST
		
						String[] valores = llave.split("_");
		
						if(valores.length>=3 && valores[0].length()==3 && valores[1].length()==1){
		
							String Referencia = valores[0];
							String Tipo = valores[1];
							String llaveOriginal = llave.substring(6);
		
							if(Tipo.equals("M")){
								String[] Indices = llaveOriginal.split("_");
								String indice = Indices[0]; // Nro
								llaveOriginal = llaveOriginal.substring((Indices[0].length()+1));
								addFieldMultiple(Referencia, indice, llaveOriginal, value);
							} else {
								addWhere(Referencia, Tipo, llaveOriginal, value);
							}
		
						} else if(valores.length>=2 && valores[0].length()==3){
							String Referencia = valores[0];
							String llaveOriginal = llave.substring(4);
							addField(Referencia, llaveOriginal, value);
						}
		
					}
				}
	
			} catch (Exception e) {
				e.printStackTrace();
				throw new Exception("ohSolutions.Jpo - Error parsing data");
			}
			
		}
		
			@SuppressWarnings("unchecked")
			private void addField(String prefix, String key, String value) {
				LinkedHashMap<String,String> listaParametros = null;
				if(fields.get(prefix) == null){
					listaParametros = new LinkedHashMap<String,String>();
				} else {
					listaParametros = (LinkedHashMap<String, String>) fields.get(prefix);
				}
				listaParametros.put(key, value);
				fields.put(prefix, listaParametros);
			}
			
			@SuppressWarnings("unchecked")
			private void addWhere(String prefix_key, String type, String where_key, String value) {
				LinkedHashMap<String,Object> listaTipos = null;
				if(wheres.get(prefix_key) == null){
					listaTipos = new LinkedHashMap<String,Object>();
				} else {
					listaTipos = (LinkedHashMap<String, Object>) wheres.get(prefix_key);
				}
		
				LinkedHashMap<String,String> listaParametros = null;
				if(listaTipos.get(type) == null){
					listaParametros = new LinkedHashMap<String,String>();
				} else {
					listaParametros = (LinkedHashMap<String, String>) listaTipos.get(type);
				}
				listaParametros.put(where_key, value);
				listaTipos.put(type, listaParametros);
				wheres.put(prefix_key, listaTipos);
			}
			
			@SuppressWarnings("unchecked")
			private void addFieldMultiple(String prefix_key, String type, String where_key, String value) {
				LinkedHashMap<String,Object> listaTipos = null;
				if(multiple.get(prefix_key) == null){
					listaTipos = new LinkedHashMap<String,Object>();
				} else {
					listaTipos = (LinkedHashMap<String, Object>) multiple.get(prefix_key);
				}
		
				TreeMap<String, String> listaParametros = null;
				if(listaTipos.get(type) == null){
					listaParametros = new TreeMap<String, String>();
				} else {
					listaParametros = (TreeMap<String, String>) listaTipos.get(type);
				}
				listaParametros.put(where_key, value);
				listaTipos.put(type, listaParametros);
				multiple.put(prefix_key, listaTipos);
			}
		

	@SuppressWarnings("deprecation")
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
	}
	
	private String getScheme() {
		return scheme.equals("")?"": scheme +".";
	}

	public Tabla tabla(String table, String prefix){
		return new Tabla(this, type, getScheme() + table, fields.get(prefix), multiple.get(prefix), wheres.get(prefix));
	}

	public Tabla tabla(String table){
		return new Tabla(this, type, getScheme() + table);
	}

	public Tablas tablas(String[] nombreTablas,String[] referencias){

		Object[] datoTipos = new Object[nombreTablas.length];

		for(int i = 0;i<nombreTablas.length;i++){
			nombreTablas[i] = getScheme() + nombreTablas[i];
			String referencia = referencias[i];
			datoTipos[i] = wheres.get(referencia);
		}

		return new Tablas(this, nombreTablas, referencias, datoTipos);
	}

	public boolean sql(String query) throws Exception{
		ConexionBD conexionBD = new ConexionBD(this);
		long time_init = System.currentTimeMillis();
		boolean response = conexionBD.query(query);
		logger.info("t<"+(System.currentTimeMillis() - time_init)+"> "+query);
		return response;
	}

	public Jpo commitear() throws SQLException{
		if(c != null && !c.isClosed()){
			if(!c.getAutoCommit()){
				c.commit();
			}
		}
		return this;
	}
	
	public Jpo commit() throws SQLException {
		if(c != null && !c.isClosed()){
			if(!c.getAutoCommit()){
				c.commit();
			}
		}
		return this;
	}
	
	/**
	 * Estaba escrito mal
	 *
	 * @deprecated use {@link #new()} instead.  
	 */
	@Deprecated
	public Jpo roJllback() throws SQLException {
		if(c != null && !c.isClosed() && !c.getAutoCommit()){
			c.rollback();
		}
		return this;
	}
	
	public Jpo rollback() throws SQLException {
		if(c != null && !c.isClosed() && !c.getAutoCommit()){
			c.rollback();
		}
		return this;
	}
	
	public void finalizar() throws SQLException {
		if(c != null && !c.isClosed()){
			//System.out.println("Desconectando "+this.getSourceInfo()+" | "+this.hashConection);
			logger.debug("Desconentando de "+ db +"' correctamente en : "+url);
			c.close();
		}
	}

	public Procedimiento procedimiento(String nombreProcedimiento) {
		return new Procedimiento(this, nombreProcedimiento, null);
	}

	public Procedimiento procedimiento(String nombreProcedimiento, String Referencia) {
		return new Procedimiento(this, nombreProcedimiento, fields.get(Referencia));
	}
	
	public Procedure procedure(String nombreProcedimiento) {
		return new Procedure(this, nombreProcedimiento, null);
	}

	public Procedure procedure(String nombreProcedimiento, String Referencia) {
		return new Procedure(this, nombreProcedimiento, fields.get(Referencia));
	}

	public Consulta consulta(String string) {
		return new Consulta(this, string);
	}

	@SuppressWarnings("unchecked")
	public String getData(String prefix, String field) {
		if(fields.get(prefix) == null){
			return null;
		} else {
			LinkedHashMap<String, String> listaParametros = (LinkedHashMap<String, String>) fields.get(prefix);
			return listaParametros.get(field);
		}
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, String> getObject(String prefix) {
		if(fields.get(prefix) == null){
			return null;
		} else {
			return (LinkedHashMap<String, String>) fields.get(prefix);
		}
	}
	
	public void setData(String prefix, String field, String value) {
		addField(prefix, field, value);
	}
	
	@SuppressWarnings("unchecked")
	public void removeData(String prefix, String field) {
		if(fields.get(prefix) != null){
			LinkedHashMap<String, String> listaParametros = (LinkedHashMap<String, String>) fields.get(prefix);
			listaParametros.remove(field);
			fields.put(field, listaParametros);
		}
	}
	
	@SuppressWarnings("unchecked")
	public String getWhere(String prefix, String field) {
		if(wheres.get(prefix) == null){
			return null;
		} else {
			LinkedHashMap<String, Object> listaTipos = (LinkedHashMap<String, Object>) wheres.get(prefix);
			if(listaTipos.get("W") == null){
				return null;
			} else {
				LinkedHashMap<String, String> listaParametros = (LinkedHashMap<String, String>) listaTipos.get("W");
				return listaParametros.get(field);
			}
		}
	}

	public void setWhere(String prefix, String field, String value) {
		addWhere(prefix, "W", field, value);
	}
	
	@SuppressWarnings("unchecked")
	public void removeWhere(String prefix, String field) {
		if(wheres.get(prefix) != null){
			LinkedHashMap<String, Object> listaTipos = (LinkedHashMap<String, Object>) wheres.get(prefix);
			if(listaTipos.get("W") != null){
				LinkedHashMap<String, String> listaParametros = (LinkedHashMap<String, String>) listaTipos.get("W");
				listaParametros.remove(field);
				listaTipos.put("W", listaParametros);
				wheres.put(field, listaTipos);
			}
		}
	}

	public JpoHttpRequest getJpoRequest() {
		return jpoRequest;
	}

	public void setJpoRequest(JpoHttpRequest jpoRequest) {
		this.jpoRequest = jpoRequest;
	}
	
}