package domain;

import javax.persistence.*;


import java.util.*;

@Entity
public class SesionVoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idSesion;
    @Temporal(TemporalType.TIMESTAMP)
    private Date horaInicio;
    @Temporal(TemporalType.TIMESTAMP)
    private Date horaFin;
    private String resultado;  // "", "negra" o "blanca"
    
    @ManyToOne
    private Conclave sesionesVotoDelConclave;
    
    @ManyToOne
    private Persona ganador; //ganador

    @ManyToMany
    private Set<Persona> candidatosVotados;

    @ManyToMany
    private Set<CardenalElector> yaHanVotado; //Cardenales

    public static final String RESULTADO_PENDIENTE = "";
    public static final String RESULTADO_NEGRA = "negra";
    public static final String RESULTADO_BLANCA = "blanca";


    public SesionVoto(Date horaInicio, Conclave sesionesVotoDelConclave) {
        this.horaInicio = horaInicio;
        this.horaFin = null;
        this.resultado = RESULTADO_PENDIENTE;
        this.sesionesVotoDelConclave = sesionesVotoDelConclave;
        
        this.candidatosVotados = new HashSet<Persona>();
        this.yaHanVotado = new HashSet<CardenalElector>();
        
        
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
    	return sesionesVotoDelConclave; 
    }

    // Setters
    public void setHoraFin(Date horaFin) {
        this.horaFin = horaFin;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }
    
    public void setConclave(Conclave sesionesVotoDelConclave) { 
    	this.sesionesVotoDelConclave = sesionesVotoDelConclave; 
    }
	public Set<CardenalElector> getYaHanVotado() {
		return yaHanVotado;
	}
    
	public Set<Persona> getCandidatosVotados() {
		
		return candidatosVotados;
	}
	
	public void setGanador(Cardenal ganador) {
		this.ganador=ganador;
	}
    

    @Override
    public String toString() {
        return "Sesión " + idSesion 
                + " - Inicio: " + horaInicio
                + (horaFin != null ? " - Fin: " + horaFin : " (abierta)")
                + " - Resultado: " + (resultado.isEmpty() ? "pendiente" : resultado);
    }




    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}