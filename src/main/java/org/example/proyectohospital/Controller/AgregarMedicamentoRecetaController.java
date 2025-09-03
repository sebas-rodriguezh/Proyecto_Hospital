package org.example.proyectohospital.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.example.proyectohospital.Modelo.Medicamento;

import java.net.URL;
import java.util.ResourceBundle;


public class AgregarMedicamentoRecetaController {
    @FXML private Button btnSalir;
    @FXML private Button btnSeleccionar;
    @FXML private TextField txtValorBuscado;
    @FXML private ComboBox comboBoxFiltro;
    @FXML private TableColumn<Medicamento, String> colPresentacionMedicamento;
    @FXML private TableColumn<Medicamento, String> colNombreMedicamento;
    @FXML private TableColumn<Medicamento, String> colCodigoMedicamento;
    @FXML private TableView<Medicamento> tbvResultadoBPaciente;


//    @Override
//    public void initialize(URL url, ResourceBundle resourceBundle) {
//        //Para inicializar y poder cargar listas de medicamentos desde la logica -> repositorio.
//    }

    @FXML
    private void volverAAnterior(ActionEvent actionEvent) {
    }


    @FXML
    private void seleccionarMedicamento(ActionEvent actionEvent) {
    }
}
