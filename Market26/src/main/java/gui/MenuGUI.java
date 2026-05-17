package gui;

import java.awt.EventQueue;


import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import businessLogic.BLFacade;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class MenuGUI extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	
    private static BLFacade blfacadeinterface;
	
    public static BLFacade getBusinessLogic(){
        return blfacadeinterface;
    }
	 
	public static void setBussinessLogic (BLFacade facade){
		blfacadeinterface=facade;
	}
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MenuGUI frame = new MenuGUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	
	
	
	

	/**
	 * Create the frame.
	 */
	public MenuGUI() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JButton elegirElectorGUIbutton = new JButton("cardenal Elector");
		elegirElectorGUIbutton.setBounds(10, 82, 210, 60);
		contentPane.add(elegirElectorGUIbutton);
		
		JButton elegirMainGUIbutton = new JButton("Maestro de ceremonias");
		elegirMainGUIbutton.setBounds(10, 11, 210, 60);
		contentPane.add(elegirMainGUIbutton);
		
		PantallaExternaGUI.setBussinessLogic(blfacadeinterface);
		JButton personaButton = new JButton("Persona ganadora");
		personaButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				if(PersonaGUI.getInstance()!=null) {
					PersonaGUI.getInstance().setVisible(true);
					PersonaGUI.setBussinessLogic(blfacadeinterface);
					
		            }else 
		            {System.out.println("solo una gui de persona");}
				
			}
		});
		
		personaButton.setBounds(10, 153, 210, 60);
		contentPane.add(personaButton);
			
		
		
		elegirMainGUIbutton.addActionListener(e -> {
            
			if(MainGUI.getInstance()!=null) {
            MainGUI.getInstance().setVisible(true);
            MainGUI.setBussinessLogic(blfacadeinterface);
        
            
            
            }else {System.out.println("solo puede haber un maestroDeCeremonias");}
        });

		elegirElectorGUIbutton.addActionListener(e -> {
            ElectorGUI electorGUI = new ElectorGUI();
            electorGUI.setVisible(true);
            ElectorGUI.setBussinessLogic(blfacadeinterface);
            
        });
		
		
	}
	
	
}
