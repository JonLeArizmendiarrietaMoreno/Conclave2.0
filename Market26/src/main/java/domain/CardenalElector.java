package domain;

import javax.persistence.*;
import java.util.Date;

@Entity


public class CardenalElector extends Cardenal {
	
    public CardenalElector(int id, String nombre, Date fechaNacimiento, String cargo, boolean presente) {
        super(id, nombre, fechaNacimiento,cargo,presente);
    }
	
	

    @Override
    public String toString() {
        return "Elector: " + super.toString();
    }
}