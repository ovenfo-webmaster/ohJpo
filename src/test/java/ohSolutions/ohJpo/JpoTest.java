package ohSolutions.ohJpo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.google.gson.Gson;

import ohSolutions.ohJpo.dao.Jpo;
import ohSolutions.ohJpo.dao.Procedimiento;
import ohSolutions.ohJpo.dao.Procedure;
import ohSolutions.ohJpo.dao.Tabla;

public class JpoTest {

    //@Test
    public void testSQLite() throws Exception {

        /* in application.properties
            jpo.ds.type=SQLITE
            jpo.ds.url=C:\\myfold\\dsolutions\\data\\bd
            jpo.ds.db=dgenerador.db
        * */ 

        Jpo miJpo = new Jpo();
        // Discomment for test
        //System.out.println("Test executed");
        //System.out.println((new Gson()).toJson(miJpo.tabla("proyecto").seleccionar("*")));

        miJpo.finalizar();

    }

    //@Test
    public void testSQLiteConfig() throws Exception {

        Map<String, String> config = new HashMap<String, String>();

        config.put("type", "SQLITE");
        config.put("url", "C:\\myfolder\\myproject\\myapp\\bd");
        config.put("db", "mydb.db");

        Jpo miJpo = new Jpo(config);

        System.out.println("Result test");
        System.out.println((new Gson()).toJson(miJpo.tabla("proyecto").seleccionar("*")));

        miJpo.finalizar();

    }

    //@Test
    public void testSQLServer() throws Exception {

        /* in application.properties
            jpo.ds.type=SQLSERVER
            jpo.ds.url=192.168.1.20
            jpo.ds.db=MYDB
            jpo.ds.scheme=
            jpo.ds.username=user
            jpo.ds.password=password1
        * */

        Jpo miJpo = new Jpo();

        System.out.println("Result");
        System.out.println((new Gson()).toJson(miJpo.tabla("COMTC_ROL").seleccionar("*")));

        miJpo.finalizar();

    }

    //@Test
    public void testSQLServerFromDS() throws Exception {

        /* in application.properties
            spring.datasource.url=jdbc:sqlserver://192.168.1.23;databaseName=mydatabase
            spring.datasource.username=user
            spring.datasource.password=test
            spring.datasource.driverClassName=com.microsoft.sqlserver.jdbc.SQLServerDriver
            jpo.ds.source=spring.datasource
        * */

        Jpo miJpo = new Jpo();

        System.out.println("Result");
        System.out.println((new Gson()).toJson(miJpo.tabla("COMTC_ROL").seleccionar("*")));

        miJpo.finalizar();

    }

    //@Test
    public void testBoolean() throws Exception {

        /* in application.properties
            spring.datasource.url=jdbc:sqlserver://192.168.1.200;databaseName=mydatabase
            spring.datasource.username=userp
            spring.datasource.password=passw0rd1
            spring.datasource.driverClassName=com.microsoft.sqlserver.jdbc.SQLServerDriver
            jpo.ds.source=spring.datasource
        * */

        Jpo miJpo = new Jpo();

        System.out.println("Result");
        System.out.println((new Gson()).toJson(miJpo.tabla("mytable").donde("myid = '34343'").seleccionar("*")));

        miJpo.finalizar();

    }

    //@Test
    public void testQueryDatetime() throws Exception {

        /* in application.properties
            spring.datasource.url=jdbc:sqlserver://192.168.1.35;databaseName=mydatabase
            spring.datasource.username=user
            spring.datasource.password=passw
            spring.datasource.driverClassName=com.microsoft.sqlserver.jdbc.SQLServerDriver
            jpo.ds.source=spring.datasource
        * */

        Jpo jpo = new Jpo();

        Procedimiento pDetalle = jpo.procedimiento("mystoreprocedure");
        pDetalle.entrada("projectcode", Jpo.CADENA);
        pDetalle.entrada("date", Jpo.FECHAHORA);
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        System.out.println(dateFormat.format(date));
        pDetalle.setValor("Fecha", dateFormat.format(date));
        Object resultado = pDetalle.ejecutarL();

        jpo.finalizar();

        System.out.println("Result");
        System.out.println((new Gson()).toJson(resultado));


    }
    
