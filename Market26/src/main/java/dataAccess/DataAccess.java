package dataAccess;



import domain.*;


import java.util.*;
import javax.persistence.*;
import gui.PantallaExternaGUI;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import javax.imageio.ImageIO;
import configuration.ConfigXML;
import configuration.UtilDate;
import exceptions.FileNotUploadedException;
import exceptions.MustBeLaterThanTodayException;
import exceptions.SaleAlreadyExistException;





/**
 * It implements the data access to the objectDb database
 */
public class DataAccess {
	private  EntityManager  em;
	
	private  EntityManagerFactory emf;
    private static final int baseSize = 160;

	private static final String basePath="src/main/resources/images/";
	private static final String dbServerDir = "src/main/resources/db/";

	ConfigXML c=ConfigXML.getInstance();


	
     public DataAccess()  {
		if (c.isDatabaseInitialized()) {
			String fileName=c.getDbFilename();

			if (!c.isDatabaseLocal()) fileName=dbServerDir+fileName;
			
			File fileToDelete= new File(fileName);
			if(fileToDelete.delete()){
				File fileToDeleteTemp= new File(fileName+"$");
				fileToDeleteTemp.delete();
				System.out.println("File deleted");
			 } else {
				 System.out.println("Operation failed");
				}
		}
		open();
		if  (c.isDatabaseInitialized()) 
			initializeDB();
		System.out.println("DataAccess created => isDatabaseLocal: "+c.isDatabaseLocal()+" isDatabaseInitialized: "+c.isDatabaseInitialized());

		close();

	}
     
    public DataAccess(EntityManager em) {
    	this.em=em;
    }

    //----------------------------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------------------------
    //INITIALIZE!!!!!!!!!!!!!
    //----------------------------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------------------------
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


    private List<Cardenal> crearCardenalesNoElectores(SimpleDateFormat sdf) throws Exception {
        return Arrays.asList(
            new Cardenal("Cardenal1",   sdf.parse("1940-02-10"), "Cardenal Obispo",   true),  // >80 años
            new Cardenal("Cardenal2", sdf.parse("1975-05-20"), "Cardenal Diácono", false),  // ausente
            //electores
            new Cardenal("Elector1", sdf.parse("1980-03-15"), "Cardenal Presbítero", true),
            new Cardenal("Elector12",  sdf.parse("1982-07-22"), "Cardenal Diacono",   true),
            new Cardenal("Elector123", sdf.parse("1985-11-05"), "Cardenal Obispo",    true),
            new Cardenal("Elector1234",  sdf.parse("1978-09-10"), "Cardenal Presbítero", true),
            new Cardenal("Elector12345", sdf.parse("1985-11-05"), "Cardenal Obispo",    true),
            new Cardenal("Elector123456",  sdf.parse("1978-09-10"), "Cardenal Presbítero", true)
        );
    }

    private List<Persona> crearPersonasExternas(SimpleDateFormat sdf) throws Exception {
        return Arrays.asList(
            new Persona("persona1", sdf.parse("1985-03-20")),
            new Persona("persona2",    sdf.parse("1990-07-12"))
        );
    }   
    
    
    
    
    
    /**
     * Devuelve un HashMap con todos los cardenales y valor false (inicial).
     */
    public List<Cardenal> getCardenales() {
        em.getTransaction().begin();
        TypedQuery<Cardenal> query = em.createQuery(
            "SELECT c FROM Cardenal c WHERE TYPE(c) = Cardenal", 
            Cardenal.class);
        List<Cardenal> lista = query.getResultList();
        em.getTransaction().commit();
        return lista;
    }
    
 
    /**
     * Guarda un nuevo cónclave.
     */
    public void addConclave(Conclave conclave) {
        em.getTransaction().begin();
        em.persist(conclave);
        em.getTransaction().commit();
    }
    
    
    /**
     * Recorre el HashMap, comprueba si cada cardenal es elector (presente y edad < 80),
     * actualiza el valor a true en el mapa y persiste un objeto CardenalElector en BD.
     */
    public void añadirElectores(List<Cardenal> lista, Conclave conclave) {
        Calendar rightNow = Calendar.getInstance();
        int anyoActual = rightNow.get(Calendar.YEAR);
        
        em.getTransaction().begin();
        
        for (Cardenal cardenal : lista) {

            rightNow.setTime(cardenal.getFechaNacimiento());
            int edad = anyoActual - rightNow.get(Calendar.YEAR);
            boolean esElector = cardenal.isPresente() && (edad < 80);
            
            if (esElector) {

                CardenalElector elector = new CardenalElector(cardenal);

                elector.setConclave(conclave);
                em.persist(elector);
                
                conclave.getCardenalesElectores().add(elector);
            }
        }
        em.getTransaction().commit();
    }
    
    
    
