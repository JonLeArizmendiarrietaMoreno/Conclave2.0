package domain;

import javax.persistence.*;
import java.util.*;

@Entity
public class CardenalElector extends Cardenal {
	
	@ManyToMany(mappedBy = "yaHanVotado")
	private Set<SesionVoto> votosEmitidos;
	
    public CardenalElector(String nombre, Date fechaNacimiento, String cargo, boolean presente) {
        super(nombre, fechaNacimiento,cargo,presente);
    }
	
    public CardenalElector() {super();}

    @Override
    public String toString() {
        return "Elector: " + super.toString();
    }
}