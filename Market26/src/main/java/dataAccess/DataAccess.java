package dataAccess;

import javax.persistence.EntityManager;

import domain.Cardenal;
import domain.CardenalElector;
import domain.Conclave;
import domain.MaestroDeCeremonias;
import domain.Papa;
import domain.Persona;
import domain.SesionVoto;
import gui.PantallaExterna;






import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Date;
import java.text.SimpleDateFormat;

import javax.imageio.ImageIO;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import configuration.ConfigXML;
import configuration.UtilDate;
import domain.Seller;
import domain.Sale;
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
    public void initializeDB(){
		
		em.getTransaction().begin();

		try { 
	       
			
		    //Create sellers 
			Seller seller1=new Seller("seller1@gmail.com","Aitor Fernandez");
			Seller seller2=new Seller("seller22@gmail.com","Ane Gaztañaga");
			Seller seller3=new Seller("seller3@gmail.com","Test Seller");

			
			//Create products
			Date today = UtilDate.trim(new Date());
		
			
			seller1.addSale("futbol baloia", "oso polita, gutxi erabilita", 2, 10,  today, null);
			seller1.addSale("salomon mendiko botak", "44 zenbakia, 3 ateraldi",2, 20,  today, null);
			seller1.addSale("samsung 42\" telebista", "berria, erabili gabe", 2, 175,  today, null);


			seller2.addSale("imac 27", "7 urte, dena ondo dabil", 1, 200,today, null);
			seller2.addSale("iphone 17", "oso gutxi erabilita", 2, 400, today, null);
			seller2.addSale("orbea mendiko bizikleta", "29\" 10 urte, mantenua behar du", 3,225, today, null);
			seller2.addSale("polar kilor erlojua", "Vantage M, ondo dago", 3, 30, today, null);

			seller3.addSale("sukaldeko mahaia", "1.8*0.8, 4 aulkiekin. Prezio finkoa", 3,45, today, null);

			
			em.persist(seller1);
			em.persist(seller2);
			em.persist(seller3);
			
			
			SimpleDateFormat a = new SimpleDateFormat("yyyy-MM-dd");


			System.out.println(a.parse("1970-05-20"));
			em.getTransaction().commit();
			System.out.println("Db initialized");
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
    
    
    public void initializeDB2(){
		
		em.getTransaction().begin();

		try { 
	        SimpleDateFormat today = new SimpleDateFormat("yyyy-MM-dd");
		
	        Cardenal card1 = new Cardenal("Juan Pérez", today.parse("1970-05-20"), "Obispo de Roma", true);
	            Cardenal card2 = new Cardenal("Luis Gómez", today.parse("1945-03-10"), "Cardenal Presbítero", true);
	            Cardenal card3 = new Cardenal("Carlos Ruiz", today.parse("1990-07-15"), "Diácono", false);
	        // ... persistir cada uno
	        em.persist(card1);
	        em.persist(card2);
	        em.persist(card3);
	        
	        
	        
	        
	        
			em.getTransaction().commit();
			
			System.out.println(today.parse("1970-05-20"));
			System.out.println("Db initialized");
		}
		catch (Exception e){
			e.printStackTrace();
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
