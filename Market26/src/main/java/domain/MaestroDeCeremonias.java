package domain;
import javax.persistence.*;
import java.util.*;


@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class MaestroDeCeremonias extends Persona {
	
	@OneToMany (mappedBy = "MaestroDeCeremonias")
	private List<Conclave> maestroDeCeremoniasUnico;
	
    public MaestroDeCeremonias(int id, String nombre, Date fechaNacimiento) {
        super(nombre, fechaNacimiento);
    }

    @Override
    public String toString() {
        return "Maestro de Ceremonias: " + getNombre() + " (ID: " + getId()
                + ", nacimiento: " + getFechaNacimiento() + ")";
    }
}