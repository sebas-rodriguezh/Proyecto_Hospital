package org.example.proyectohospital.Datos;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class PacienteDatos {
    private final Path xmlPath;
    private JAXBContext ctx; //Contexto del archivo.
    private PacienteConector cache;
    
    public PacienteDatos(String filePath) {
        try {
            this.xmlPath = Path.of(Objects.requireNonNull(filePath));
            this.ctx = JAXBContext.newInstance(PacienteConector.class, PacienteEntity.class);
        }
        catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public synchronized PacienteConector load() { //Cada vez que haya un upd, lo pueda sync y llevarlo a presentación.
        try {
            if (cache != null) {
                return cache;
            }

            if (!Files.exists(xmlPath)) {
                cache = new PacienteConector();
                save(cache); //Crea un archivo vacío.
                return cache;
            }

            //Convierte XML a Java.
            Unmarshaller u = ctx.createUnmarshaller(); //Convertidor.

            //Gestiona la información convertida del archivo XML.
            cache = (PacienteConector) u.unmarshal(xmlPath.toFile()); //Tiene que convertir.

            //Esto es si el archivo está vacío.
            if (cache.getPacientes() == null)
            {
                //Aquí creamos una primera instancia de clientes dentro del archivo.
                //Esto se aplicaría la primera vez que se corre el sistema.
                //o cuando se limpia la información de BD.
                cache.setPacientes(new java.util.ArrayList<>());

            }
            return cache;
        }
        catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public synchronized void save(PacienteConector data) {
        try {
            Marshaller m = ctx.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,Boolean.TRUE); //Convertimos todas las propiedas sí o sí.
            m.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

            File out = xmlPath.toFile(); //Tome el XML path y hagalo archivo.
            File parent = out.getParentFile();

            if (parent != null) parent.mkdirs();

            java.io.StringWriter sw = new java.io.StringWriter();
            m.marshal(data, sw); //Pasa los datos a escritura.
            m.marshal(data, out); //Escribe los datos en el archivo.

            cache = data;
        }

        catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public  Path getXmlPath() {
        return xmlPath;
    }

}
