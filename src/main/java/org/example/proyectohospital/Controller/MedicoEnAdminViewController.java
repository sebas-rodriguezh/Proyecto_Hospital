package org.example.proyectohospital.Controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.example.proyectohospital.Modelo.Medico;
import org.example.proyectohospital.Modelo.Personal;

import java.net.URL;
import java.util.ResourceBundle;

public class MedicoEnAdminViewController implements Initializable {
    @FXML private Button btnMostrarTodosLosMedicos;
    @FXML private Button btnModificarMedico;
    @FXML private TextField txtBuscarMedico;
    @FXML private Button btnBuscarMedico;
    @FXML private TableView<Medico> tbvResultadoBusquedaMedico;
    @FXML private TableColumn<Medico, String> colIDMedico;
    @FXML private TableColumn<Medico, String> colNombreMedico;
    @FXML private TableColumn<Medico, String> colEspecialidadMedico;
    @FXML private Button btnBorrarMedico;
    @FXML private Button btnLimpiarCampos;
    @FXML private Button btnGuardarMedico;
    @FXML private TextField txtEspecialidadMedico;
    @FXML private TextField txtNombreMedico;
    @FXML private TextField txtIdMedico;

    public MedicoEnAdminViewController() {

    }

    @FXML
    private void mostrarTodosLosMedicos(ActionEvent actionEvent) {
    }

    @FXML
    private void modificarMedico(ActionEvent actionEvent) {
    }

    @FXML
    private void buscarMedico(ActionEvent actionEvent) {
    }


    @FXML
    private void borrarMedico(ActionEvent actionEvent) {
    }

    @FXML
    private void limpiarCamposMedicos(ActionEvent actionEvent) {
    }

    @FXML
    private void guardarMedico(ActionEvent actionEvent) {
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colIDMedico.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getId()));

        colNombreMedico.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getNombre()));

        colEspecialidadMedico.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getEspecialidad()));
    }
}
