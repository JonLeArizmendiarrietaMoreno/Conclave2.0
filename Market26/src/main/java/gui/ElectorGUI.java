package gui;

import java.awt.EventQueue;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;


import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.JButton;

import businessLogic.*;
import javax.swing.JTextArea;

public class ElectorGUI extends JFrame {
	
	
	

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField elegirAQuienVotar;
	private JTextField nombreElector;

	private JTextArea displayElectorGUI;
	
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ElectorGUI frame = new ElectorGUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private static BLFacade blfacadeinterface;
	
	public static BLFacade getBusinessLogic(){
		return blfacadeinterface;
	}
	 
	public static void setBussinessLogic (BLFacade facade){
		blfacadeinterface=facade;
	}
	
	/**
	 * Create the frame.
	 */
	public ElectorGUI() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		elegirAQuienVotar = new JTextField();
		elegirAQuienVotar.setText("Cardenal1");
		elegirAQuienVotar.setBounds(10, 68, 133, 31);
		contentPane.add(elegirAQuienVotar);
		elegirAQuienVotar.setColumns(10);
		
		JButton votarButton = new JButton("Votar");
		votarButton.setBounds(211, 28, 89, 23);
		contentPane.add(votarButton);
		
		nombreElector = new JTextField();
		nombreElector.setText("Elector1");
		nombreElector.setBounds(10, 11, 133, 31);
		contentPane.add(nombreElector);
		nombreElector.setColumns(10);
		
		displayElectorGUI = new JTextArea();
		displayElectorGUI.setBounds(10, 116, 138, 70);
		contentPane.add(displayElectorGUI);
		
		
		
		
		
		
		
		//votar
        votarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	
                String nombreElectorStr = nombreElector.getText();
                String nombreCandidatoStr = elegirAQuienVotar.getText();
                System.out.println(nombreElectorStr+nombreCandidatoStr);
                

                
               System.out.println("nombreelector y candidato no vacio");
               if( MainGUI.getBusinessLogic().votar(nombreElectorStr, nombreCandidatoStr)) 
               {
            	   displayElectorGUI.setText("Voto emitido ");

               }else {
            	   displayElectorGUI.setText("Voto no emitido" );
                }return;
            }
        });
		
	}
	
	
	public void mostrarMensaje(String mensaje) {
        SwingUtilities.invokeLater(() -> {
        	displayElectorGUI.setText(mensaje);
        });
	}


}
