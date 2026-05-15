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
            
            // 1. Maestro de ceremonias
            MaestroDeCeremonias maestro = new MaestroDeCeremonias("Juan Pérez", sdf.parse("1960-05-10"));
            em.persist(maestro);
            
            // 2. Cardenales (3 electores, 3 no electores)
            // Electores (menores de 80 años y presentes)
            Date fechaElector1 = sdf.parse("1975-03-15");
            Date fechaElector2 = sdf.parse("1978-07-22");
            Date fechaElector3 = sdf.parse("1980-11-05");
            
            Cardenal cardBase1 = new Cardenal("Luis Martínez", fechaElector1, "Cardenal Presbítero", true);
            Cardenal cardBase2 = new Cardenal("Andrés Gómez", fechaElector2, "Cardenal Diácono", true);
            Cardenal cardBase3 = new Cardenal("Fernando Ruiz", fechaElector3, "Cardenal Obispo", true);
            
            // No electores (mayores de 80 años o no presentes)
            Date fechaNoElector1 = sdf.parse("1940-02-10");
            Date fechaNoElector2 = sdf.parse("1938-09-25");
            Date fechaNoElector3 = sdf.parse("1942-12-01");
            
            Cardenal cardNoElect1 = new Cardenal("Tomás Romero", fechaNoElector1, "Cardenal Obispo", false);
            Cardenal cardNoElect2 = new Cardenal("Javier Mendoza", fechaNoElector2, "Cardenal Presbítero", true);  // presente pero >80 años → no elector
            Cardenal cardNoElect3 = new Cardenal("Roberto Silva", fechaNoElector3, "Cardenal Diácono", false);
            
            // Crear los cardenales electores (subclase CardenalElector)
            CardenalElector elector1 = new CardenalElector(cardBase1.getNombre(), cardBase1.getFechaNacimiento(), cardBase1.getCargo(), cardBase1.isPresente());
            CardenalElector elector2 = new CardenalElector(cardBase2.getNombre(), cardBase2.getFechaNacimiento(), cardBase2.getCargo(), cardBase2.isPresente());
            CardenalElector elector3 = new CardenalElector(cardBase3.getNombre(), cardBase3.getFechaNacimiento(), cardBase3.getCargo(), cardBase3.isPresente());
            
            // Persistir todos
            em.persist(cardBase1); em.persist(cardBase2); em.persist(cardBase3);
            em.persist(cardNoElect1); em.persist(cardNoElect2); em.persist(cardNoElect3);
            em.persist(elector1); em.persist(elector2); em.persist(elector3);
            
            // 3. Conclave
            Date fechaInicioConclave = UtilDate.trim(new Date());
            Conclave conclave = new Conclave(fechaInicioConclave);
            conclave.setMaestroDeCeremonias(maestro);
            conclave.getCardenalesElectores().add(elector1);
            conclave.getCardenalesElectores().add(elector2);
            conclave.getCardenalesElectores().add(elector3);
            elector1.setConclave(conclave);
            elector2.setConclave(conclave);
            elector3.setConclave(conclave);
            em.persist(conclave);
            
            // 4. Personas externas (candidatos que no son cardenales)
            Persona externa1 = new Persona("Juan Ciudadano", sdf.parse("1985-03-20"));
            Persona externa2 = new Persona("María Laica", sdf.parse("1990-07-12"));
            em.persist(externa1);
            em.persist(externa2);
            
            // 5. Primera sesión (fumata negra)
            Calendar cal = Calendar.getInstance();
            cal.setTime(fechaInicioConclave);
            cal.add(Calendar.HOUR_OF_DAY, 2);
            Date horaInicio1 = cal.getTime();
            SesionVoto sesion1 = new SesionVoto(horaInicio1, conclave);
            cal.add(Calendar.HOUR_OF_DAY, 1);
            Date horaFin1 = cal.getTime();
            sesion1.setHoraFin(horaFin1);
            sesion1.setResultado(SesionVoto.RESULTADO_NEGRA);
            sesion1.getYaHanVotado().add(elector1);
            sesion1.getYaHanVotado().add(elector2);
            sesion1.getYaHanVotado().add(elector3);
            // Candidatos votados (cardenales no electores + externos)
            sesion1.getCandidatosVotados().add(cardNoElect1);
            sesion1.getCandidatosVotados().add(cardNoElect2);
            sesion1.getCandidatosVotados().add(externa1);
            sesion1.getCandidatosVotados().add(externa2);
            em.persist(sesion1);
            
            // 6. Segunda sesión (fumata blanca) – elegimos a cardNoElect1 como papa
            cal.add(Calendar.DAY_OF_MONTH, 1);
            Date horaInicio2 = cal.getTime();
            SesionVoto sesion2 = new SesionVoto(horaInicio2, conclave);
            cal.add(Calendar.HOUR_OF_DAY, 1);
            Date horaFin2 = cal.getTime();
            sesion2.setHoraFin(horaFin2);
            sesion2.setResultado(SesionVoto.RESULTADO_BLANCA);
            sesion2.getYaHanVotado().add(elector1);
            sesion2.getYaHanVotado().add(elector2);
            sesion2.getYaHanVotado().add(elector3);
            sesion2.getCandidatosVotados().add(cardNoElect1);
            sesion2.getCandidatosVotados().add(cardNoElect2);
            sesion2.getCandidatosVotados().add(externa1);
            sesion2.setGanador(cardNoElect1);
            em.persist(sesion2);
            
            // 7. Crear el Papa (nueva entidad, con los datos del cardenal elegido)
            Papa papa = new Papa(cardNoElect1.getNombre(), cardNoElect1.getFechaNacimiento(), horaFin2); // número 266
            conclave.setPapaElegido(papa);
            papa.setPapaConclave(conclave);
            em.persist(papa);
            
            em.getTransaction().commit();
            System.out.println("Base de datos inicializada correctamente.");
        } catch (Exception e) {
            e.printStackTrace();
            em.getTransaction().rollback();
        }
    }
    
    
    
    
    /**
     * Devuelve un HashMap con todos los cardenales y valor false (inicial).
     */
    public HashMap<Cardenal, Boolean> getCardenales() {
        em.getTransaction().begin();
        TypedQuery<Cardenal> query = em.createQuery("SELECT c FROM Cardenal c", Cardenal.class);
        List<Cardenal> lista = query.getResultList();
        em.getTransaction().commit();
        
        HashMap<Cardenal, Boolean> map = new HashMap<>();
        for (Cardenal c : lista) {
            map.put(c, false);
        }
        return map;
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
    public void añadirElectores(HashMap<Cardenal, Boolean> mapa, Conclave conclave) {
        Calendar rightNow = Calendar.getInstance();
        int anyoActual = rightNow.get(Calendar.YEAR);
        
        em.getTransaction().begin();
        for (Map.Entry<Cardenal, Boolean> entry : mapa.entrySet()) {
            Cardenal cardenal = entry.getKey();
            rightNow.setTime(cardenal.getFechaNacimiento());
            int edad = anyoActual - rightNow.get(Calendar.YEAR);
            boolean esElector = cardenal.isPresente() && (edad < 80);
            
            if (esElector) {
                entry.setValue(true);
                // Crear el elector copiando datos del cardenal base
                CardenalElector elector = new CardenalElector(
                    cardenal.getNombre(), 
                    cardenal.getFechaNacimiento(), 
                    cardenal.getCargo(), 
                    true
                );
                // Establecer la relación bidireccional
                elector.setConclave(conclave);
                em.persist(elector);
                
                // Añadir a la lista del conclave (para mantener coherencia en el lado Java)
                conclave.getCardenalesElectores().add(elector);
            }
        }
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
            "SELECT s FROM SesionVoto s WHERE s.sesionesVotoDelConclave = :conclave ORDER BY s.idSesion DESC", SesionVoto.class);
        query.setParameter("conclave", conclave);
        query.setMaxResults(1);
        List<SesionVoto> result = query.getResultList();
        
        if (result.isEmpty()) {
            return null;
        } else {
            return result.get(0);
        }
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


	
	public CardenalElector findCardenalElectorPorNombre(String nombre) {
	    TypedQuery<CardenalElector> query = em.createQuery(
	        "SELECT e FROM CardenalElector e WHERE e.nombre = :nombre", CardenalElector.class);
	    query.setParameter("nombre", nombre);
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
	
}
