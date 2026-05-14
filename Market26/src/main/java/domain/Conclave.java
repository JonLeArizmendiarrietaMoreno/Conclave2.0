//domain/Conclave.java
package domain;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
public class Conclave {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private Date fechaInicio;
    private Date fechaFin;  // null por defecto = cónclave abierto


    public Conclave(Date fechaInicio) {
        this.fechaInicio = fechaInicio;

        this.fechaFin = null;
    }

    // Getters
    public Date getFechaInicio() {
        return fechaInicio;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

    
    
    // Setter solo para fechaFin (la de inicio no se modifica)
    public void setFechaFin(Date fechaFin) {
        if (fechaFin != null && fechaFin.before(this.fechaInicio)) {
            throw new IllegalArgumentException("La fecha de fin no puede ser anterior a la de inicio.");
        }
        this.fechaFin = fechaFin;
    }
    
    
    
    @Override
    public String toString() {
        return "Cónclave iniciado el " + fechaInicio + " - finalizado el " + fechaFin;
    }
}