    //@Test
    public void textXMLReg() throws Exception {

        Map<String, String> config = new HashMap<String, String>();

        config.put("type", "SQLSERVER");
        config.put("url", "192.168.1.22");
        config.put("db", "mydb");
        config.put("username", "user");
        config.put("password", "passw0rd1");
        
        Jpo ppo = new Jpo(config);
        
        ppo.setData("FLG", "xmlParametro", "<myrow><line><product>1</product></line></myrow>");
        
		Procedure pResult = ppo.procedure("mystoreprocedure","mys");
		pResult.input("xmlparam", Jpo.XML);
		pResult.output("message", Jpo.STRING);
		pResult.output("result", Jpo.CHARACTER);
		Object response = pResult.executeL();
		ppo.commit();
		ppo.finalizar();
        System.out.println("Result");
        System.out.println((new Gson()).toJson(response));

        
    }

	//@Test
    @SuppressWarnings("unused")
	public void testParseData() throws Exception {

        Map<String, String> config = new HashMap<String, String>();

        config.put("type", "SQLSERVER");
        config.put("url", "192.168.1.33");
        config.put("db", "mydb");
        config.put("username", "myuser");
        config.put("password", "mypassword11");

        Jpo miJpo = new Jpo(config);
        
        Procedure store = miJpo.procedure("menu_configurations");
        /*
        List<MenuConfiguracionMas> outputList = store.get(new TypeToken<List<MenuConfiguracionMas>>(){}.getType());
        
        System.out.println(outputList.size());
        
        for(int i = 0; i < outputList.size(); i++) {
        	System.out.println("test");
        	System.out.println(outputList.get(i).toString());
        }*/

        miJpo.finalizar();

    }

