package ohSolutions.ohJpo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

import ohSolutions.ohJpo.dao.Jpo;
import ohSolutions.ohJpo.dao.Procedimiento;
import ohSolutions.ohJpo.dao.Procedure;
import ohSolutions.ohJpo.dao.Tabla;

public class JpoTest {

    //@Test
    public void testConectionSQLite() throws Exception {

        /* in application.properties
            jpo.ds.type=SQLITE
            jpo.ds.url=C:\\odhp\\dsolutions\\dgeneradorData\\bd
            jpo.ds.db=dgenerador.db
        * */ 

        Jpo miJpo = new Jpo();
        // Discomment for test
        //System.out.println("Resultado de prueba");
        //System.out.println((new Gson()).toJson(miJpo.tabla("proyecto").seleccionar("*")));

        miJpo.finalizar();

    }

    //@Test
    public void testConectionSQLiteConfig() throws Exception {

        Map<String, String> config = new HashMap<String, String>();

        config.put("type", "SQLITE");
        config.put("url", "C:\\odhp\\dsolutions\\dgeneradorData\\bd");
        config.put("db", "dgenerador.db");

        Jpo miJpo = new Jpo(config);

        System.out.println("Resultado de prueba");
        System.out.println((new Gson()).toJson(miJpo.tabla("proyecto").seleccionar("*")));

        miJpo.finalizar();

    }

    //@Test
    public void testConectionSQLServer() throws Exception {

        /* in application.properties
            jpo.ds.type=SQLSERVER
            jpo.ds.url=10.10.10.37
            jpo.ds.db=MILLENIUMDEV
            jpo.ds.scheme=
            jpo.ds.username=sa
            jpo.ds.password=Pr0t0c0l0
        * */

        Jpo miJpo = new Jpo();

        System.out.println("Resultado de prueba");
        System.out.println((new Gson()).toJson(miJpo.tabla("COMTC_ROL").seleccionar("*")));

        miJpo.finalizar();

    }

    //@Test
    public void testConectionSQLServerFromDS() throws Exception {

        /* in application.properties
            spring.datasource.url=jdbc:sqlserver://10.10.10.37;databaseName=MILLENIUMDEV
            spring.datasource.username=sa
            spring.datasource.password=Pr0t0c0l0
            spring.datasource.driverClassName=com.microsoft.sqlserver.jdbc.SQLServerDriver
            jpo.ds.source=spring.datasource
        * */

        Jpo miJpo = new Jpo();

        System.out.println("Resultado de prueba");
        System.out.println((new Gson()).toJson(miJpo.tabla("COMTC_ROL").seleccionar("*")));

        miJpo.finalizar();

    }

    //@Test
    public void testConsultaBoolean() throws Exception {

        /* in application.properties
            spring.datasource.url=jdbc:sqlserver://10.10.10.37;databaseName=MILLENIUMDEV
            spring.datasource.username=sa
            spring.datasource.password=Pr0t0c0l0
            spring.datasource.driverClassName=com.microsoft.sqlserver.jdbc.SQLServerDriver
            jpo.ds.source=spring.datasource
        * */

        Jpo miJpo = new Jpo();

        System.out.println("Resultado de prueba");
        System.out.println((new Gson()).toJson(miJpo.tabla("VACTV_EIR").donde("EIR_C_NUMERO = '0000757933'").seleccionar("*")));

        miJpo.finalizar();

    }

