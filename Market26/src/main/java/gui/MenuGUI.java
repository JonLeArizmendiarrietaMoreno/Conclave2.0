package gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;

public class MenuGUI extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

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
		elegirElectorGUIbutton.setBounds(10, 128, 210, 75);
		contentPane.add(elegirElectorGUIbutton);
		
		JButton elegirMainGUIbutton = new JButton("Maestro elector");
		elegirMainGUIbutton.setBounds(10, 11, 210, 75);
		contentPane.add(elegirMainGUIbutton);
		
		
		
		elegirMainGUIbutton.addActionListener(e -> {
            
			if(MainGUI.getInstance()!=null) {
            MainGUI.getInstance().setVisible(true);
            PantallaExternaGUI.getInstance().setVisible(true);
            }else {System.out.println("solo puede haber un maestroDeCeremonias");}
        });

		elegirElectorGUIbutton.addActionListener(e -> {
            ElectorGUI electorGUI = new ElectorGUI();
            electorGUI.setVisible(true);
            PantallaExternaGUI.getInstance().setVisible(true);
            
        });
		
		
	}
	
	
}
