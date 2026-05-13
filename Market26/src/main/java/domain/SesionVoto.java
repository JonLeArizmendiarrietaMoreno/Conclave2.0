package domain;

import java.util.Date;

public class SesionVoto {
    private int idSesion;
    private Date horaInicio;
    private Date horaFin;
    private String resultado;  // "", "negra" o "blanca"

    // Valores permitidos para resultado
    public static final String RESULTADO_PENDIENTE = "";
    public static final String RESULTADO_NEGRA = "negra";
    public static final String RESULTADO_BLANCA = "blanca";

    
    public SesionVoto(int idSesion, Date horaInicio) {
        if (horaInicio == null) {
            throw new IllegalArgumentException("La hora de inicio no puede ser nula.");
        }
        this.idSesion = idSesion;
        this.horaInicio = horaInicio;
        this.horaFin = null;
        this.resultado = RESULTADO_PENDIENTE;
    }

    // Getters
    public int getIdSesion() {
        return idSesion;
    }

    public Date getHoraInicio() {
        return horaInicio;
    }

    public Date getHoraFin() {
        return horaFin;
    }

    public String getResultado() {
        return resultado;
    }

    // Setters
    public void setHoraFin(Date horaFin) {
        this.horaFin = horaFin;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

    @Override
    public String toString() {
        return "Sesión " + idSesion 
                + " - Inicio: " + horaInicio
                + (horaFin != null ? " - Fin: " + horaFin : " (abierta)")
                + " - Resultado: " + (resultado.isEmpty() ? "pendiente" : resultado);
    }
}