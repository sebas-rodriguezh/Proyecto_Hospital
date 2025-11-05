package org.example.frontend.Controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.proyectohospital.Modelo.Paciente;

import org.example.frontend.Modelo.HospitalFrontend;
import org.example.frontend.Servicios.HospitalServiceProxy;
import org.example.proyectohospital.shared.RespuestaBackend;
import org.example.proyectohospital.shared.SolicitudBackend;


import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class BuscarPacienteController implements Initializable {
    @FXML private ProgressIndicator progressBusqueda;
    @FXML private Button btnSalir;
    @FXML private Button btnSeleccionar;
    @FXML private TextField txtValorBuscado;
    @FXML private ComboBox<String> comboBoxFiltro;
    @FXML private TableColumn<Paciente, Integer> colTelefonoPaciente;
    @FXML private TableColumn<Paciente, LocalDate> colFechaNacimientoPaciente;
    @FXML private TableColumn<Paciente, String> colNombrePaciente;
    @FXML private TableColumn<Paciente, String> colIDPaciente;
    @FXML private TableView<Paciente> tbvResultadoBPaciente;

    private Paciente pacienteSeleccionado;
    private TabPrescibirController controllerPadre;
    private List<Paciente> todosLosPacientes = new ArrayList<>();

    private boolean operacionEnProgreso = false;

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
        cargarPacientesAsync();
    }



    private void filtrarPacientes(String texto) {
        if (texto == null || texto.isBlank()) {
            tbvResultadoBPaciente.setItems(FXCollections.observableArrayList(todosLosPacientes));
            return;
        }

        try {
            String filtro = texto.toLowerCase().trim();
            String tipoFiltro = comboBoxFiltro.getValue();

            List<Paciente> filtrados = todosLosPacientes.stream()
                    .filter(p -> {
                        switch (tipoFiltro) {
                            case "ID": return p.getId().toLowerCase().contains(filtro);
                            case "Nombre": return p.getNombre().toLowerCase().contains(filtro);
                            case "Teléfono": return String.valueOf(p.getTelefono()).contains(filtro);
                            default: return p.getNombre().toLowerCase().contains(filtro) ||
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

    public void cargarPacientesAsync() {
        if (operacionEnProgreso) return;

        operacionEnProgreso = true;
        if (progressBusqueda != null) progressBusqueda.setVisible(true);

        Async.run(() -> {
                    try {
                        HospitalServiceProxy proxy = new HospitalServiceProxy();
                        if (proxy.conectar()) {
                            SolicitudBackend solicitud = new SolicitudBackend("OBTENER_PACIENTES");
                            RespuestaBackend respuesta = (RespuestaBackend) proxy.enviarSolicitud(solicitud);
                            proxy.desconectar();

                            if (respuesta.isExito()) {
                                return (List<Paciente>) respuesta.getDatos();
                            } else {
                                throw new RuntimeException(respuesta.getMensaje());
                            }
                        }
                        throw new RuntimeException("No se pudo conectar al backend");
                    } catch (Exception e) {
                        throw new RuntimeException("Error al cargar pacientes: " + e.getMessage());
                    }
                },
                pacientes -> {
                    operacionEnProgreso = false;
                    if (progressBusqueda != null) progressBusqueda.setVisible(false);
                    this.todosLosPacientes = pacientes;
                    tbvResultadoBPaciente.setItems(FXCollections.observableArrayList(pacientes));
                },
                error -> {
                    operacionEnProgreso = false;
                    if (progressBusqueda != null) progressBusqueda.setVisible(false);
                    mostrarAlerta("Error", "No se pudieron cargar los pacientes: " + error.getMessage());
                });
    }

}