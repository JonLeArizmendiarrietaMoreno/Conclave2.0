package businessLogic;

import java.io.File;




import java.util.*;

import gui.*;
import domain.*;



import exceptions.FileNotUploadedException;
import exceptions.MustBeLaterThanTodayException;
import exceptions.SaleAlreadyExistException;

import javax.jws.WebMethod;
import javax.jws.WebService;
import java.awt.image.BufferedImage;
import java.awt.Image;


/**
 * Interface that specifies the business logic.
 */
@WebService
public interface BLFacade  {
	

    @WebMethod
    public HashMap<Cardenal, Boolean> iniciarConclave(Date fechaInicio);
	
	
	
    @WebMethod
	public boolean iniciarVotacion(Date horaInicio);
	
	
    @WebMethod
    public boolean registrarPersona(String nombre, Date fechaNacimiento);
	
	
    @WebMethod
    public boolean votar(String nombreElector, String nombreCandidato);
    
    @WebMethod    
    public boolean cerrarVotacion(Date horaFin) ;
    
    @WebMethod
    public boolean procesarDecisionCandidatura(boolean decision);
	
	/**
	 * This method calls the data access to initialize the database with some sellers and products.
	 * It is only invoked  when the option "initialize" is declared in the tag dataBaseOpenMode of resources/config.xml file
	 */	
	@WebMethod public void initializeBD();
	
		
}
