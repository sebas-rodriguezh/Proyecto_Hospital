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
        Receta seleccionada = tableRecetasAlistado.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAlerta("Selección requerida", "Debe seleccionar una receta válida");
            return;
        }

        //LOGICA DE ALISTAR EL MEDICAMENTO
    }

    @FXML
    private void buscarReceta(ActionEvent actionEvent) {
        String filtro = txtBuscarPacienteSolicitud.getText().trim();
        if(filtro.isEmpty()){
            mostrarAlerta("Búsqueda inválida","Ingrese ID o nombre del paciente.");
            return;
        }

        //LOGICA DE BUSCAR RECETA
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        colIdAlistado.setCellValueFactory(new PropertyValueFactory<>("id"));

        colPacienteAlistado.setCellValueFactory(new PropertyValueFactory<>("nombrePaciente"));

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

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
