package domain;

import java.util.Date;

public class Cardenal extends Persona {
    private String cargo;
    private boolean presente;

    // Constructor completo
    public Cardenal(int id, String nombre, Date fechaNacimiento, String cargo, boolean presente) {
        super(id, nombre, fechaNacimiento);
        this.cargo = cargo;
        this.presente = presente;
    }

    // Constructor sin 'presente' (por defecto false)
    public Cardenal(int id, String nombre, Date fechaNacimiento, String cargo) {
        this(id, nombre, fechaNacimiento, cargo, false);
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

    @Override
    public String toString() {
        return getNombre() + " (" + cargo + ") - ID: " + getId()
                + ", nacimiento: " + getFechaNacimiento()
                + ", presente: " + (presente ? "Sí" : "No");
    }
}