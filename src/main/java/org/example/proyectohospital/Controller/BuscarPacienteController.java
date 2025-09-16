package org.example.proyectohospital.Controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.proyectohospital.Logica.GestorPacientes;
import org.example.proyectohospital.Logica.Hospital;
import org.example.proyectohospital.Modelo.Paciente;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class BuscarPacienteController implements Initializable {
    @FXML private Button btnSalir;
    @FXML private Button btnSeleccionar;
    @FXML private TextField txtValorBuscado;
    @FXML private ComboBox<String> comboBoxFiltro;
    @FXML private TableColumn<Paciente, Integer> colTelefonoPaciente;
    @FXML private TableColumn<Paciente, LocalDate> colFechaNacimientoPaciente;
    @FXML private TableColumn<Paciente, String> colNombrePaciente;
    @FXML private TableColumn<Paciente, String> colIDPaciente;
    @FXML private TableView<Paciente> tbvResultadoBPaciente;

    private final GestorPacientes gestorPacientes = Hospital.getInstance().getGestorPacientes();
    private Paciente pacienteSeleccionado;
    private TabPrescibirController controllerPadre;

    public BuscarPacienteController() {
    }

    public void setControllerPadre(TabPrescibirController controller) {
        this.controllerPadre = controller;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colIDPaciente.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombrePaciente.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colFechaNacimientoPaciente.setCellValueFactory(new PropertyValueFactory<>("fechaNacimiento"));
        colTelefonoPaciente.setCellValueFactory(new PropertyValueFactory<>("telefono"));

        comboBoxFiltro.setItems(FXCollections.observableArrayList("ID", "Nombre", "Teléfono"));
        comboBoxFiltro.setValue("Nombre");

        cargarTodosLosPacientes();

        txtValorBuscado.textProperty().addListener((obs, oldVal, newVal) -> {
            filtrarPacientes(newVal);
        });

        tbvResultadoBPaciente.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    pacienteSeleccionado = newValue;
                    btnSeleccionar.setDisable(newValue == null);
                }
        );

        btnSeleccionar.setDisable(true);
    }

    private void cargarTodosLosPacientes() {
        try {
            List<Paciente> pacientes = gestorPacientes.getPacientes();
            tbvResultadoBPaciente.setItems(FXCollections.observableArrayList(pacientes));
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al cargar los pacientes: " + e.getMessage());
        }
    }

    private void filtrarPacientes(String texto) {
        if (texto == null || texto.isBlank()) {
            cargarTodosLosPacientes();
            return;
        }

        try {
            String filtro = texto.toLowerCase().trim();
            String tipoFiltro = comboBoxFiltro.getValue();

            List<Paciente> todosLosPacientes = gestorPacientes.getPacientes();
            List<Paciente> filtrados = todosLosPacientes.stream()
                    .filter(p -> {
                        switch (tipoFiltro) {
                            case "ID":
                                return p.getId().toLowerCase().contains(filtro);
                            case "Nombre":
                                return p.getNombre().toLowerCase().contains(filtro);
                            case "Teléfono":
                                return String.valueOf(p.getTelefono()).contains(filtro);
                            default:
                                return p.getNombre().toLowerCase().contains(filtro) ||
                                        p.getId().toLowerCase().contains(filtro) ||
                                        String.valueOf(p.getTelefono()).contains(filtro);
                        }
                    })
                    .collect(Collectors.toList());

            tbvResultadoBPaciente.setItems(FXCollections.observableArrayList(filtrados));
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al filtrar pacientes: " + e.getMessage());
        }
    }

    @FXML
    private void volverAAnterior(ActionEvent actionEvent) {
        cerrarVentana();
    }

    @FXML
    private void seleccionarCliente(ActionEvent actionEvent) {
        if (pacienteSeleccionado == null) {
            mostrarAlerta("Seleccione paciente", "Debe seleccionar un paciente.");
            return;
        }

        try {
            if (controllerPadre != null) {
                controllerPadre.setPacienteSeleccionado(pacienteSeleccionado);
            }
            cerrarVentana();
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al seleccionar paciente: " + e.getMessage());
        }
    }

    private void cerrarVentana() {
        Stage stage = (Stage) btnSalir.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}