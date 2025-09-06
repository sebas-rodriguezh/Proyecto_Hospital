package org.example.proyectohospital.Datos;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class MedicamentoDatos {
    private final Path xmlPath;
    private JAXBContext ctx;
    private MedicamentoConector cache;

    public MedicamentoDatos(String filePath) {
        try {
            this.xmlPath = Path.of(Objects.requireNonNull(filePath));
            this.ctx = JAXBContext.newInstance(MedicamentoConector.class, MedicamentoEntity.class);
        } catch (Exception e) {
            throw new RuntimeException("Error inicializando MedicamentoDatos: " + e.getMessage());
        }
    }

    public synchronized MedicamentoConector load() {
        try {
            if (cache != null) {
                return cache;
            }

            if (!Files.exists(xmlPath)) {
                cache = new MedicamentoConector();
                save(cache);
                return cache;
            }

            Unmarshaller u = ctx.createUnmarshaller();
            cache = (MedicamentoConector) u.unmarshal(xmlPath.toFile());

            if (cache.getMedicamentos() == null) {
                cache.setMedicamentos(new java.util.ArrayList<>());
            }

            return cache;
        } catch (Exception e) {
            throw new RuntimeException("Error cargando medicamentos: " + e.getMessage());
        }
    }

    public synchronized void save(MedicamentoConector data) {
        try {
            Marshaller m = ctx.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            m.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

            File out = xmlPath.toFile();
            File parent = out.getParentFile();

            if (parent != null) parent.mkdirs();

            java.io.StringWriter sw = new java.io.StringWriter();
            m.marshal(data, sw);
            m.marshal(data, out);

            cache = data;
        } catch (Exception e) {
            throw new RuntimeException("Error guardando medicamentos: " + e.getMessage());
        }
    }

    public Path getXmlPath() {
        return xmlPath;
    }

}
