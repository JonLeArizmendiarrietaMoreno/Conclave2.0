package gui;

import java.awt.EventQueue;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;


import javax.swing.JTextField;
import javax.swing.JButton;

import businessLogic.*;

public class ElectorGUI extends JFrame {
	
	
	

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField elegirAQuienVotar;
	private JTextField nombreElector;

	
	
	
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
		elegirAQuienVotar.setText("Juan Ciudadano");
		elegirAQuienVotar.setBounds(10, 68, 133, 31);
		contentPane.add(elegirAQuienVotar);
		elegirAQuienVotar.setColumns(10);
		
		JButton votarButton = new JButton("Votar");
		votarButton.setBounds(211, 28, 89, 23);
		contentPane.add(votarButton);
		
		nombreElector = new JTextField();
		nombreElector.setText("Fernando Ruiz");
		nombreElector.setBounds(10, 11, 133, 31);
		contentPane.add(nombreElector);
		nombreElector.setColumns(10);
		
		
		
		
		
		
		
		//votar
        votarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	
                String nombreElectorStr = nombreElector.getText();
                String nombreCandidatoStr = elegirAQuienVotar.getText();
                System.out.println(nombreElectorStr+nombreCandidatoStr);
                
                /*
                if (nombreElectorStr.isEmpty() || nombreCandidatoStr.isEmpty()) {
                    JOptionPane.showMessageDialog(ElectorGUI.this, 
                        "Debe introducir el nombre del elector y del candidato", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
           
                */
                
            
                
                
                
                
                
               System.out.println("nombreelector y candidato no vacio");
               if( MainGUI.getBusinessLogic().votar(nombreElectorStr, nombreCandidatoStr)) 
               {
            	   JOptionPane.showMessageDialog(ElectorGUI.this,"voto procesado",
                           "Exito", JOptionPane.INFORMATION_MESSAGE);

               }else {
                    JOptionPane.showMessageDialog(ElectorGUI.this,
                        "No se pudo emitir el voto",
                        "Error", JOptionPane.ERROR_MESSAGE);
                }return;
            }
        });
		
	}
}
