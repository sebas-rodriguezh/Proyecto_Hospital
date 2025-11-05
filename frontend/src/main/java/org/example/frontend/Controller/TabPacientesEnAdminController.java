package org.example.frontend.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.frontend.Modelo.HospitalFrontend;
import org.example.frontend.Servicios.HospitalServiceProxy;
import org.example.proyectohospital.shared.RespuestaBackend;
import org.example.proyectohospital.shared.SolicitudBackend;
import org.example.proyectohospital.Modelo.Paciente;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class TabPacientesEnAdminController implements Initializable {
    @FXML private Button btnAbrirChat;
    @FXML private ProgressIndicator progressPacientes;
    @FXML private TextField txtIdPaciente;
    @FXML private TextField txtNombrePaciente;
    @FXML private TextField txtNumeroTelefonoPaciente;
    @FXML private TextField txtBuscarPaciente;
    @FXML private DatePicker dtpFechaNacimientoPaciente;
    @FXML private TableView<Paciente> tbvResultadoBPaciente;
    @FXML private TableColumn<Paciente,String> colIDPaciente;
    @FXML private TableColumn<Paciente,String> colNombrePaciente;
    @FXML private TableColumn<Paciente,Integer> colTelefonoPaciente;
    @FXML private TableColumn <Paciente, LocalDate> colFechaNacimientoPaciente;
    @FXML private Button btnGuardarPaciente;
    @FXML private Button btnLimpiarCampos;
    @FXML private Button btnBorrarPaciente;
    @FXML private Button btnModificarPaciente;
    @FXML private Button btnMostrarTodosLosPacientes;
    @FXML private Button btnBuscarPaciente;
    private List<Paciente> todosLosPacientes = new ArrayList<>();
    private ObservableList<Paciente> listaPacientes = FXCollections.observableArrayList();

    private boolean cargaEnProgreso = false;
    private boolean operacionEnProgreso = false;


    public void initialize(URL url, ResourceBundle resourceBundle) {
        colIDPaciente.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombrePaciente.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colFechaNacimientoPaciente.setCellValueFactory(new PropertyValueFactory<>("fechaNacimiento"));
        colTelefonoPaciente.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        tbvResultadoBPaciente.setItems(listaPacientes);

        tbvResultadoBPaciente.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        configurarCampoId(false);
                        llenarCamposConPaciente(newValue);
                    } else {
                        configurarCampoId(true);
                    }
                }
        );
        configurarCampoId(true);
        mostrarTodosLosPacientes();
    }


    @FXML
    public void mostrarTodosLosPacientes() {
        try {
            cargarPacientesAsync();
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al cargar medicamentos: " + e.getMessage());
        }
    }

    @FXML
    private void abrirVentanaChat() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/frontend/View/chat-view.fxml"));
            Parent root = loader.load();

            Stage chatStage = new Stage();
            chatStage.setTitle("Chat del Hospital - " + HospitalFrontend.getInstance().getUsuarioLogueadoNombre());
            chatStage.setScene(new Scene(root));
            chatStage.setResizable(true);
            chatStage.show();

        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo abrir el chat: " + e.getMessage());
            e.printStackTrace();
        }
    }


    @FXML
    private void limpiarCamposPaciente() {
        txtIdPaciente.clear();
        txtNombrePaciente.clear();
        txtNumeroTelefonoPaciente.clear();
        dtpFechaNacimientoPaciente.setValue(null);
        txtBuscarPaciente.clear();
        tbvResultadoBPaciente.getSelectionModel().clearSelection();
        configurarCampoId(true);
    }

    private void mostrarAlerta(String titulo, String mensaje){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }


    @FXML
    public void guardarPaciente(javafx.event.ActionEvent actionEvent)
    {
        if (operacionEnProgreso)
        {
            return;
        }

        try
        {
            String idPaciente = txtIdPaciente.getText().trim();
            String nombrePaciente = txtNombrePaciente.getText().trim();
            String numeroTelefonoPaciente = txtNumeroTelefonoPaciente.getText().trim();
            LocalDate fechaNacimientoPaciente = dtpFechaNacimientoPaciente.getValue();

            if(idPaciente.isEmpty() || nombrePaciente.isEmpty() || numeroTelefonoPaciente.isEmpty() || fechaNacimientoPaciente == null) {
                mostrarAlerta("Error","Debe llenar todos los campos obligatorios");
                return;
            }

            if (idPaciente.length() > 9 || numeroTelefonoPaciente.length() > 9) {
                mostrarAlerta("Error","Debe llenar todos los campos obligatorios");
                return;
            }
            int telefonoPaciente = 0;
            try
            {
                try
                {
                    telefonoPaciente = Integer.parseInt(numeroTelefonoPaciente);
                }
                catch(NumberFormatException e)
                {
                    mostrarAlerta("Error","Debe ingresar un numero de telefono con longitud de 8.");
                    return;
                }

                Paciente nuevo = new Paciente(telefonoPaciente, fechaNacimientoPaciente, nombrePaciente, idPaciente);
                guardarPacienteAsync(nuevo);
            }

            catch (Exception e)
            {
                mostrarAlerta("Error","Error al guardar el paciente: " + e.getMessage());
            }

        } catch (Exception e) {
            mostrarAlerta("Error","Error al guardar el paciente."+ e.getMessage());
        }
    }



    @FXML
    public void borrarPaciente(javafx.event.ActionEvent actionEvent) {
        if (operacionEnProgreso)
        {
            return;
        }

        try
        {
            Paciente seleccionado = tbvResultadoBPaciente.getSelectionModel().getSelectedItem();

            if(seleccionado == null) {
                mostrarAlerta("Error", "Debe seleccionar un paciente");
                return;
            }
            eliminarPacienteAsync(seleccionado);

        } catch (Exception e) {
            mostrarAlerta("Error", "Error al eliminar paciente: " + e.getMessage());
        }
    }

    @FXML
    public void buscarPaciente(javafx.event.ActionEvent actionEvent) {
        String criterio = txtBuscarPaciente.getText().toLowerCase().trim();

        if(criterio.isEmpty()) {
            cargarPacientesAsync();
            return;
        }

        List<Paciente> resultados = todosLosPacientes.stream()
                .filter(p -> p.getNombre().toLowerCase().contains(criterio) ||
                        p.getId().toLowerCase().contains(criterio))
                .collect(Collectors.toList());
        listaPacientes.setAll(resultados);
    }


    public boolean validarCamposParaModificacion(String nombre, String telefono, LocalDate fechaNacimientoPaciente)
    {
        if (nombre.isEmpty() || telefono.isEmpty() || fechaNacimientoPaciente == null) {
            return false;
        }
        return true;
    }

    @FXML
    public void modificarPaciente(javafx.event.ActionEvent actionEvent) {

        if (operacionEnProgreso)
        {
            return;
        }

        try
        {
            Paciente seleccionado = tbvResultadoBPaciente.getSelectionModel().getSelectedItem();
            if(seleccionado == null) {
                mostrarAlerta("Error", "Debe seleccionar un paciente");
                return;
            }
            String nombre = txtNombrePaciente.getText().trim();
            String telefonoStr = txtNumeroTelefonoPaciente.getText().trim();
            LocalDate fechaNacimiento = dtpFechaNacimientoPaciente.getValue();

            if (!validarCamposParaModificacion(nombre, telefonoStr, fechaNacimiento))
            {
                mostrarAlerta("Error", "Todos los campos son obligatorios");
                return;
            }

            int telefono;
            try {
                telefono = Integer.parseInt(telefonoStr);
            } catch(NumberFormatException e) {
                mostrarAlerta("Error", "Teléfono debe ser numérico");
                return;
            }

            seleccionado.setNombre(nombre);
            seleccionado.setTelefono(telefono);
            seleccionado.setFechaNacimiento(fechaNacimiento);

            modificarPacienteAsync(seleccionado);

        } catch (Exception e)
        {
            mostrarAlerta("Error", "No se pudo modificar el paciente: " + e.getMessage());
        }
    }


    private void configurarCampoId(boolean editable) {
        txtIdPaciente.setEditable(editable);
        txtIdPaciente.setFocusTraversable(editable);

        if (editable) {
            txtIdPaciente.setStyle("-fx-background-color: white; -fx-text-fill: black;");
        } else {
            txtIdPaciente.setStyle("-fx-background-color: #f0f0f0; -fx-text-fill: #666;");
        }
    }

    private void llenarCamposConPaciente(Paciente paciente) {
        txtIdPaciente.setText(paciente.getId());
        txtNombrePaciente.setText(paciente.getNombre());
        txtNumeroTelefonoPaciente.setText(String.valueOf(paciente.getTelefono()));
        dtpFechaNacimientoPaciente.setValue(paciente.getFechaNacimiento());
    }


    public void cargarPacientesAsync() {
        if (cargaEnProgreso) return;

        cargaEnProgreso = true;
        progressPacientes.setVisible(true);
        if (btnMostrarTodosLosPacientes != null) btnMostrarTodosLosPacientes.setDisable(true);

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
                    cargaEnProgreso = false;
                    progressPacientes.setVisible(false);
                    if (btnMostrarTodosLosPacientes != null) btnMostrarTodosLosPacientes.setDisable(false);
                    this.todosLosPacientes = pacientes;
                    listaPacientes.setAll(pacientes);
                },
                error -> {
                    cargaEnProgreso = false;
                    progressPacientes.setVisible(false);
                    if (btnMostrarTodosLosPacientes != null) btnMostrarTodosLosPacientes.setDisable(false);
                });
    }

    public void guardarPacienteAsync(Paciente nuevo) {
        operacionEnProgreso = true;
        progressPacientes.setVisible(true);
        btnGuardarPaciente.setDisable(true);

        Async.run(() -> {
                    try {
                        HospitalServiceProxy proxy = new HospitalServiceProxy();
                        if (proxy.conectar()) {
                            SolicitudBackend solicitudVerificar = new SolicitudBackend("VERIFICAR_ID_PERSONAL");
                            solicitudVerificar.agregarParametro("id", nuevo.getId());
                            RespuestaBackend respuestaVerificar = (RespuestaBackend) proxy.enviarSolicitud(solicitudVerificar);

                            if (respuestaVerificar.isExito() && (Boolean) respuestaVerificar.getDatos()) {
                                proxy.desconectar();
                                return false;
                            }

                            SolicitudBackend solicitudInsertar = new SolicitudBackend("INSERTAR_PACIENTE");
                            solicitudInsertar.agregarParametro("paciente", nuevo);
                            RespuestaBackend respuestaInsertar = (RespuestaBackend) proxy.enviarSolicitud(solicitudInsertar);
                            proxy.desconectar();

                            return respuestaInsertar.isExito();
                        }
                        return false;
                    } catch (Exception e) {
                        throw new RuntimeException("Error al guardar paciente: " + e.getMessage());
                    }
                },
                resultado -> {
                    operacionEnProgreso = false;
                    progressPacientes.setVisible(false);
                    btnGuardarPaciente.setDisable(false);

                    if (resultado) {
                        cargarPacientesAsync();
                        limpiarCamposPaciente();
                        mostrarAlerta("Éxito", "Se pudo insertar el paciente correctamente.");
                    } else {
                        mostrarAlerta("Error", "Ya existe un paciente con ese ID");
                    }
                },
                error -> {
                    operacionEnProgreso = false;
                    progressPacientes.setVisible(false);
                    btnGuardarPaciente.setDisable(false);
                    mostrarAlerta("Error", "Ya existe un paciente con ese ID: " + error.getMessage());
                });
    }


    public void modificarPacienteAsync(Paciente paciente) {
        operacionEnProgreso = true;
        progressPacientes.setVisible(true);
        btnModificarPaciente.setDisable(true);

        Async.run(() -> {
                    try {
                        HospitalServiceProxy proxy = new HospitalServiceProxy();
                        if (proxy.conectar()) {
                            SolicitudBackend solicitud = new SolicitudBackend("ACTUALIZAR_PACIENTE");
                            solicitud.agregarParametro("paciente", paciente);
                            RespuestaBackend respuesta = (RespuestaBackend) proxy.enviarSolicitud(solicitud);
                            proxy.desconectar();
                            return respuesta.isExito();
                        }
                        return false;
                    } catch (Exception e) {
                        throw new RuntimeException("Error al modificar paciente: " + e.getMessage());
                    }
                },
                resultado -> {
                    operacionEnProgreso = false;
                    progressPacientes.setVisible(false);
                    btnModificarPaciente.setDisable(false);
                    cargarPacientesAsync();
                    limpiarCamposPaciente();
                    mostrarAlerta("Éxito", "Se pudo modificar el paciente correctamente.");
                },
                error -> {
                    operacionEnProgreso = false;
                    progressPacientes.setVisible(false);
                    btnModificarPaciente.setDisable(false);
                    mostrarAlerta("Error", "No se pudo modificar al paciente: " + error.getMessage());
                });
    }

    public void eliminarPacienteAsync(Paciente paciente) {
        operacionEnProgreso = true;
        progressPacientes.setVisible(true);
        btnBorrarPaciente.setDisable(true);

        Async.run(() -> {
                    try {
                        HospitalServiceProxy proxy = new HospitalServiceProxy();
                        if (proxy.conectar()) {
                            SolicitudBackend solicitud = new SolicitudBackend("ELIMINAR_PACIENTE");
                            solicitud.agregarParametro("id", paciente.getId());
                            RespuestaBackend respuesta = (RespuestaBackend) proxy.enviarSolicitud(solicitud);
                            proxy.desconectar();
                            return respuesta.isExito();
                        }
                        return false;
                    } catch (Exception e) {
                        throw new RuntimeException("Error al eliminar paciente: " + e.getMessage());
                    }
                },
                resultado -> {
                    operacionEnProgreso = false;
                    progressPacientes.setVisible(false);
                    btnBorrarPaciente.setDisable(false);

                    if (resultado) {
                        cargarPacientesAsync();
                        limpiarCamposPaciente();
                        mostrarAlerta("Éxito", "Se pudo eliminar el paciente correctamente.");
                    } else {
                        mostrarAlerta("Error", "No se pudo eliminar al paciente");
                    }
                },
                error -> {
                    operacionEnProgreso = false;
                    progressPacientes.setVisible(false);
                    btnBorrarPaciente.setDisable(false);
                    mostrarAlerta("Error", "No se pudo eliminar al paciente: " + error.getMessage());
                });
    }


}
