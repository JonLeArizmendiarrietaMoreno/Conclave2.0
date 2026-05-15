package gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class PersonaGUI extends JFrame {

	private static final long serialVersionUID = 1L;
	private JTextArea displayPersonaGUI;
	private JPanel contentPane;
	private static PersonaGUI instancia;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					PersonaGUI frame = new PersonaGUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	
	public static PersonaGUI getInstance() {
        if (instancia == null) {
            instancia = new PersonaGUI();
        }
        return instancia;
    }
	/**
	 * Create the frame.
	 */
	public PersonaGUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		displayPersonaGUI = new JTextArea();
		displayPersonaGUI.setBounds(10, 11, 208, 239);
		contentPane.add(displayPersonaGUI);
		
		JButton aceptarButton = new JButton("Aceptar");
		aceptarButton.setBounds(258, 28, 89, 23);
		contentPane.add(aceptarButton);
		
		
		JButton rechazarButton = new JButton("Rechazar");
		rechazarButton.setBounds(258, 99, 89, 23);
		contentPane.add(rechazarButton);
		
		// En PersonaGUI.java
		aceptarButton.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		        MainGUI.getInstance().setDecisionCandidato(true);   // guarda la decisión
		        MainGUI.getInstance().mostrarMensaje("Decisión del candidato: ACEPTADO. Presione 'AceptarRechazarCandidatura' para finalizar.");
		    }
		});

		rechazarButton.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		        MainGUI.getInstance().setDecisionCandidato(false);  // guarda la decisión
		        MainGUI.getInstance().mostrarMensaje("Decisión del candidato: RECHAZADO. Presione 'AceptarRechazarCandidatura' para continuar.");
		    }
		});

	}


	public void mostrarMensaje(String mensaje) {
        SwingUtilities.invokeLater(() -> {
        	displayPersonaGUI.setText(mensaje);
        });
	}


















}
