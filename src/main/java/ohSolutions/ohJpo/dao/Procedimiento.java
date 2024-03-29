/*
 * PPO v1.8.7
 * Author		: Oscar Huertas
 * Date			: 2017-09-27
 * Update date	: 2018-07-11 - v1.7.0
 * Update date	: 2019-03-13 - v1.8.7
 * Description	: !Deprecated - Class to manage store procedure querys
 * Description	: !Deprecated - Adding log4j2
 * */
package ohSolutions.ohJpo.dao;
 
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

public class Procedimiento {

	final static Logger logger = LogManager.getLogger(Procedimiento.class);
	
	private String nombreTabla;
	private LinkedHashMap<String,String> data = new LinkedHashMap<String,String>();

	private ConexionBD conexionBD = null;
	
	List<Campo> campos = new ArrayList<Campo>();
	Campo campoSalida = null;
	Map<String, Integer> camposId = new LinkedHashMap<String, Integer>();
	
	@SuppressWarnings("unchecked")
	public Procedimiento(Jpo jpo,String nombreTabla,Object data) {
		conexionBD = new ConexionBD(jpo);
		this.nombreTabla = nombreTabla;
		if(data!=null){
			this.data = (LinkedHashMap<String, String>) data;
		}
	}
	
	public Object ejecutar() throws Exception{
		return ejecutar(false);
	}
	
	public <T> T obtener(Type typeOfT) throws Exception{
		Object resultado = ejecutar(false);
		Gson gson = new Gson();
		return gson.fromJson(gson.toJson(resultado), typeOfT);
	}
	
	@SuppressWarnings("unchecked")
	public JpoLista obtenerLista() throws Exception{
		return new JpoLista((List<Object>) ejecutar(false));
	}
	
	public Object ejecutar(boolean unRegistro) throws Exception {
		return execute(unRegistro, "normal");
	}

	public Object ejecutarL() throws Exception{
		return ejecutarL(false);
	}
	
	public Object ejecutarL(boolean unRegistro) throws Exception {
		return execute(unRegistro, "light");
	}
	
	private Object execute(boolean oneRegister, String type) throws Exception {
		long time_init = System.currentTimeMillis();
		String query = "{"+(campoSalida != null?"? = ":"")+"call "+nombreTabla+getEntradas()+"}";
		Object response = null;
		try {
			if(type.equals("light")) {
				response = conexionBD.procedimientoL(query, campos, campoSalida, oneRegister);
			} else {
				response = conexionBD.procedimiento(query, campos, campoSalida, oneRegister);
			}
			logger.info("t<"+(System.currentTimeMillis() - time_init)+"> "+query);
			logger.debug("input: "+new Gson().toJson(campos));
			if(campoSalida != null) {
				logger.debug("output: "+new Gson().toJson(campoSalida));
			}
		} catch(Exception e) {
			logger.info("t<"+(System.currentTimeMillis() - time_init)+"> "+query);
			logger.debug("input: "+new Gson().toJson(campos));
			logger.error(e.getMessage());
			throw new Exception(e);
		}
		return response;
	}

	private String getEntradas() throws Exception { 
		String parametros = "";
		parametros += "(";
		for(int i = 0 ; i < campos.size(); i++) {
			Campo campo = campos.get(i);
			if(campo.getTipoParametro()==Jpo.ENTRADA || campo.getTipoParametro()==Jpo.ENTSAL) {
				String valor = data.get(campos.get(i).getNombre());
				if(campos.get(i).getValor() == null) {
					campos.get(i).setValor(valor);
				}
				if(campos.get(i).validar()) {
					parametros += "?,";
				}
			} else {
				parametros += "?,";
			}
		}
		if(parametros.length()>1) {
			parametros = parametros.substring(0,parametros.length()-1)+")";
		} else {
			parametros += ")";
		}
		return parametros;
	}
	
	public String getValor(String campo){
		if(data.get(campo)!=null){
			return data.get(campo);
		}
		return null;
	}
	
	public void setValor(String campo,String valor){
		campos.get(camposId.get(campo)).setValor(valor);
	}

	@SuppressWarnings("unlikely-arg-type")
	public void removerCampo(String campo){
		campos.remove(camposId.get(campo));
	}
	
	public void entrada(String field, int type) throws Exception {
		entrada(field, null, type, null);
	}
	public void entrada(String field, int type, String rules) throws Exception {
		entrada(field, null, type, rules);
	}
	public void entrada(String field,String value, int type) throws Exception {
		entrada(field, value, type, null);
	}
	public void entrada(String field,String value, int type, String rules) throws Exception {
		camposId.put(field, campos.size());
		campos.add(new Campo(field, Jpo.ENTRADA, type, rules, value));
	}

	public void entsal(String field, int type) throws Exception {
		entsal(field, type, null);
	}
	public void entsal(String field, int type, String rules) throws Exception {
		camposId.put(field, campos.size());
		campos.add(new Campo(field, Jpo.ENTSAL, type, rules));
	}
	public void salida(String field, int type) throws Exception {
		camposId.put(field, campos.size());
		campos.add(new Campo(field, Jpo.SALIDA, type, null));
	}

	public void getResultado(int type) throws Exception {
		campoSalida = new Campo("resultado", Jpo.SALIDA, type);
	}

}