package domain;

import javax.persistence.*;
import java.util.Date;


@MappedSuperclass
public class Persona {
	@Id
    private int id;
    private String nombre;
    private Date fechaNacimiento;

    public Persona(int id, String nombre, Date fechaNacimiento) {
        this.id = id;
        this.nombre = nombre;
        this.fechaNacimiento = fechaNacimiento;
    }

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