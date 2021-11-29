/*
 * PPO v1.7.0
 * Author		: Oscar Huertas
 * Date			: 2017-09-27
 * Update date	: 2018-07-11 - v1.7.0
 * Description	: Define the field for Store procedure: Name, type, rules
 * */
package ohSolutions.ohJpo.dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Campo {
	
	public final static String _REQUIRED = "REQUIRED";
	public final static String _INTEGER = "INTEGER";
	public final static String _DECIMAL = "DECIMAL";
	public final static String _MV = "MV";
	public final static String _XV = "XV";
	public final static String _ML = "ML";
	public final static String _XL = "XL";
	public final static String _EL = "EL";
	public final static String _KEY = "KEY";
	public final static String _NAME = "NAME";
	public final static String _EMAIL = "EMAIL";
	public final static String _DATE = "DATE";
	public final static String _DATETIME = "DATETIME";
	public final static String _BOOL = "BOOL";
	
	private String nombre;
	private String valor;
	private int tipoParametro;
	private int tipoDato;
	private String validacion;
	private String formato;
	
	private Map<String, String> reglas = new HashMap<String, String>();
	private LinkedHashMap<String, String> valida = new LinkedHashMap<String, String>();
	private Map<String, String> exps = new HashMap<String, String>();
	
	private static String fechaFormatoDefecto = "dd/MM/yyyy";
	private static String fechaHoraFormatoDefecto = "dd/MM/yyyy HH:mm:ss";
	
	public Campo(String nombre) {
		super();
		this.nombre = nombre;
	}
	
	public Campo(String nombre, int tipoParametro, int tipoDato) throws Exception {
		super();
		inicializar(nombre, tipoParametro, tipoDato, null, null);
	}

	public Campo(String nombre, int tipoParametro, int tipoDato, String validacion) throws Exception {
		super();
		inicializar(nombre, tipoParametro, tipoDato, validacion, null);
	}
	
	public Campo(String nombre, int tipoParametro, int tipoDato, String validacion, String valor) throws Exception {
		super();
		inicializar(nombre, tipoParametro, tipoDato, validacion, valor);
	}
	
	private void inicializar(String nombre, int tipoParametro, int tipoDato, String validacion, String valor) throws Exception {
		this.nombre = nombre;
		this.tipoParametro = tipoParametro;
		this.tipoDato = tipoDato;
		this.validacion = validacion;
		this.valor = valor;

		if(Jpo.FECHA == this.tipoDato || Jpo.FECHAHORA == this.tipoDato) {
			this.formato = (Jpo.FECHA == this.tipoDato)?fechaFormatoDefecto:fechaHoraFormatoDefecto;
		}
		if(validacion != null) {
			cargaReglas();
			cargarVali();
		}
	}
	
	public String getNombre() {
		return nombre;
	}
	
	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}
	
	public int getTipoParametro() {
		return tipoParametro;
	}

	public int getTipoDato() {
		return tipoDato;
	}

	private void cargaReglas() {
		reglas.put(_REQUIRED, ""); // Required
		reglas.put(_INTEGER, ""); // Valor entero
		reglas.put(_DECIMAL, ""); // Valor decimal
		reglas.put(_MV, ""); // Valor minimo
		reglas.put(_XV, ""); // Valor maximo
		reglas.put(_ML, ""); // Longitud mnima
		reglas.put(_XL, ""); // Longitud maximo
		reglas.put(_EL, ""); // Longitud exacta
		reglas.put(_KEY, ""); // Minimo 1 digito, 1 Mayuscula, 1 minuscula, 1 caracter especial, no espacios, 8 caracteres
		reglas.put(_NAME, ""); // Nombre
		reglas.put(_EMAIL, ""); // Correo
		reglas.put(_DATE, ""); // Fecha dd/MM/yyyy o dd-MM-yyyy
		reglas.put(_DATETIME, ""); // Fecha dd/MM/yyyy o dd-MM-yyyy
		reglas.put(_BOOL, ""); // Condicion True False
		
		exps.put(_DECIMAL, "^(?!-0?(\\.0+)?$)-?(0|[1-9]\\d*)?(\\.\\d+)?(?<=\\d)$");
		exps.put(_KEY, "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$");
		exps.put(_NAME, "^[\\\\p{L} .'-]+$");
		exps.put(_EMAIL, "^[\\\\w-]+(\\\\.[\\\\w-]+)*@[A-Za-z0-9]+(\\\\.[A-Za-z0-9]+)*(\\\\.[A-Za-z]{2,})$");
		//exps.put("FECHA", "^[0-9]{1,2}(/|-)[0-9]{1,2}(/|-)[0-9]{4}$");
		
	}
	
	private void cargarVali() throws Exception {
		if(this.validacion != null && this.validacion.length()>0) {
			String[] condis = this.validacion.split("\\|");
			for(int i = 0; i < condis.length; i++) {
				String condi = condis[i].trim();
				if(condi.length()>0) {
					String[] parteCond = condi.split(":");
					String regla = "";
					String valor = "";
					if(parteCond.length>1) {
						regla = parteCond[0].trim().toUpperCase();
						if(parteCond.length==2) {
							valor = parteCond[1].trim();
						} else {
							for(int e = 1; e < parteCond.length; e++) {
								valor += parteCond[e]+":";
							}
							valor = valor.substring(0, valor.length()-1).trim();
						}
					} else {
						regla = parteCond[0].trim().toUpperCase();
					}
					if(reglas.get(regla) != null) {
						if(valor.length()>0) {
							valida.put(regla, valor);
						} else {
							valida.put(regla, "OK");
						}
					} else {
						throw new Exception("Regla '"+regla+"' para el parametro ´"+this.nombre+"´ no identificada");
					}
				}
			}
		}
	}

	public boolean validar() throws Exception {
		if(this.valida.size()>0) {
			for (Map.Entry<String, String> entry : valida.entrySet()) {
				String campo = entry.getKey();
				String regla = entry.getValue();
				if(campo.equals(_REQUIRED) && (valor==null || valor.length()==0)) {
					throw new Exception("The field ´"+this.nombre+"´ is required");
				}
				if(campo.equals(_INTEGER) && valor!=null && valor.length()>0 && !isInteger()) {
					throw new Exception("The field ´"+this.nombre+"´ must be integer");
				}
				if(campo.equals(_DECIMAL) && valor!=null && valor.length()>0 && !validarExp(campo)) {
					throw new Exception("The field ´"+this.nombre+"´ must be decimal");
				}
				if(campo.equals(_MV) && valor!=null && MenorA(regla)) {
					throw new Exception("The field ´"+this.nombre+"´ must has a min value of "+regla);
				}
				if(campo.equals(_XV) && valor!=null && MayorA(regla)) {
					throw new Exception("The field ´"+this.nombre+"´ must has a max value of "+regla);
				}
				if(campo.equals(_ML) && valor!=null && valor.length() < Integer.parseInt(regla)) {
					throw new Exception("The field ´"+this.nombre+"´ must has a min length of "+regla+" caracteres");
				}
				if(campo.equals(_XL) && valor!=null && valor.length() > Integer.parseInt(regla)) {
					throw new Exception("The field ´"+this.nombre+"´ must has a max legnth of "+regla+" caracteres");
				}
				if(campo.equals(_EL) && valor!=null && valor.length() != Integer.parseInt(regla)) {
					throw new Exception("The field ´"+this.nombre+"´ must has a length of "+regla+" caracteres");
				}
				if(campo.equals(_KEY) && valor!=null && valor.length()>0 && !validarExp(campo)) {
					throw new Exception("The field ´"+this.nombre+"´ must be a valid key : 1 digit, 1 Mayus, 1 Minus, 1 especial character, no spaces, min 8 characters");
				}
				if(campo.equals(_NAME) && valor!=null && valor.length()>0 && !validarExp(campo)) {
					throw new Exception("The field ´"+this.nombre+"´ must be a text");
				}
				if(campo.equals(_EMAIL) && valor!=null && valor.length()>0 && !validarExp(campo)) {
					throw new Exception("The field ´"+this.nombre+"´ must be a email");
				}
				if(campo.equals(_DATE) && this.tipoDato == Jpo.FECHA && valor.length()>0 && !this.valor.equals("null") && !validarFecha()) {
					throw new Exception("The field ´"+this.nombre+"´ must be a format date ´"+getFormato()+"´");
				}
				if(campo.equals(_DATETIME) && this.tipoDato == Jpo.FECHAHORA && valor.length()>0 && !this.valor.equals("null") && !validarFecha()) {
					throw new Exception("The field ´"+this.nombre+"´ must be a format datetime ´"+getFormato()+"´");
				}
				if(campo.equals(_BOOL) && valor!=null && valor.length()>0 && !validarCondicion()) {
					throw new Exception("The field ´"+this.nombre+"´ must be ´true´ o ´false´");
				}
			}
		} else {
			if(this.tipoDato == Jpo.FECHA && this.valor != null && this.valor.length()>0 && !this.valor.equals("null") && !validarFecha()) {
				throw new Exception("The field ´"+this.nombre+"´ must be a format date ´"+getFormato()+"´");
			}
			if(this.tipoDato == Jpo.FECHAHORA && this.valor != null && this.valor.length()>0 && !this.valor.equals("null") && !validarFecha()) {
				throw new Exception("The field ´"+this.nombre+"´ must be a format datetime ´"+getFormato()+"´");
			}
		} 
		if(valida.get(_BOOL) != null && valor!=null && valor.length()!=0) {
			this.valor = this.valor.equals("true")?"1":"0";
		}
		return true;
	}
	
	private boolean validarFecha() {
		String formato = this.formato;
		SimpleDateFormat formatoSDF = new SimpleDateFormat(formato);
		formatoSDF.setLenient(false);
	    try {
			formatoSDF.parse(this.valor);
			return true;
		} catch (ParseException e) {
			return false;
		}
	}

	private boolean MenorA(String regla) {
		if(this.tipoDato == Jpo.ENTERO || this.valida.get(_INTEGER) != null) {
			if(Integer.parseInt(valor) < Integer.parseInt(regla)) {
				return true;
			}
		}
		if(this.tipoDato == Jpo.DECIMAL || this.valida.get(_DECIMAL) != null) {
			if(Double.parseDouble(valor) < Double.parseDouble(regla)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean MayorA(String regla) {
		if(this.tipoDato == Jpo.ENTERO || this.valida.get(_INTEGER) != null) {
			if(Integer.parseInt(valor) > Integer.parseInt(regla)) {
				return true;
			}
		}
		if(this.tipoDato == Jpo.DECIMAL || this.valida.get(_DECIMAL) != null) {
			if(Double.parseDouble(valor) > Double.parseDouble(regla)) {
				return true;
			}
		}
		return false;
	}

	private boolean validarCondicion() {
		if(this.valor.toLowerCase().equals("true") || this.valor.toLowerCase().equals("false")) {
			return true;
		}
		return false;
	}

	public boolean validarExp(String Tipo) {
		String exp = exps.get(Tipo);
		if(exp !=null && ((Pattern.compile(exp)).matcher(this.valor)).matches()) {
			return true;
		} else {
			return false;
		}
	}

	private boolean isInteger() {
	    return isInteger(this.valor,10);
	}

	private boolean isInteger(String s, int radix) {
	    if(s.isEmpty()) return false;
	    for(int i = 0; i < s.length(); i++) {
	        if(i == 0 && s.charAt(i) == '-') {
	            if(s.length() == 1) return false;
	            else continue;
	        }
	        if(Character.digit(s.charAt(i),radix) < 0) return false;
	    }
	    return true;
	}

	public String getFormato() {
		return this.formato;
	}
	
}