package dataAccess;

import domain.*;

import java.util.*;
import javax.persistence.*;
import gui.PantallaExterna;

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
                CardenalElector elector = new CardenalElector(cardenal.getNombre(), cardenal.getFechaNacimiento(), cardenal.getCargo(), true);
                em.persist(elector);
            }
        }
        em.getTransaction().commit();
    }

    /**
     * Envía un mensaje a la pantalla externa (simulado).
     */
    public PantallaExterna getPantalla(String mensaje) {
        return new PantallaExterna();
        
    }
    
    
    
    /*
    db.getTransaction().begin();
    
    Pilot pilot = new Pilot(name, nac, points);
    db.persist(pilot);
    db.getTransaction().commit();
    
    return db.find(Pilot.class,name);
    
    */
    
    
    
    
    
    
    
    
    
    /**
     * Busca el ultimo Conclave.
     */
    public Conclave getConclaveActivo() {
        TypedQuery<Conclave> query = em.createQuery(
            "SELECT c FROM Conclave c WHERE c.fechaFin IS NULL", Conclave.class);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
    
    
    
    /**
     * Busca la ultima sesion voto.
     */
    public SesionVoto getLastSesionVoto(Conclave conclave) {
        TypedQuery<SesionVoto> query = em.createQuery(
            "SELECT s FROM SesionVoto s WHERE s.conclave = :conclave ORDER BY s.idSesion DESC", SesionVoto.class);
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
    
    
    
    
    
    
    
    
    

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    

    //----------------------------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------------------------
    //FUNCIONES Deprecated!!!!!!!!!!!!!
    //----------------------------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------------------------
		/**
	 * This method creates/adds a product to a seller
	 * 
	 * @param title of the product
	 * @param description of the product
	 * @param status 
	 * @param selling price
	 * @param category of a product
	 * @param publicationDate
	 * @return Product
 	 * @throws SaleAlreadyExistException if the same product already exists for the seller
	 */
	public Sale createSale(String title, String description, int status, float price,  Date pubDate, String sellerEmail, File file) throws  FileNotUploadedException, MustBeLaterThanTodayException, SaleAlreadyExistException {
		

		System.out.println(">> DataAccess: createProduct=> title= "+title+" seller="+sellerEmail);
		try {
		

			if(pubDate.before(UtilDate.trim(new Date()))) {
				throw new MustBeLaterThanTodayException(ResourceBundle.getBundle("Etiquetas").getString("DataAccess.ErrorSaleMustBeLaterThanToday"));
			}
			if (file==null)
				throw new FileNotUploadedException(ResourceBundle.getBundle("Etiquetas").getString("DataAccess.ErrorFileNotUploadedException"));

			em.getTransaction().begin();
			
			Seller seller = em.find(Seller.class, sellerEmail);
			if (seller.doesSaleExist(title)) {
				em.getTransaction().commit();
				throw new SaleAlreadyExistException(ResourceBundle.getBundle("Etiquetas").getString("DataAccess.SaleAlreadyExist"));
			}

			Sale sale = seller.addSale(title, description, status, price, pubDate, file);
			//next instruction can be obviated

			em.persist(seller); 
			em.getTransaction().commit();
			 System.out.println("sale stored "+sale+ " "+seller);

			return sale;
		} catch (NullPointerException e) {
			   e.printStackTrace();
			// TODO Auto-generated catch block
			em.getTransaction().commit();
			return null;
		}
		
		
	}
	
	/**
	 * This method retrieves all the products that contain a desc text in a title
	 * 
	 * @param desc the text to search
	 * @return collection of products that contain desc in a title
	 */
	public List<Sale> getSales(String desc) {
		System.out.println(">> DataAccess: getProducts=> from= "+desc);

		List<Sale> res = new ArrayList<Sale>();	
		TypedQuery<Sale> query = em.createQuery("SELECT s FROM Sale s WHERE s.title LIKE ?1",Sale.class);   
		query.setParameter(1, "%"+desc+"%");
		
		List<Sale> sales = query.getResultList();
	 	 for (Sale sale:sales){
		   res.add(sale);
		  }
	 	return res;
	}
	
	/**
	 * This method retrieves the products that contain a desc text in a title and the publicationDate today or before
	 * 
	 * @param desc the text to search
	 * @return collection of products that contain desc in a title
	 */
	public List<Sale> getPublishedSales(String desc, Date pubDate) {
		System.out.println(">> DataAccess: getProducts=> from= "+desc);

		List<Sale> res = new ArrayList<Sale>();	
		TypedQuery<Sale> query = em.createQuery("SELECT s FROM Sale s WHERE s.title LIKE ?1 AND s.pubDate <=?2",Sale.class);   
		query.setParameter(1, "%"+desc+"%");
		query.setParameter(2,pubDate);
		
		List<Sale> sales = query.getResultList();
	 	 for (Sale sale:sales){
		   res.add(sale);
		  }
	 	return res;
	}

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

	public BufferedImage getFile(String fileName) {
		File file=new File(basePath+fileName);
		BufferedImage targetImg=null;
		try {
             targetImg = rescale(ImageIO.read(file));
        } catch (IOException ex) {
            //Logger.getLogger(MainAppFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
		return targetImg;

	}
	
	public BufferedImage rescale(BufferedImage originalImage)
    {
		System.out.println("rescale "+originalImage);
        BufferedImage resizedImage = new BufferedImage(baseSize, baseSize, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, baseSize, baseSize, null);
        g.dispose();
        return resizedImage;
    }
	
	
	
	public void close(){
		em.close();
		System.out.println("DataAcess closed");
	}
	
}
