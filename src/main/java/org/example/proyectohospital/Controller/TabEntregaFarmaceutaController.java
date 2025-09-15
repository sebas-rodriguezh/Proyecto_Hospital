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

public class TabEntregaFarmaceutaController implements Initializable {
    @FXML private TextArea txtDetallesEntregaFarmaceuta;
    @FXML private Button btnEntregarRecetaFarmaceuta;
    @FXML private TableColumn <Receta, LocalDate> colFechaRetiroEntrega;
    @FXML private TableColumn <Receta, String> colPacienteEntrega;
    @FXML private TableColumn <Receta,String> colIdEntrega;
    @FXML private TableView <Receta> tableTabEntregaFarmaceuta;
    @FXML private Button btnBuscarTabEntregaFarmaceuta;
    @FXML private TextField txtIdNombreEntregaFarmaceuta;

    private GestorRecetas gestorRecetas;


    @FXML
    public void entregarReceta(ActionEvent actionEvent) {
        Receta seleccionada = tableTabEntregaFarmaceuta.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAlerta("Selección requerida", "Debe seleccionar una receta válida");
            return;
        }

        String detalles = txtDetallesEntregaFarmaceuta.getText().trim();
        if (detalles.isEmpty()) {
            mostrarAlerta("Datos incompletos", "Debe agregar detalles de entrega");
            return;
        }

        // Aquí podrías guardar los detalles si quieres
        if (gestorRecetas.actualizarEstadoReceta(seleccionada.getId(), 4)) { // cambia a Entregada
            mostrarAlerta("Éxito", "Receta entregada correctamente");
            buscarRecetaParaEntrega(null); // refrescar tabla
        }
    }

    @FXML
    public void tableTabEntregaFarmaceuta(SortEvent<TableView> tableViewSortEvent) {
        //LOGICA AL ORDENAR TABLA
    }

    @FXML
    public void buscarRecetaParaEntrega(ActionEvent actionEvent) {
        String filtro = txtIdNombreEntregaFarmaceuta.getText().trim();
        List<Receta> recetas = gestorRecetas.obtenerRecetasPorEstado(3);

        if (!filtro.isEmpty()) {
            recetas = recetas.stream()
                    .filter(r -> r.getId().toLowerCase().contains(filtro.toLowerCase()) ||
                            r.getNombrePaciente().toLowerCase().contains(filtro.toLowerCase()))
                    .toList();
        }

        tableTabEntregaFarmaceuta.getItems().setAll(recetas);
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colIdEntrega.setCellValueFactory(new PropertyValueFactory<>("id"));

        colPacienteEntrega.setCellValueFactory(new PropertyValueFactory<>("nombrePaciente"));

        colFechaRetiroEntrega.setCellValueFactory(new PropertyValueFactory<>("fechaRetiro"));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        colFechaRetiroEntrega.setCellFactory(column -> new TableCell<>() {
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
