package org.example.proyectohospital.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.proyectohospital.Logica.GestorRecetas;
import org.example.proyectohospital.Logica.Hospital;
import org.example.proyectohospital.Modelo.*;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class TabPrescibirController implements Initializable {

    @FXML private Button btnDetallesReceta;
    @FXML private Button btnLimpiarCampos;
    @FXML private Button btnDescartarMedicamento;
    @FXML private Button btnGuardarReceta;

    // Tabla de detalles de medicamentos
    @FXML private TableColumn<DetalleMedicamento, Integer> colDuracionMedicamento;
    @FXML private TableColumn<DetalleMedicamento, String> colIndicacionesMedicamento;
    @FXML private TableColumn<DetalleMedicamento, Integer> colCantidadMedicamento;
    @FXML private TableColumn<DetalleMedicamento, String> colPresentacionMedicamento;
    @FXML private TableColumn<DetalleMedicamento, String> colNombreMedicamento;
    @FXML private TableView<DetalleMedicamento> tbvResultadoBusquedaMedicamento;

    // Tabla de información del paciente
    @FXML private TableColumn<Paciente, Integer> colTelefonoPaciente;
    @FXML private TableColumn<Paciente, LocalDate> colFechaNacimientoPaciente;
    @FXML private TableColumn<Paciente, String> colNombrePaciente;
    @FXML private TableColumn<Paciente, String> colIDPaciente;
    @FXML private TableView<Paciente> tbvResultadoBusquedaPaciente;

    @FXML private Button btnSeleccionarMedicamento;
    @FXML private Button btnSeleccionarPaciente;
    @FXML private DatePicker dtpFechaRetiro;
    @FXML private DatePicker dtpFechaPrescripcion;

    private final GestorRecetas gestorRecetas = Hospital.getInstance().getGestorRecetas();

    // Variables para almacenar los datos de la receta en construcción
    private Medico medicoActual; // El médico que inició sesión
    private Paciente pacienteSeleccionado;
    private ObservableList<DetalleMedicamento> detallesMedicamentos;
    private static int contadorRecetas = 1; // Contador estático


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Configurar columnas de la tabla de pacientes
        this.medicoActual = Hospital.getInstance().getMedicoLogueado();
        colIDPaciente.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombrePaciente.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colFechaNacimientoPaciente.setCellValueFactory(new PropertyValueFactory<>("fechaNacimiento"));
        colTelefonoPaciente.setCellValueFactory(new PropertyValueFactory<>("telefono"));

        // Configurar columnas de la tabla de medicamentos
        colNombreMedicamento.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getMedicamento().getNombre()));
        colPresentacionMedicamento.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getMedicamento().getPresentacion()));
        colCantidadMedicamento.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colDuracionMedicamento.setCellValueFactory(new PropertyValueFactory<>("duracion")); // Corregido: "duracion" no "duracionDias"
        colIndicacionesMedicamento.setCellValueFactory(new PropertyValueFactory<>("indicacion"));

        // Inicializar lista observable para medicamentos
        detallesMedicamentos = FXCollections.observableArrayList();
        tbvResultadoBusquedaMedicamento.setItems(detallesMedicamentos);

        // Establecer fechas por defecto
        dtpFechaPrescripcion.setValue(LocalDate.now());
        dtpFechaRetiro.setValue(LocalDate.now().plusDays(3));

        // Configurar validaciones simples
        configurarValidaciones();
    }

    private void configurarValidaciones() {
        // Validación simple: fecha de retiro no puede ser anterior a prescripción
        dtpFechaRetiro.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && dtpFechaPrescripcion.getValue() != null &&
                    newVal.isBefore(dtpFechaPrescripcion.getValue())) {
                dtpFechaRetiro.setValue(dtpFechaPrescripcion.getValue().plusDays(1));
            }
        });
    }

    @FXML
    public void seleccionarPaciente(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/proyectohospital/View/BuscarPaciente.fxml"));
            Parent root = loader.load();

            BuscarPacienteController buscarController = loader.getController();
            buscarController.setControllerPadre(this);

            Stage ventana = new Stage();
            ventana.setTitle("Seleccionar Paciente");
            ventana.setScene(new Scene(root));
            ventana.initModality(Modality.WINDOW_MODAL);
            ventana.initOwner(btnSeleccionarPaciente.getScene().getWindow());
            ventana.showAndWait();

        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo abrir la ventana: " + e.getMessage());
        }
    }

    @FXML
    public void seleccionarMedicamento(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/proyectohospital/View/AgregarMedicamentoReceta.fxml"));
            Parent root = loader.load();

            AgregarMedicamentoRecetaController medicamentoController = loader.getController();
            medicamentoController.setControllerPadre(this);

            Stage ventana = new Stage();
            ventana.setTitle("Seleccionar Medicamento");
            ventana.setScene(new Scene(root));
            ventana.initModality(Modality.WINDOW_MODAL);
            ventana.initOwner(btnSeleccionarMedicamento.getScene().getWindow());
            ventana.showAndWait();

        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo abrir la ventana: " + e.getMessage());
        }
    }

    // Método llamado desde BuscarPacienteController
    public void setPacienteSeleccionado(Paciente paciente) {
        this.pacienteSeleccionado = paciente;
        ObservableList<Paciente> listaPaciente = FXCollections.observableArrayList(paciente);
        tbvResultadoBusquedaPaciente.setItems(listaPaciente);
    }

    // Método llamado desde AgregarMedicamentoRecetaController
    public void agregarDetalleMedicamento(DetalleMedicamento detalleMedicamento) {
        // Verificar duplicados
        boolean existe = detallesMedicamentos.stream()
                .anyMatch(detalle -> detalle.getMedicamento().getCodigo().equals(
                        detalleMedicamento.getMedicamento().getCodigo()));

        if (existe) {
            mostrarAlerta("Medicamento duplicado", "Este medicamento ya está en la receta.");
            return;
        }
        detallesMedicamentos.add(detalleMedicamento);
    }

    @FXML
    public void mostrarDetallesReceta(ActionEvent actionEvent) {
//        if (!validarDatos()) return;
//
//        StringBuilder resumen = new StringBuilder();
//        resumen.append("=== RESUMEN DE LA RECETA ===\n\n");
//        resumen.append("Médico: ").append(medicoActual.getNombre()).append("\n");
//        resumen.append("Especialidad: ").append(medicoActual.getEspecialidad()).append("\n");
//        resumen.append("Paciente: ").append(pacienteSeleccionado.getNombre()).append("\n");
//        resumen.append("Fecha Prescripción: ").append(dtpFechaPrescripcion.getValue()).append("\n");
//        resumen.append("Fecha Retiro: ").append(dtpFechaRetiro.getValue()).append("\n\n");
//        resumen.append("Medicamentos:\n");
//
//        for (DetalleMedicamento detalle : detallesMedicamentos) {
//            resumen.append("• ").append(detalle.getMedicamento().getNombre())
//                    .append(" - ").append(detalle.getCantidad()).append(" unidades")
//                    .append(" - ").append(detalle.getDuracion()).append(" días\n")
//                    .append("  ").append(detalle.getIndicaciones()).append("\n\n");
//        }
//
//        Alert alert = new Alert(Alert.AlertType.INFORMATION);
//        alert.setTitle("Detalles de la Receta");
//        alert.setHeaderText(null);
//
//        TextArea textArea = new TextArea(resumen.toString());
//        textArea.setEditable(false);
//        textArea.setWrapText(true);
//        textArea.setPrefRowCount(20);
//
//        alert.getDialogPane().setContent(textArea);
//        alert.showAndWait();
    }

    @FXML
    public void limpiarCamposReceta(ActionEvent actionEvent) {
        pacienteSeleccionado = null;
        tbvResultadoBusquedaPaciente.getItems().clear();
        detallesMedicamentos.clear();
        dtpFechaPrescripcion.setValue(LocalDate.now());
        dtpFechaRetiro.setValue(LocalDate.now().plusDays(3));
    }

    @FXML
    public void descartarMedicamentoDeReceta(ActionEvent actionEvent) {
        DetalleMedicamento seleccionado = tbvResultadoBusquedaMedicamento.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAlerta("Seleccione medicamento", "Debe seleccionar un medicamento para eliminarlo.");
            return;
        }

        detallesMedicamentos.remove(seleccionado);
    }

    @FXML
    public void guardarReceta(ActionEvent actionEvent) {
        if (!validarDatos()) return;

        try {
            String idReceta = generarIdReceta();
            Receta nuevaReceta = new Receta(idReceta, medicoActual, pacienteSeleccionado,
                    dtpFechaPrescripcion.getValue(), dtpFechaRetiro.getValue(), 1);


            for (int i = 0; i < detallesMedicamentos.size(); i++) {
                DetalleMedicamento detalle = detallesMedicamentos.get(i);
                nuevaReceta.agregarDetalleMedicamento(detalle);
            }

            boolean guardada = gestorRecetas.insertarReceta(nuevaReceta);

            if (guardada) {
                mostrarAlerta("Éxito", "Receta guardada con " + detallesMedicamentos.size() + " medicamentos");
                limpiarCamposReceta(null);
            }

        } catch (Exception e) {
            mostrarAlerta("Error", "Problemas al ingresar los " + detallesMedicamentos.size() + " de medicamentos");
            e.printStackTrace();
        }
    }

    private boolean validarDatos() {
        if (medicoActual == null) {
            mostrarAlerta("Error", "No se ha establecido el médico.");
            return false;
        }
        if (pacienteSeleccionado == null) {
            mostrarAlerta("Falta paciente", "Debe seleccionar un paciente.");
            return false;
        }
        if (detallesMedicamentos.isEmpty()) {
            mostrarAlerta("Faltan medicamentos", "Debe agregar al menos un medicamento.");
            return false;
        }
        if (dtpFechaPrescripcion.getValue() == null || dtpFechaRetiro.getValue() == null) {
            mostrarAlerta("Faltan fechas", "Debe seleccionar las fechas.");
            return false;
        }
        return true;
    }

    private String generarIdReceta() {
        LocalDate fecha = LocalDate.now();
        long timestamp = System.currentTimeMillis() % 1000000;

        String idReceta = String.format("REC%02d%02d%06d",
                fecha.getMonthValue(),
                fecha.getDayOfMonth(),
                timestamp);

        return idReceta;
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.show();
    }
}