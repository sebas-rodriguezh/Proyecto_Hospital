package org.example.proyectohospital.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.proyectohospital.Modelo.DetalleMedicamento;
import org.example.proyectohospital.Modelo.Medicamento;
import org.example.proyectohospital.Modelo.Paciente;
import org.example.proyectohospital.Modelo.Receta;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class TabPrescibirController implements Initializable {

    @FXML private Button btnDetallesReceta;
    @FXML private Button btnLimpiarCampos;
    @FXML private Button btnDescartarMedicamento;
    @FXML private Button btnGuardarReceta;
    @FXML private TableColumn <DetalleMedicamento, Integer> colDuracionMedicamento;
    @FXML private TableColumn <DetalleMedicamento, String> colIndicacionesMedicamento;
    @FXML private TableColumn <DetalleMedicamento, Integer> colCantidadMedicamento;
    @FXML private TableColumn <Medicamento, String> colPresentacionMedicamento;
    @FXML private TableColumn <Medicamento, String> colNombreMedicamento;
    @FXML private TableView <Receta> tbvResultadoBusquedaMedicamento;
    @FXML private TableColumn <Paciente, Integer> colTelefonoPaciente;
    @FXML private TableColumn <Paciente, LocalDate> colFechaNacimientoPaciente;
    @FXML private TableColumn <Paciente, String> colNombrePaciente;
    @FXML private TableColumn <Paciente, String> colIDPaciente;
    @FXML private TableView <Paciente> tbvResultadoBusquedaPaciente;
    @FXML private Button btnSeleccionarMedicamento;
    @FXML private Button btnSeleccionarPaciente;
    @FXML private DatePicker dtpFechaRetiro;
    @FXML private DatePicker dtpFechaPrescripcion;

    @FXML
    public void mostrarDetallesReceta(ActionEvent actionEvent) {
    }

    @FXML
    public void limpiarCamposReceta(ActionEvent actionEvent) {
    }

    @FXML
    public void descartarMedicamentoDeReceta(ActionEvent actionEvent) {
    }

    @FXML
    public void guardarReceta(ActionEvent actionEvent) {
    }

    private void mostrarAlerta(String titulo, String mensaje){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }



    @FXML
    public void seleccionarMedicamento(ActionEvent actionEvent) {
        try
        {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/proyectohospital/View/AgregarMedicamentoReceta.fxml"));
            Parent root = loader.load();

            Stage nuevaVentana = new Stage();
            nuevaVentana.setTitle("Selección de medicamento para receta.");
            nuevaVentana.setScene(new Scene(root));
            nuevaVentana.setMaximized(true);
            nuevaVentana.show();
        }
        catch (Exception e) {
            mostrarAlerta("Error","No se ha podido abrir la ventana." + e.getMessage());
        }

    }

    @FXML
    public void seleccionarPaciente(ActionEvent actionEvent) {





    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
