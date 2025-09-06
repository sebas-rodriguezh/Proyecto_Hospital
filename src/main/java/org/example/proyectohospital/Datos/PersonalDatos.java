package org.example.proyectohospital.Datos;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class PersonalDatos {
    private final Path xmlPath;
    private JAXBContext ctx;
    private PersonalConector cache;


    public PersonalDatos(String filePath) {
        try {
            this.xmlPath = Path.of(Objects.requireNonNull(filePath));
            this.ctx = JAXBContext.newInstance(
                    PersonalConector.class,
                    PersonalEntity.class,
                    MedicoEntity.class,
                    AdministradorEntity.class,
                    FarmaceutaEntity.class
            );
        } catch (Exception e) {
            throw new RuntimeException("Error inicializando PersonalDatos: " + e.getMessage());
        }
    }

    public synchronized PersonalConector load() {
        try {
            if (cache != null) {
                return cache;
            }

            if (!Files.exists(xmlPath)) {
                cache = new PersonalConector();
                save(cache);
                return cache;
            }

            Unmarshaller u = ctx.createUnmarshaller();
            cache = (PersonalConector) u.unmarshal(xmlPath.toFile());

            if (cache.getPersonal() == null) {
                cache.setPersonal(new java.util.ArrayList<>());
            }

            return cache;
        } catch (Exception e) {
            throw new RuntimeException("Error cargando personal: " + e.getMessage());
        }
    }

    public synchronized void save(PersonalConector data) {
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


    public Path getXmlPath() {
        return xmlPath;
    }



}
