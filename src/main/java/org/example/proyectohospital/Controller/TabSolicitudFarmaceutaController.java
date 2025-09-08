package org.example.proyectohospital.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.proyectohospital.Modelo.Receta;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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


    @FXML
    private void buscarReceta(ActionEvent event) {
        String filtro = txtBuscarPacienteSolicitud.getText().trim();
        LocalDate fechaRetiro = dateRetiroSolicitud.getValue();

        if(filtro.isEmpty() && fechaRetiro == null) {
            mostrarAlerta("Selección requerida", "Debe seleccionar una receta válida");
            return;
        }
        //LOGICA DE FILTRADO, BUSCAR RECETAS SEGUN ID, PACIENTE O FECHA
    }


    @FXML
    private void ponerEnProceso(ActionEvent event) {
        Receta receta = tableRecetasSolicitud.getSelectionModel().getSelectedItem();
        if(receta == null) {
            mostrarAlerta("Selección requerida", "Debe seleccionar una receta válida");
            return;
        }
        //LOGICA ACTUALIZAR EL ESTADO DE LA RECETA
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
