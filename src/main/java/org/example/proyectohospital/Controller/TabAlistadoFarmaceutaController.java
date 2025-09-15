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

public class TabAlistadoFarmaceutaController implements Initializable {

    @FXML private Button btnPonerEnLista;
    @FXML private Button btnBuscarSolicitud;
    @FXML private TextField txtBuscarPacienteSolicitud;
    @FXML private TableColumn <Receta, String> colIdAlistado;
    @FXML private TableColumn<Receta, String> colPacienteAlistado;
    @FXML private TableColumn <Receta, LocalDate> colFechaRetiroAlistado;
    @FXML private TableView <Receta> tableRecetasAlistado;

    private GestorRecetas gestorRecetas;

    @FXML
    public void buscarReceta(ActionEvent event) {
        String filtro = txtBuscarPacienteSolicitud.getText().trim();

        List<Receta> recetas = gestorRecetas.obtenerRecetasPorEstado(2);

        if (!filtro.isEmpty()) {
            recetas = recetas.stream()
                    .filter(r -> r.getId().toLowerCase().contains(filtro.toLowerCase()) ||
                            r.getNombrePaciente().toLowerCase().contains(filtro.toLowerCase()))
                    .toList();
        }

        tableRecetasAlistado.getItems().setAll(recetas);
    }

    @FXML
    public void alistarMedicamentoSeleccionado(ActionEvent actionEvent) {
        Receta seleccionada = tableRecetasAlistado.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAlerta("Selección requerida", "Debe seleccionar una receta válida");
            return;
        }

        if (gestorRecetas.actualizarEstadoReceta(seleccionada.getId(), 3)) { // cambia a Lista
            mostrarAlerta("Éxito", "Receta alistada correctamente");
            buscarReceta(null); // refrescar tabla
        }
    }



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        gestorRecetas = new GestorRecetas("src/main/resources/recetas.xml");

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
