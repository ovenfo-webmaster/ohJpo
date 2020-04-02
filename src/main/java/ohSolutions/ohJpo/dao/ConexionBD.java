/*
 * PPO v1.8.7
 * Author		: Oscar Huertas
 * Date			: 2017-09-27
 * Update date	: 2018-07-11 - v1.7.0
 * Update date	: 2019-03-13 - v1.8.7
 * Description	: Define the steps to execute a query, dml, ddl or store
 * */
package ohSolutions.ohJpo.dao;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConexionBD {

	final static Logger logger = LogManager.getLogger(ConexionBD.class);
	
	private Jpo jpo = null;
	
	public ConexionBD(Jpo jpo) {
		this.jpo = jpo;
	}
	
	public Connection getConexion() throws Exception{
		return this.jpo.getConexion();
	}
	
	public boolean query(String query) throws Exception { // For anything
		Statement stmt = null;
		Connection conexion = getConexion();
	    try {
	    	stmt = conexion.createStatement();
	    	stmt.executeUpdate(query);
	    	return true;
	    } catch ( Exception e ) {
	    	excepcion(conexion, e);
	    	return false;
	    } finally {
	    	finalizar(null, stmt);
	    }
	}
	
	public boolean dml(String query) throws Exception { // For register, edit and delete
		Statement stmt = null;
		Connection conexion = getConexion();
	    try {
	    	stmt = conexion.createStatement();
	    	stmt.executeUpdate(query);
	    	
	    	return true;
	    } catch ( Exception e ) {
	    	excepcion(conexion, e);
	    	return false;
	    } finally {
	    	finalizar(null, stmt);
	    }
	}
	
	public boolean dml(String[] querys) throws Exception { // For register, edit and delete
		Statement stmt = null;
		int i = 0;
		Connection conexion = getConexion();
		try {
	    	stmt = conexion.createStatement();
	    	for(i = 0;i<querys.length;i++){
	    		stmt.executeUpdate(querys[i]);
	    	}
	    	return true;
	    } catch ( Exception e ) {
	    	excepcion(conexion, e);
	    	return false;
	    } finally {
	    	finalizar(null, stmt);
	    }
	}
	
	public int contarRegistro(String query) throws Exception {
		int total = 0;
		Connection conexion = getConexion();
		Statement stmt = null;
		ResultSet rs = null;
		try {
	      	stmt = conexion.createStatement();
	      	rs = stmt.executeQuery(query);
	      	
	      	while(rs.next()){
				total = rs.getInt("COUNT");
			}
	      	return total;
	    } catch ( Exception e ) {
	    	excepcion(conexion, e);
	    	return -1;
	    } finally {
	    	finalizar(rs, stmt);
	    }
	}
	
	public Map<String, Object> obtener(String query) throws Exception {
		Map<String, Object> detalle = new LinkedHashMap<String, Object>();
		Connection conexion = getConexion();
      	Statement stmt = null;
      	ResultSet rs = null;
		try {
			stmt = conexion.createStatement();
	      	rs = stmt.executeQuery(query);
	      	ResultSetMetaData metaData = rs.getMetaData();
	      	
			while(rs.next()){
			    for (int i = 1; i <= metaData.getColumnCount(); i++) {
 		    		Object valor = obtenerCampos(i, rs);
			    	if(valor != null){
			    		detalle.put(metaData.getColumnLabel(i), valor);
			    	}
			    }
			    break;
			}
			return detalle;
	    } catch ( Exception e ) {
	    	excepcion(conexion, e);
	    	return null;
	    } finally {
	    	finalizar(rs, stmt);
	    }
	    
	}
	
	public List<Object> obtenerL(String query) throws Exception {
		List<Object> detalle = new ArrayList<Object>();
		Connection conexion = getConexion();
      	Statement stmt = null;
      	ResultSet rs = null;
		try {
			stmt = conexion.createStatement();
	      	rs = stmt.executeQuery(query);
	      	
			while(rs.next()){
			    for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
			    	detalle.add(obtenerCampos(i, rs));
			    }
			    break;
			}
			return detalle;
	    } catch ( Exception e ) {
	    	excepcion(conexion, e);
	    	return null;
	    } finally {
	    	finalizar(rs, stmt);
	    }
	    
	}

	public List<Map<String, Object>> seleccionar(String query) throws Exception {
		List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
		Connection conexion = getConexion();
      	Statement stmt = null;
      	ResultSet rs = null;
		try {
			stmt = conexion.createStatement();
	      	rs = stmt.executeQuery(query);
	      	
	      	ResultSetMetaData metaData = rs.getMetaData();
			while(rs.next()){
				Map<String, Object> columns = new LinkedHashMap<String, Object>();
			    for (int i = 1; i <= metaData.getColumnCount(); i++) {
 		    		Object valor = obtenerCampos(i, rs);
			    	if(valor != null){
			    		columns.put(metaData.getColumnLabel(i), valor);
			    	}
			    }
			    rows.add(columns);
			}
	      return rows;
	    } catch ( Exception e ) {
	    	excepcion(conexion, e);
	    	return null;
	    } finally {
	    	finalizar(rs, stmt);
	    }
	}
	
	public List<List<Object>> seleccionarL(String query) throws Exception {
		List<List<Object>> rows = new ArrayList<List<Object>>();
		Connection conexion = getConexion();
      	Statement stmt = null;
      	ResultSet rs = null;
		try {
			stmt = conexion.createStatement();
	      	rs = stmt.executeQuery(query);
	      	
			while(rs.next()){
				List<Object> columns = new ArrayList<Object>();
			    for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
			    	columns.add(obtenerCampos(i, rs));
			    }
			    rows.add(columns);
			}
			return rows;
		} catch (Exception e) {
	    	excepcion(conexion, e);
	    	return null;
	    } finally {
	    	finalizar(rs, stmt);
	    }
	}
	
	public Object procedimiento(String query, List<Campo> campos, Campo campoSalida, boolean unRegistro) throws Exception {
		CallableStatement cstmt = null;
	    ResultSet rs = null;
	    Connection conexion = getConexion();
	    
	    try {
	    	
	        cstmt = conexion.prepareCall(query);
	        cstmt = prepareStore(cstmt, campos, campoSalida);
	        
	        int e = (campoSalida != null)?1:0;
	        
	        if(e==1) {
	        	if(campoSalida.getTipoDato() == Jpo.RESULTADO) {
	        		rs = (ResultSet) cstmt.getObject(e);
	        		List<Map<String, Object>> resultado = getResultado(rs);
		        	if(unRegistro && resultado.size()>0) {
		        		return resultado.get(0);
		        	} else {
		        		return resultado;
		        	}		        	
        		} else {
        			return getCampoSalida(cstmt, campoSalida, e);
        		}
	        }
	        
	        Object salidaFin = new Object();
	        
	        List<List<Map<String, Object>>> resultados = new ArrayList<>();
        	do {
        		try (ResultSet rsa = cstmt.getResultSet()) {
        			resultados.add(getResultado(rsa));
        		}
        	} while (cstmt.getMoreResults());
        	
        	if(resultados.size()==1) {  // 1: [[[1,"valor"],[1,"valor"]]]
        		if(unRegistro && resultados.get(0) != null && resultados.get(0).size()>0) {
        			salidaFin = resultados.get(0).get(0); // return [1,"valor"]
        		} else {
        			salidaFin = resultados.get(0); // return [[1,"valor"],[1,"valor"]]
        		}
        	} else if(resultados.size()>1) {  // 2: [[[1,"valor"],[1,"valor"]], [[33,"rangoA"],[34,"rangoB"]]]
        		if(unRegistro) {
        			List<Object> _resultados = new ArrayList<>();
	        		for(int i = 0; i < resultados.size(); i++) {
		        		if(resultados.get(i).size()>0) {
		        			_resultados.add(resultados.get(i).get(0));
		        		} else {
		        			_resultados.add(new ArrayList<Map<String, Object>>());
		        		}
	        		}
	        		salidaFin = _resultados; // return [[1,"valor"], [33,"rangoA"]]
        		} else {
        			salidaFin = resultados; // return [[[1,"valor"],[1,"valor"]], [[33,"rangoA"],[34,"rangoB"]]]
        		}
        	} else {
        		salidaFin = resultados;
        	}
        	
	        if(tieneSalida(campos)) {
        		rs = cstmt.getResultSet();
        		Map<String, Object> salida = new LinkedHashMap<String, Object>();
            	for(int i = 0 ; i < campos.size(); i++) {
        			Campo campo = campos.get(i);
        			if(campo.getTipoParametro()==Jpo.SALIDA || campo.getTipoParametro()==Jpo.ENTSAL) {
        				salida.put(campo.getNombre(), getCampoSalida(cstmt, campo, i+1+e));
        			}
        		}
    	        if(resultados.size()>0 && salidaFin != null) {
    	        	salida.put("salida", salidaFin);
	        	}
    	        return salida;
        	} else {
        		return salidaFin;
        	}
	        
	    } catch (Exception e) {
	    	excepcion(conexion, e);
	    	return null;
	    } finally {
	    	finalizar(rs, cstmt);
	    }
	    
	}

	public Object procedimientoL(String query, List<Campo> campos, Campo campoSalida, boolean unRegistro) throws Exception {
		CallableStatement cstmt = null;
	    ResultSet rs = null;
	    Connection conexion = getConexion();
	    
	    try {
	        cstmt = conexion.prepareCall(query);
	        cstmt = prepareStore(cstmt, campos, campoSalida);
	        
	        int e = (campoSalida != null)?1:0;
	        
	        if(e == 1) {
        		if(campoSalida.getTipoDato() == Jpo.RESULTADO) {
        			rs = (ResultSet) cstmt.getObject(e);
	        		List<List<Object>> resultado = getResultadoL(rs);
		        	if(unRegistro && resultado.size()>0) {
		        		return resultado.get(0);
		        	} else { 
		        		return resultado;
		        	}
        		} else {
        			return getCampoSalida(cstmt, campoSalida, e, "L");
        		}
	        }
	        
        	Object salidaFin = new Object();
        
        	List<List<List<Object>>> resultados = new ArrayList<>();
        	do {
        		try (ResultSet rsa = cstmt.getResultSet()) {
        			resultados.add(getResultadoL(rsa));
        		}
        	} while (cstmt.getMoreResults());
        	
        	if(resultados.size()==1) {  // 1: [[[1,"valor"],[1,"valor"]]]
        		if(unRegistro && resultados.get(0) != null && resultados.get(0).size()>0) {
        			salidaFin = resultados.get(0).get(0); // return [1,"valor"]
        		} else {
        			salidaFin = resultados.get(0); // return [[1,"valor"],[1,"valor"]]
        		}
        	} else if(resultados.size()>1) {  // 2: [[[1,"valor"],[1,"valor"]], [[33,"rangoA"],[34,"rangoB"]]]
        		if(unRegistro) {
        			List<Object> _resultados = new ArrayList<>();
	        		for(int i = 0; i < resultados.size(); i++) {
		        		if(resultados.get(i).size()>0) {
		        			_resultados.add(resultados.get(i).get(0));
		        		} else {
		        			_resultados.add(new ArrayList<Object>());
		        		}
	        		}
	        		salidaFin = _resultados; // return [[1,"valor"], [33,"rangoA"]]
        		} else {
        			salidaFin = resultados; // return [[[1,"valor"],[1,"valor"]], [[33,"rangoA"],[34,"rangoB"]]]
        		}
        	} else {
        		salidaFin = resultados;
        	}
        	
        	if(tieneSalida(campos)) {
        		rs = cstmt.getResultSet();
    	        List<Object> salida = new ArrayList<Object>();
    	        for(int i = 0 ; i < campos.size(); i++) {
        			Campo campo = campos.get(i);
        			if(campo.getTipoParametro()==Jpo.SALIDA || campo.getTipoParametro()==Jpo.ENTSAL) {
        				salida.add(getCampoSalida(cstmt, campo, i+1+e, "L"));
        			}
        		}
    	        if(resultados.size()>0 && salidaFin != null) {
    	        	salida.add(salidaFin);
	        	}
    	        return salida;
        	} else {
        		return salidaFin;
        	}
	        	
	    } catch (Exception e) {
	    	excepcion(conexion, e);
	    	return null;
	    } finally {
	    	finalizar(rs, cstmt);
	    }
	}
	
	private boolean tieneSalida(List<Campo> campos) {
        for(int i = 0 ; i < campos.size(); i++) {
			Campo campo = campos.get(i);
			if(campo.getTipoParametro()==Jpo.SALIDA || campo.getTipoParametro()==Jpo.ENTSAL) {
				return true;
			}
		}
        return false;
	}
	
	private void excepcion(Connection conexion, Exception e) throws Exception {
    	conexion.rollback();
    	conexion.close();
    	throw new Exception("BD: "+e.getClass().getName()+"="+e.getMessage()); 
	}
	
	private void finalizar(ResultSet rs, CallableStatement cstmt) throws SQLException {
    	if(rs != null){
    		rs.close();
    	}
    	if(cstmt != null){
    		cstmt.close();
    	}
	}
	
	private void finalizar(ResultSet rs, Statement stmt) throws SQLException {
    	if(rs != null){
    		rs.close();
    	}
    	if(stmt != null){
    		stmt.close();
    	}
	}
	
	private CallableStatement prepareStore(CallableStatement cstmt, List<Campo> campos, Campo campoSalida) throws SQLException, ParseException {
		
		int e = 0;
		if(campoSalida != null) {
	    	e++;
	    	cstmt.registerOutParameter(e, getTipoDatoBD(campoSalida.getTipoDato()));
		}
		
		for(int i = 0 ; i < campos.size(); i++) {
			Campo campo = campos.get(i);
			int tipoCampo = campos.get(i).getTipoDato();
			if(campo.getTipoParametro()==Jpo.ENTRADA || campo.getTipoParametro()==Jpo.ENTSAL) {
        		if(tipoCampo == Jpo.CADENA) {
	        		if(campos.get(i).getValor() == null || campos.get(i).getValor().equals("null")) {
	        			cstmt.setNull(i+1+e, Types.VARCHAR);
	        		} else {
	        			cstmt.setString(i+1+e, campos.get(i).getValor());
	        		}
	        	}
				if(tipoCampo == Jpo.TEXTO || tipoCampo == Jpo.XML) {
					if(campos.get(i).getValor() == null || campos.get(i).getValor().equals("null")) {
	        			cstmt.setNull(i+1+e, Types.VARCHAR);
	        		} else {
	        			cstmt.setNString(i+1+e, campos.get(i).getValor());
	        		}
	        	}
				if(tipoCampo == Jpo.ENTERO) {
	        		if(campos.get(i).getValor() == null || campos.get(i).getValor().equals("") || campos.get(i).getValor().equals("null")) {
	        			cstmt.setNull(i+1+e, Types.INTEGER);
	        		} else {
	        			cstmt.setInt(i+1+e, Integer.parseInt(campos.get(i).getValor(), 10));
	        		}
	        	}
				if(tipoCampo == Jpo.BIGINTEGER) {
	        		if(campos.get(i).getValor() == null || campos.get(i).getValor().equals("") || campos.get(i).getValor().equals("null")) {
	        			cstmt.setNull(i+1+e, Types.BIGINT);
	        		} else {
	        			cstmt.setBigDecimal(i+1+e, new BigDecimal(campos.get(i).getValor()));
	        		}
	        	}
				if(tipoCampo == Jpo.CARACTER) {
	        		if(campos.get(i).getValor() == null || campos.get(i).getValor().equals("") || campos.get(i).getValor().equals("null")) {
	        			cstmt.setNull(i+1+e, Types.CHAR);
	        		} else {
	        			cstmt.setString(i+1+e, campos.get(i).getValor());
	        		}
	        	}
				if(tipoCampo == Jpo.DECIMAL) {
	        		if(campos.get(i).getValor() == null || campos.get(i).getValor().equals("") || campos.get(i).getValor().equals("null")) {
	        			cstmt.setNull(i+1+e, Types.DECIMAL);
	        		} else {
	        			cstmt.setBigDecimal(i+1+e, new BigDecimal(campos.get(i).getValor()));
	        		}
	        	}
				if(tipoCampo == Jpo.FECHA) {
	        		if(campos.get(i).getValor() == null || campos.get(i).getValor().equals("") || campos.get(i).getValor().equals("null")) {
	        			cstmt.setNull(i+1+e, Types.DATE);
	        		} else {
	        	        SimpleDateFormat formato = new SimpleDateFormat(campos.get(i).getFormato());
	        	        java.util.Date parseo = formato.parse(campos.get(i).getValor());
	        			cstmt.setDate(i+1+e, new Date(parseo.getTime()));
	        		}
	        	}
				if(tipoCampo == Jpo.FECHAHORA) {
	        		if(campos.get(i).getValor() == null || campos.get(i).getValor().equals("") || campos.get(i).getValor().equals("null")) {
	        			cstmt.setNull(i+1+e, Types.TIMESTAMP);
	        		} else {
	        	        SimpleDateFormat formato = new SimpleDateFormat(campos.get(i).getFormato());
	        	        java.util.Date parseo = formato.parse(campos.get(i).getValor());
	        			cstmt.setTimestamp(i+1+e, new Timestamp(parseo.getTime()));
	        		}
	        	}
			}
			if(campo.getTipoParametro()==Jpo.SALIDA || campo.getTipoParametro()==Jpo.ENTSAL) {
				cstmt.registerOutParameter(i+1+e, getTipoDatoBD(tipoCampo));
			}
		}

        cstmt.execute();
        return cstmt;
	}
	
	private Object obtenerCampos(int i,ResultSet rs) throws SQLException {
		ResultSetMetaData metaData = rs.getMetaData();
    	Object valor = new Object();
    	String tipo = metaData.getColumnTypeName(i).toUpperCase();
    	Object valorRetorno = rs.getObject(i);
    	if((tipo.equals("CHARACTER") || tipo.equals("CHAR") || tipo.equals("BPCHAR") || tipo.equals("NCHAR")) && valorRetorno != null){
    		valor = valorRetorno;
    	} else if((tipo.equals("_TEXT") || tipo.equals("_OID") || tipo.equals("_CHAR")) && valorRetorno != null){
    		valor = rs.getArray(i).getArray();
    	} else {
    		valor = valorRetorno;
    	}
    	return valor;
	}
	
	private Object getCampoSalida(CallableStatement cstmt, Campo campo, int indice) throws SQLException {
		return getCampoSalida(cstmt, campo, indice, "");
	}

	private int getTipoDatoBD(int tipoDato) {
		if(tipoDato == Jpo.CADENA) {
			return Types.VARCHAR;
    	}
		if(tipoDato == Jpo.TEXTO) {
			return Types.NVARCHAR;
    	}
		if(tipoDato == Jpo.XML) {
			return Types.SQLXML;
    	}
		if(tipoDato == Jpo.ENTERO) {
			return Types.INTEGER;
    	}
		if(tipoDato == Jpo.BIGINTEGER) {
			return Types.BIGINT;
    	}
		if(tipoDato == Jpo.CARACTER) {
			return Types.CHAR;
    	}
		if(tipoDato == Jpo.DECIMAL) {
			return Types.NUMERIC;
    	}
		if(tipoDato == Jpo.FECHA) {
			return Types.DATE;
    	}
		if(tipoDato == Jpo.FECHAHORA) {
			return Types.TIMESTAMP;
    	}
		if(tipoDato == Jpo.RESULTADO) {
			return Types.OTHER;
    	}
		return -1;
	}
	
	private Object getCampoSalida(CallableStatement cstmt, Campo campo, int indice, String tipoJson) throws SQLException {
		if(campo.getTipoDato() == Jpo.CADENA || campo.getTipoDato() == Jpo.TEXTO || campo.getTipoDato() == Jpo.XML) {
			return cstmt.getString(indice);
    	}
    	if(campo.getTipoDato() == Jpo.ENTERO) {
    		return cstmt.getInt(indice);
    	}
    	if(campo.getTipoDato() == Jpo.BIGINTEGER) {
    		return cstmt.getBigDecimal(indice);
    	}
    	if(campo.getTipoDato() == Jpo.DECIMAL) {
			return cstmt.getBigDecimal(indice);
    	}
		if(campo.getTipoDato() == Jpo.CARACTER) {
			return cstmt.getString(indice);
    	}
		if(campo.getTipoDato() == Jpo.FECHA) {
			return cstmt.getDate(indice);
    	}
		if(campo.getTipoDato() == Jpo.FECHAHORA) {
			return cstmt.getTimestamp(indice);
    	}
		if(campo.getTipoDato() == Jpo.RESULTADO) {
			if(tipoJson.equals("L")) {
				return getResultadoL((ResultSet) cstmt.getObject(indice));
			} else {
				return getResultado((ResultSet) cstmt.getObject(indice));
			}
    	}
		return null;
	}
	
	private List<Map<String, Object>> getResultado(ResultSet rs) throws SQLException {
        if(rs!=null) {
        	ResultSetMetaData metaData = rs.getMetaData();
	        
	        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
 			while(rs.next()){
 				Map<String, Object> columns = new LinkedHashMap<String, Object>();
 				for (int i = 1; i <= metaData.getColumnCount(); i++) {
 		    		Object valor = obtenerCampos(i, rs);
			    	if(valor != null){
			    		columns.put(metaData.getColumnLabel(i), valor);
			    	}
 			    }
 			    rows.add(columns);
 			}
 			return rows;
        } else {
        	return null;
        }
	}
	
	private List<List<Object>> getResultadoL(ResultSet rs) throws SQLException {
        if(rs!=null) {
            List<List<Object>> rows = new ArrayList<List<Object>>();
          	while(rs.next()){
        		List<Object> columns = new ArrayList<Object>();
            	for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
            		columns.add(obtenerCampos(i, rs));
        	    }
        	    rows.add(columns);
        	}
          	return rows;
        } else {
        	return null;
        }
	}
	
}