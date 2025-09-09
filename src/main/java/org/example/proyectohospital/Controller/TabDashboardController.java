package org.example.proyectohospital.Controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.example.proyectohospital.Logica.Hospital;
import org.example.proyectohospital.Modelo.DetalleMedicamento;
import org.example.proyectohospital.Modelo.Medicamento;
import org.example.proyectohospital.Modelo.Receta;
import org.example.proyectohospital.Logica.GestorRecetas;
import org.example.proyectohospital.Logica.GestorMedicamentos;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class TabDashboardController implements Initializable {
    @FXML private TableColumn <Receta, String> colIMeses;
    @FXML private TableColumn <Receta, String> colIMedicamento;
    @FXML private TableView <Receta> tbvResultadoBMedicamento;
    @FXML private Button btnDesplegarDashboard;
    @FXML private Button btnAddMedicamento;
    @FXML private ComboBox <Medicamento> comboBoxMedicamentos;
    @FXML private DatePicker dtpHasta;
    @FXML private DatePicker dtpDesde;

    private final GestorRecetas gestorRecetas = Hospital.getInstance().getGestorRecetas();
    private final GestorMedicamentos gestorMedicamentos = Hospital.getInstance().getGestorMedicamentos();

    @FXML
    public void seleccionMedicamento(ActionEvent actionEvent) {
        Medicamento medicamento = comboBoxMedicamentos.getSelectionModel().getSelectedItem();
        if (medicamento != null) {
            System.out.println("Medicamento seleccionado: " + medicamento.getNombre());
        }

    }

    @FXML
    public void insertarMedicamentoAlDashboard(ActionEvent actionEvent) {
        Medicamento medicamento = comboBoxMedicamentos.getSelectionModel().getSelectedItem();
        if (medicamento != null) {
            mostrarAlerta("Error", "Debe seleccionar un medicamento");
            return;
        }

        boolean resultado = gestorMedicamentos.insertarMedicamento(medicamento);
        if (!resultado) {
            mostrarAlerta("Error","No se logro ingresar el medicamento");
        }

    }

    @FXML
    public void desplegarDashboard() {
        colIMedicamento.setCellValueFactory(cellData-> {
            List<DetalleMedicamento> detalles = cellData.getValue().getDetalleMedicamentos();
            String nombreMedicamento = detalles.isEmpty() ? "Sin medicamento" : detalles.get(0).getMedicamento().getNombre();
            return new SimpleStringProperty(nombreMedicamento);
        });
        colIMeses.setCellValueFactory(cellData->{
            Receta receta = cellData.getValue();
            String rango = receta.getFechaPrescripcion() + " - " + receta.getFechaRetiro();
            return new SimpleStringProperty(rango);
        });

        LocalDate desde = dtpDesde.getValue();
        LocalDate hasta = dtpHasta.getValue();
        Medicamento medicamentoSeleccionado = comboBoxMedicamentos.getSelectionModel().getSelectedItem();
        if(desde == null || hasta == null || medicamentoSeleccionado == null) {
            mostrarAlerta("Error", "Debe seleccionar un medicamento y un rango de fechas valido");
        }
        List<Receta> recetasFiltradas = gestorRecetas.getRecetas().stream().
                filter(r-> r.getFechaPrescripcion().compareTo(desde)>=0 &&
                        r.getFechaRetiro().compareTo(hasta) <=0 &&
                        r.getDetalleMedicamentos().stream()
                            .anyMatch(d->d.getMedicamento().equals(medicamentoSeleccionado)))
                .toList();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        List<Medicamento> medicamentos = gestorMedicamentos.getMedicamentos();
        comboBoxMedicamentos.getItems().setAll(medicamentos);

        comboBoxMedicamentos.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Medicamento med, boolean empty) {
                super.updateItem(med, empty);
                setText(empty || med == null ? "" : med.getNombre());
            }
        });

        comboBoxMedicamentos.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Medicamento med, boolean empty) {
                super.updateItem(med, empty);
                setText(empty || med == null ? "" : med.getNombre());
            }
        });

    }

    private void mostrarAlerta(String titulo, String mensaje){
     Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

}
