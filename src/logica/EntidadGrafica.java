package logica;

import java.net.URL;

import javax.swing.ImageIcon;

public class EntidadGrafica {
	
	protected URL[] imagenes;
	protected ImageIcon grafico;
	
	public EntidadGrafica() {
		imagenes = new URL[10];
	    grafico = new ImageIcon();
		URL ruta = getClass().getResource("/img/Numeros/blank.png");
		imagenes[0] = ruta;
		for(int i = 1; i<=9; i++) {
		   ruta = getClass().getResource("/img/Numeros/n" + i + ".png");
		   imagenes[i] = ruta;
		}
   }
	
	public void actualizar(int valor) {
		if(valor<0 || valor>9)
		   valor = 0;
		grafico.setImage((new ImageIcon(imagenes[valor])).getImage());
	}
	
	public ImageIcon getGrafico() {
		return grafico;
	}
	
}
