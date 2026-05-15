//domain/Conclave.java
package domain;

import javax.persistence.*;

import java.util.*;

@Entity
public class Conclave {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idConclave;
    private Date fechaInicio;
    private Date fechaFin;  // null por defecto

    
    @OneToMany(mappedBy = "electores")   // Cardenal tiene el many-to-one
    private List<CardenalElector> electores;

    @OneToMany(mappedBy = "sesionesVotoDelConclave")   // SesionVoto tiene el many-to-one
    private List<SesionVoto> sesionesVotoDelConclave;

    @ManyToOne 
    private MaestroDeCeremonias maestroDeCeremoniasUnico;

    @OneToOne
    private Papa papaElegido;
    
    

    public Conclave(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
        this.fechaFin = null;
        this.electores = new ArrayList<CardenalElector>();
        this.sesionesVotoDelConclave = new ArrayList<SesionVoto>();
    }

    // Getters
    public Date getFechaInicio() {
        return fechaInicio;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

	public List<CardenalElector> getCardenalesElectores() {
		return electores;
	}
    
    // Setters
    public void setFechaFin(Date fechaFin) {
        if (fechaFin != null && fechaFin.before(this.fechaInicio)) {
            throw new IllegalArgumentException("La fecha de fin no puede ser anterior a la de inicio.");
        }
        this.fechaFin = fechaFin;
    }
    
	public void setMaestroDeCeremonias(MaestroDeCeremonias maestroDeCeremoniasUnico) {
		this.maestroDeCeremoniasUnico = maestroDeCeremoniasUnico;
		
	}
	
	public void setPapaElegido(Papa papaElegido) {
		this.papaElegido=papaElegido;
	}
    
    @Override
    public String toString() {
        return "Cónclave iniciado el " + fechaInicio + " - finalizado el " + fechaFin;
    }






	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}