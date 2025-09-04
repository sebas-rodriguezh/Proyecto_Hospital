package org.example.proyectohospital.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.example.proyectohospital.Modelo.Medicamento;
import org.example.proyectohospital.Modelo.Receta;

import java.net.URL;
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

    //private final GestorMedicamentos gestorMedicamentos = new GestorMedicamentos();


    @FXML
    public void seleccionMedicamento(ActionEvent actionEvent) {
    }

    @FXML
    public void insertarMedicamentoAlDashboard(ActionEvent actionEvent) {
    }

    @FXML
    public void desplegarDashboard(ActionEvent actionEvent) {
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //List<Medicamento> medicamentos = gestorMedicamentos.obtenerTodos();
        //comboBoxMedicamentos.getItems().setAll(medicamentos);

//        comboBoxMedicamentos.setCellFactory(listView -> new ListCell<>() {
//            @Override
//            protected void updateItem(Medicamento med, boolean empty) {
//                super.updateItem(med, empty);
//                setText(empty || med == null ? "" : med.getNombre());
//            }
//        });
//
//        comboBoxMedicamentos.setButtonCell(new ListCell<>() {
//            @Override
//            protected void updateItem(Medicamento med, boolean empty) {
//                super.updateItem(med, empty);
//                setText(empty || med == null ? "" : med.getNombre());
//            }
//        });

    }

}
