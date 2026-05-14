package domain;

import javax.persistence.*;
import java.util.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class CardenalElector extends Cardenal {
	
    public CardenalElector(String nombre, Date fechaNacimiento, String cargo, boolean presente) {
        super(nombre, fechaNacimiento,cargo,presente);
    }
	
    public CardenalElector() {super();}

    @Override
    public String toString() {
        return "Elector: " + super.toString();
    }
}