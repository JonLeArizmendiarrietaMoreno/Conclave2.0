package gui;

/**
 * @author Jon Le Arizmendiarrieta
 */

import businessLogic.BLFacade;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.FlowLayout;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Date;
import java.util.HashMap;

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

	/**
	 * This is the default constructor
	 */
	public MainGUI( String mail) {
		
		super();

		this.sellerMail=mail;
		Locale.setDefault(new Locale("es"));
		this.setSize(495, 290);
		jLabelSelectOption = new JLabel("MainGUI");
		jLabelSelectOption.setBounds(10, 11, 51, 16);
		jLabelSelectOption.setFont(new Font("Tahoma", Font.BOLD, 13));
		jLabelSelectOption.setForeground(Color.BLACK);
		jLabelSelectOption.setHorizontalAlignment(SwingConstants.CENTER);
		
		IniciarConclave = new JButton();
		IniciarConclave.setBounds(10, 60, 109, 23);
		IniciarConclave.setText("Iniciar Conclave");
		

		IniciarVotacion = new JButton();
		IniciarVotacion.setBounds(10, 94, 105, 23);
		IniciarVotacion.setText("Iniciar Votacion");
		IniciarVotacion.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				JFrame a = new QuerySalesGUI();

				a.setVisible(true);
			}
		});
		
		jContentPane = new JPanel();
		setContentPane(jContentPane);
		
		
		JLabel lblHora = new JLabel("Hora:");
		lblHora.setBounds(347, 160, 40, 20);
		jContentPane.add(lblHora);
		
		
		SpinnerDateModel modelHora = new SpinnerDateModel();
		spinnerHora = new JSpinner(modelHora);
		
		JSpinner.DateEditor editorHora = new JSpinner.DateEditor(spinnerHora, "HH:mm");
		spinnerHora.setEditor(editorHora);
		spinnerHora.setBounds(397, 158, 80, 25);
		jContentPane.add(spinnerHora);
		
		
		JButton RegistrarPersona = new JButton("Registrar Persona"); //$NON-NLS-1$ //$NON-NLS-2$
		RegistrarPersona.setBounds(10, 128, 119, 23);
		RegistrarPersona.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		
		
		JButton CerrarVotacion = new JButton("Cerrar Votacion"); //$NON-NLS-1$ //$NON-NLS-2$
		CerrarVotacion.setBounds(10, 162, 107, 23);
		
		
		JButton AceptarRechazarCandidatura = new JButton("AceptarRechazarCandidatura");
		AceptarRechazarCandidatura.setBounds(10, 196, 175, 23);
		
		
		NombrePersona = new JTextField();
		NombrePersona.setForeground(SystemColor.activeCaptionBorder);
		NombrePersona.setText("NombrePersona");
		NombrePersona.setBounds(391, 197, 86, 20);
		NombrePersona.setColumns(10);
		jContentPane.setLayout(null);
		jContentPane.add(jLabelSelectOption);
		jContentPane.add(AceptarRechazarCandidatura);
		jContentPane.add(IniciarConclave);
		jContentPane.add(IniciarVotacion);
		jContentPane.add(RegistrarPersona);
		jContentPane.add(CerrarVotacion);
		jContentPane.add(NombrePersona);
		setTitle("Conclave2.0");
		
		
		jCalendar.setBounds(new Rectangle(252, 0, 225, 150));
		this.getContentPane().add(jCalendar, null);
		
		displayMainGUI = new JTextArea();
		displayMainGUI.setText("Dios esta moribundo y lo voy a rematar");
		displayMainGUI.setBounds(192, 158, 145, 82);
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
		
		
		IniciarConclave.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		        try {
		            // Llamada a la fachada (asegúrate de que appFacadeInterface no sea null)
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
		
		
		
		
		
		
		
		
		
		
	
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(1);
			}
		});
	}
} // @jve:decl-index=0:visual-constraint="0,0"

