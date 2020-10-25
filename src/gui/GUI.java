package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.Duration;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import logica.Celda;
import logica.Juego;

/**
 * 
 * @author Giuliano Giannotti
 *
 */
public class GUI extends JFrame {

	private JButton[][] tableroGrafico;
	private static final long serialVersionUID = 1L;
	private static Logger logger;
	private Timer reloj;
	private JButton sCeldaGrafica;
	private JPanel panelContenedor;
	private Juego juego;
	private Color colorCorrecto;
	private Color colorIncorrecto;
	private Color colorSeleccionado;

	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI frame = new GUI();
					frame.setVisible(true);
				    } catch (Exception e) {
					         e.printStackTrace();
				      }
			  
			}
		});
	}

	public GUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		panelContenedor = new JPanel();
		panelContenedor.setBorder(new EmptyBorder(5, 5, 5, 5));
		panelContenedor.setLayout(new BorderLayout(0, 0));
		setContentPane(panelContenedor);
		comenzarLogger();
		setTitle("Sudoku"); 
		juego = new Juego("/files/sk_bien.txt", 9, 22);
		int cantidadPanelesLinea = juego.cantidadPanelesLinea();
		int cantidadCeldasLineaPanel = juego.cantidadCeldasLineaPanel();
		int cantidadCeldasLinea = juego.cantidadCeldasLinea();
		if(juego.getTablero() == null) {
		   logger.severe("El juego no ha posido ser iniciado. ");
		   terminarJuego();
		}
		
		setBounds(300, 100, 1100, 575 + 50*(cantidadCeldasLinea/10 +1));
		Color color = new Color(255, 255, 204);
		Color bordes = new Color(102,102,102);
		Color bordeBotones = new Color(255,154,0);
		Color panelColor = Color.WHITE;
		colorIncorrecto = new Color(255,0,0);
		colorCorrecto = Color.WHITE;
		
		JPanel arriba = new JPanel();
		JPanel abajo = new JPanel();
		JPanel centro = new JPanel();

		panelContenedor.setBackground(color);
		arriba.setBackground(color);
		centro.setBackground(color);
		abajo.setBackground(color);
		panelContenedor.add(arriba, BorderLayout.NORTH);
		panelContenedor.add(centro, BorderLayout.CENTER);
		panelContenedor.add(abajo, BorderLayout.WEST);
		
		JPanel panelInfo; 
		JPanel panelTablero;
		JPanel panelBotones;
		Dimension dimPanelInfo = new Dimension(200, 50);
		Dimension dimTablero = new Dimension(550, 450); 
		Dimension dimPanelBotones = new Dimension(450, 70 * (cantidadCeldasLinea / 10 + 1));
		panelInfo = new JPanel();
		panelInfo.setBorder(new LineBorder(Color.BLACK, 3));
		panelInfo.setPreferredSize(dimPanelInfo);
		arriba.add(panelInfo);
		panelInfo.setBackground(panelColor);
		
		panelTablero = new JPanel();
		panelTablero.setBorder(new LineBorder(bordes));
		panelTablero.setPreferredSize(dimTablero);
		centro.add(panelTablero);
		panelTablero.setBackground(bordes);
        panelBotones = new JPanel();
		panelBotones.setBorder(new LineBorder(bordeBotones, 5));
		panelBotones.setPreferredSize(dimPanelBotones);
		abajo.add(panelBotones);
		panelBotones.setBackground(panelColor);
		
		JPanel relojPanel = new JPanel();
		JLabel [] timeDisplay = new JLabel[8];
		JLabel digito;
		Dimension dimensionReloj;
		Dimension digitoDim;	
		
		digitoDim = new Dimension(22, 44);
		dimensionReloj = new Dimension( (int)digitoDim.getWidth() * timeDisplay.length, (int) digitoDim.getHeight());
		relojPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
		relojPanel.setPreferredSize(dimensionReloj);
		relojPanel.setBackground(panelColor);
		
		for(int i = 0; i<timeDisplay.length; i++) {
		   digito = timeDisplay[i] = new JLabel();
		   digito.setVisible(true);
		   digito.setPreferredSize(digitoDim);
		   relojPanel.add(digito);
		}
		
	    panelInfo.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
		relojPanel.setVisible(true);
		panelInfo.add(relojPanel);
		ImageIcon icono = new ImageIcon(GUI.class.getResource("/img/Reloj/separador.png"));
		Image imagen = icono.getImage();
		Image imagenNueva = imagen.getScaledInstance((int)digitoDim.getWidth(), (int) digitoDim.getHeight(), java.awt.Image.SCALE_SMOOTH);
		icono.setImage(imagenNueva);
		timeDisplay[2].setIcon(icono); 
		timeDisplay[5].setIcon(icono);
		configurarReloj(timeDisplay);
		
		panelTablero.setLayout(new GridLayout(cantidadPanelesLinea, cantidadPanelesLinea, 1, 1));
	    tableroGrafico = new JButton[cantidadCeldasLinea][cantidadCeldasLinea];
		JPanel[][] Panel = new JPanel[cantidadPanelesLinea][cantidadPanelesLinea];
		Dimension dimensionCelda = new Dimension((int) dimTablero.getWidth()/cantidadCeldasLinea, (int) dimTablero.getHeight()/cantidadCeldasLinea);
		for(int i = 0; i<cantidadPanelesLinea; i++) {
		   for(int j = 0; j<cantidadPanelesLinea; j++) {
			  JPanel PanelInferior = new JPanel();
			  PanelInferior.setLayout(new GridLayout(cantidadCeldasLineaPanel, cantidadCeldasLineaPanel));
			  PanelInferior.setBackground(bordes); //Testing
			  panelTablero.add(PanelInferior);
			  Panel[i][j] = PanelInferior;
			}
	   }

		for (int i = 0; i<cantidadCeldasLinea; i++) {
			for (int j = 0; j<cantidadCeldasLinea; j++) {
				JPanel PanelInferior = Panel[i/cantidadPanelesLinea][j/cantidadPanelesLinea];
				JButton celdaGrafica = new JButton();
				tableroGrafico[i][j] = celdaGrafica;
			    celdaGrafica.setPreferredSize(dimensionCelda);
				celdaGrafica.setActionCommand(i+","+j);
				celdaGrafica.setIcon(juego.getCelda(i, j).getEntidadGrafica().getGrafico());
				PanelInferior.add(celdaGrafica);
                if(!juego.getCelda(i, j).esEditable()) {
				   celdaGrafica.setEnabled(false);
				}
				celdaGrafica.addActionListener(new ActionListener() {
					
					public void actionPerformed(ActionEvent e) {
						if (sCeldaGrafica == null) {
							sCeldaGrafica = celdaGrafica;
							sCeldaGrafica.setBackground(Color.GRAY);
						}
						else {
							  String[] indice = sCeldaGrafica.getActionCommand().split(",");
							  int f = Integer.parseInt(indice[0]);
							  int c = Integer.parseInt(indice[1]);
                              Color color;
							  if (juego.getCelda(f, c).esCorrecta())
								  color = colorCorrecto;
							  else
								  color = colorIncorrecto;
							
							sCeldaGrafica.setBackground(color);
							if(sCeldaGrafica == celdaGrafica) {
							   sCeldaGrafica = null;
							}
							else {
								  sCeldaGrafica = celdaGrafica;
								  sCeldaGrafica.setBackground(colorSeleccionado);
							}
						}
					}
				});
				
			}
		}
		actualizarTablero();
		
		ImageIcon iconoNuevo;
		Image imagenNuevo;
		panelBotones.setLayout(new GridLayout(cantidadCeldasLinea/10 + 1, cantidadCeldasLinea+1));
		for (int i = 0; i<cantidadCeldasLinea+1; i++) {
			JButton boton = new JButton();
		    boton.setBackground(Color.WHITE);
			panelBotones.add(boton);
			if(i < cantidadCeldasLinea) {
			   iconoNuevo = new ImageIcon(GUI.class.getResource("/img/Numeros/n" + (i+1) + ".png"));
			   boton.setActionCommand(Integer.toString(i+1));
			}
			else {
				  iconoNuevo = new ImageIcon(GUI.class.getResource("/img/Numeros/borrar.png"));
			 	  boton.setActionCommand("0");
			}
			imagenNuevo = iconoNuevo.getImage().getScaledInstance(30, 30, java.awt.Image.SCALE_SMOOTH);
			iconoNuevo.setImage(imagenNuevo);
			boton.setIcon(iconoNuevo);
			boton.addActionListener(new ActionListener() {
				
				public void actionPerformed(ActionEvent e) {
					if(sCeldaGrafica != null) {
					   String[] indice = sCeldaGrafica.getActionCommand().split(",");
					   int i = Integer.parseInt(indice[0]);
					   int j = Integer.parseInt(indice[1]);
					   int valor = Integer.parseInt(boton.getActionCommand());
					   juego.accion(juego.getCelda(i, j), valor);
					   actualizarTablero();
					   sCeldaGrafica = null;
					   if (juego.gano()) {
						   triunfo();
						}
					}
				}
			});
			
		}
	}
	
	private void configurarReloj(JLabel[] timeDisplay) {
		reloj = new Timer();
        int actualizacion = 1000;
        TimerTask actualizarReloj = new TimerTask() {
			
			public void run() {
				Duration duracion = juego.tiempoTranscurrido();
				String tiempo = String.format("%02d:%02d:%02d", duracion.toHours(), duracion.toMinutesPart(), duracion.toSecondsPart());
				Dimension dimension;
				JLabel digito;
				ImageIcon icono;
				int altura;
				int ancho;
				Image imagen;
				Image nuevaImagen;
				for(int i = timeDisplay.length-1; i>=0; i--) {
				    digito = timeDisplay[i]; 
					if(i != 2 && i != 5) {
					  dimension = digito.getPreferredSize();
					  ancho = (int) dimension.getWidth();
					  altura = (int) dimension.getHeight();
					  icono = new ImageIcon(GUI.class.getResource("/img/Reloj/d" + tiempo.charAt(i) + ".png"));
					  imagen = icono.getImage();
					  nuevaImagen = imagen.getScaledInstance(ancho, altura, java.awt.Image.SCALE_SMOOTH);
					  icono.setImage(nuevaImagen);
				      digito.setIcon(icono);
                    }
				}
			}
		};
    reloj.schedule(actualizarReloj, 0, actualizacion);
}

	private void terminarJuego() {
		if (reloj != null)
			reloj.cancel();
		JOptionPane.showMessageDialog(this, "Ha ocurrido un error inesperado a la hora de cargar el juego. ");
		System.exit(0);
	}
	
	private void actualizarTablero() {
		Image imagen;
		Image imagenNueva;
		JButton celdaGrafica;
		Celda celda;
		int altura;
		int ancho;
		Dimension dimension;
		ImageIcon grafico;
		Color color;
	    for(int i = 0; i<tableroGrafico.length; i++) {
		    for(int j = 0; j<tableroGrafico[i].length; j++) {
			   celdaGrafica = tableroGrafico[i][j];
			   celda = juego.getCelda(i, j);
			   if (celda.esCorrecta())
			       color = colorCorrecto;
			   else
			       color = colorIncorrecto;
				celdaGrafica.setBackground(color);
				if(celda.esModificada()) {
				   celda.setModificada(false);
				   dimension = celdaGrafica.getPreferredSize();
				   ancho = (int) dimension.getWidth() - 2;
				   altura = (int) dimension.getHeight() - 2;
				   grafico = celda.getEntidadGrafica().getGrafico();
				   imagen = celda.getEntidadGrafica().getGrafico().getImage();
				   imagenNueva = imagen.getScaledInstance(ancho, altura, java.awt.Image.SCALE_SMOOTH);
				   grafico.setImage(imagenNueva);
				}
				celdaGrafica.repaint();
				
			}
		}
	}
	
	private void comenzarLogger() {
		if (logger == null) {
			logger = Logger.getLogger(GUI.class.getName());
			Handler handler = new ConsoleHandler();
			handler.setLevel(Level.ALL);
			logger.addHandler(handler);
			logger.setLevel(Level.ALL);
			Logger rootLoger = logger.getParent();
			for (Handler hnd : rootLoger.getHandlers())
				hnd.setLevel(Level.OFF);
		}
	}
	
	private void triunfo() {
		for(int i = 0; i<tableroGrafico.length; i++) {
			for(int j = 0; j<tableroGrafico.length; j++) {
			   tableroGrafico[i][j].setBackground(new Color(117, 213, 100));
			   tableroGrafico[i][j].setEnabled(false);
			}
		}
		reloj.cancel();
		JOptionPane.showMessageDialog(this, "Felicitaciones!\nHa ganado el juego! ");
		int res = JOptionPane.showConfirmDialog(this, "¿Desea salir del juego?");
		if (res == 0)
			System.exit(0);
  	}

	


}
