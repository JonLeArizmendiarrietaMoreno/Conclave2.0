package domain;

import java.util.Date;

public class MaestroDeCeremonias extends Persona {

    // Constructor
    public MaestroDeCeremonias(int id, String nombre, Date fechaNacimiento) {
        super(id, nombre, fechaNacimiento);
    }

    @Override
    public String toString() {
        return "Maestro de Ceremonias: " + getNombre() + " (ID: " + getId()
                + ", nacimiento: " + getFechaNacimiento() + ")";
    }
}