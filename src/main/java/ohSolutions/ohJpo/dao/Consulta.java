/*
 * PPO v1.8.7
 * Author		: Oscar Huertas
 * Date			: 2017-09-27
 * Update date	: 2019-03-13 - v1.8.7
 * Description	: Class to execute any query
 * Description	: Adding log4j2
 * */
package ohSolutions.ohJpo.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Consulta {

	final static Logger logger = LogManager.getLogger(Consulta.class);
	
	private ConexionBD conexionBD = null;
	private String sentencia;
	
	public Consulta(Jpo jpo,String sentencia) {
		conexionBD = new ConexionBD(jpo);
		this.sentencia = sentencia;
	}
	
	public Object ejecutar() throws Exception {
		long time_init = System.currentTimeMillis();
		Object response = conexionBD.query(sentencia);
		logger.info("t<"+(System.currentTimeMillis() - time_init)+"> "+sentencia);
		return response;
	}
	
	public Object seleccionar() throws Exception {
		long time_init = System.currentTimeMillis();
		Object response = conexionBD.seleccionar(sentencia);
		logger.info("t<"+(System.currentTimeMillis() - time_init)+"> "+sentencia);
		return response;
	}
	
	public Object seleccionarL() throws Exception {
		long time_init = System.currentTimeMillis();
		Object response = conexionBD.seleccionarL(sentencia);
		logger.info("t<"+(System.currentTimeMillis() - time_init)+"> "+sentencia);
		return response;
	}

}