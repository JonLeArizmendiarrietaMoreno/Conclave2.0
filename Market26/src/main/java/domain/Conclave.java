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

    
    @OneToMany(mappedBy = "conclave")   // Cardenal tiene el many-to-one
    private List<CardenalElector> cardenalesElectores;

    @OneToMany(mappedBy = "conclave")   // SesionVoto tiene el many-to-one
    private List<SesionVoto> sesionesVoto;

    @ManyToOne 
    private MaestroDeCeremonias maestroDeCeremonias;

    @OneToOne
    private Papa papaElegido;
    
    

    public Conclave(Date fechaInicio) {
        this.fechaInicio = fechaInicio;

        this.fechaFin = null;
        this.cardenalesElectores = new ArrayList<CardenalElector>();
        this.sesionesVoto = new ArrayList<SesionVoto>();
    }

    // Getters
    public Date getFechaInicio() {
        return fechaInicio;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

	public List<CardenalElector> getCardenalesElectores() {
		return cardenalesElectores;
	}
    
    // Setters
    public void setFechaFin(Date fechaFin) {
        if (fechaFin != null && fechaFin.before(this.fechaInicio)) {
            throw new IllegalArgumentException("La fecha de fin no puede ser anterior a la de inicio.");
        }
        this.fechaFin = fechaFin;
    }
    
	public void setMaestroDeCeremonias(MaestroDeCeremonias maestroDeCeremonias) {
		this.maestroDeCeremonias = maestroDeCeremonias;
		
	}
	
	public void setPapaElegido(Papa papaElegido) {
		this.papaElegido=papaElegido;
	}
    
    @Override
    public String toString() {
        return "Cónclave iniciado el " + fechaInicio + " - finalizado el " + fechaFin;
    }






	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}