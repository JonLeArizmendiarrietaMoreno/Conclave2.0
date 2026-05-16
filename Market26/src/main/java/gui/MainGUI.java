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
	
	
    private String sellerMail;
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
		IniciarConclave.setBounds(350, 169, 109, 23);
		IniciarConclave.setText("Iniciar Conclave");
		

		IniciarVotacion = new JButton();
		IniciarVotacion.setBounds(350, 203, 105, 23);
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
		RegistrarPersona.setBounds(350, 237, 119, 23);
		RegistrarPersona.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		
		
		JButton CerrarVotacion = new JButton("Cerrar Votacion"); //$NON-NLS-1$ //$NON-NLS-2$
		CerrarVotacion.setBounds(350, 271, 107, 23);
		
		
		JButton AceptarRechazarCandidatura = new JButton("AceptarRechazarCandidatura");
		
		AceptarRechazarCandidatura.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		        if (!hayDecision) {
		            displayMainGUI.setText("No hay una decisión pendiente del candidato. Primero debe aparecer la ventana PersonaGUI y el candidato debe elegir.");
		            return;
		        }
		        try {
		            boolean exito = blfacadeinterface.procesarDecisionCandidatura(decision);
		            if (exito) {
		                displayMainGUI.setText("Proceso completado. Cónclave finalizado o reiniciado según decisión.");
		                hayDecision = false; // reiniciar
		            } else {
		                displayMainGUI.setText("Error al procesar la decisión.");
		            }
		        } catch (Exception ex) {
		            displayMainGUI.setText("Error: " + ex.getMessage());
		        }
		    }
		});
		
		AceptarRechazarCandidatura.setBounds(350, 305, 175, 23);
		jContentPane.add(AceptarRechazarCandidatura);
		
		
		NombrePersona = new JTextField();
		NombrePersona.setForeground(SystemColor.activeCaptionBorder);
		NombrePersona.setText("NombrePersona");
		NombrePersona.setBounds(71, 10, 249, 20);
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
		displayMainGUI.setBounds(10, 49, 330, 279);
		displayMainGUI.setLineWrap(true);
		displayMainGUI.setWrapStyleWord(true);
		displayMainGUI.setLineWrap(true);
		displayMainGUI.setWrapStyleWord(true);


		jContentPane.add(displayMainGUI);
		
						
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
		
		
		
		//iniciarConclave
		IniciarConclave.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		        try {
		            // Llamada a la fachada (asegúrate de que appFacadeInterface no sea null)
		        	System.out.println(jCalendar.getDate());
		            HashMap<Cardenal, Boolean> resultado = blfacadeinterface.iniciarConclave(jCalendar.getDate());

		            // Mostrar información en el área de texto
		            int electores = 0;
		            for (Boolean esElector : resultado.values()) {
		                if (esElector) electores++;
		            }
		            displayMainGUI.setText("Cónclave iniciado.\nElectores: " + electores +
		                                    "\nTotal cardenales: " + resultado.size());
		        } catch (Exception ex) {
		            ex.printStackTrace();
		            displayMainGUI.setText("Error al iniciar cónclave: " + ex.getMessage());
		        }
		    }
		});
		
		
		//IniciarVotacion
		IniciarVotacion.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		    	Date fechaHoraInicio = getFechaHoraSeleccionada();
		        boolean conseguido = blfacadeinterface.iniciarVotacion(fechaHoraInicio);
		        if (conseguido) {
		            displayMainGUI.setText("Votación iniciada a las " + jCalendar.getDate());
		        } else {
		            displayMainGUI.setText("Error: ya hay una votación abierta.");
		        }
		    }
		});
		
		//RegistrarPersona
		RegistrarPersona.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		        String nombre = NombrePersona.getText().trim();
		        if (nombre.isEmpty()) {
		            displayMainGUI.setText("Error: ingrese un nombre.");
		            return;
		        }
		        Date fechaNacimiento = jCalendar.getDate(); // fecha seleccionada en el calendario
		        if (fechaNacimiento == null) {
		            displayMainGUI.setText("Error: seleccione una fecha de nacimiento.");
		            return;
		        }
		        boolean ok = blfacadeinterface.registrarPersona(nombre, fechaNacimiento);
		        if (ok) {
		            displayMainGUI.setText("Persona registrada correctamente:\n" + nombre + " (" + fechaNacimiento + ")");
		            NombrePersona.setText(""); // limpiar campo
		        } else {
		            displayMainGUI.setText("Error: ya existe una persona con ese nombre y fecha.");
		        }
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
		            boolean ok = blfacadeinterface.cerrarVotacion(fechaHoraCierre);
		            if (ok) {
		                displayMainGUI.setText("Votación cerrada correctamente.");
		            } else {
		                displayMainGUI.setText("No se pudo cerrar la votación (verifique que haya pasado 1 hora).");
		            }
		        } catch (Exception ex) {
		            displayMainGUI.setText("Error al cerrar votación: " + ex.getMessage());
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
	private boolean decision = false;
	private boolean hayDecision = false;

	public void setDecisionCandidato(boolean decision) {
	    this.decision = decision;
	    this.hayDecision = true;
	}
	
	
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
	
	
	
	
	
	
	
} // @jve:decl-index=0:visual-constraint="0,0"

