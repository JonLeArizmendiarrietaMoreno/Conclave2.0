package util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import configuration.ConfigXML;
import configuration.UtilDate;
import domain.Cardenal;
import domain.CardenalElector;
import domain.Conclave;
import domain.MaestroDeCeremonias;
import domain.Papa;
import domain.Persona;
import domain.SesionVoto;

public class GestorDB {

    private EntityManager em;

    public GestorDB(EntityManager em) {
        this.em = em;
    }

    public void initializeDB() {
        em.getTransaction().begin();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            // 1. Maestro de ceremonias
            MaestroDeCeremonias maestro = new MaestroDeCeremonias("Juan Pérez", sdf.parse("1960-05-10"));
            em.persist(maestro);

            // 2. Cardenales (3 electores, 3 no electores)
            Date fechaElector1 = sdf.parse("1975-03-15");
            Date fechaElector2 = sdf.parse("1978-07-22");
            Date fechaElector3 = sdf.parse("1980-11-05");

            Cardenal cardBase1 = new Cardenal("Luis Martínez", fechaElector1, "Cardenal Presbítero", true);
            Cardenal cardBase2 = new Cardenal("Andrés Gómez", fechaElector2, "Cardenal Diácono", true);
            Cardenal cardBase3 = new Cardenal("Fernando Ruiz", fechaElector3, "Cardenal Obispo", true);

            Date fechaNoElector1 = sdf.parse("1940-02-10");
            Date fechaNoElector2 = sdf.parse("1938-09-25");
            Date fechaNoElector3 = sdf.parse("1942-12-01");

            Cardenal cardNoElect1 = new Cardenal("Tomás Romero", fechaNoElector1, "Cardenal Obispo", false);
            Cardenal cardNoElect2 = new Cardenal("Javier Mendoza", fechaNoElector2, "Cardenal Presbítero", true);
            Cardenal cardNoElect3 = new Cardenal("Roberto Silva", fechaNoElector3, "Cardenal Diácono", false);

            CardenalElector elector1 = new CardenalElector(cardBase1.getNombre(), cardBase1.getFechaNacimiento(), cardBase1.getCargo(), cardBase1.isPresente());
            CardenalElector elector2 = new CardenalElector(cardBase2.getNombre(), cardBase2.getFechaNacimiento(), cardBase2.getCargo(), cardBase2.isPresente());
            CardenalElector elector3 = new CardenalElector(cardBase3.getNombre(), cardBase3.getFechaNacimiento(), cardBase3.getCargo(), cardBase3.isPresente());

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

            // 4. Personas externas
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
            sesion1.getCandidatosVotados().add(cardNoElect1);
            sesion1.getCandidatosVotados().add(cardNoElect2);
            sesion1.getCandidatosVotados().add(externa1);
            sesion1.getCandidatosVotados().add(externa2);
            em.persist(sesion1);

            // 6. Segunda sesión (fumata blanca)
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

            // 7. Papa
            Papa papa = new Papa(cardNoElect1.getNombre(), cardNoElect1.getFechaNacimiento(), horaFin2);
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