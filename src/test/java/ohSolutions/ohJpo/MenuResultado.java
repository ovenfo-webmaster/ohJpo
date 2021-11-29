package ohSolutions.ohJpo;

public class MenuResultado {
	
	private int estado;
	private String mensaje;
	
	public int getEstado() {
		return estado;
	}
	public void setEstado(int estado) {
		this.estado = estado;
	}
	public String getMensaje() {
		return mensaje;
	}
	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}
	
	@Override
	public String toString() {
		return "MenuResultado [estado=" + estado + ", mensaje=" + mensaje + "]";
	}
		
}