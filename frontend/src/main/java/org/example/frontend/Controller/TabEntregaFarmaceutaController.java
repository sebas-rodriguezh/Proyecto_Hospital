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

public class TabEntregaFarmaceutaController implements Initializable {
    @FXML private Button btnAbrirChat;
    @FXML private ProgressIndicator progressRecetasEntrega;
    @FXML private Button btnEntregarRecetaFarmaceuta;
    @FXML private Button btnBuscarTabEntregaFarmaceuta;
    @FXML private TableColumn <Receta, LocalDate> colFechaRetiroEntrega;
    @FXML private TableColumn <Receta, String> colPacienteEntrega;
    @FXML private TableColumn <Receta,String> colIdEntrega;
    @FXML private TableColumn <Receta,String> colEstadoReceta;
    @FXML private TableView <Receta> tableTabEntregaFarmaceuta;
    @FXML private TextField txtIdNombreEntregaFarmaceuta;
    private final ObservableList<Receta> listaRecetas = FXCollections.observableArrayList();

    private boolean operacionEnProgreso = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (progressRecetasEntrega != null) {
            progressRecetasEntrega.setVisible(false);
        }
        inicializarTabla();
    }

    private void inicializarTabla() {
        colIdEntrega.setCellValueFactory(new PropertyValueFactory<>("id"));
        colPacienteEntrega.setCellValueFactory(new PropertyValueFactory<>("nombrePaciente"));
        colFechaRetiroEntrega.setCellValueFactory(new PropertyValueFactory<>("fechaRetiro"));
        colEstadoReceta.setCellValueFactory(new PropertyValueFactory<>("nombreEstado"));

        cargarRecetasEntregaAsync();
    }

    public void actualizarTabla() {
        cargarRecetasEntregaAsync();
    }

    @FXML
    public void entregarReceta(ActionEvent actionEvent)
    {
        if (operacionEnProgreso) {
            return;
        }

        Receta seleccionada = tableTabEntregaFarmaceuta.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAlerta("Selección requerida", "Debe seleccionar una receta válida");
            return;
        }
        actualizarEstadoEntregaAsync(seleccionada.getId(), 4);
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
    public void buscarRecetaParaEntrega(ActionEvent actionEvent) {
        if (operacionEnProgreso) {
            return;
        }

        String filtro = txtIdNombreEntregaFarmaceuta.getText().trim();

        if (filtro.isEmpty()) {
            cargarRecetasEntregaAsync();
        } else {
            buscarRecetasEntregaAsync(filtro);
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    public void cargarRecetasEntregaAsync() {
        if (operacionEnProgreso) {
            return;
        }

        operacionEnProgreso = true;
        progressRecetasEntrega.setVisible(true);

        Async.run(
                () -> {
                    try {
                        HospitalServiceProxy proxy = new HospitalServiceProxy();
                        if (proxy.conectar()) {
                            SolicitudBackend solicitud = new SolicitudBackend("OBTENER_RECETAS_POR_ESTADO");
                            solicitud.agregarParametro("estado", 3);
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
                        throw new RuntimeException("Error al cargar recetas para entrega: " + e.getMessage());
                    }
                },
                recetasCargadas -> {
                    operacionEnProgreso = false;
                    progressRecetasEntrega.setVisible(false);
                    listaRecetas.setAll(recetasCargadas);
                    tableTabEntregaFarmaceuta.setItems(listaRecetas);
                },
                error -> {
                    operacionEnProgreso = false;
                    progressRecetasEntrega.setVisible(false);
                    mostrarAlerta("Error", "No se pudieron cargar las recetas: " + error.getMessage());
                }
        );
    }

    public void buscarRecetasEntregaAsync(String filtro) {
        operacionEnProgreso = true;
        progressRecetasEntrega.setVisible(true);

        Async.run(() -> {
                    try {
                        HospitalServiceProxy proxy = new HospitalServiceProxy();
                        if (proxy.conectar()) {
                            SolicitudBackend solicitud = new SolicitudBackend("OBTENER_RECETAS_POR_ESTADO");
                            solicitud.agregarParametro("estado", 3);
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
                    progressRecetasEntrega.setVisible(false);
                    tableTabEntregaFarmaceuta.getItems().setAll(recetasFiltradas);
                },
                error -> {
                    operacionEnProgreso = false;
                    progressRecetasEntrega.setVisible(false);
                    mostrarAlerta("Error", "Error en búsqueda: " + error.getMessage());
                });
    }

    public void actualizarEstadoEntregaAsync(String idReceta, int nuevoEstado) {
        operacionEnProgreso = true;
        progressRecetasEntrega.setVisible(true);
        btnEntregarRecetaFarmaceuta.setDisable(true);

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
                    progressRecetasEntrega.setVisible(false);
                    btnEntregarRecetaFarmaceuta.setDisable(false);

                    if (resultado) {
                        mostrarAlerta("Éxito", "Receta entregada correctamente");
                        cargarRecetasEntregaAsync();
                    } else {
                        mostrarAlerta("Error", "No se pudo entregar la receta");
                    }
                },
                error -> {
                    operacionEnProgreso = false;
                    progressRecetasEntrega.setVisible(false);
                    btnEntregarRecetaFarmaceuta.setDisable(false);
                    mostrarAlerta("Error", "Error al procesar entrega: " + error.getMessage());
                }
        );
    }
}
