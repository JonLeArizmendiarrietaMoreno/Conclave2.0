package gui;

/**
 * @author Jon Le Arizmendiarrieta
 */

import businessLogic.BLFacade;






import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.toedter.calendar.JCalendar;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.SystemColor;

import domain.*;


public class MainGUI extends JFrame {
	
	
	private static final long serialVersionUID = 1L;

	private JPanel jContentPane = null;

	private JButton IniciarConclave;

	private JButton IniciarVotacion;

    private static BLFacade blfacadeinterface;
	
    public static BLFacade getBusinessLogic(){
        return blfacadeinterface;
    }
	 
	public static void setBussinessLogic (BLFacade facade){
		blfacadeinterface=facade;
	}
	
	protected JLabel jLabelSelectOption;
	private JTextField NombrePersona;
	
	private JTextField textNombrePersona;   // NombrePersona
	private JCalendar calendarFechaInicio; // fechaInicio
	private JSpinner spinnerHora;          // hora (con formato HH:mm)
	private Date fecha;

	
	
	private JCalendar jCalendar = new JCalendar();
	private Calendar calendarAct = null;
	private Calendar calendarAnt = null;
	private JTextArea displayMainGUI;
	
	
	private static MainGUI instancia;

	/**
	 * This is the default constructor
	 */
	private MainGUI() {
		
		super();
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		this.setSize(621, 378);
		jLabelSelectOption = new JLabel("MainGUI");
		jLabelSelectOption.setBounds(10, 11, 51, 16);
		jLabelSelectOption.setFont(new Font("Tahoma", Font.BOLD, 13));
		jLabelSelectOption.setForeground(Color.BLACK);
		jLabelSelectOption.setHorizontalAlignment(SwingConstants.CENTER);
		
		IniciarConclave = new JButton();
		IniciarConclave.setBounds(356, 159, 109, 23);
		IniciarConclave.setText("Iniciar Conclave");
		

		IniciarVotacion = new JButton();
		IniciarVotacion.setBounds(366, 193, 105, 23);
		IniciarVotacion.setText("Iniciar Votacion");
				
		jContentPane = new JPanel();
		setContentPane(jContentPane);
		
		
		JLabel lblHora = new JLabel("Hora:");
		lblHora.setBounds(475, 160, 40, 20);
		jContentPane.add(lblHora);
		
		
		SpinnerDateModel modelHora = new SpinnerDateModel();
		spinnerHora = new JSpinner(modelHora);
		
		JSpinner.DateEditor editorHora = new JSpinner.DateEditor(spinnerHora, "HH:mm");
		spinnerHora.setEditor(editorHora);
		spinnerHora.setBounds(525, 158, 80, 25);
		jContentPane.add(spinnerHora);
		
		
		JButton RegistrarPersona = new JButton("Registrar Persona"); //$NON-NLS-1$ //$NON-NLS-2$
		RegistrarPersona.setBounds(356, 227, 124, 23);
		RegistrarPersona.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		
		
		JButton CerrarVotacion = new JButton("Cerrar Votacion");
		CerrarVotacion.setBounds(498, 191, 107, 23);
		
		
		JButton AceptarRechazarCandidatura = new JButton("AceptarRechazarCandidatura");
		AceptarRechazarCandidatura.setEnabled(false);
		
		AceptarRechazarCandidatura.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		        try {
		            String mensaje = blfacadeinterface.procesarDecisionCandidatoDesdeGUI();
		            mostrarMensaje(mensaje);
		        } catch (Exception ex) {
		        	mostrarMensaje("Error: " + ex.getMessage());
		        }
		    }
		});
		
		AceptarRechazarCandidatura.setBounds(366, 305, 175, 23);
		jContentPane.add(AceptarRechazarCandidatura);
		
		
		NombrePersona = new JTextField();
		NombrePersona.setForeground(SystemColor.activeCaptionBorder);
		NombrePersona.setText("NombrePersona");
		NombrePersona.setBounds(482, 225, 119, 25);
		NombrePersona.setColumns(10);
		jContentPane.setLayout(null);
		jContentPane.add(jLabelSelectOption);
		jContentPane.add(IniciarConclave);
		jContentPane.add(IniciarVotacion);
		jContentPane.add(RegistrarPersona);
		jContentPane.add(CerrarVotacion);
		jContentPane.add(NombrePersona);
		setTitle("Conclave2.0");
		
		
		jCalendar.setBounds(new Rectangle(350, 0, 255, 150));
		this.getContentPane().add(jCalendar, null);
		
		
		
		
		displayMainGUI = new JTextArea();
		displayMainGUI.setText("Dios esta moribundo y lo voy a rematar");
		displayMainGUI.setBounds(0, 38, 340, 290);
		displayMainGUI.setLineWrap(true);
		displayMainGUI.setWrapStyleWord(true);
		displayMainGUI.setLineWrap(true);
		displayMainGUI.setWrapStyleWord(true);


		jContentPane.add(displayMainGUI);
		
		refreshButton = new JButton("Actualizar DB");
		
		refreshButton.addActionListener(e -> {
			try {
		        String sesion = blfacadeinterface.obtenerSesionPendienteConResultado();
		        if (sesion.equals("pendiente")) {
		            mostrarMensaje("No hay ninguna votación pendiente.");
		            return;
		        }
		        if (sesion.equals(SesionVoto.RESULTADO_BLANCA)) {
		            mostrarMensaje("El candidato ACEPTÓ. ¡Fumata blanca! Conclave finalizado.");
		            AceptarRechazarCandidatura.setEnabled(true);
		        } else if (sesion.equals(SesionVoto.RESULTADO_NEGRA)) {
		            mostrarMensaje("El candidato RECHAZÓ. Fumata negra. Se puede iniciar otra votación.");
		            AceptarRechazarCandidatura.setEnabled(true);
		        } else {
		            mostrarMensaje("Votación pendiente. El candidato aún no ha respondido.");
		        }
		    } catch (Exception ex) {
		        mostrarMensaje("Error al consultar: " + ex.getMessage());
		    }
		});
		refreshButton.setBounds(366, 261, 119, 23);
		jContentPane.add(refreshButton);
		
						
		//-------------------------------------------------------------------------------------------------
		//-------------------------------------------------------------------------------------------------
		//LISTENERS!!!!!!!!!!!!
		//-------------------------------------------------------------------------------------------------
		//-------------------------------------------------------------------------------------------------

		this.jCalendar.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent propertychangeevent) {
//			
				if (propertychangeevent.getPropertyName().equals("locale")) {
					jCalendar.setLocale((Locale) propertychangeevent.getNewValue());
				} else if (propertychangeevent.getPropertyName().equals("calendar")) {
					calendarAnt = (Calendar) propertychangeevent.getOldValue();
					calendarAct = (Calendar) propertychangeevent.getNewValue();
					
					int monthAnt = calendarAnt.get(Calendar.MONTH);
					int monthAct = calendarAct.get(Calendar.MONTH);
					if (monthAct!=monthAnt) {
						if (monthAct==monthAnt+2) { 
							// Si en JCalendar está 30 de enero y se avanza al mes siguiente, devolverá 2 de marzo (se toma como equivalente a 30 de febrero)
							// Con este código se dejará como 1 de febrero en el JCalendar
							calendarAct.set(Calendar.MONTH, monthAnt+1);
							calendarAct.set(Calendar.DAY_OF_MONTH, 1);
						}
						
						jCalendar.setCalendar(calendarAct);						
	
					}
					jCalendar.setCalendar(calendarAct);
					int offset = jCalendar.getCalendar().get(Calendar.DAY_OF_WEEK);
					
						if (Locale.getDefault().equals(new Locale("es")))
							offset += 4;
						else
							offset += 5;
				Component o = (Component) jCalendar.getDayChooser().getDayPanel().getComponent(jCalendar.getCalendar().get(Calendar.DAY_OF_MONTH) + offset);
				}}});
		
		
		/*
		IniciarConclave.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		        try {
		            // Llamada a la fachada (asegúrate de que appFacadeInterface no sea null)
		        	System.out.println(jCalendar.getDate());
		            List<Cardenal> resultado = blfacadeinterface.iniciarConclave(jCalendar.getDate());

		            mostrarMensaje("Cónclave iniciado.\nElectores: ");
		            for (Cardenal cardenal : resultado) {
		            	
		                int edad = calcularEdad(cardenal.getFechaNacimiento(),jCalendar.getDate());
		                boolean esElector = cardenal.isPresente() && (edad < 80);
		                
		                if (esElector) {

		                    CardenalElector elector = new CardenalElector(cardenal);
		                    mostrarMensaje(elector.toString());

		                }
		            }
		            
		        } catch (Exception ex) {
		            ex.printStackTrace();
		            mostrarMensaje("Error al iniciar cónclave: " + ex.getMessage());
		        }
		    }
		});*/
		
		
		//iniciarConclave
		IniciarConclave.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		        try {
		            System.out.println(jCalendar.getDate());
		            List<Cardenal> resultado = blfacadeinterface.iniciarConclave(jCalendar.getDate());

		            // Construir el mensaje completo
		            StringBuilder sb = new StringBuilder();
		            sb.append("Cónclave iniciado.\n\n");
		            
		            // Lista de todos los cardenales
		            sb.append("--- TODOS LOS CARDENALES ---\n");
		            for (Cardenal cardenal : resultado) {
		                int edad = calcularEdad(cardenal.getFechaNacimiento(), jCalendar.getDate());
		                boolean esElector = cardenal.isPresente() && (edad < 80);
		                sb.append(cardenal.getNombre())
		                  .append(" (")
		                  .append(edad).append(" años, ")
		                  .append(cardenal.isPresente() ? "presente" : "ausente")
		                  .append(")\n");
		            }
		            
		            // Lista de electores (solo los que cumplen condición)
		            sb.append("\n--- CARDENALES ELECTORES ---\n");
		            for (Cardenal cardenal : resultado) {
		                int edad = calcularEdad(cardenal.getFechaNacimiento(), jCalendar.getDate());
		                boolean esElector = cardenal.isPresente() && (edad < 80);
		                if (esElector) {
		                    CardenalElector elector = new CardenalElector(cardenal);
		                    sb.append(elector.toString()).append("\n");
		                }
		            }
		            
		            // Mostrar todo de una vez
		            mostrarMensaje(sb.toString());
		            
		        } catch (Exception ex) {
		            ex.printStackTrace();
		            mostrarMensaje("Error al iniciar cónclave: " + ex.getMessage());
		        }
		    }
		});
		
		
		//IniciarVotacion
		IniciarVotacion.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		    	Date fechaHoraInicio = getFechaHoraSeleccionada();
		        String mensaje = blfacadeinterface.iniciarVotacion(fechaHoraInicio);
		       
		        mostrarMensaje(mensaje);
		       
		    }
		});
		
		//RegistrarPersona
		RegistrarPersona.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		        String nombre = NombrePersona.getText().trim();
		        if (nombre.isEmpty()) {
		        	mostrarMensaje("Error: ingrese un nombre.");
		            return;
		        }
		        Date fechaNacimiento = jCalendar.getDate();

		        String mensaje = blfacadeinterface.registrarPersona(nombre, fechaNacimiento);
		      	mostrarMensaje(mensaje);
		        
		    }
		});
		
		
		
		
		
		
	
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(1);
			}
		});
		
		
		
		CerrarVotacion.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		        try {
		        	Date fechaHoraCierre = getFechaHoraSeleccionada();
		            String mensaje = blfacadeinterface.cerrarVotacion(fechaHoraCierre);
		            mostrarMensaje(mensaje);
		        } catch (Exception ex) {
		        	mostrarMensaje("Error al cerrar votación: " + ex.getMessage());
		        }
		    }
		});
		
		
		
		
	}
	private Date getFechaHoraSeleccionada() {
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(jCalendar.getDate()); // fecha (día/mes/año)
	    // Obtener hora y minutos del spinner
	    Date time = (Date) spinnerHora.getValue();
	    Calendar timeCal = Calendar.getInstance();
	    timeCal.setTime(time);
	    cal.set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY));
	    cal.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE));
	    cal.set(Calendar.SECOND, 0);
	    cal.set(Calendar.MILLISECOND, 0);
	    return cal.getTime();
	}
	
	
	/*
	 * 
	 * 
	
	public void mostrarDecision(String mensaje) 
	{
		JButton AceptarRechazarCandidatura = new JButton("AceptarRechazarCandidatura");
		AceptarRechazarCandidatura.setBounds(350, 305, 175, 23);
		jContentPane.add(AceptarRechazarCandidatura);
		this.mostrarMensaje(mensaje);
	}
	

	 * */
	
	
	// En MainGUI.java
	private JButton refreshButton;

	
	
	public static MainGUI getInstance() {
        if (instancia == null) {
            instancia = new MainGUI();
        }
        return instancia;
    }
	
	
	public void mostrarMensaje(String mensaje) {
        SwingUtilities.invokeLater(() -> {
            displayMainGUI.setText(mensaje);
        });
	}
	public static int calcularEdad(Date fechaNacimiento, Date fechaActual) {
        // Crear un calendario para la fecha de nacimiento
        Calendar calNacimiento = Calendar.getInstance();
        calNacimiento.setTime(fechaNacimiento);
        
        // Crear un calendario para la fecha actual	
        Calendar calActual = Calendar.getInstance();
        calActual.setTime(fechaActual);
        
        // Calcular la edad inicial (diferencia de años)
        int edad = calActual.get(Calendar.YEAR) - calNacimiento.get(Calendar.YEAR);
        
        // Verificar si ya cumplió años en el año actual
        if (calActual.get(Calendar.DAY_OF_YEAR) < calNacimiento.get(Calendar.DAY_OF_YEAR)) {
            edad--; // Aún no ha cumplido años este año
        }
        
        return edad;
    }
	
	
	
	
	
	
} // @jve:decl-index=0:visual-constraint="0,0"

