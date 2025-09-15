package org.example.proyectohospital.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.proyectohospital.Logica.Hospital;
import org.example.proyectohospital.Modelo.Receta;
import org.example.proyectohospital.Logica.GestorRecetas;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class TabSolicitudFarmaceutaController implements Initializable {

    @FXML private TableView<Receta> tableRecetasSolicitud;
    @FXML private TableColumn<Receta, String> colIdSolicitud;
    @FXML private TableColumn<Receta, String> colPacienteSolicitud;
    @FXML private TableColumn<Receta, LocalDate> colFechaRetiroSolicitud;
    @FXML private TableColumn<Receta, String> colEstadoSolicitud;

    @FXML private TextField txtBuscarPacienteSolicitud;
    @FXML private DatePicker dateRetiroSolicitud;
    @FXML private Button btnBuscarSolicitud;
    @FXML private Button btnPonerEnProceso;

    private final GestorRecetas gestorRecetas = Hospital.getInstance().getGestorRecetas();
    private final ObservableList<Receta> listaRecetas = FXCollections.observableArrayList();
    private Receta recetaSeleccionada;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        inicializarTabla();

        tableRecetasSolicitud.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    recetaSeleccionada = newValue;
                }
        );
    }

    private void inicializarTabla() {
        colIdSolicitud.setCellValueFactory(new PropertyValueFactory<>("id"));
        colPacienteSolicitud.setCellValueFactory(new PropertyValueFactory<>("nombrePaciente"));
        colFechaRetiroSolicitud.setCellValueFactory(new PropertyValueFactory<>("fechaRetiro"));
        colEstadoSolicitud.setCellValueFactory(new PropertyValueFactory<>("nombreEstado"));


        listaRecetas.setAll(gestorRecetas.obtenerRecetasPorEstado(1));
        tableRecetasSolicitud.setItems(listaRecetas);

    }

    public void actualizarTabla() {
        listaRecetas.setAll(gestorRecetas.obtenerRecetasPorEstado(1));
    }


    @FXML
    public void buscarReceta(ActionEvent event) {
        try {
            String filtro = txtBuscarPacienteSolicitud.getText().trim();
            LocalDate fechaRetiro = dateRetiroSolicitud.getValue();

            List<Receta> recetas = gestorRecetas.obtenerRecetasPorEstado(1);

            if (filtro.isEmpty() && fechaRetiro == null) {
                listaRecetas.setAll(gestorRecetas.obtenerRecetasPorEstado(1));
                return;
            }

            if (!filtro.isEmpty()) {
                recetas = recetas.stream()
                        .filter(r -> r.getId().toLowerCase().contains(filtro.toLowerCase()) ||
                                r.getNombrePaciente().toLowerCase().contains(filtro.toLowerCase()) ||
                                (r.getPaciente() != null && r.getPaciente().getId().toLowerCase().contains(filtro.toLowerCase())))
                        .toList();
            }

            if (fechaRetiro != null) {
                recetas = recetas.stream()
                        .filter(r -> r.getFechaRetiro().equals(fechaRetiro))
                        .toList();
            }

            tableRecetasSolicitud.getItems().setAll(recetas);
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo buscar ninguna receta.");
        }
    }

    @FXML
    public void ponerEnProceso(ActionEvent event) {
        try {
            if (recetaSeleccionada == null) {
                mostrarAlerta("Selección requerida", "Debe seleccionar una receta válida");
                return;
            }

            LocalDate fechaRetiroPaciente = dateRetiroSolicitud.getValue();

            if (fechaRetiroPaciente == null) {
                mostrarAlerta("Error", "Debe de indicar la fecha de recepción.");
                return;
            }

            LocalDate fechaRetiroReceta = recetaSeleccionada.getFechaRetiro();
            LocalDate fechaMinima = fechaRetiroReceta.minusDays(3);
            LocalDate fechaMaxima = fechaRetiroReceta.plusDays(3);

            if (fechaRetiroPaciente.isBefore(fechaMinima) || fechaRetiroPaciente.isAfter(fechaMaxima)) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                String mensaje = String.format("La fecha de retiro debe estar entre %s y %s", fechaMinima.format(formatter), fechaMaxima.format(formatter));
                mostrarAlerta("Error", mensaje);
            } else {
                if (gestorRecetas.actualizarEstadoReceta(recetaSeleccionada.getId(), 2)) {
                    mostrarAlerta("Éxito", "Receta puesta en proceso correctamente");
                    listaRecetas.setAll(gestorRecetas.obtenerRecetasPorEstado(1));
                    actualizarTabla();
                }
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo procesar la receta.");
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}