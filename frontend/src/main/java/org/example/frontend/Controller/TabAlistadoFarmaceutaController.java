package org.example.frontend.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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

import org.example.proyectohospital.Modelo.Receta;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class TabAlistadoFarmaceutaController implements Initializable {

    @FXML private Button btnAbrirChat;
    @FXML private Button btnPonerEnLista;
    @FXML private Button btnBuscarSolicitud;
    @FXML private TextField txtBuscarPacienteSolicitud;
    @FXML private TableColumn <Receta, String> colEstadoReceta;
    @FXML private TableColumn <Receta, String> colIdAlistado;
    @FXML private TableColumn<Receta, String> colPacienteAlistado;
    @FXML private TableColumn <Receta, LocalDate> colFechaRetiroAlistado;
    @FXML private TableView <Receta> tableRecetasAlistado;
    @FXML private ProgressIndicator progressRecetasAlistado;

    private boolean operacionEnProgreso = false;
    private final ObservableList<Receta> listaRecetas = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (progressRecetasAlistado != null) {
            progressRecetasAlistado.setVisible(false);
        }
        inicializarTabla();
    }

    private void inicializarTabla() {
        colIdAlistado.setCellValueFactory(new PropertyValueFactory<>("id"));
        colPacienteAlistado.setCellValueFactory(new PropertyValueFactory<>("nombrePaciente"));
        colFechaRetiroAlistado.setCellValueFactory(new PropertyValueFactory<>("fechaRetiro"));
        colEstadoReceta.setCellValueFactory(new PropertyValueFactory<>("nombreEstado"));

        cargarRecetasAlistadoAsync();

    }

    public void actualizarTabla() {
        cargarRecetasAlistadoAsync();
    }


    @FXML
    public void buscarReceta(ActionEvent event) {
        if (operacionEnProgreso) return;

        String filtro = txtBuscarPacienteSolicitud.getText().trim();

        if (filtro.isEmpty()) {
            cargarRecetasAlistadoAsync();
        } else {
            buscarRecetasAlistadoAsync(filtro);
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
    public void alistarMedicamentoSeleccionado(ActionEvent actionEvent) {
        if (operacionEnProgreso) return;

        Receta seleccionada = tableRecetasAlistado.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAlerta("Selección requerida", "Debe seleccionar una receta válida");
            return;
        }

        actualizarEstadoAlistadoAsync(seleccionada.getId(), 3);
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }


    public void cargarRecetasAlistadoAsync() {
        if (operacionEnProgreso) return;

        operacionEnProgreso = true;
        progressRecetasAlistado.setVisible(true);

        Async.run(
                () -> {
                    try {
                        HospitalServiceProxy proxy = new HospitalServiceProxy();
                        if (proxy.conectar()) {
                            SolicitudBackend solicitud = new SolicitudBackend("OBTENER_RECETAS_POR_ESTADO");
                            solicitud.agregarParametro("estado", 2);
                            RespuestaBackend respuesta = (RespuestaBackend) proxy.enviarSolicitud(solicitud);
                            proxy.desconectar();

                            if (respuesta.isExito()) {
                                return (List<Receta>) respuesta.getDatos();
                            } else {
                                throw new RuntimeException(respuesta.getMensaje());
                            }
                        }
                        throw new RuntimeException("No se pudo conectar al backend");
                    } catch (Exception e) {
                        throw new RuntimeException("Error al cargar recetas para alistado: " + e.getMessage());
                    }
                },
                recetasCargadas -> {
                    operacionEnProgreso = false;
                    progressRecetasAlistado.setVisible(false);
                    listaRecetas.setAll(recetasCargadas);
                    tableRecetasAlistado.setItems(listaRecetas);
                },
                error -> {
                    operacionEnProgreso = false;
                    progressRecetasAlistado.setVisible(false);
                    mostrarAlerta("Error", "No se pudieron cargar las recetas: " + error.getMessage());
                }
        );
    }

    public void buscarRecetasAlistadoAsync(String filtro) {
        operacionEnProgreso = true;
        progressRecetasAlistado.setVisible(true);

        Async.run(() -> {
                    try {
                        HospitalServiceProxy proxy = new HospitalServiceProxy();
                        if (proxy.conectar()) {
                            SolicitudBackend solicitud = new SolicitudBackend("OBTENER_RECETAS_POR_ESTADO");
                            solicitud.agregarParametro("estado", 2);
                            RespuestaBackend respuesta = (RespuestaBackend) proxy.enviarSolicitud(solicitud);
                            proxy.desconectar();

                            if (respuesta.isExito()) {
                                List<Receta> recetas = (List<Receta>) respuesta.getDatos();

                                if (!filtro.isEmpty()) {
                                    recetas = recetas.stream()
                                            .filter(r -> r.getId().toLowerCase().contains(filtro.toLowerCase()) ||
                                                    r.getNombrePaciente().toLowerCase().contains(filtro.toLowerCase()))
                                            .collect(Collectors.toList());
                                }
                                return recetas;
                            } else {
                                throw new RuntimeException(respuesta.getMensaje());
                            }
                        }
                        throw new RuntimeException("No se pudo conectar al backend");
                    } catch (Exception e) {
                        throw new RuntimeException("Error al buscar recetas: " + e.getMessage());
                    }
                },
                recetasFiltradas -> {
                    operacionEnProgreso = false;
                    progressRecetasAlistado.setVisible(false);
                    tableRecetasAlistado.getItems().setAll(recetasFiltradas);
                },
                error -> {
                    operacionEnProgreso = false;
                    progressRecetasAlistado.setVisible(false);
                    mostrarAlerta("Error", "Error en búsqueda: " + error.getMessage());
                });
    }

    public void actualizarEstadoAlistadoAsync(String idReceta, int nuevoEstado) {
        operacionEnProgreso = true;
        progressRecetasAlistado.setVisible(true);
        btnPonerEnLista.setDisable(true);

        Async.run(
                () -> {
                    try {
                        HospitalServiceProxy proxy = new HospitalServiceProxy();
                        if (proxy.conectar()) {
                            SolicitudBackend solicitud = new SolicitudBackend("ACTUALIZAR_ESTADO_RECETA");
                            solicitud.agregarParametro("idReceta", idReceta);
                            solicitud.agregarParametro("nuevoEstado", nuevoEstado);
                            RespuestaBackend respuesta = (RespuestaBackend) proxy.enviarSolicitud(solicitud);
                            proxy.desconectar();
                            return respuesta.isExito();
                        }
                        return false;
                    } catch (Exception e) {
                        throw new RuntimeException("Error al actualizar estado: " + e.getMessage());
                    }
                },
                resultado -> {
                    operacionEnProgreso = false;
                    progressRecetasAlistado.setVisible(false);
                    btnPonerEnLista.setDisable(false);

                    if (resultado) {
                        mostrarAlerta("Éxito", "Receta alistada correctamente");
                        cargarRecetasAlistadoAsync();
                    } else {
                        mostrarAlerta("Error", "No se pudo alistar la receta");
                    }
                },
                error -> {
                    operacionEnProgreso = false;
                    progressRecetasAlistado.setVisible(false);
                    btnPonerEnLista.setDisable(false);
                    mostrarAlerta("Error", "Error al procesar: " + error.getMessage());
                }
        );
    }

}