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
    
    public HashMap<Cardenal, Boolean> iniciarConclave(Date fechaInicio) {
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
            
            HashMap<Cardenal, Boolean> hm = dataAccess.getCardenales();
            
            // Usar la fecha recibida como parámetro, no new Date()
            Conclave conclave = new Conclave(fechaInicio);
            conclave.setMaestroDeCeremonias(maestro);
            
            dataAccess.añadirElectores(hm, conclave);
            dataAccess.addConclave(conclave);
            
            mensaje = "Extra Omnes";
            return hm;
        } finally {
            PantallaExternaGUI.getInstance().mostrarMensaje(mensaje);
            dataAccess.close();
        }
    }
    
    
    
    public boolean iniciarVotacion(Date horaInicio) {
    	System.out.println("empieza iniciarVotacion");
    	
    	dataAccess.open();
    	mensaje ="";
    	
        try {
        	
            Conclave conclaveActual = dataAccess.getConclaveActivo();
            if (conclaveActual == null) {
            	mensaje="No hay conclaves abiertos";
                return false;  
            }

            SesionVoto ultima = dataAccess.getLastSesionVoto(conclaveActual);

            if (ultima != null && ultima.getHoraFin() == null) {
            	mensaje="SesionVoto previa aún abierta";
                return false;
            }
            
            return dataAccess.añadirSesionVoto(horaInicio, conclaveActual);
        } finally {
        	PantallaExternaGUI.getInstance().mostrarMensaje(mensaje);
            dataAccess.close();
        }
        

    }

    
    
    
 // businessLogic/BLFacadeImplementation.java
    @Override
    public boolean registrarPersona(String nombre, Date fechaNacimiento) {
        dataAccess.open();
        try {
            // Verificar si ya existe una persona con mismo nombre y fecha
            if (dataAccess.existePersona(nombre, fechaNacimiento)) {
                return false; // ya existe, no se registra
            }
            // Crear nueva persona y guardar
            Persona nueva = new Persona(nombre, fechaNacimiento);
            dataAccess.addPersona(nueva);
            return true;
        } finally {
            dataAccess.close();
        }
    }
    
    
    @Override
    public boolean votar(String nombreElector, String nombreCandidato) {
        dataAccess.open();
        try {
        	
            Conclave conclave = dataAccess.getConclaveActivo();
            if (conclave == null) {
                return false;
            }
            
            SesionVoto sesionActual = dataAccess.getLastSesionVoto(conclave);
            if (sesionActual == null) {
                return false;
            }
            
            //Verrificar elector
            CardenalElector elector = dataAccess.findCardenalElectorPorNombre(nombreElector);
            if (elector == null) {
                return false;
            }
            
            //corrupto?
            if (sesionActual.getYaHanVotado().contains(elector)) {
                return false;
            }
            
            Persona candidato = dataAccess.findPersonaPorNombre(nombreCandidato);
            if (candidato == null) {
            	
            	MainGUI.getInstance().mostrarMensaje("Error: El candidato '" + nombreCandidato + "' no está registrado.");
            	
                return false;

            }
            
            dataAccess.registrarVoto(sesionActual, elector, candidato);
            
            return true;
        } finally {
            dataAccess.close();
        }
    }
    
    
    
    
    @Override
    public boolean cerrarVotacion(Date ahora) throws IllegalStateException {
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
            
            // 3. Verificar que ha pasado al menos 1 hora desde el inicio
            
            long diffMillis = ahora.getTime() - sesion.getHoraInicio().getTime();
            if (diffMillis < 60 * 60 * 1000) {
                // No ha pasado una hora → rechazar cierre (según flujo alternativo)
                PantallaExternaGUI.getInstance().mostrarMensaje(
                    "No se puede cerrar la votación antes de 1 hora");
                return false;
            }
            
            // 4. Cerrar la sesión (establecer horaFin)
            dataAccess.cerrarSesionVoto(sesion, ahora);
            
            // 5. Calcular total de votos emitidos
            int totalVotos = sesion.getYaHanVotado().size();
            if (totalVotos == 0) {
                // Sin votos, fumata negra
                PantallaExternaGUI.getInstance().mostrarMensaje("Fumata negra");
                sesion.setResultado(SesionVoto.RESULTADO_NEGRA);
                dataAccess.updateSesionVoto(sesion);
                return true;
            }
            
            // 6. Contar votos por candidato (usando la lista de candidatosVotados)
            List<Persona> candidatos = sesion.getCandidatosVotados(); // ahora es List
            Map<Persona, Integer> recuento = new HashMap<>();
            for (Persona p : candidatos) {
                recuento.put(p, recuento.getOrDefault(p, 0) + 1);
            }
            
            // 7. Buscar el candidato con más votos y comprobar si alcanza 2/3
            Persona ganador = null;
            int maxVotos = 0;
            for (Map.Entry<Persona, Integer> entry : recuento.entrySet()) {
                if (entry.getValue() > maxVotos) {
                    maxVotos = entry.getValue();
                    ganador = entry.getKey();
                }
            }
            
            boolean mayoria = (maxVotos * 3 >= totalVotos * 2); // 2/3 o más
            
            if (!mayoria) {
                // Fumata negra
                PantallaExternaGUI.getInstance().mostrarMensaje("Fumata negra");
                sesion.setResultado(SesionVoto.RESULTADO_NEGRA);
                dataAccess.updateSesionVoto(sesion);
                return true;
            }
            
            // Hay mayoría: mostrar ganador en MainGUI y guardar en sesión (sin aceptación aún)
            MainGUI.getInstance().mostrarMensaje("El candidato ganador es: " + ganador.getNombre() +
                    " con " + maxVotos + " votos de " + totalVotos);
            
            sesion.setGanador(ganador);
            dataAccess.updateSesionVoto(sesion);
            
            PersonaGUI.getInstance().mostrarMensaje(ganador.toString()+"has sido elegido como Papa, aceptas?");

            
            return true;
            
        } finally {
            dataAccess.close();
        }

    }
    
    
    @Override
    public boolean procesarDecisionCandidatura(boolean acepta) {

        dataAccess.open();
        try {
        	Conclave conclave = dataAccess.getConclaveActivo();
        	SesionVoto sesion = dataAccess.getLastSesionVoto(conclave);
        	Persona ganador = sesion.getGanador();
            if (acepta) {
                Papa nuevoPapa = new Papa(ganador.getNombre(), ganador.getFechaNacimiento(), sesion.getHoraFin());
                dataAccess.addPapa(nuevoPapa);
            
                conclave.setFechaFin(new Date());
                conclave.setPapaElegido(nuevoPapa);
                nuevoPapa.setPapaConclave(conclave);
                dataAccess.updateConclave(conclave);

                
                
                sesion.setResultado(SesionVoto.RESULTADO_BLANCA);
                dataAccess.updateSesionVoto(sesion);
                
                PantallaExternaGUI.getInstance().mostrarMensaje("Fumata blanca");
                MainGUI.getInstance().mostrarMensaje("¡Tenemos nuevo Papa! " + nuevoPapa.getNombre());
            } else {
                // Rechaza
            	sesion.setResultado(SesionVoto.RESULTADO_NEGRA);
                dataAccess.updateSesionVoto(sesion);
                PantallaExternaGUI.getInstance().mostrarMensaje("Fumata negra");
                MainGUI.getInstance().mostrarMensaje("El candidato rechazó. Se puede iniciar una nueva votación.");
            }
            return true;
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

