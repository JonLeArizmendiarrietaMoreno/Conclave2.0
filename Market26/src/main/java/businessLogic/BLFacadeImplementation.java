package businessLogic;




import java.io.File;

import java.util.Date;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebService;

import dataAccess.DataAccess;
import domain.*;
import domain.Sale;
import domain.Cardenal;


import domain.CardenalElector;
import domain.Conclave;
import java.util.ArrayList;
import java.util.Calendar;






import exceptions.FileNotUploadedException;
import exceptions.MustBeLaterThanTodayException;
import exceptions.SaleAlreadyExistException;

import java.awt.image.BufferedImage;
import java.awt.Image;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.HashMap;

/**
 * It implements the business logic as a web service.
 */
@WebService(endpointInterface = "businessLogic.BLFacade")
public class BLFacadeImplementation  implements BLFacade {
	 private static final int baseSize = 160;

		private static final String basePath="src/main/resources/images/";
	DataAccess dataAccess;

	public BLFacadeImplementation()  {		
		System.out.println("Creating BLFacadeImplementation instance");
		dataAccess=new DataAccess();		
	}
	
    public BLFacadeImplementation(DataAccess da)  {
		System.out.println("Creating BLFacadeImplementation instance with DataAccess parameter");
		dataAccess=da;		
	}
    

    
    

    
    
    @Override
    public HashMap<Cardenal, Boolean> iniciarConclave(Date fechaInicio) {
        dataAccess.open();
        try {
        	HashMap<Cardenal, Boolean> hm = dataAccess.getCardenales();

        	

            // 3. Crear y guardar el cónclave
            Conclave conclave = new Conclave(new Date());
            dataAccess.updateElectores(hm, conclave);
            dataAccess.addConclave(conclave);


            // 5. Enviar mensaje "Extra Omnes" a la pantalla externa
            //dataAccess.getPantalla("Extra Omnes");

            return hm;
        } finally {
            dataAccess.close();
        }
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
	/**
	 * {@inheritDoc}
	 */
   @WebMethod
	public Sale createSale(String title, String description,int status, float price, Date pubDate, String sellerEmail, File file) throws  FileNotUploadedException, MustBeLaterThanTodayException, SaleAlreadyExistException {
		dataAccess.open();
		Sale product=dataAccess.createSale(title, description, status, price, pubDate, sellerEmail, file);		
		dataAccess.close();
		return product;
   };
	
   /**
    * {@inheritDoc}
    */
	@WebMethod 
	public List<Sale> getSales(String desc){
		dataAccess.open();
		List<Sale>  rides=dataAccess.getSales(desc);
		dataAccess.close();
		return rides;
	}
	
	/**
	    * {@inheritDoc}
	    */
		@WebMethod 
		public List<Sale> getPublishedSales(String desc, Date pubDate) {
			dataAccess.open();
			List<Sale>  rides=dataAccess.getPublishedSales(desc,pubDate);
			dataAccess.close();
			return rides;
		}
	/**
	    * {@inheritDoc}
	    */
	@WebMethod public BufferedImage getFile(String fileName) {
		return dataAccess.getFile(fileName);
	}

    
	public void close() {
		DataAccess dB4oManager=new DataAccess();
		dB4oManager.close();

	}

	/**
	 * {@inheritDoc}
	 */
    @WebMethod	
	 public void initializeBD(){
    	dataAccess.open();
		dataAccess.initializeDB();
		dataAccess.close();
	}
    /**
	 * {@inheritDoc}
	 */
    @WebMethod public Image downloadImage(String imageName) {
        File image = new File(basePath+imageName);
        try {
            return ImageIO.read(image);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    
}