	//@Test
    @SuppressWarnings("unused")
	public void testProcedureParseDataMultiple() throws Exception {

        Map<String, String> config = new HashMap<String, String>();

        config.put("type", "SQLSERVER");
        config.put("url", "192.168.1.22");
        config.put("db", "mydb");
        config.put("username", "myuser");
        config.put("password", "mypass");

        Jpo miJpo = new Jpo(config);
        
        Procedure store = miJpo.procedure("my.sp");
        
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
	
	//@Test
    public void testProcedureParseDataSalida() throws Exception {

        Map<String, String> config = new HashMap<String, String>();

        config.put("type", "SQLSERVER");
        config.put("url", "192.168.1.55");
        config.put("db", "mydb");
        config.put("username", "myuser");
        config.put("password", "mypassword");

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
        config.put("url", "192.168.1.55");
        config.put("db", "mydb");
        config.put("username", "myuser");
        config.put("password", "mypassword");

		System.out.println("--> start");
		
        Jpo miJpo = new Jpo(config);
        
        System.out.println(miJpo.hashConection);
       
        Procedure store = miJpo.procedure("env.system");
        /*Object data = store.ejecutar();
        System.out.println(data);
*/
        Tabla usr = miJpo.tabla("usr.tab");
        /*Object data_user = usr.seleccionar("id, correo, nombres");
        System.out.println(data_user);
        */
        
         
		System.out.println("--> finish");
	}
	
	//@Test
    @SuppressWarnings("unchecked")
	public void testMaximoLimiteString() throws Exception {
		
        Map<String, String> config = new HashMap<String, String>();

        config.put("type", "SQLSERVER");
        config.put("url", "192.168.1.55");
        config.put("db", "mydb");
        config.put("username", "myuser");
        config.put("password", "mypassword");

		System.out.println("--> Start");
		
        Jpo miJpo = new Jpo(config);
        
		Procedure pEmail = miJpo.procedure("seg.mysp");
		pEmail.input("user", "1", Jpo.INTEGER);
		pEmail.input("instancetask", "10553", Jpo.INTEGER);
		
		List<Object> rEmail = (List<Object>) pEmail.executeL(true); // [1, , {"id":155910}
		
		System.out.println(rEmail.get(2));
		
	}

	//@Test
    public void testAccess() throws Exception {
		Jpo ppo = new Jpo("batcher", null);
		System.out.println(ppo.hashConection);
		System.out.println(ppo.getSourceInfo());
		ppo.finalizar();
    }
    
	//@Test
    public void testPGSQL() throws Exception {

		System.out.println("--> Start");
		
        Jpo miJpo = new Jpo("{}", "mydatabssource", null, null);
        
        System.out.println(miJpo.hashConection);
 	   	System.out.println(miJpo.getSourceInfo());
       
        Tabla usr = miJpo.tabla("schema.table");
        Object data_user = usr.seleccionar("*");
        System.out.println(data_user);
        
		System.out.println("--> Finish");
	}
	
	//@Test
    public void testEnvVariable() throws Exception {

		  Jpo ppo = new Jpo();

		  System.out.println(ppo.getSourceInfo());
		  System.out.println(System.getenv().get("JPO_DS_URL"));
	      Tabla usr = ppo.tabla("my.tab");
	      Object data_user = usr.seleccionar("*");
	      System.out.println(data_user);
		
	}
	
	//@Test
    public void testOutputEmpty() throws Exception {
		
        Map<String, String> config = new HashMap<String, String>();

        config.put("type", "SQLSERVER");
        config.put("url", "mydatabase");
        config.put("db", "mydb");
        config.put("username", "user");
        config.put("password", "pass");
        
        Jpo miJpo = new Jpo(config);
        
		Procedure pResult = miJpo.procedure("sch.table","TAB");
		pResult.input("cat_id", Jpo.STRING);
		Object dasd = pResult.execute(true);
		System.out.println(dasd);
		
	}
	
	//@Test
	public void testMultipleConnections() throws Exception {
		
        Map<String, String> configA = new HashMap<String, String>();

        
        Jpo miJpoA = new Jpo(configA);
        	miJpoA.setData("BUI", "menu_id", "65555");
        
		Procedure pResult = miJpoA.procedure("seg.menu_list","BUI");
				  pResult.input("menu_base_id", Jpo.DECIMAL);
		Object ohb_responseA = pResult.executeL();
		
		
        Map<String, String> configB = new HashMap<String, String>();

		
        Jpo miJpoB = new Jpo(configB);
        	miJpoB.setData("BUI", "project_id" , "2");
		
		Procedure pResultB = miJpoB.procedure("tab.mytable","BUI");
				  pResultB.input("project_id", Jpo.INTEGER);
				  pResultB.output("templates", Jpo.RESULT);
		Object ohb_responseB = pResultB.executeL();
		
		System.out.println(ohb_responseA);
		System.out.println(ohb_responseB);
		
		miJpoA.finalizar();
		miJpoB.finalizar();

	}
	
    @SuppressWarnings("unchecked")
	//@Test
    public void testConectionSQLiteLast() throws Exception {

        Map<String, String> config = new HashMap<String, String>();

        config.put("type", "SQLITE");
        config.put("url", "C:\\myfolder\\wks\\database");
        config.put("db", "mydb.db");
        
        Jpo miJpo = new Jpo(config);
        
        Tabla tNumerator = miJpo.tabla("numerator");
        
        tNumerator.donde("numerator_id = 1");
        
        Map<String, Object> oLastNumerator = (Map<String, Object>) tNumerator.obtener("last_numerator");
       
        int lastNumerator = (int) oLastNumerator.get("last_numerator");

        System.out.println(lastNumerator);
        
        tNumerator.setData("last_numerator", "250");
        tNumerator.editar();
        miJpo.commit();
        
        System.out.println("Edited");
        
        miJpo.finalizar();
        
    }
    
	@Test
	public void testOracle() throws Exception {
		
        Map<String, String> config = new HashMap<String, String>();

        config.put("type", "ORACLE");
        config.put("url", "mydatabaseoracle:1521");
        config.put("db", "mydb");
        config.put("username", "user");
        config.put("password", "password1");

		System.out.println("--> Start");
		
        Jpo miJpo = new Jpo(config);
        
        
		Procedure pList = miJpo.procedure("mysp");
		pList.input("user", null, Jpo.STRING);
		pList.input("pass", null, Jpo.STRING);
		pList.output("c1", Jpo.RESULT);
		pList.output("c2", Jpo.RESULT);
		
        System.out.println(pList.execute());
 
	}
    
	@Test
	public void testload() throws Exception {
		
        Map<String, String> config = new HashMap<String, String>();

        config.put("type", "ORACLE");
        config.put("url", "mydatabaseoracle:1521");
        config.put("db", "mydb");
        config.put("username", "user");
        config.put("password", "password1");

		System.out.println("--> Start");
		
        Jpo miJpo = new Jpo(config);
        
        
		Procedure pList = miJpo.procedure("person_list");
		pList.input("p_first_name", null, Jpo.STRING);
		pList.input("p_last_name", null, Jpo.STRING);
		pList.output("c1", Jpo.RESULT);
		pList.output("c2", Jpo.RESULT);
		
        System.out.println(pList.execute());
        
	}
	
	
}