    //@Test
    public void testConsultaFechaHora() throws Exception {

        /* in application.properties
            spring.datasource.url=jdbc:sqlserver://10.10.10.37;databaseName=MILLENIUMDEV
            spring.datasource.username=sa
            spring.datasource.password=Pr0t0c0l0
            spring.datasource.driverClassName=com.microsoft.sqlserver.jdbc.SQLServerDriver
            jpo.ds.source=spring.datasource
        * */

        Jpo jpo = new Jpo();

        Procedimiento pDetalle = jpo.procedimiento("LIS_AVISO");
        pDetalle.entrada("CodigoProyecto", Jpo.CADENA);
        pDetalle.entrada("Fecha", Jpo.FECHAHORA);
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        System.out.println(dateFormat.format(date));
        pDetalle.setValor("Fecha", dateFormat.format(date));
        Object resultado = pDetalle.ejecutarL();

        jpo.finalizar();

        System.out.println("Resultado de prueba");
        System.out.println((new Gson()).toJson(resultado));


    }
    
    //@Test
    public void textXMLReg() throws Exception {

        Map<String, String> config = new HashMap<String, String>();

        config.put("type", "SQLSERVER");
        config.put("url", "10.10.10.37");
        config.put("db", "db_averias");
        config.put("username", "sa");
        config.put("password", "Pr0t0c0l0");
        
        Jpo ppo = new Jpo(config);
        
        ppo.setData("FLG", "xmlParametro", "<Row><linea><AveriaID>1</AveriaID><LugarID>25</LugarID><DetalleInspeccionID>93838</DetalleInspeccionID><UsuarioID>22</UsuarioID><OrdenAveria>1</OrdenAveria><Area>2</Area><SubArea>1</SubArea><Dano>1</Dano><Severidad>2</Severidad><Reparo>Z</Reparo><Observacion>asdas</Observacion><OrdenInspeccion></OrdenInspeccion><Nombre></Nombre><Valor></Valor><InspeccionID>2508</InspeccionID><CodigoVin>LZWACAGA2JE600889</CodigoVin><WareHouseID>1</WareHouseID><ClienteID>8</ClienteID><Ubicacion></Ubicacion><GuiaRemision></GuiaRemision></linea></Row>");
        
		Procedure pResult = ppo.procedure("Averias_Insert","FLG");
		pResult.input("xmlParametro", Jpo.XML);
		pResult.output("mensaje", Jpo.STRING);
		pResult.output("estadoProceso", Jpo.CHARACTER);
		Object response = pResult.executeL();
		ppo.commit();
		ppo.finalizar();
        System.out.println("Resultado de prueba");
        System.out.println((new Gson()).toJson(response));

        
    }
    
	/*USE [Inland]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
ALTER PROC [seg].[menu_configuraciones] 
	@estado INT OUT,
	@mensaje varchar(max) OUT 
AS

SET NOCOUNT ON 

	SELECT catalogo_id, descricion_larga FROM ges.catalogo WHERE catalogo_padre_id = 30821 ORDER BY catalogo_id 
	 * */
	//@Test
    @SuppressWarnings("unused")
	public void testProcedureParseData() throws Exception {

        Map<String, String> config = new HashMap<String, String>();

        config.put("type", "SQLSERVER");
        config.put("url", "10.10.10.51");
        config.put("db", "Inland");
        config.put("username", "sa");
        config.put("password", "Pr0t0c0l0");

        Jpo miJpo = new Jpo(config);
        
        Procedure store = miJpo.procedure("seg.menu_configuraciones");
        /*
        List<MenuConfiguracionMas> outputList = store.get(new TypeToken<List<MenuConfiguracionMas>>(){}.getType());
        
        System.out.println(outputList.size());
        
        for(int i = 0; i < outputList.size(); i++) {
        	System.out.println("test");
        	System.out.println(outputList.get(i).toString());
        }*/

        miJpo.finalizar();

    }

	/*USE [Inland]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
ALTER PROC [seg].[menu_configuraciones_test] 
	@estado INT OUT,
	@mensaje varchar(max) OUT 
AS

SET NOCOUNT ON 

	SELECT catalogo_id, descricion_larga FROM ges.catalogo WHERE catalogo_padre_id = 30821 ORDER BY catalogo_id 

	SELECT * FROM ges.catalogo WHERE catalogo_padre_id = 30821 ORDER BY catalogo_id ASC
	 * */

