package org.example.proyectohospital.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.proyectohospital.Modelo.Paciente;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class BuscarPacienteController implements Initializable {
    @FXML private Button btnSalir;
    @FXML private Button btnSeleccionar;
    @FXML private TextField txtValorBuscado;
    @FXML private ComboBox<String> comboBoxFiltro;
    @FXML private TableColumn <Paciente, Integer> colTelefonoPaciente;
    @FXML private TableColumn <Paciente, LocalDate> colFechaNacimientoPaciente;
    @FXML private TableColumn <Paciente, String> colNombrePaciente;
    @FXML private TableColumn <Paciente, String> colIDPaciente;
    @FXML private TableView<Paciente> tbvResultadoBPaciente;

    public BuscarPacienteController() {

    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Esos string deben llamarse igual que los atributos.
        colIDPaciente.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombrePaciente.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colFechaNacimientoPaciente.setCellValueFactory(new PropertyValueFactory<>("fechaNacimiento"));
        colTelefonoPaciente.setCellValueFactory(new PropertyValueFactory<>("telefono"));
    }

    @FXML
    private void volverAAnterior(ActionEvent actionEvent) {
        //Debe volver al tabDePrescribir.
    }

    @FXML
    private void seleccionarCliente(ActionEvent actionEvent) {
    }
}
