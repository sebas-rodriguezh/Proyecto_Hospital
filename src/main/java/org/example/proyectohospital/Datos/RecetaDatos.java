package org.example.proyectohospital.Datos;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class RecetaDatos {
    private final Path xmlPath;
    private JAXBContext ctx;
    private RecetaConector cache;

    public RecetaDatos(String filePath) {
        try {
            this.xmlPath = Path.of(Objects.requireNonNull(filePath));

            // CAMBIO: Incluir todas las entidades necesarias en el contexto JAXB
            this.ctx = JAXBContext.newInstance(
                    RecetaConector.class,
                    RecetaEntity.class,
                    DetalleMedicamentoEntity.class,
                    // Entidades de Personal
                    PersonalEntity.class,
                    MedicoEntity.class,
                    AdministradorEntity.class,
                    FarmaceutaEntity.class,
                    // Entidades de Paciente
                    PacienteEntity.class,
                    // Entidades de Medicamento
                    MedicamentoEntity.class
            );
        } catch (Exception e) {
            throw new RuntimeException("Error inicializando RecetaDatos: " + e.getMessage());
        }
    }

    public synchronized RecetaConector load() {
        try {
            if (cache != null) {
                return cache;
            }

            if (!Files.exists(xmlPath)) {
                cache = new RecetaConector();
                save(cache);
                return cache;
            }

            Unmarshaller u = ctx.createUnmarshaller();
            cache = (RecetaConector) u.unmarshal(xmlPath.toFile());

            if (cache.getRecetas() == null) {
                cache.setRecetas(new java.util.ArrayList<>());
            }

            return cache;
        } catch (Exception e) {
            throw new RuntimeException("Error cargando recetas: " + e.getMessage());
        }
    }

    public synchronized void save(RecetaConector data) {
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
            throw new RuntimeException("Error guardando recetas: " + e.getMessage());
        }
    }

    public Path getXmlPath() {
        return xmlPath;
    }
}