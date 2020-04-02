package ohSolutions.ohJpo;

public class MenuConfiguracion {
	
	private int catalogo_id;
	private String descricion_larga;
	
	public int getCatalogo_id() {
		return catalogo_id;
	}
	public void setCatalogo_id(int catalogo_id) {
		this.catalogo_id = catalogo_id;
	}
	public String getDescricion_larga() {
		return descricion_larga;
	}
	public void setDescricion_larga(String descricion_larga) {
		this.descricion_larga = descricion_larga;
	}
	
	@Override
	public String toString() {
		return "MenuConfiguracion [catalogo_id=" + catalogo_id + ", descricion_larga=" + descricion_larga + "]";
	}
	
}