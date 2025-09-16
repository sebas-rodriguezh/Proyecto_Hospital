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

public class TabEntregaFarmaceutaController implements Initializable {
    @FXML private Button btnEntregarRecetaFarmaceuta;
    @FXML private Button btnBuscarTabEntregaFarmaceuta;
    @FXML private TableColumn <Receta, LocalDate> colFechaRetiroEntrega;
    @FXML private TableColumn <Receta, String> colPacienteEntrega;
    @FXML private TableColumn <Receta,String> colIdEntrega;
    @FXML private TableColumn <Receta,String> colEstadoReceta;
    @FXML private TableView <Receta> tableTabEntregaFarmaceuta;
    @FXML private TextField txtIdNombreEntregaFarmaceuta;

    private final GestorRecetas gestorRecetas = Hospital.getInstance().getGestorRecetas();
    private final ObservableList<Receta> listaRecetas = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        inicializarTabla();
    }

    private void inicializarTabla() {
        listaRecetas.setAll(gestorRecetas.obtenerRecetasPorEstado(3));
        tableTabEntregaFarmaceuta.setItems(listaRecetas);
        colIdEntrega.setCellValueFactory(new PropertyValueFactory<>("id"));
        colPacienteEntrega.setCellValueFactory(new PropertyValueFactory<>("nombrePaciente"));
        colFechaRetiroEntrega.setCellValueFactory(new PropertyValueFactory<>("fechaRetiro"));
        colEstadoReceta.setCellValueFactory(new PropertyValueFactory<>("nombreEstado"));
    }

    public void actualizarTabla() {
        listaRecetas.setAll(gestorRecetas.obtenerRecetasPorEstado(3));
    }

    @FXML
    public void entregarReceta(ActionEvent actionEvent) {
        Receta seleccionada = tableTabEntregaFarmaceuta.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAlerta("Selección requerida", "Debe seleccionar una receta válida");
            return;
        }

        if (gestorRecetas.actualizarEstadoReceta(seleccionada.getId(), 4)) {
            mostrarAlerta("Éxito", "Receta entregada correctamente");
            buscarRecetaParaEntrega(null);
        }
    }

    @FXML
    public void buscarRecetaParaEntrega(ActionEvent actionEvent) {
        String filtro = txtIdNombreEntregaFarmaceuta.getText().trim();
        List<Receta> recetas = gestorRecetas.obtenerRecetasPorEstado(3);

        if (!filtro.isEmpty()) {
            recetas = recetas.stream()
                    .filter(r -> r.getId().toLowerCase().contains(filtro.toLowerCase()) ||
                            r.getNombrePaciente().toLowerCase().contains(filtro.toLowerCase()) ||
                            r.getPaciente().getId().toLowerCase().contains(filtro.toLowerCase())
                    )
                    .toList();
        } else {
            actualizarTabla();
        }
        tableTabEntregaFarmaceuta.getItems().setAll(recetas);
    }


    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