	//@Test
    @SuppressWarnings("unused")
	public void testProcedureParseDataMultiple() throws Exception {

        Map<String, String> config = new HashMap<String, String>();

        config.put("type", "SQLSERVER");
        config.put("url", "10.10.10.51");
        config.put("db", "Inland");
        config.put("username", "sa");
        config.put("password", "Pr0t0c0l0");

        Jpo miJpo = new Jpo(config);
        
        Procedure store = miJpo.procedure("seg.menu_configuraciones_test");
        
        //List<MenuConfiguracion> conf = store.get(0, new TypeToken<List<MenuConfiguracion>>(){}.getType());
        //List<MenuConfiguracionMas> conf_mas = store.get(1, new TypeToken<List<MenuConfiguracionMas>>(){}.getType());
        /*
        System.out.println(conf.size());
        for(int i = 0; i < conf.size(); i++) {
        	System.out.println("conf");
        	System.out.println(conf.get(i).toString());
        }
        
        System.out.println(conf_mas.size());
        for(int i = 0; i < conf_mas.size(); i++) {
        	System.out.println("conf_mas");
        	System.out.println(conf_mas.get(i).toString());
        }

        miJpo.finalizar();*/

    }
	
	/*
	 * ALTER PROC [seg].[menu_configuraciones_testb] 
	@estado INT OUT,
	@mensaje varchar(max) OUT 
AS

SET NOCOUNT ON 
	
	set @estado = 1
	set @mensaje = 'Hola'
	 * */
	//@Test
    public void testProcedureParseDataSalida() throws Exception {

        Map<String, String> config = new HashMap<String, String>();

        config.put("type", "SQLSERVER");
        config.put("url", "10.10.10.51");
        config.put("db", "Inland");
        config.put("username", "sa");
        config.put("password", "Pr0t0c0l0");

        Jpo miJpo = new Jpo(config);
        
        Procedure store = miJpo.procedure("seg.menu_configuraciones_testb");
        store.output("estado", Jpo.INTEGER);
        store.output("mensaje", Jpo.CADENA);
        
        //MenuResultado conf = store.get(new TypeToken<MenuResultado>(){}.getType());
        
        System.out.println("conf");
    	//System.out.println(conf.toString());
        
        miJpo.finalizar();

    }
	
	@SuppressWarnings("unused")
	//@Test
    public void testInstanciaAutomática() throws Exception {
		
        Map<String, String> config = new HashMap<String, String>();

        config.put("type", "SQLSERVER");
        config.put("url", "10.10.10.51");
        config.put("db", "Inland");
        config.put("username", "sa");
        config.put("password", "Pr0t0c0l0");

		System.out.println("--> Inicio");
		
        Jpo miJpo = new Jpo(config);
        
        System.out.println(miJpo.hashConection);
       
        Procedure store = miJpo.procedure("seg.sistema_fecha");
        /*Object data = store.ejecutar();
        System.out.println(data);
*/
        Tabla usr = miJpo.tabla("seg.usuario");
        /*Object data_user = usr.seleccionar("id, correo, nombres");
        System.out.println(data_user);
        */
        
         
		System.out.println("--> Finalizo");
	}
	
	//@Test
    @SuppressWarnings("unchecked")
	public void testMaximoLimiteString() throws Exception {
		
        Map<String, String> config = new HashMap<String, String>();

        config.put("type", "SQLSERVER");
        config.put("url", "10.10.10.51");
        config.put("db", "Inland");
        config.put("username", "sa");
        config.put("password", "Pr0t0c0l0");

		System.out.println("--> Inicio");
		
        Jpo miJpo = new Jpo(config);
        
		Procedure pEmail = miJpo.procedure("bpm.tarea_correo_pendiente_enviar");
		pEmail.input("usuario_id", "1", Jpo.INTEGER);
		pEmail.input("instancia_tarea_id", "10553", Jpo.INTEGER);
		
		List<Object> rEmail = (List<Object>) pEmail.executeL(true); // [1, , {"id":155910}
		
		System.out.println(rEmail.get(2));
		
	}

