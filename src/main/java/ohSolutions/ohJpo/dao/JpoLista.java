package ohSolutions.ohJpo.dao;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.Gson;

public class JpoLista {
	
	private List<Object> resultado;

	public JpoLista(List<Object> resultado) {
		super();
		this.resultado = resultado;
	}
	
	public <T> T obtener(int indice, Type typeOfT) throws Exception{
		Gson gson = new Gson();
		return gson.fromJson(gson.toJson(resultado.get(indice)), typeOfT);
	}

}