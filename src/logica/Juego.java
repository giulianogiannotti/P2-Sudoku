package logica;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Juego {
	
	private static Logger logger;
	protected LocalTime inicio;
	protected int cantidadCeldasCompletasCorrectas;
	protected Celda [][] tablero;
	
	public Juego(String ruta, int tamano, int cantidadEliminar) {
		comenzarLogger();
		String linea;
		BufferedReader bufferedreader;
		String [] fila;
		try {
			if(!cuadradoPerfecto(tamano)) {
			   logger.severe("Error al momento de crear el tablero ");
			   throw new Exception();
			}
			tablero = new Celda[tamano][tamano];
			cantidadCeldasCompletasCorrectas = tamano * tamano;
			InputStream in = Juego.class.getResourceAsStream(ruta);
			InputStreamReader inr = new InputStreamReader(in);
			bufferedreader = new BufferedReader(inr);
			for(int i = 0; i<tamano; i++) {
				linea = bufferedreader.readLine();
				if (linea == null) {
					bufferedreader.close();
					logger.severe("Error al momento de crear el tablero. Faltan filas. ");
					throw new Exception();
				}
				fila = linea.trim().split(" ");
				if(fila.length < tamano) {
				   bufferedreader.close();
				   logger.severe("Error al momento de crear el tablero. Faltan columnas. ");
				   throw new Exception();
				}
				for(int j = 0; j<tamano; j++) {
				    tablero[i][j] = new Celda(Integer.parseInt(fila[j]));
				}
			}
			bufferedreader.close();
			if (control()) {
				logger.fine("La solucion inicial es la correcta. ");
			}
			else {
				logger.severe("La solucion inicial es incorrecta. ");
				throw new Exception();
			}
		} catch (Exception e) {
			tablero = null;
			return;
		}
        eliminarCeldas(cantidadEliminar);
		inicio = LocalTime.now();
	}
	
	public Juego(String ruta, int tamano) {
		this(ruta, tamano, 40);
	}
	
	public Juego() {
		this("/files/sk_bien.txt", 9);
	}
	
	private boolean cuadradoPerfecto(int n) {
		int i = 0;
		boolean cuadradoPerfecto = n == 1;
		while(i <= (n/2) && !cuadradoPerfecto) {
			cuadradoPerfecto = i*i == n;
			i++;
		}
		return cuadradoPerfecto;
	}
	
	private void eliminarCeldas(int cantidadEliminar) {
		Random r = new Random();
		int cantCeldasLinea = cantidadCeldasLinea();
		int cantMax = (int) Math.pow(cantCeldasLinea, 2);
		Celda celda;
		int contador;	
		if (cantidadEliminar > cantMax) {
			logger.info(cantidadEliminar + " Se superan la cantidad de celdas del tablero. Se van a eliminar " + cantMax);
			cantidadEliminar = cantMax;
		}
		cantidadCeldasCompletasCorrectas -= cantidadEliminar;
		contador = 0;
		while(contador < cantidadEliminar) {
			int f = r.nextInt(cantCeldasLinea);
			int c = r.nextInt(cantCeldasLinea);
			celda = tablero[f][c];
			if(celda.getValor() != 0) {
			   celda.setValor(0);
			   celda.setEditable(true);
			   contador++;
			   logger.finest("Se eliminara la celda (" + f + ", " + c + ")");
			}
		}
	}
	private void comenzarLogger() {
		if(logger == null) {
		   logger = Logger.getLogger(Juego.class.getName());
		   Handler handler = new ConsoleHandler();
		   handler.setLevel(Level.ALL);
		   logger.addHandler(handler);
		   logger.setLevel(Level.ALL);
		   Logger rootLoger = logger.getParent();
		   for (Handler hnd : rootLoger.getHandlers())
				hnd.setLevel(Level.OFF);
		}
	}

	private boolean control() {
		Celda c;
		for (int i = 0; i < tablero.length; i++) {
			for (int j = 0; j < tablero[i].length; j++) {
				c = tablero[i][j];
				if (!c.esCorrecta()) {
					c.setCorrecta(true);
				}
			}
		}
		cantidadCeldasCompletasCorrectas = (int) Math.pow(cantidadCeldasLinea(), 2);
		logger.fine("El tablero ha quedado con su estado correcto");
		return controlCeldasConflictivas();
	}
	
	public Celda[][] getTablero() {
		return tablero;
	}
	
	private boolean controlCeldasConflictivas() {
		boolean correctas = true;
		Map<Integer, Celda> map;
		Celda celdaAnterior;
		Celda celdaActual;
		int cantidadCeldasLinea;
		int cantidadPanelesLinea;
		int cantidadCeldasLineaPanel;
		int valor;
		cantidadCeldasLinea = cantidadCeldasLinea();
		
		for(int i = 0; i<cantidadCeldasLinea; i++) {
		    map = new HashMap<Integer, Celda>();
		    for(int j = 0; j<cantidadCeldasLinea; j++) {
			   celdaActual = tablero[i][j];
			   valor = celdaActual.getValor();
			   if(valor != 0) {
				  celdaAnterior = map.put(valor, celdaActual);
				  if(celdaAnterior != null) {
				     correctas = false;
					 celdaActual.setCorrecta(false);
					 logger.info("Se ha actualizado la celda incorrecta. ");
					 if (celdaAnterior.esCorrecta()) {
						celdaAnterior.setCorrecta(false);
						cantidadCeldasCompletasCorrectas--;
						logger.info("Se ha actualizado la celda incorrecta. ");
						}
					}
				}
				else {
					  cantidadCeldasCompletasCorrectas--;
				}
			}
		}
	
		for(int i = 0; i<cantidadCeldasLinea; i++) {
			map = new HashMap<Integer, Celda>();
			for(int j = 0; j<cantidadCeldasLinea; j++) {
				celdaActual = tablero[j][i];
				valor = celdaActual.getValor();
				if(valor != 0) {
				   celdaAnterior = map.put(valor, celdaActual);
				   if(celdaAnterior != null) {
					  correctas = false;
					  if(celdaActual.esCorrecta()) {
						 celdaActual.setCorrecta(false);
						 cantidadCeldasCompletasCorrectas--;
						 logger.info("Se ha actualizado la celda incorrecta. ");
					  }
				      if(celdaAnterior.esCorrecta()) {
				    	 celdaAnterior.setCorrecta(false);
						 cantidadCeldasCompletasCorrectas--;
						 logger.info("Se ha actualizado la celda incorrecta. ");
					  }
					}
				}
				else {
					  cantidadCeldasCompletasCorrectas--;
				}
			}
		}
		cantidadPanelesLinea = cantidadPanelesLinea();
		cantidadCeldasLineaPanel = cantidadCeldasLineaPanel();
		for (int i = 0; i<cantidadPanelesLinea; i++) {
			for(int j = 0; j<cantidadPanelesLinea; j++) {
				map = new HashMap<Integer, Celda>();
				for(int f = i*cantidadCeldasLineaPanel; f<(i+1)*cantidadCeldasLineaPanel; f++) {
                    for(int c = j*cantidadCeldasLineaPanel; c<(j+1)*cantidadCeldasLineaPanel; c++) {
						celdaActual = tablero[f][c];
						valor = celdaActual.getValor();
						if(valor != 0) {
						   celdaAnterior = map.put(valor, celdaActual);
						   if(celdaAnterior != null) {
							  correctas = false;
							  if(celdaActual.esCorrecta()) {
								 celdaActual.setCorrecta(false);
								 cantidadCeldasCompletasCorrectas--;
								 logger.info("Se actualizo la celda incorrecta. ");
							  }
						      if(celdaAnterior.esCorrecta()) {
								 celdaAnterior.setCorrecta(false);
								 cantidadCeldasCompletasCorrectas--;
								 logger.info("Se actualizo la celda incorrecta. ");
							  }
						   }
						}
						else {
							  cantidadCeldasCompletasCorrectas--;
						}
					}
				}
			}
		}
		return correctas;
	}
	

	public Celda getCelda(int f, int c) {
		if(0 <= f && f < tablero.length && 0 <= c && c < tablero[0].length) {
		   return tablero[f][c];
		}
		else {
			  logger.warning("Indice (" + f + ", " + c + ") fuera de los limites del tablero del juego. ");
			  return null;
		}
	}
	
	public int cantidadCeldasLinea() {
		return tablero.length;
	}
	
	public int cantidadCeldasLineaPanel() {
		return (int) Math.sqrt(tablero.length);
	}
	
	public void accion(Celda c, int valor) {
		c.setValor(valor);
		logger.fine("Se ha actualizado el valor de una celda del juego. ");
		control();
	}
	
	public int cantidadPanelesLinea() {
		return (int) Math.sqrt(tablero.length);
	}

	public boolean gano() {
		boolean ganar = cantidadCeldasCompletasCorrectas == Math.pow(cantidadCeldasLinea(), 2);  
		if (ganar) {
			logger.info("Gano el juego. ");
		}
		return ganar;
	}
	
	public Duration tiempoTranscurrido() {
		return Duration.between(inicio, LocalTime.now());
	}
	
}
 