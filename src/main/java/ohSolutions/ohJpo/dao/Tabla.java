/*
 * PPO v1.8.7
 * Author		: Oscar Huertas
 * Date			: 2017-09-27
 * Update date	: 2018-07-11 - v1.7.0
 * Update date	: 2019-03-13 - v1.8.7
 * Description	: Class to manage simple or joined tables querys
 * Description	: Adding log4j2
 * */
package ohSolutions.ohJpo.dao;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Tabla {

	final static Logger logger = LogManager.getLogger(Tabla.class);
	
	private static String Tipo_Where = "W";
	//private static String Tipo_WhereLike = "L";
	private static String Tipo_WhereIN = "I";
	
	private String nombreTabla;
	private LinkedHashMap<String,String> data = new LinkedHashMap<String,String>();
	private LinkedHashMap<String,Object> dataTipo = new LinkedHashMap<String,Object>();
	private Map<String,Object> dataMultiple = new TreeMap<String,Object>();
	private ConexionBD conexionBD = null;
	
	private String dondePersonalizado = "";
	private String ordenadoPor = "";
	private int limite = 0;
	
	private String tI = "\"";
	private String tW = null;
	
	private String type = "";
	
	@SuppressWarnings("unchecked")
	public Tabla(Jpo jpo,String type, String nombreTabla,Object data,Object dataMultiple,Object dataTipo) {
		conexionBD = new ConexionBD(jpo);
		this.nombreTabla = nombreTabla;
		if(data!=null){
			this.data = (LinkedHashMap<String, String>) data;
		}
		if(dataTipo!=null){
			this.dataTipo = (LinkedHashMap<String, Object>) dataTipo;
		}
		if(dataMultiple!=null){
			this.dataMultiple = (TreeMap<String, Object>) dataMultiple;
		}
		this.type = type;
	}
	
	public Tabla(Jpo jpo, String type, String nombreTabla) {
		conexionBD = new ConexionBD(jpo);
		this.nombreTabla = nombreTabla;
		this.type = type;
	}
	
	public Object editar() throws Exception{
		String query = "UPDATE "+nombreTabla+" SET "+getValuesUpdate()+" "+getWheres();
		return execute(query, "dml");
	}
	
	public Object eliminar() throws Exception{
		String query = "DELETE FROM "+nombreTabla+" "+getWheres();
		return execute(query, "dml");
	}
	
	@SuppressWarnings("unchecked")
	public Object registrarMultiple() throws Exception{
		String[] querys = new String[dataMultiple.size()];
		int contador = 0;
		for (Entry<String, Object> entry : dataMultiple.entrySet()) {
			querys[contador] = "INSERT INTO "+nombreTabla+" "+getValuesInsert((TreeMap<String, String>) entry.getValue());
	    	contador++;
		}
		return execute(querys);
	}
	
	public Object registrarMultiple(TreeMap<String, Object> dataMultiple) throws Exception{
		this.setDataMultiple(dataMultiple);
		return registrarMultiple();
	}
	
	public Object registrar() throws Exception{
		String query = "INSERT INTO "+nombreTabla+" "+getValuesInsertHM(data);
		return execute(query, "dml");
	}

	public Object obtener(String valores) throws Exception{
		String query = "SELECT "+valores+" FROM "+nombreTabla+" "+ getWheres() +" "+ getOrden();
		return execute(query, "obtener");
	}
	
	@SuppressWarnings("unchecked")
	public int max(String campo) throws Exception{
		String com = (type.equals("POSTGRESQL"))?"\"":"'";
		String query = "SELECT MAX("+campo+") "+com+"maximo"+com+" FROM "+nombreTabla+" "+ getWheres();
		Map<String, Object> resultado = (Map<String, Object>) execute(query, "obtener");
		return Integer.parseInt(""+resultado.get("maximo"));
	}
	
	public Object obtenerL(String valores) throws Exception{
		String query = "SELECT "+valores+" FROM "+nombreTabla+" "+ getWheres() +" "+ getOrden();
		return execute(query, "obtenerL");
	}
	
	public Tabla donde(String where) throws Exception{
		this.dondePersonalizado = where;
		return this;
	}	
	
	public Tabla ordenadoPor(String ordenadoPor) throws Exception{
		this.ordenadoPor = ordenadoPor;
		return this;
	}
	
	public Tabla limite(int limite) throws Exception{
		this.limite = limite;
		return this;
	}
	
	public Object seleccionar(String valores) throws Exception{
		String query = "SELECT "+valores+" FROM "+nombreTabla+" "+getWheres() +" "+ getOrden()+" "+ getLimite();
		return execute(query, "seleccionar");
	}
	
	public Object seleccionarL(String valores) throws Exception{
		String query = "SELECT "+valores+" FROM "+nombreTabla+" "+getWheres() +" "+ getOrden()+" "+ getLimite();
		return execute(query, "seleccionarL");
	}
	
	public int contar() throws Exception{
		String query = "SELECT COUNT(*) AS COUNT FROM "+nombreTabla+" "+getWheres();
		return (int) execute(query, "contarRegistro");
	}
	
	private Object execute(String query, String type) throws Exception {
		long time_init = System.currentTimeMillis();
		Object response = null;
		if(type.equals("dml")) {
			response = conexionBD.dml(query);
		} else if(type.equals("obtener")) {
			response = conexionBD.obtener(query);
		} else if(type.equals("obtenerL")) {
			response = conexionBD.obtenerL(query);
		} else if(type.equals("seleccionar")) {
			response = conexionBD.seleccionar(query);
		} else if(type.equals("seleccionarL")) {
			response = conexionBD.seleccionarL(query);
		} else if(type.equals("contarRegistro")) {
			response = conexionBD.contarRegistro(query);
		}
		logger.info("t<"+(System.currentTimeMillis() - time_init)+"> "+query);
		return response;
	}
	
	private Object execute(String[] querys) throws Exception {
		long time_init = System.currentTimeMillis();
		logger.info("t<"+(System.currentTimeMillis() - time_init)+"> "+querys);
		Object response = conexionBD.dml(querys);
		return response;
	}
	
	private String getValuesUpdate(){
		if(data.size()>0){
			
			StringBuilder campos = new StringBuilder("");
			
			for (Entry<String, String> entry : data.entrySet()) {
				String campo = entry.getKey();
					campo = ((tI!=null)?tI:"")+campo+((tI!=null)?tI:"");
		    	String valor = entry.getValue();
		    	if(valor == null || valor.equals("IS_NULL")){
		    		campos.append(campo+"= NULL, ");
		    	} else if(valor.length()>0 && valor.charAt(0) == '\'') {
		    		campos.append(campo+"="+valor.substring(1)+", ");
		    	} else {
		    		campos.append(campo+"='"+valor+"', ");
		    	}
		    	
			}
			
			String camposString = campos.toString();
			
			return camposString.substring(0,camposString.length()-2);
		} else {
			return "";
		}
	}
	
	private String getValuesInsert(TreeMap<String,String> data){
		if(data.size()>0){
			StringBuilder campos = new StringBuilder("");
			StringBuilder valores = new StringBuilder("");
			
			for (Entry<String, String> entry : data.entrySet()) {
				String campo = entry.getKey();
		    	String valor = entry.getValue();
		    	campos.append(((tI!=null)?tI:"")+campo+((tI!=null)?tI:"")+",");
		    	
		    	if(valor == null || valor.equals("IS_NULL")){
		    		valores.append("null,");
		    	} else if(valor.length()>0 && valor.charAt(0) == '\'') {
		    		valores.append(valor.substring(1)+",");
		    	} else {
		    		valores.append("'"+valor+"',");
		    	}
		    	
			}
			String camposString = campos.toString();
			String valorString = valores.toString();
			camposString = "("+camposString.substring(0,camposString.length()-1)+")";
			valorString = "("+valorString.substring(0,valorString.length()-1)+")";
			return camposString+" VALUES "+valorString;
		} else {
			return "";
		}
	}
	
	private String getValuesInsertHM(LinkedHashMap<String,String> data){
		if(data.size()>0){
			StringBuilder campos = new StringBuilder("");
			StringBuilder valores = new StringBuilder("");
			
			for (Entry<String, String> entry : data.entrySet()) {
				String campo = entry.getKey();
		    	String valor = entry.getValue();
		    	//valor = valor.replaceAll("'", "''");
		    	campos.append(((tI!=null)?tI:"")+campo+((tI!=null)?tI:"")+",");
		    	//valores.append("'"+valor+"',");
		    	
		    	if(valor == null || valor.equals("IS_NULL")){
		    		valores.append("null,");
		    	} else if(valor.length()>0 && valor.charAt(0) == '\'') {
		    		valores.append(valor.substring(1)+",");
		    	} else {
		    		valores.append("'"+valor+"',");
		    	}
		    	
			}
			String camposString = campos.toString();
			String valorString = valores.toString();
			camposString = "("+camposString.substring(0,camposString.length()-1)+")";
			valorString = "("+valorString.substring(0,valorString.length()-1)+")";
			return camposString+" VALUES "+valorString;
		} else {
			return "";
		}
	}
	
	private String getOrden(){
		if(ordenadoPor.length()>0){
			return "ORDER BY "+ordenadoPor;
		} else {
			return "";
		}
	}
	
	private String getLimite(){
		if(limite>0){
			return "LIMIT "+limite;
		} else {
			return "";
		}
	}
	
	private String getFormatoCS(String valor){
		String resultado = "";
		String[] valores = valor.split(",");
		if(valores.length>0){
			for(int i = 0;i <valores.length; i++){
				resultado += "'"+valores[i]+"',";
			}
			resultado = resultado.substring(0,resultado.length()-1);
		}
		return resultado;
	}
		
	@SuppressWarnings("unchecked")
	private String getWheres(){
		String resultado = "";
		
		Object tipoWheres = dataTipo.get(Tipo_Where);
		Object tipoWheresIn = dataTipo.get(Tipo_WhereIN);
		
		if(tipoWheres!=null || tipoWheresIn!=null){
			resultado += "WHERE ";
		}
			
		if(tipoWheres!=null){
			StringBuilder where = new StringBuilder("");
			Map<String,String> wheres = (Map<String, String>) tipoWheres;
			for (Entry<String, String> entry : wheres.entrySet()) {
				String campo = entry.getKey();
		    	String valor = entry.getValue();
		    	
		    	String campoFor = ((tW!=null)?tW:"")+campo+((tW!=null)?tW:"");
		    	
		    	if(valor == null || valor.equals("IS_NULL")){
		    		where.append(campoFor+" IS NULL AND ");
		    	} else if(valor.length()>0 && valor.charAt(0) == '\'') {
		    		where.append(campoFor+" = "+valor.substring(1)+" AND ");
		    	} else {
		    		where.append(campoFor+" = '"+valor+"' AND ");
		    	}
		    	
			}
			String whereStr = where.toString();
			resultado += whereStr.substring(0,whereStr.length()-5);
		}
		
		if(tipoWheresIn!=null){
			StringBuilder where = new StringBuilder("");
			Map<String,String> wheres = (Map<String, String>) tipoWheresIn;
			for (Entry<String, String> entry : wheres.entrySet()) {
				String campo = entry.getKey();
		    	String valor = entry.getValue();
		    	where.append(((tW!=null)?tW:"")+campo+((tW!=null)?tW:"")+" IN ("+getFormatoCS(valor)+") AND ");
			}
			String whereStr = where.toString();
			resultado += whereStr.substring(0,whereStr.length()-5);
		}
		
		if(dondePersonalizado.length()>0){
			if(resultado.length()>0){
				resultado += dondePersonalizado;
			} else {
				resultado += "WHERE "+dondePersonalizado;;
			}
		}
		
		return resultado;
	}
	
	public String getData(String campo){
		if(data.get(campo)!=null){
			return data.get(campo);
		}
		return "";
	}
	
	public void setData(String campo,String valor){
		data.put(campo, valor);
	}
	
	public void setDataMultiple(TreeMap<String, Object> dataMultiple){
		this.dataMultiple = dataMultiple;
	}
	
	public void removeData(String campo){
		data.remove(campo);
	}
	
	@SuppressWarnings("unchecked")
	public String getDataWhere(String campo){
		Object tipoWheres = dataTipo.get(Tipo_Where);
		if(tipoWheres!=null){
			Map<String,String> wheres = (Map<String, String>) tipoWheres;
			if(wheres.get(campo)!=null){
				return wheres.get(campo);
			}
		}
		return "";
	}
	
	@SuppressWarnings("unchecked")
	public void setDataWhere(String campo,String valor){
		Object tipoWheres = dataTipo.get(Tipo_Where);
		if(tipoWheres==null){
			dataTipo.put(Tipo_Where, new LinkedHashMap<String,Object>());
			tipoWheres = dataTipo.get(Tipo_Where);
		}
		if(tipoWheres!=null){
			Map<String,String> wheres = (Map<String, String>) tipoWheres;
			wheres.put(campo, valor);
			dataTipo.put("W", wheres);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void removeDataWhere(String campo){
		Object tipoWheres = dataTipo.get(Tipo_Where);
		if(tipoWheres!=null){
			Map<String,String> wheres = (Map<String, String>) tipoWheres;
			wheres.remove(campo);
			dataTipo.put("W", wheres);
		}
	}
	
}