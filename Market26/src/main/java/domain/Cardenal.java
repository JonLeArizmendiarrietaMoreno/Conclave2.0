package domain;

import java.util.Date;
import javax.persistence.*;



@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Cardenal extends Persona {
	
	@ManyToOne
	private Conclave conclave;
    private String cargo;
    private boolean presente;

    // Constructor completo
    public Cardenal(int id, String nombre, Date fechaNacimiento, String cargo, boolean presente) {
        super(id, nombre, fechaNacimiento);
        this.cargo = cargo;
        this.presente = presente;
    }


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

    public Conclave getConclave() { return conclave; }
    public void setConclave(Conclave conclave) { this.conclave = conclave; }
    
    @Override
    public String toString() {
        return getNombre() + " (" + cargo + ") - ID: " + getId()
                + ", nacimiento: " + getFechaNacimiento()
                + ", presente: " + (presente ? "Sí" : "No");
    }
}