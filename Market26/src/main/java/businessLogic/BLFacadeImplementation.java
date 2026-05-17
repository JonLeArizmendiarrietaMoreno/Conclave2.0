package businessLogic;


import java.util.*;

import domain.*;
import gui.*;


import java.io.File;

import javax.jws.WebMethod;
import javax.jws.WebService;

import dataAccess.DataAccess;

import exceptions.FileNotUploadedException;
import exceptions.MustBeLaterThanTodayException;
import exceptions.SaleAlreadyExistException;

import java.awt.image.BufferedImage;
import java.awt.Image;
import javax.imageio.ImageIO;
import java.io.IOException;


/**
 * It implements the business logic as a web service.
 */
@WebService(endpointInterface = "businessLogic.BLFacade")
public class BLFacadeImplementation  implements BLFacade {
	 //private static final int baseSize = 160;

	private static final String basePath="src/main/resources/images/";
	
	DataAccess dataAccess;
	
	String mensaje="";
	
	
	
	public BLFacadeImplementation()  {		
		System.out.println("Creating BLFacadeImplementation instance");
		dataAccess=new DataAccess();		
	}
	
    public BLFacadeImplementation(DataAccess da)  {
		System.out.println("Creating BLFacadeImplementation instance with DataAccess parameter");
		dataAccess=da;		
	}
    
  //----------------------------------------------------------------------------------------------------------------------------------------
  //----------------------------------------------------------------------------------------------------------------------------------------
  //Aqui empiezan l
  //----------------------------------------------------------------------------------------------------------------------------------------
  //----------------------------------------------------------------------------------------------------------------------------------------
    @WebMethod
    public List<Cardenal> iniciarConclave(Date fechaInicio) {
    	
        dataAccess.open();
        String mensaje = "";
        try {
        	
            Conclave activo = dataAccess.getConclaveActivo();
            if (activo != null) {
                throw new IllegalStateException("Ya hay un cónclave activo");
            }
            MaestroDeCeremonias maestro = dataAccess.getMaestroDeCeremonias();
            if (maestro == null) {
                throw new IllegalStateException("No hay maestro de ceremonias en la BD");
            }
            
            List<Cardenal> lista = dataAccess.getCardenales();
            
            
            Conclave conclave = new Conclave(fechaInicio);
            conclave.setMaestroDeCeremonias(maestro);
            
            dataAccess.añadirElectores(lista, conclave);
            dataAccess.addConclave(conclave);
            
            mensaje = "Extra Omnes";
            return lista;
        } finally {
            PantallaExternaGUI.getInstance().mostrarMensaje(mensaje);
            dataAccess.close();
        }
    }
    
    
    @WebMethod
    public String iniciarVotacion(Date horaInicio) {
    	System.out.println("empieza iniciarVotacion");
    	
    	dataAccess.open();
    	mensaje ="";
    	
        try {
        	
            Conclave conclaveActual = dataAccess.getConclaveActivo();
            if (conclaveActual == null) {
            	mensaje="No hay conclaves abiertos";
                return mensaje;  
            }

            SesionVoto ultima = dataAccess.getLastSesionVoto(conclaveActual);

            if (ultima != null && ultima.getHoraFin() == null) {
            	mensaje="SesionVoto previa aún abierta";
                return mensaje;
            }
            
            if(dataAccess.añadirSesionVoto(horaInicio, conclaveActual)) { 
            	mensaje= "empiez la sesion de voto" ;
            }else { 
            	mensaje = "no se a podido añadir una sesion de voto";
            }
            
            
            return mensaje;
           
        } finally { 
        	PantallaExternaGUI.getInstance().mostrarMensaje(mensaje);
            dataAccess.close();
        }
        

    }

    
    
