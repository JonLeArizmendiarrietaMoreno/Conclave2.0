package util;

import java.io.File;

import configuration.ConfigXML;

public class GestorDB {

    public static void main(String[] args) {
        ConfigXML c = ConfigXML.getInstance();
        String fileName = c.getDbFilename();
        
        // Si la BD es local, el archivo está en la ruta especificada
        if (c.isDatabaseLocal()) {
            File dbFile = new File(fileName);
            if (dbFile.exists()) {
                boolean deleted = dbFile.delete();
                if (deleted) {
                    System.out.println("Archivo de BD eliminado: " + fileName);
                    // También eliminar el posible archivo temporal (ObjectDB crea uno con $)
                    File dbFileTemp = new File(fileName + "$");
                    if (dbFileTemp.exists()) {
                        dbFileTemp.delete();
                        System.out.println("Archivo temporal eliminado.");
                    }
                } else {
                    System.out.println("No se pudo eliminar el archivo. ¿Está cerrada la aplicación?");
                }
            } else {
                System.out.println("El archivo de BD no existe: " + fileName);
            }
        } else {
            System.out.println("BD remota: no se puede eliminar automáticamente, hazlo manualmente en el servidor.");
        }
    }
}