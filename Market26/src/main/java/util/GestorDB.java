package util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.persistence.*;

import configuration.ConfigXML;
import configuration.UtilDate;
import domain.*;


public class GestorDB {

	
	//--------------------------------------------------------------------------------------------------------------------------------
	//--------------------------------------------------------------------------------------------------------------------------------
	//Esta clase lo uso para tocar un poco la base de datos, el programa no lo usa
	//--------------------------------------------------------------------------------------------------------------------------------
	//--------------------------------------------------------------------------------------------------------------------------------
    private EntityManager em;

    public GestorDB(EntityManager em) {
        this.em = em; 
    }

    public void initializeDB() {
        em.getTransaction().begin();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            // 1. Maestro de ceremonias (único)
            MaestroDeCeremonias maestro = crearMaestro(sdf);
            em.persist(maestro);

            // 2. Cardenales electores (4)
            //List<CardenalElector> electores = crearCardenalesElectores(sdf);
            //electores.forEach(em::persist);

            // 3. Cardenales no electores (2)
            List<Cardenal> cardenales = crearCardenalesNoElectores(sdf);
            cardenales.forEach(em::persist);

            // 4. Personas externas (2)
            List<Persona> externas = crearPersonasExternas(sdf);
            externas.forEach(em::persist);

            em.getTransaction().commit();
            System.out.println("✅ Base de datos inicializada (escenario limpio, sin cónclave previo).");
        } catch (Exception e) {
            e.printStackTrace();
            em.getTransaction().rollback();
        }
    }

    // Métodos auxiliares
    private MaestroDeCeremonias crearMaestro(SimpleDateFormat sdf) throws Exception {
        return new MaestroDeCeremonias("Carlos Gomez", sdf.parse("1990-01-15"));
    }

    private List<CardenalElector> crearCardenalesElectores(SimpleDateFormat sdf) throws Exception {
        return List.of(
            new CardenalElector("Luis Martinez", sdf.parse("1980-03-15"), "Cardenal Presbitero", true),
            new CardenalElector("Andrés Gomez",  sdf.parse("1982-07-22"), "Cardenal Diacono",   true),
            new CardenalElector("Fernando Ruiz", sdf.parse("1985-11-05"), "Cardenal Obispo",    true),
            new CardenalElector("Miguel Angel",  sdf.parse("1978-09-10"), "Cardenal Presbitero", true)
        );
    }

    private List<Cardenal> crearCardenalesNoElectores(SimpleDateFormat sdf) throws Exception {
        return List.of(
            new Cardenal("Tomas Romero",   sdf.parse("1940-02-10"), "Cardenal Obispo",   true),  // >80 años
            new Cardenal("Javier Mendoza", sdf.parse("1975-05-20"), "Cardenal Diácono", false),  // ausente
            new Cardenal("Luis Martinez", sdf.parse("1980-03-15"), "Cardenal Presbítero", true),
            new Cardenal("Andres Gómez",  sdf.parse("1982-07-22"), "Cardenal Diacono",   true),
            new Cardenal("Fernando Ruiz", sdf.parse("1985-11-05"), "Cardenal Obispo",    true),
            new Cardenal("Miguel Angel",  sdf.parse("1978-09-10"), "Cardenal Presbítero", true)
        );
    }

    private List<Persona> crearPersonasExternas(SimpleDateFormat sdf) throws Exception {
        return List.of(
            new Persona("Juan Ciudadano", sdf.parse("1985-03-20")),
            new Persona("Maria Laica",    sdf.parse("1990-07-12"))
        );
    }   
    
    
    
    
    
    
    
    public static void main(String[] args) {
        ConfigXML c = ConfigXML.getInstance();
        String fileName = c.getDbFilename();

        // 1. Eliminar archivo de BD anterior si existe (solo BD local)
        if (c.isDatabaseLocal()) {
            File dbFile = new File(fileName);
            if (dbFile.exists()) {
                boolean deleted = dbFile.delete();
                if (deleted) {
                    System.out.println("Archivo de BD eliminado: " + fileName);
                    File dbFileTemp = new File(fileName + "$");
                    if (dbFileTemp.exists()) {
                        dbFileTemp.delete();
                        System.out.println("Archivo temporal eliminado.");
                    }
                } else {
                    System.out.println("No se pudo eliminar el archivo. ¿Está cerrada la aplicación?");
                    return; // No seguir si no se puede borrar
                }
            } else {
                System.out.println("El archivo de BD no existe, se creará uno nuevo.");
            }
        } else {
            System.out.println("BD remota: no se puede eliminar automáticamente. Asegúrate de que está vacía.");
        }

        // 2. Crear EntityManager y poblar BD
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("objectdb:" + fileName);
        EntityManager em = emf.createEntityManager();

        GestorDB gestor = new GestorDB(em);
        gestor.initializeDB();

        // 3. Cerrar recursos
        em.close();
        emf.close();
        System.out.println("Proceso completado.");
    }
}