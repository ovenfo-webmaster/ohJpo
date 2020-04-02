package ohSolutions.ohJpo;

public class MenuConfiguracionMas {
	
	private int catalogo_id;
	private int unidad_negocio_id;
	private int catalogo_padre_id;
	private String descricion;
	private String descricion_larga;
	private String estado;
	private int usuario_registro_id;
	private String fecha_registro;
	private int usuario_modificacion_id;
	private String fecha_modificacion;
	private String variable_1;
	private String variable_2;
	private String variable_3;
	
	public int getCatalogo_id() {
		return catalogo_id;
	}
	public void setCatalogo_id(int catalogo_id) {
		this.catalogo_id = catalogo_id;
	}
	public int getUnidad_negocio_id() {
		return unidad_negocio_id;
	}
	public void setUnidad_negocio_id(int unidad_negocio_id) {
		this.unidad_negocio_id = unidad_negocio_id;
	}
	public int getCatalogo_padre_id() {
		return catalogo_padre_id;
	}
	public void setCatalogo_padre_id(int catalogo_padre_id) {
		this.catalogo_padre_id = catalogo_padre_id;
	}
	public String getDescricion() {
		return descricion;
	}
	public void setDescricion(String descricion) {
		this.descricion = descricion;
	}
	public String getDescricion_larga() {
		return descricion_larga;
	}
	public void setDescricion_larga(String descricion_larga) {
		this.descricion_larga = descricion_larga;
	}
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}
	public int getUsuario_registro_id() {
		return usuario_registro_id;
	}
	public void setUsuario_registro_id(int usuario_registro_id) {
		this.usuario_registro_id = usuario_registro_id;
	}
	public String getFecha_registro() {
		return fecha_registro;
	}
	public void setFecha_registro(String fecha_registro) {
		this.fecha_registro = fecha_registro;
	}
	public int getUsuario_modificacion_id() {
		return usuario_modificacion_id;
	}
	public void setUsuario_modificacion_id(int usuario_modificacion_id) {
		this.usuario_modificacion_id = usuario_modificacion_id;
	}
	public String getFecha_modificacion() {
		return fecha_modificacion;
	}
	public void setFecha_modificacion(String fecha_modificacion) {
		this.fecha_modificacion = fecha_modificacion;
	}
	public String getVariable_1() {
		return variable_1;
	}
	public void setVariable_1(String variable_1) {
		this.variable_1 = variable_1;
	}
	public String getVariable_2() {
		return variable_2;
	}
	public void setVariable_2(String variable_2) {
		this.variable_2 = variable_2;
	}
	public String getVariable_3() {
		return variable_3;
	}
	public void setVariable_3(String variable_3) {
		this.variable_3 = variable_3;
	}
	
	@Override
	public String toString() {
		return "MenuConfiguracionMas [catalogo_id=" + catalogo_id + ", unidad_negocio_id=" + unidad_negocio_id
				+ ", catalogo_padre_id=" + catalogo_padre_id + ", descricion=" + descricion + ", descricion_larga="
				+ descricion_larga + ", estado=" + estado + ", usuario_registro_id=" + usuario_registro_id
				+ ", fecha_registro=" + fecha_registro + ", usuario_modificacion_id=" + usuario_modificacion_id
				+ ", fecha_modificacion=" + fecha_modificacion + ", variable_1=" + variable_1 + ", variable_2="
				+ variable_2 + ", variable_3=" + variable_3 + "]";
	}
	
}