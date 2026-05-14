package domain;

import javax.persistence.*;
import java.util.*;

@Entity
@MappedSuperclass
public class Persona {
	
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String nombre;
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaNacimiento;

    public Persona(String nombre, Date fechaNacimiento) {
        this.nombre = nombre;
        this.fechaNacimiento = fechaNacimiento;
    }
    public Persona() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    @Override
    public String toString() {
        return nombre + " (ID: " + id + ", nacido el " + fechaNacimiento + ")";
    }
}