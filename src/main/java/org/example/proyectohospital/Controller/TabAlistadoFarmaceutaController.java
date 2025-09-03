package org.example.proyectohospital.Controller;

import javafx.beans.property.SimpleStringProperty;
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

public class TabAlistadoFarmaceutaController implements Initializable {

    @FXML private Button btnPonerEnLista;
    @FXML private Button btnBuscarSolicitud;
    @FXML private TextField txtBuscarPacienteSolicitud;
    @FXML private TableColumn <Receta, String> colIdAlistado;
    @FXML private TableColumn<Receta, String> colPacienteAlistado;
    @FXML private TableColumn <Receta, LocalDate> colFechaRetiroAlistado;
    @FXML private TableView <Receta> tableRecetasAlistado;

    @FXML
    private void alistarMedicamentoSeleccionado(ActionEvent actionEvent) {
    }

    @FXML
    private void buscarReceta(ActionEvent actionEvent) {
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        colIdAlistado.setCellValueFactory(new PropertyValueFactory<>("id"));

        colPacienteAlistado.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getPaciente() != null
                                ? data.getValue().getPaciente().getNombre()
                                : ""
                )
        );

        colFechaRetiroAlistado.setCellValueFactory(new PropertyValueFactory<>("fechaRetiro"));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        colFechaRetiroAlistado.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate fecha, boolean empty) {
                super.updateItem(fecha, empty);
                setText(empty || fecha == null ? "" : fecha.format(formatter));
            }
        });
    }

}
