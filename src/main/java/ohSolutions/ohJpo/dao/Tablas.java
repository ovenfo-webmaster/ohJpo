/*
 * PPO v1.8.7
 * Author		: Oscar Huertas
 * Date			: 2017-09-27
 * Update date	: 2019-03-13 - v1.8.7
 * Description	: Class to manage a list of registeres to insert in a table
 * Description	: Adding log4j2
 * */
package ohSolutions.ohJpo.dao;

import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Tablas {

	final static Logger logger = LogManager.getLogger(Tablas.class);
	
	private static String Tipo_Where = "W";
	//private static String Tipo_WhereLike = "L";
	//private static String Tipo_WhereIN = "I";
	
	private String[] nombreTablas;
	private String[] referencias;
	
	private Map<String, String> unirIzquierdaTablas = new HashMap<String, String>();
	
	private List<Map<String, Object>> datoTipos = new ArrayList<Map<String, Object>>();
	
	private ConexionBD conexionBD = null;
	
	private String dondePersonalizado = "";
	private String dondeUnir = "";
	
	private String ordenadoPor = "";
	private int limite = 0;
	
	@SuppressWarnings("unchecked")
	public Tablas(Jpo jpo,String[] nombreTablas,String[] referencias,Object[] datoTipos) {
		
		conexionBD = new ConexionBD(jpo);
		
		this.nombreTablas = nombreTablas;
		this.referencias = referencias;
		
		for(int i = 0;i<nombreTablas.length;i++){
			if(datoTipos[i] != null){
				this.datoTipos.add(i, (LinkedHashMap<String, Object>) datoTipos[i]);
			} else{
				this.datoTipos.add(i, null);
			}
		}

	}
	
	public Object obtener(String valores) throws Exception{
		String query = "SELECT "+valores+" FROM "+nombreTablas+" "+ getWheres() +" "+ getOrden();
		return execute(query, "obtener");
	}
		
	public Tablas donde(String where) throws Exception{
		this.dondePersonalizado = where;
		return this;
	}
	
	public Tablas dondeUnir(String[] referencias, String[] campos) throws Exception{
		LinkedHashMap<String,Boolean> validarRepetidos = new LinkedHashMap<String,Boolean>();
		StringBuilder dondeUnir = new StringBuilder();
		for(int i = 0;i<campos.length;i++){
			for(int e = 0;e<referencias.length;e++){
				for(int u = 0;u<referencias.length;u++){
					if(!referencias[e].equals(referencias[u])){
						String refA = referencias[e]+"_"+referencias[u];
						String refB = referencias[u]+"_"+referencias[e];
						if(validarRepetidos.get(refA) == null && validarRepetidos.get(refB) == null){
							dondeUnir.append(referencias[e]+"."+campos[i]+" = "+referencias[u]+"."+campos[i]+" AND ");
							validarRepetidos.put(refA, true);
						}
					}
				}
			}
		}
		if(dondeUnir.length()>0){
			this.dondeUnir += dondeUnir.toString();
		}
		return this;
	}
	
	public Object seleccionar(String valores) throws Exception{
		String query = "SELECT "+valores+" FROM "+getTablas()+" "+getUnionesIzquierda()+" "+getWheres() +" "+ getOrden()+" "+ getLimite();
		return execute(query, "seleccionar");
	}

	public Tablas ordenadoPor(String ordenadoPor) throws Exception{
		this.ordenadoPor = ordenadoPor;
		return this;
	}
	
	public Tablas limite(int limite) throws Exception{
		this.limite = limite;
		return this;
	}

	
	private String unirIzquierdaTablaCondicion = "";
	public Tablas dondeUnirIzquierda(String idReferencia, String idTablaCondicion, String[] camposRelacion) {
		unirIzquierdaTablaCondicion = idTablaCondicion;
		return dondeUnirIzquierda(idReferencia, camposRelacion);
	}
	
	public Tablas dondeUnirIzquierda(String idReferencia, String[] camposRelacion) {
		LinkedHashMap<String,Boolean> validarRepetidos = new LinkedHashMap<String,Boolean>();
		StringBuilder dondeUnir = new StringBuilder();
		for(int i = 0;i<camposRelacion.length;i++){
			for(int e = 0;e<referencias.length;e++){
				if(!referencias[e].equals(idReferencia)){
					if(unirIzquierdaTablaCondicion.equals("") || unirIzquierdaTablaCondicion.equals(referencias[e])){
						String refA = referencias[e]+"_"+idReferencia;
						String refB = idReferencia+"_"+referencias[e];
						if(validarRepetidos.get(refA) == null && validarRepetidos.get(refB) == null){
							dondeUnir.append(referencias[e]+"."+camposRelacion[i]+" = "+idReferencia+"."+camposRelacion[i]+" AND ");
							validarRepetidos.put(refA, true);
						}
					}
				}
			}
		}
		String condicion = dondeUnir.toString();
		unirIzquierdaTablas.put(idReferencia, condicion.toString().substring(0,condicion.length()-5));
		return this;
	}
	
	public int contar() throws Exception{
		String query = "SELECT COUNT(*) AS COUNT FROM "+nombreTablas+" "+getWheres();
		return (int) execute(query, "contarRegistro");
	}
	
	private Object execute(String query, String type) throws Exception {
		long time_init = System.currentTimeMillis();
		Object response = null;
		if(type.equals("obtener")) {
			response = conexionBD.obtener(query);
		} else if(type.equals("seleccionar")) {
			response = conexionBD.seleccionar(query);
		} else if(type.equals("contarRegistro")) {
			response = conexionBD.contarRegistro(query);
		}
		logger.info("t<"+(System.currentTimeMillis() - time_init)+"> "+query);
		return response;
	}
	
	private String getTablas(){
		String tablas = "";
		for(int i = 0;i<this.nombreTablas.length;i++){
			if(unirIzquierdaTablas.get(this.referencias[i]) == null){
				tablas += this.nombreTablas[i]+" "+this.referencias[i]+",";
			}
		}
		if(tablas.length()>0){
			return tablas.substring(0,tablas.length()-1);
		} else{
			return "";
		}
	}
	
	private String getUnionesIzquierda(){ // LEFT JOIN BFP_CARTA_FIANZA.ATRIBUTO_DO B ON B.COD_ATRIBUTO = A.COD_ATRIBUTO
		String tablas = "";
		if(unirIzquierdaTablas.size()>0){
			//tablas += ;
		}
		for (Map.Entry<String, String> entry : unirIzquierdaTablas.entrySet()) {
			String key = entry.getKey();
	    	String string = entry.getValue();
			for(int i = 0;i<this.referencias.length;i++){
				if(this.referencias[i].equals(key)){
					tablas += "LEFT JOIN "+this.nombreTablas[i]+" "+this.referencias[i]+" ON "+string+" ";
				}
			}
		}
		if(tablas.length()>0){
			return tablas;
		} else{
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
	
	@SuppressWarnings("unchecked")
	private String getWheres(){
		String resultado = "";
		
		String strTipoWheres = "";
		if(datoTipos.size()>0 && this.nombreTablas.length>0){
			for(int i = 0;i<this.nombreTablas.length;i++){
				Object ObjtipoWheres = datoTipos.get(i);
				if(ObjtipoWheres != null){
					LinkedHashMap<String,Object> LHMtipoWheres = (LinkedHashMap<String, Object>)ObjtipoWheres;
					Object tipoWheres = LHMtipoWheres.get(Tipo_Where);
					StringBuilder where = new StringBuilder("");
					Map<String,String> wheres = (Map<String, String>) tipoWheres;
					for (Map.Entry<String, String> entry : wheres.entrySet()) {
						String key = entry.getKey();
				    	String String = entry.getValue();
				    	where.append(this.referencias[i]+"."+key+" = '"+String+"' AND ");
					}
					strTipoWheres += where.toString();
				}
			}
		}

		
		if(strTipoWheres.length()>0 || this.dondeUnir.length()>0 || dondePersonalizado.length()>0){
			resultado = "WHERE ";
		}
		
		// DONDE UNIR
		if(this.dondeUnir.length()>0){ // LEGA CON ' AND '
			resultado += this.dondeUnir;
		}
		
		// WHERES DE REQUEST
		if(strTipoWheres.length()>0){
			resultado += strTipoWheres;
		}
		
		if(dondePersonalizado.length()>0){
			resultado += dondePersonalizado;
		} else {
			if(resultado.length()>0){
				resultado = resultado.substring(0,resultado.length()-5);
			}
		}
		
		return resultado;
	}
	
	@SuppressWarnings("unchecked")
	private LinkedHashMap<String,Object> getTipoWhere(String referencia){
		int ei = -1;
		for(int i = 0;i<this.referencias.length;i++){
			if(this.referencias[i].equals(referencia)){
				ei = i;
				break;
			}
		}
		if(ei!=-1){
			Object ObjtipoWheres = datoTipos.get(ei);
			if(ObjtipoWheres != null){
				return (LinkedHashMap<String, Object>)ObjtipoWheres;
			}
			
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public String getDataWhere(String referencia,String campo){
		LinkedHashMap<String,Object> LHMtipoWheres = getTipoWhere(referencia);
		if(LHMtipoWheres != null){
			Object tipoWheres = LHMtipoWheres.get(Tipo_Where);
			if(tipoWheres!=null){
				Map<String,String> wheres = (Map<String, String>) tipoWheres;
				if(wheres.get(campo)!=null){
					return wheres.get(campo);
				}
			}
		}
		return "";
	}
	
	@SuppressWarnings("unchecked")
	public void setDataWhere(String referencia,String campo,String valor){
		LinkedHashMap<String,Object> LHMtipoWheres = getTipoWhere(referencia);
		if(LHMtipoWheres != null){
			Object tipoWheres = LHMtipoWheres.get(Tipo_Where);
			if(tipoWheres!=null){
				Map<String,String> wheres = (Map<String, String>) tipoWheres;
				wheres.put(campo, valor);
				LHMtipoWheres.put("W", wheres);
			}
			int ei = -1;
			for(int i = 0;i<this.referencias.length;i++){
				if(this.referencias[i].equals(referencia)){
					ei = i;
					break;
				}
			}
			datoTipos.add(ei, LHMtipoWheres);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void removeDataWhere(String referencia,String campo){
		LinkedHashMap<String,Object> LHMtipoWheres = getTipoWhere(referencia);
		if(LHMtipoWheres != null){
			Object tipoWheres = LHMtipoWheres.get(Tipo_Where);
			if(tipoWheres!=null){
				Map<String,String> wheres = (Map<String, String>) tipoWheres;
				wheres.remove(campo);
				LHMtipoWheres.put("W", wheres);
			}
			int ei = -1;
			for(int i = 0;i<this.referencias.length;i++){
				if(this.referencias[i].equals(referencia)){
					ei = i;
					break;
				}
			}
			datoTipos.add(ei, LHMtipoWheres);
		}
	}
	
}