package domain;

import java.util.Date;

public class Papa extends Persona {
    private Date fechaEleccion;
    private int idPapa;

    // Constructor completo
    public Papa(int id, String nombre, Date fechaNacimiento, Date fechaEleccion, int idPapa) {
        super(id, nombre, fechaNacimiento);
        this.fechaEleccion = fechaEleccion;
        this.idPapa = idPapa;
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