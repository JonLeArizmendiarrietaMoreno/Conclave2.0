package domain;

import javax.persistence.*;
import java.util.Date;

@Entity
public class SesionVoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idSesion;   // Ahora se genera automáticamente

    @Temporal(TemporalType.TIMESTAMP)
    private Date horaInicio;

    @Temporal(TemporalType.TIMESTAMP)
    private Date horaFin;

    private String resultado;  // "", "negra" o "blanca"

    @ManyToOne  // Muchas sesiones de voto pertenecen a un cónclave
    private Conclave conclave;

    // Valores permitidos
    public static final String RESULTADO_PENDIENTE = "";
    public static final String RESULTADO_NEGRA = "negra";
    public static final String RESULTADO_BLANCA = "blanca";

    // Constructor sin ID (lo genera la BD)
    public SesionVoto(Date horaInicio, Conclave conclave) {
        if (horaInicio == null) {
            throw new IllegalArgumentException("La hora de inicio no puede ser nula.");
        }
        this.horaInicio = horaInicio;
        this.horaFin = null;
        this.resultado = RESULTADO_PENDIENTE;
        this.conclave = conclave;
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

    public Conclave getConclave() { 
    	return conclave; 
    }

    // Setters
    public void setHoraFin(Date horaFin) {
        this.horaFin = horaFin;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }
    
    public void setConclave(Conclave conclave) { 
    	this.conclave = conclave; 
    }
    
    
    
    

    @Override
    public String toString() {
        return "Sesión " + idSesion 
                + " - Inicio: " + horaInicio
                + (horaFin != null ? " - Fin: " + horaFin : " (abierta)")
                + " - Resultado: " + (resultado.isEmpty() ? "pendiente" : resultado);
    }
}