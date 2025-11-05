package org.example.frontend.Controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import org.example.proyectohospital.Modelo.Medico;
import org.example.proyectohospital.Modelo.Personal;

import org.example.frontend.Modelo.HospitalFrontend;
import org.example.frontend.Servicios.HospitalServiceProxy;
import org.example.proyectohospital.shared.RespuestaBackend;
import org.example.proyectohospital.shared.SolicitudBackend;


import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class MedicoEnAdminViewController implements Initializable {
    @FXML private Button btnAbrirChat;
    @FXML private ProgressIndicator progressMedicos;
    @FXML private Button btnMostrarTodosLosMedicos;
    @FXML private Button btnModificarMedico;
    @FXML private Button btnBuscarMedico;
    @FXML private Button btnBorrarMedico;
    @FXML private Button btnLimpiarCampos;
    @FXML private Button btnGuardarMedico;
    @FXML private TextField txtBuscarMedico;
    @FXML private TableView<Medico> tbvResultadoBusquedaMedico;
    @FXML private TableColumn<Medico, String> colIDMedico;
    @FXML private TableColumn<Medico, String> colNombreMedico;
    @FXML private TableColumn<Medico, String> colEspecialidadMedico;
    @FXML private TextField txtEspecialidadMedico;
    @FXML private TextField txtNombreMedico;
    @FXML private TextField txtIdMedico;

    private List<Medico> todosLosMedicos = new ArrayList<>();
    private final ObservableList<Medico> listaMedicos = FXCollections.observableArrayList();

    private boolean operacionEnProgreso = false;

    public MedicoEnAdminViewController() {

    }

    @FXML
    public void mostrarTodosLosMedicos() {
        try {
            cargarMedicosAsync();
        }
        catch (Exception e) {
            mostrarAlerta("Error","Error al cargar medicos." + e.getMessage());
        }
    }

    @FXML
    private void modificarMedico(ActionEvent actionEvent) {
        if (operacionEnProgreso) {
            return;
        }
        try
        {
            Medico seleccionado = tbvResultadoBusquedaMedico.getSelectionModel().getSelectedItem();

            if (seleccionado == null) {
                mostrarAlerta("Error", "Seleccione un medico");
                return;
            }

            String nombre = txtNombreMedico.getText().trim();
            String especialidad = txtEspecialidadMedico.getText().trim();

            if (nombre.isEmpty() || especialidad.isEmpty()) {
                mostrarAlerta("Error", "Debe de rellenar todos los campos.");
                return;
            }

            seleccionado.setNombre(nombre);
            seleccionado.setEspecialidad(especialidad);
            modificarMedicoAsync(seleccionado);


        } catch (Exception e) {
            mostrarAlerta("Error", "Error al modificar medico: " + e.getMessage());
        }
    }

    @FXML
    private void buscarMedico(ActionEvent actionEvent) {
        String criterio = txtBuscarMedico.getText().toLowerCase();
        if(criterio.isEmpty()) {
            cargarMedicosAsync();
            return;
        }

        List<Medico> resultados = todosLosMedicos.stream()
                .filter(f -> {
                    String textoBusqueda = criterio.toLowerCase().trim();
                    return f.getId().toLowerCase().contains(textoBusqueda) ||
                            f.getNombre().toLowerCase().contains(textoBusqueda);
                })
                .collect(Collectors.toList());
        listaMedicos.setAll(resultados);
    }

    @FXML
    private void borrarMedico(ActionEvent actionEvent) {
        if (operacionEnProgreso) {
            return;
        }
        try
        {
            Medico seleccionado = tbvResultadoBusquedaMedico.getSelectionModel().getSelectedItem();
            if (seleccionado == null) {
                mostrarAlerta("Error", "Debe seleccionar un medico para borrar");
                return;
            }
            eliminarMedicoAsync(seleccionado);

        } catch (Exception e) {
            mostrarAlerta("Error", "Error al borrar medico: " + e.getMessage());
        }
    }

    @FXML
    private void limpiarCamposMedicos() {
        txtIdMedico.clear();
        txtNombreMedico.clear();
        txtEspecialidadMedico.clear();
        txtBuscarMedico.clear();
        tbvResultadoBusquedaMedico.getSelectionModel().clearSelection();
        configurarCampoId(true);
    }

    @FXML
    private void guardarMedico(ActionEvent actionEvent) {
        if (operacionEnProgreso) {
            return;
        }

        try {
            String idMedico = txtIdMedico.getText();
            String nombreMedico = txtNombreMedico.getText();
            String especialidadMedico = txtEspecialidadMedico.getText();

            if(idMedico.isEmpty() || nombreMedico.isEmpty() || especialidadMedico.isEmpty()) {
                mostrarAlerta("Error","Debe llenar todos los campos obligatorios");
                return;
            }
            Personal nuevo = new Medico(nombreMedico, idMedico, idMedico, especialidadMedico);
            guardarMedicoAsync(nuevo);
        }
        catch (Exception e) {
            mostrarAlerta("Error","No se logro insertar el medico");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colIDMedico.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getId()));

        colNombreMedico.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getNombre()));

        colEspecialidadMedico.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getEspecialidad()));

        tbvResultadoBusquedaMedico.setItems(listaMedicos);

        tbvResultadoBusquedaMedico.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        configurarCampoId(false);
                        llenarCamposConMedico(newValue);
                    } else {
                        configurarCampoId(true);
                    }
                }
        );

        configurarCampoId(true);
        cargarMedicosAsync();
    }

    private void configurarCampoId(boolean editable) {
        txtIdMedico.setEditable(editable);
        txtIdMedico.setFocusTraversable(editable);

        if (editable) {
            txtIdMedico.setStyle("-fx-background-color: white; -fx-text-fill: black;");
        } else {
            txtIdMedico.setStyle("-fx-background-color: #f0f0f0; -fx-text-fill: #666;");
        }
    }

    private void llenarCamposConMedico(Medico medico) {
        txtIdMedico.setText(medico.getId());
        txtNombreMedico.setText(medico.getNombre());
        txtEspecialidadMedico.setText(medico.getEspecialidad());
    }

    private void mostrarAlerta(String titulo, String mensaje){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
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


    public void cargarMedicosAsync() {
        if (operacionEnProgreso) return;

        operacionEnProgreso = true;
        progressMedicos.setVisible(true);
        if (btnMostrarTodosLosMedicos != null) btnMostrarTodosLosMedicos.setDisable(true);

        Async.run(() -> {
                    try {
                        HospitalServiceProxy proxy = new HospitalServiceProxy();
                        if (proxy.conectar()) {
                            SolicitudBackend solicitud = new SolicitudBackend("OBTENER_MEDICOS");
                            RespuestaBackend respuesta = (RespuestaBackend) proxy.enviarSolicitud(solicitud);
                            proxy.desconectar();

                            if (respuesta.isExito()) {
                                return (List<Medico>) respuesta.getDatos();
                            } else {
                                throw new RuntimeException(respuesta.getMensaje());
                            }
                        }
                        throw new RuntimeException("No se pudo conectar al backend");
                    } catch (Exception e) {
                        throw new RuntimeException("Error al cargar médicos: " + e.getMessage());
                    }
                },
                medicos -> {
                    operacionEnProgreso = false;
                    progressMedicos.setVisible(false);
                    if (btnMostrarTodosLosMedicos != null) btnMostrarTodosLosMedicos.setDisable(false);
                    this.todosLosMedicos = medicos;
                    listaMedicos.setAll(medicos);
                },
                error -> {
                    operacionEnProgreso = false;
                    progressMedicos.setVisible(false);
                    if (btnMostrarTodosLosMedicos != null) btnMostrarTodosLosMedicos.setDisable(false);
                    mostrarAlerta("Error", "No se pudieron cargar los médicos: " + error.getMessage());
                });
    }

    public void guardarMedicoAsync(Personal medico) {
        operacionEnProgreso = true;
        progressMedicos.setVisible(true);
        btnGuardarMedico.setDisable(true);

        Async.run(() -> {
                    try {
                        HospitalServiceProxy proxy = new HospitalServiceProxy();
                        if (proxy.conectar()) {
                            SolicitudBackend solicitudVerificar = new SolicitudBackend("VERIFICAR_ID_PERSONAL");
                            solicitudVerificar.agregarParametro("id", medico.getId());
                            RespuestaBackend respuestaVerificar = (RespuestaBackend) proxy.enviarSolicitud(solicitudVerificar);

                            if (respuestaVerificar.isExito() && (Boolean) respuestaVerificar.getDatos()) {
                                proxy.desconectar();
                                return false;
                            }

                            SolicitudBackend solicitudInsertar = new SolicitudBackend("INSERTAR_PERSONAL");
                            solicitudInsertar.agregarParametro("personal", medico);
                            RespuestaBackend respuestaInsertar = (RespuestaBackend) proxy.enviarSolicitud(solicitudInsertar);
                            proxy.desconectar();

                            return respuestaInsertar.isExito();
                        }
                        return false;
                    } catch (Exception e) {
                        throw new RuntimeException("Error al guardar médico: " + e.getMessage());
                    }
                },
                resultado -> {
                    operacionEnProgreso = false;
                    progressMedicos.setVisible(false);
                    btnGuardarMedico.setDisable(false);

                    if (resultado) {
                        cargarMedicosAsync();
                        limpiarCamposMedicos();
                        mostrarAlerta("Éxito", "Se pudo insertar el medico correctamente.");
                    } else {
                        mostrarAlerta("Error", "Ya existe un usuario con ese ID");
                    }
                },
                error -> {
                    operacionEnProgreso = false;
                    progressMedicos.setVisible(false);
                    btnGuardarMedico.setDisable(false);
                    mostrarAlerta("Error", "Error al guardar: " + error.getMessage());
                });
    }

    public void modificarMedicoAsync(Medico medico) {
        operacionEnProgreso = true;
        progressMedicos.setVisible(true);
        btnModificarMedico.setDisable(true);

        Async.run(() -> {
                    try {
                        HospitalServiceProxy proxy = new HospitalServiceProxy();
                        if (proxy.conectar()) {
                            SolicitudBackend solicitud = new SolicitudBackend("ACTUALIZAR_PERSONAL");
                            solicitud.agregarParametro("personal", medico);
                            RespuestaBackend respuesta = (RespuestaBackend) proxy.enviarSolicitud(solicitud);
                            proxy.desconectar();

                            Medico medicoLogueado = HospitalFrontend.getInstance().getMedicoLogueado();
                            if (medicoLogueado != null && medicoLogueado.getId().equals(medico.getId())) {
                                HospitalFrontend.getInstance().setMedicoLogueado(medico);
                            }

                            return respuesta.isExito();
                        }
                        return false;
                    } catch (Exception e) {
                        throw new RuntimeException("Error al modificar médico: " + e.getMessage());
                    }
                },
                resultado -> {
                    operacionEnProgreso = false;
                    progressMedicos.setVisible(false);
                    btnModificarMedico.setDisable(false);

                    cargarMedicosAsync();
                    limpiarCamposMedicos();
                    mostrarAlerta("Éxito", "Se pudo modificar al médico correctamente.");
                },
                error -> {
                    operacionEnProgreso = false;
                    progressMedicos.setVisible(false);
                    btnModificarMedico.setDisable(false);
                    mostrarAlerta("Error", "No se pudo modificar al médico: " + error.getMessage());
                });
    }

    public void eliminarMedicoAsync(Medico medico) {
        operacionEnProgreso = true;
        progressMedicos.setVisible(true);
        btnBorrarMedico.setDisable(true);

        Async.run(() -> {
                    try {
                        HospitalServiceProxy proxy = new HospitalServiceProxy();
                        if (proxy.conectar()) {
                            SolicitudBackend solicitud = new SolicitudBackend("ELIMINAR_PERSONAL");
                            solicitud.agregarParametro("id", medico.getId());
                            RespuestaBackend respuesta = (RespuestaBackend) proxy.enviarSolicitud(solicitud);
                            proxy.desconectar();
                            return respuesta.isExito();
                        }
                        return false;
                    } catch (Exception e) {
                        throw new RuntimeException("Error al eliminar médico: " + e.getMessage());
                    }
                },
                resultado -> {
                    operacionEnProgreso = false;
                    progressMedicos.setVisible(false);
                    btnBorrarMedico.setDisable(false);

                    if (resultado) {
                        cargarMedicosAsync();
                        limpiarCamposMedicos();
                        mostrarAlerta("Éxito", "Se pudo eliminar el médico correctamente.");
                    } else {
                        mostrarAlerta("Error", "No se pudo eliminar al médico");
                    }
                },
                error -> {
                    operacionEnProgreso = false;
                    progressMedicos.setVisible(false);
                    btnBorrarMedico.setDisable(false);
                    mostrarAlerta("Error", "No se pudo eliminar al médico: " + error.getMessage());
                });
    }

}