package gui;

import javax.swing.*;
import java.awt.*;

public class PantallaExternaGUI extends JFrame {
    private static PantallaExternaGUI instancia;
    private JTextField textFieldMensaje;

    // Constructor privado para Singleton
    private PantallaExternaGUI() {
        setTitle("Pantalla Externa - Cónclave");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 450, 300);
        setLayout(new BorderLayout());

        textFieldMensaje = new JTextField();
        textFieldMensaje.setFont(new Font("Monospaced", Font.BOLD, 24));
        textFieldMensaje.setHorizontalAlignment(JTextField.CENTER);
        textFieldMensaje.setEditable(false);
        add(textFieldMensaje, BorderLayout.CENTER);

        // Mensaje inicial (opcional)
        textFieldMensaje.setText("Esperando mensajes...");
    }

    public static PantallaExternaGUI getInstance() {
        if (instancia == null) {
            instancia = new PantallaExternaGUI();
        }
        return instancia;
    }

    public void mostrarMensaje(String mensaje) {
        // Actualiza el texto y asegura que la ventana sea visible
        SwingUtilities.invokeLater(() -> {
            textFieldMensaje.setText(mensaje);
            instancia.setVisible(true);
            instancia.toFront();  // Trae la ventana al frente si está oculta
        });
    }
}