	//@Test
    public void testAccess() throws Exception {
		Jpo ppo = new Jpo("dsbachero", null);
		System.out.println(ppo.hashConection);
		System.out.println(ppo.getSourceInfo());
		ppo.finalizar();
    }
    
	@SuppressWarnings("unused")
	//@Test
    public void testPGSQL() throws Exception {
		/*
        Map<String, String> config = new HashMap<String, String>();

        config.put("type", "POSTGRESQL");
        config.put("url", "postgresqlpilotodevopsdev.postgres.database.azure.com");
        config.put("db", "dgenerador");
        config.put("username", "adminpostgresql@postgresqlpilotodevopsdev");
        config.put("password", "5YG6T#W~CgP%#7&,");
*/
		System.out.println("--> Inicio");
		
        Jpo miJpo = new Jpo("{}", "dsbachero", null, null);
        
        System.out.println(miJpo.hashConection);
 	   System.out.println("------------------------------------------------------------------");
 	   System.out.println("public Function<Jpo, Object> buiinicializar()");
 	   System.out.println(miJpo.getSourceInfo());
       
        Tabla usr = miJpo.tabla("bui.usuario");
        Object data_user = usr.seleccionar("*");
        System.out.println(data_user);
        
		System.out.println("--> Finalizo");
	}
	
	//@Test
    public void testEnvVariable() throws Exception {

		  Jpo ppo = new Jpo();

		  System.out.println(ppo.getSourceInfo());
		  // SQLSERVER|pilotodbqa.database.windows.net|maersk-apmtis-sqlserver-inland-dev||adminsqlqa|H${:3Q]pE7X&N7JW
		  System.out.println(System.getenv().get("JPO_DS_URL"));
	      Tabla usr = ppo.tabla("seg.usuario");
	      Object data_user = usr.seleccionar("*");
	      System.out.println(data_user);
		
	}
	
	@SuppressWarnings("unused")
	//@Test
    public void testOutputEmpty() throws Exception {
		
        Map<String, String> config = new HashMap<String, String>();

        config.put("type", "SQLSERVER");
        config.put("url", "pesazu039db04.database.windows.net");
        config.put("db", "tucontenedor_qa");
        config.put("username", "adminapm");
        config.put("password", "Pr0t0c0l0");
        
        Jpo miJpo = new Jpo(config);
        
		Procedure pResult = miJpo.procedure("ges.catalogo_detalle_obtener","ADM");
		pResult.input("catalogo_id", Jpo.STRING);
		Object dasd = pResult.execute(true);
		System.out.println(dasd);
		
	}
	
	//@Test
	public void testMultipleConnections() throws Exception {
		
        Map<String, String> configA = new HashMap<String, String>();

        
        Jpo miJpoA = new Jpo(configA);
        	miJpoA.setData("BUI", "menu_base_id", "40329");
        
		Procedure pResult = miJpoA.procedure("seg.menu_listar_nuevo","BUI");
				  pResult.input("menu_base_id", Jpo.DECIMAL);
		Object ohb_responseA = pResult.executeL();
		
		
		
		
		
        Map<String, String> configB = new HashMap<String, String>();

		
        Jpo miJpoB = new Jpo(configB);
        	miJpoB.setData("BUI", "proyecto_id" , "2");
		
		Procedure pResultB = miJpoB.procedure("bui.plantilla_menu_listar","BUI");
				  pResultB.input("proyecto_id", Jpo.INTEGER);
				  pResultB.output("plantillas", Jpo.RESULT);
		Object ohb_responseB = pResultB.executeL();
		
		
		
		System.out.println(ohb_responseA);
		System.out.println(ohb_responseB);
		
		miJpoA.finalizar();
		miJpoB.finalizar();

	}
	
}