package domain;

import javax.persistence.*;
import java.util.*;

@Entity
public class CardenalElector extends Cardenal {
	
	@ManyToMany(mappedBy = "yaHanVotado")
	private Set<SesionVoto> yaHanVotado;
	
	@ManyToOne
	private Conclave electores;



	
    public CardenalElector(String nombre, Date fechaNacimiento, String cargo, boolean presente) {
        super(nombre, fechaNacimiento,cargo,presente);
    }
    
    public CardenalElector(Cardenal cardenal) {
        super(cardenal.getNombre(), cardenal.getFechaNacimiento(),cardenal.getCargo(),true);
    }
	
    public CardenalElector() {super();}

    public Conclave getConclave() { 
    	return electores; 
    }
    
    public void setConclave(Conclave electores) { 
    	this.electores = electores; 
    }
    
    @Override
    public String toString() {
        return "Elector: " + super.toString();
    }
}