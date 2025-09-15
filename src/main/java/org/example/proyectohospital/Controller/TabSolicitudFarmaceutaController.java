package org.example.proyectohospital.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
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

    private GestorRecetas gestorRecetas;

    @FXML
    public void buscarReceta(ActionEvent event) {
        String filtro = txtBuscarPacienteSolicitud.getText().trim();
        LocalDate fechaRetiro = dateRetiroSolicitud.getValue();

        List<Receta> recetas = gestorRecetas.obtenerRecetasPorEstado(1);

        if (!filtro.isEmpty()) {
            recetas = recetas.stream()
                    .filter(r -> r.getId().toLowerCase().contains(filtro.toLowerCase()) ||
                            r.getNombrePaciente().toLowerCase().contains(filtro.toLowerCase()))
                    .toList();
        }

        if (fechaRetiro != null) {
            recetas = recetas.stream()
                    .filter(r -> r.getFechaRetiro().equals(fechaRetiro))
                    .toList();
        }

        tableRecetasSolicitud.getItems().setAll(recetas);
    }

    @FXML
    public void ponerEnProceso(ActionEvent event) {
        Receta receta = tableRecetasSolicitud.getSelectionModel().getSelectedItem();
        if (receta == null) {
            mostrarAlerta("Selección requerida", "Debe seleccionar una receta válida");
            return;
        }

        if (gestorRecetas.actualizarEstadoReceta(receta.getId(), 2)) { // cambia a Confeccionada
            mostrarAlerta("Éxito", "Receta puesta en proceso correctamente");
            buscarReceta(null); // refrescar tabla
        }
    }


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colIdSolicitud.setCellValueFactory(new PropertyValueFactory<>("id"));

        colPacienteSolicitud.setCellValueFactory(new PropertyValueFactory<>("nombrePaciente"));

        colFechaRetiroSolicitud.setCellValueFactory(new PropertyValueFactory<>("fechaRetiro"));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        colFechaRetiroSolicitud.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate fecha, boolean empty) {
                super.updateItem(fecha, empty);
                setText(empty || fecha == null ? "" : fecha.format(formatter));
            }
        });
    }


    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