 // En DataAccess.java
    public void addPapa(Papa papa) {
        em.getTransaction().begin();
        em.persist(papa);
        em.getTransaction().commit();
    }
    
    
    public void updateConclave(Conclave conclave) {
        em.getTransaction().begin();
        em.merge(conclave);
        em.getTransaction().commit();
    }
    
    
    
    
    
    
    
    
    
    
    public MaestroDeCeremonias getMaestroDeCeremonias() {
        em.getTransaction().begin();
        TypedQuery<MaestroDeCeremonias> query = em.createQuery(
            "SELECT m FROM MaestroDeCeremonias m", MaestroDeCeremonias.class);
        MaestroDeCeremonias maestro = query.getResultList().stream().findFirst().orElse(null);
        em.getTransaction().commit();
        return maestro;
    }


    
    /**
     * Busca el ultimo Conclave.
     */  
    public Conclave getConclaveActivo() {
        try {
            return em.createQuery("SELECT c FROM Conclave c WHERE c.fechaFin IS NULL", Conclave.class)
                     .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
    
    
    
    /**
     * Busca la ultima sesion voto.
     */


    public SesionVoto getLastSesionVoto(Conclave conclave) {
        TypedQuery<SesionVoto> query = em.createQuery(
            "SELECT s FROM SesionVoto s WHERE s.sesionesVotoDelConclave = :conclave " +
            "AND s.resultado = :pendiente ORDER BY s.idSesion DESC",
            SesionVoto.class);
        query.setParameter("conclave", conclave);
        query.setParameter("pendiente", SesionVoto.RESULTADO_PENDIENTE);
        query.setMaxResults(1);
        List<SesionVoto> result = query.getResultList();
        return result.isEmpty() ? null : result.get(0);
        //return result.isEmpty() ? null : result.get(0); TERNARIO
        //return  if                true : false
    }
    
    
    
    public SesionVoto getLastSesionVotoAcabado(Conclave conclave) {
        TypedQuery<SesionVoto> query = em.createQuery(
            "SELECT s FROM SesionVoto s ORDER BY s.idSesion DESC",
            SesionVoto.class);
        query.setParameter("conclave", conclave);
        query.setParameter("pendiente", SesionVoto.RESULTADO_PENDIENTE);
        query.setMaxResults(1);
        List<SesionVoto> result = query.getResultList();
        return result.isEmpty() ? null : result.get(0);
        //return result.isEmpty() ? null : result.get(0); TERNARIO
        //return  if                true : false
    }
    
    
    public boolean añadirSesionVoto(Date horaInicio,Conclave conclaveActual) 
    {
    	
    	em.getTransaction().begin();
    	try {
        // id automatic, resultado se inicia en "", horafin == null
        SesionVoto nuevaSesionVoto = new SesionVoto(horaInicio, conclaveActual);
        em.persist(nuevaSesionVoto);
        em.getTransaction().commit();
        
    	}
    	catch (NoResultException e) {
    		em.getTransaction().rollback();
            return false;
        }
        return true;
    }
    
    
 // dataAccess/DataAccess.java
    public boolean existePersona(String nombre, Date fechaNacimiento) {
        em.getTransaction().begin();
        TypedQuery<Long> query = em.createQuery(
            "SELECT COUNT(p) FROM Persona p WHERE p.nombre = :nombre AND p.fechaNacimiento = :fecha",
            Long.class);
        query.setParameter("nombre", nombre);
        query.setParameter("fecha", fechaNacimiento);
        long count = query.getSingleResult();
        em.getTransaction().commit();
        return count > 0;
    }

    public void addPersona(Persona persona) {
        em.getTransaction().begin();
        em.persist(persona);
        em.getTransaction().commit();
    }
    
    
    public CardenalElector findCardenalElectorPorNombre(String nombre, Conclave conclave) {
        TypedQuery<CardenalElector> query = em.createQuery(
            "SELECT e FROM CardenalElector e WHERE e.nombre = :nombre AND e.electores = :conclave",
            CardenalElector.class);
        query.setParameter("nombre", nombre);
        query.setParameter("conclave", conclave);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

	public Persona findPersonaPorNombre(String nombre) {
	    TypedQuery<Persona> query = em.createQuery(
	        "SELECT p FROM Persona p WHERE p.nombre = :nombre",
	        Persona.class);
	    query.setParameter("nombre", nombre);
	    try {
	        return query.getSingleResult();
	    } catch (NoResultException e) {
	        return null;
	    }
	}

	public void registrarVoto(SesionVoto sesionVoto, CardenalElector cardenalElector, Persona candidato) {
	    em.getTransaction().begin();
	    SesionVoto managed = em.merge(sesionVoto);
	    managed.getYaHanVotado().add(cardenalElector);
	    managed.getCandidatosVotados().add(candidato);
	    em.getTransaction().commit();
	}

	public boolean getYaHanVotadoEnSesion(int idSesion, int idElector) {
	    em.getTransaction().begin();
	    try {
	        TypedQuery<Long> query = em.createQuery(
	            "SELECT COUNT(s) FROM SesionVoto s JOIN s.yaHanVotado e " +
	            "WHERE s.idSesion = :idSesion AND e.id = :idElector", Long.class);
	        query.setParameter("idSesion", idSesion);
	        query.setParameter("idElector", idElector);
	        long count = query.getSingleResult();
	        em.getTransaction().commit();
	        return count > 0;
	    } catch (Exception e) {
	        em.getTransaction().rollback();
	        return false;
	    }
	}

	
	
	public void cerrarSesionVoto(SesionVoto sesion, Date horaFin) {
	    em.getTransaction().begin();
	    // Asegurar que la sesión está gestionada
	    SesionVoto managed = em.merge(sesion);
	    managed.setHoraFin(horaFin);
	    em.getTransaction().commit();
	}

	public void updateSesionVoto(SesionVoto sesion) {
	    em.getTransaction().begin();
	    em.merge(sesion);
	    em.getTransaction().commit();
	}
	
	
	public SesionVoto getUltimaSesion() {
	    TypedQuery<SesionVoto> query = em.createQuery(
	        "SELECT s FROM SesionVoto s ORDER BY s.idSesion DESC", 
	        SesionVoto.class);
	    query.setMaxResults(1);
	    List<SesionVoto> result = query.getResultList();
	    return result.isEmpty() ? null : result.get(0);
	}
	
    
    //------------------------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------    
    //funciones no tocar
    //------------------------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------
    
    
    
    public void open(){
		
		String fileName=c.getDbFilename();
		if (c.isDatabaseLocal()) {
			emf = Persistence.createEntityManagerFactory("objectdb:"+fileName);
			em = emf.createEntityManager();
		} else {
			Map<String, String> properties = new HashMap<String, String>();
			  properties.put("javax.persistence.jdbc.user", c.getUser());
			  properties.put("javax.persistence.jdbc.password", c.getPassword());

			  emf = Persistence.createEntityManagerFactory("objectdb://"+c.getDatabaseNode()+":"+c.getDatabasePort()+"/"+fileName, properties);
			  em = emf.createEntityManager();
    	   }
		System.out.println("DataAccess opened => isDatabaseLocal: "+c.isDatabaseLocal());
	}
	
	public void close(){
		em.close();
		System.out.println("DataAcess closed");
	}


	
	
	
	
	
	
	
	
	
	
	
}
