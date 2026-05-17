package gui;

import java.awt.EventQueue;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import domain.SesionVoto;
import businessLogic.BLFacade;

public class PersonaGUI extends JFrame {
    private static final long serialVersionUID = 1L;
    private JTextArea displayPersonaGUI;
    private JPanel contentPane;
    private static PersonaGUI instancia;
    private static BLFacade blfacadeinterface;
    
    private String ganadorPendiente = "";
    private JButton aceptarButton;
    private JButton rechazarButton;
    private JButton refreshButton;

    public static PersonaGUI getInstance() {
        if (instancia == null) instancia = new PersonaGUI();
        return instancia;
    }
    
    public static void setBussinessLogic(BLFacade facade) {
        blfacadeinterface = facade;
    }
    
    private PersonaGUI() {
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
		aceptarButton.setBounds(228, 53, 89, 23);
		contentPane.add(aceptarButton);
		
		
		JButton rechazarButton = new JButton("Rechazar");
		rechazarButton.setBounds(335, 53, 89, 23);
		contentPane.add(rechazarButton);
		
        aceptarButton.setEnabled(false);
        rechazarButton.setEnabled(false);
		
		JButton refreshButton = new JButton("ActualizarBD");
		refreshButton.setBounds(228, 12, 196, 23);
		contentPane.add(refreshButton);
        
        // Lógica del botón Actualizar
        refreshButton.addActionListener(e -> {
            try {
                String ganador = blfacadeinterface.obtenerSesionPendienteConGanador();
                if(!ganador.equals(""))
                {
                mostrarMensaje(ganador + " has sido elegido Papa. ¿Aceptas?");
                aceptarButton.setEnabled(true);
                rechazarButton.setEnabled(true);
                }
            } catch (Exception ex) {
                mostrarMensaje("Error al consultar: " + ex.getMessage());
            }
        });
        
        aceptarButton.addActionListener(e -> {
            try {
                String resultado = blfacadeinterface.añadirDecision(true);
                mostrarMensaje(resultado);
                aceptarButton.setEnabled(false);
                rechazarButton.setEnabled(false);
            } catch (Exception ex) {
                mostrarMensaje("Error: " + ex.getMessage());
            }
        });
        
        rechazarButton.addActionListener(e -> {
            if (ganadorPendiente == null) return;
            try {
                String resultado = blfacadeinterface.añadirDecision(false);
                mostrarMensaje(resultado);
                aceptarButton.setEnabled(false);
                rechazarButton.setEnabled(false);
                ganadorPendiente = null;
            } catch (Exception ex) {
                mostrarMensaje("Error: " + ex.getMessage());
            }
        });
    }
    
    public void mostrarMensaje(String mensaje) {
        SwingUtilities.invokeLater(() -> displayPersonaGUI.setText(mensaje));
    }
}