    @WebMethod
    public String registrarPersona(String nombre, Date fechaNacimiento) {
        dataAccess.open();
        mensaje = "";
        try {
            // Verificar si ya existe una persona con mismo nombre y fecha
            if (dataAccess.existePersona(nombre, fechaNacimiento)) {
            	mensaje="Ya existe";
            }
            Persona nueva = new Persona(nombre, fechaNacimiento);
            dataAccess.addPersona(nueva);
            mensaje = "persona añadida: "+ nueva.toString();
            return mensaje;
        } finally {
            dataAccess.close();
        }
    }
    
    
    @WebMethod
    public String votar(String nombreElector, String nombreCandidato) {
        dataAccess.open();
        mensaje = "";
        try {
        	
            Conclave conclave = dataAccess.getConclaveActivo();
            if (conclave == null) {
            	return "no hay conclave activo";
                
            }
            
            SesionVoto sesionActual = dataAccess.getLastSesionVoto(conclave);
            if (sesionActual == null) {
            	return "no hay sesion de voto activo";
            }
            
            //Verrificar elector
            CardenalElector elector = dataAccess.findCardenalElectorPorNombre(nombreElector);
            if (elector == null) {
            	return "Nombre elector incorrecto";
            }
            
            //corrupto?
            if (sesionActual.getYaHanVotado().contains(elector)) {
            	return "Ya has votado";
            }
            
            Persona candidato = dataAccess.findPersonaPorNombre(nombreCandidato);
            if (candidato == null) {
            	return "La persona no esta en la base de datos, espera un momento he intentalo otra vez";

            }
            
            dataAccess.registrarVoto(sesionActual, elector, candidato);
            return "Se a registrado el voto: "+elector.toString() + " a " + candidato.toString();
           
        } finally {
            dataAccess.close();
        }
    }
    
    
    
    
    @WebMethod
    public String cerrarVotacion(Date ahora) {
        dataAccess.open();
        try {
            // 1. Obtener cónclave activo
            Conclave conclave = dataAccess.getConclaveActivo();
            if (conclave == null) {
                throw new IllegalStateException("No hay cónclave activo");
            }
            
            // 2. Obtener última sesión de voto (debe estar abierta)
            SesionVoto sesion = dataAccess.getLastSesionVoto(conclave);
            if (sesion == null || sesion.getHoraFin() != null) {
                throw new IllegalStateException("No hay ninguna votación abierta");
            }
                        
            long diffMillis = ahora.getTime() - sesion.getHoraInicio().getTime();
            if (diffMillis < 60 * 60 * 1000) {
                return "no ha transcurrido 1 hora";
            }
            

            dataAccess.cerrarSesionVoto(sesion, ahora);
            
            
            int totalVotos = sesion.getYaHanVotado().size();
            if (totalVotos == 0) {
                PantallaExternaGUI.getInstance().mostrarMensaje("Fumata negra");
                sesion.setResultado(SesionVoto.RESULTADO_NEGRA);
                dataAccess.updateSesionVoto(sesion);
                return "ningun voto";
            }
            
            List<Persona> candidatos = sesion.getCandidatosVotados();
            Map<Persona, Integer> recuento = new HashMap<>();
            for (Persona p : candidatos) {
                recuento.put(p, recuento.getOrDefault(p, 0) + 1);
            }

        	Persona ganador = null;
            
            int maxVotos = 0;
            for (Map.Entry<Persona, Integer> entry : recuento.entrySet()) {
                if (entry.getValue() > maxVotos) {
                    maxVotos = entry.getValue();
                    ganador = entry.getKey();
                }
            }
                       
            if (!(maxVotos * 3 >= totalVotos * 2)  ) {
                PantallaExternaGUI.getInstance().mostrarMensaje("Fumata negra");
                sesion.setResultado(SesionVoto.RESULTADO_NEGRA);
                dataAccess.updateSesionVoto(sesion);
                return "no ha habido mayoria";
            }
            

            
            sesion.setGanador(ganador);
            dataAccess.updateSesionVoto(sesion);
            mensaje = "GANADOR:" + ganador.getNombre() + ":" + maxVotos + ":" + totalVotos;   
            return mensaje;
        } finally {
            dataAccess.close();
        }

    }
    
    
 // En BLFacadeImplementation.java

    @WebMethod
    public String obtenerSesionPendienteConGanador() {
        dataAccess.open();
        try {
        	Conclave conclave = dataAccess.getConclaveActivo();
        if (conclave == null) {
            return "";
        }
        
        SesionVoto sesion = dataAccess.getUltimaSesion();
        return sesion.getResultado();

        } finally {
            dataAccess.close();
        }
    }

    @WebMethod
    public String añadirDecision(boolean decision) {
        dataAccess.open();
        try {
        	
            Conclave conclave = dataAccess.getConclaveActivo();
            if (conclave == null) {
                return "";
            }
            SesionVoto sesion = dataAccess.getUltimaSesion();
            if (sesion == null) {
                return "No hay ninguna votación pendiente.";
            }

            if(decision) 
            {
            	sesion.setResultado("blanca");
            }
            else 
            {
            	sesion.setResultado("negra");
            }
            

            dataAccess.updateSesionVoto(sesion);
            
            return "precesado con Exito";
           
        } finally {
            dataAccess.close();
        }
    }
    
    
    
    
    
    @WebMethod
    public String procesarDecisionCandidatoDesdeGUI() {
        dataAccess.open();
        try {
        	
            Conclave conclave = dataAccess.getConclaveActivo();
            if (conclave == null) {
                return "";
            }
            SesionVoto sesion = dataAccess.getUltimaSesion();

            Persona ganador = sesion.getGanador();
            
            
            if (sesion.getResultado().equals("blanca")) {
                Papa nuevoPapa = new Papa(ganador.getNombre(), ganador.getFechaNacimiento(), new Date());
                dataAccess.addPapa(nuevoPapa);
                conclave.setFechaFin(new Date());
                conclave.setPapaElegido(nuevoPapa);
                nuevoPapa.setPapaConclave(conclave);
                dataAccess.updateConclave(conclave);
                PantallaExternaGUI.getInstance().mostrarMensaje("Fumata blanca");
                return "¡Tenemos nuevo Papa! " + nuevoPapa.getNombre();
            } else {
                PantallaExternaGUI.getInstance().mostrarMensaje("Fumata negra");
                return "El candidato rechazó. Se puede iniciar una nueva votación.";
            }
        } finally {
            dataAccess.close();
        }
    }
    
    
    @WebMethod
    public String obtenerSesionPendienteConResultado() {

        dataAccess.open();
        try {
        	Conclave conclave = dataAccess.getConclaveActivo();
        	SesionVoto sesion = dataAccess.getLastSesionVotoAcabado(conclave);
        	return sesion.getResultado();
        } finally {
            dataAccess.close();
        }
    }
    
    
    
    
    
    
    
    
    //----------------------------------------------------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------------------------------------------------
    //No, no, DO NOT touch me there, this is my no no square
    //----------------------------------------------------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------------------------------------------------
    
    
    
    
    
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

    
}

