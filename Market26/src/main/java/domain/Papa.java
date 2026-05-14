package domain;

import javax.persistence.*;
import java.util.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Papa extends Persona {
	
	@Temporal(TemporalType.TIMESTAMP)
    private Date fechaEleccion;
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idPapa;
	
	@OneToOne
	private Conclave papaConclave;
    // Constructor completo
    public Papa(String nombre, Date fechaNacimiento, Date fechaEleccion, int idPapa) {
        super(nombre, fechaNacimiento);
        this.fechaEleccion = fechaEleccion;
        this.idPapa = idPapa;
    }
    public Papa() 
    {
    	super();
    }

    // Getters y setters específicos
    public Date getFechaEleccion() {
        return fechaEleccion;
    }

    public void setFechaEleccion(Date fechaEleccion) {
        this.fechaEleccion = fechaEleccion;
    }

    public int getIdPapa() {
        return idPapa;
    }

    public void setIdPapa(int idPapa) {
        this.idPapa = idPapa;
    }

    @Override
    public String toString() {
        return getNombre() + " (Papa nº " + idPapa + ") - ID: " + getId()
                + ", nacimiento: " + getFechaNacimiento()
                + ", elegido el: " + fechaEleccion;
    }
}