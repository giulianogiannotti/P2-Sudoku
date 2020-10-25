package logica;

public class Celda {
	
	protected boolean correcta;
	protected boolean editable;
	protected boolean modificada;
	protected int valor;
	protected EntidadGrafica entidadGrafica;

	public Celda(int v) {
		valor = v;
		correcta = true;
		editable = false;
		modificada = true;
		entidadGrafica = new EntidadGrafica();
		entidadGrafica.actualizar(valor);
	}

	public EntidadGrafica getEntidadGrafica() {
		return entidadGrafica;
	}
	
	public void setValor(int v) {
	    valor = v;
		entidadGrafica.actualizar(valor);
		modificada = true;
	}
	
	public int getValor() {
		return valor;
	}
	
	public void setCorrecta(boolean c) {
		correcta = c;
	}

	public boolean esCorrecta() {
		return correcta;
	}
	
	public void setEditable(boolean e) {
		editable = e;
	}
    
    public boolean esEditable() {
		return editable;
	}
    
	public void setModificada(boolean m) {
		modificada = m;
	}

	public boolean esModificada() {
		return modificada;
	}

}
