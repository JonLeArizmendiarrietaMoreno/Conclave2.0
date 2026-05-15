package domain;

import javax.persistence.*;
import java.util.*;



@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Cardenal extends Persona {
	

    private String cargo;
    private boolean presente;
    

    
    // Constructor completo
    public Cardenal(String nombre, Date fechaNacimiento, String cargo, boolean presente) {
        super(nombre, fechaNacimiento);
        this.cargo = cargo;
        this.presente = presente;
    }
    public Cardenal() {super();}

    // Getters y setters específicos
    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public boolean isPresente() {
        return presente;
    }

    public void setPresente(boolean presente) {
        this.presente = presente;
    }
   

    
    @Override
    public String toString() {
        return getNombre() + " (" + cargo + ") - ID: " + getId()
                + ", nacimiento: " + getFechaNacimiento()
                + ", presente: " + (presente ? "Sí" : "No");
    }
}