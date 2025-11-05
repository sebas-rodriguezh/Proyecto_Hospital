package org.example.frontend.Controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.frontend.Servicios.HospitalServiceProxy;
import org.example.proyectohospital.shared.RespuestaBackend;
import org.example.proyectohospital.shared.SolicitudBackend;
import org.example.proyectohospital.Modelo.DetalleMedicamento;
import org.example.proyectohospital.Modelo.Receta;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class TabHistoricoRecetasController implements Initializable {
    @FXML private TableView<Receta> tblViewRecetasHistoricoRecetas;
    @FXML private TableColumn<Receta, String> colIdHistorico;
    @FXML private TableColumn<Receta, String> colPacienteHistorico;
    @FXML private TableColumn <Receta, LocalDate> colFechaNacimientoPaciente;
    @FXML private TableColumn<Receta, LocalDate> colFechaConfeccionHistorico;
    @FXML private TableColumn<Receta, LocalDate> colFechaRetiroHistorico;
    @FXML private TableColumn<Receta, String> colEstadoHistorico;
    @FXML private TableView<DetalleMedicamento> tableDetallesHistorico;
    @FXML private TableColumn<DetalleMedicamento, String> colMedicamento;
    @FXML private TableColumn<DetalleMedicamento, String> colPresentacion;
    @FXML private TableColumn<DetalleMedicamento, Integer> colCantidad;
    @FXML private TableColumn<DetalleMedicamento, String> colIndicaciones;
    @FXML private TableColumn<DetalleMedicamento, Integer> colDuracion;
    @FXML private TextField txtBuscarPacienteIdHistoricoRecetas;
    @FXML private ComboBox<String> comboEstadoHistorico;
    @FXML private Button btnBuscarHistorico;
    @FXML private Button btnVerDetallesHistorico;
    @FXML private ProgressIndicator progressRecetasHistorico;
    @FXML private ProgressIndicator progressDetallesHistorico;

    private final ObservableList<Receta> listaReceta = FXCollections.observableArrayList();
    private List<Receta> todasLasRecetas = new ArrayList<>();
    private boolean operacionEnProgreso = false;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (progressRecetasHistorico != null) {
            progressRecetasHistorico.setVisible(false);
        }
        if (progressDetallesHistorico != null) {
            progressDetallesHistorico.setVisible(false);
        }
        inicializarTabla();
    }

    private void inicializarTabla() {
        comboEstadoHistorico.getItems().addAll("Todas", "Confeccionada", "Procesada", "Lista", "Entregada");
        comboEstadoHistorico.setValue("Todas");
        colIdHistorico.setCellValueFactory(new PropertyValueFactory<>("id"));
        colPacienteHistorico.setCellValueFactory(new PropertyValueFactory<>("nombrePaciente"));
        colFechaNacimientoPaciente.setCellValueFactory(new PropertyValueFactory<>("fechaNacimiento"));
        colFechaConfeccionHistorico.setCellValueFactory(new PropertyValueFactory<>("fechaPrescripcion"));
        colFechaRetiroHistorico.setCellValueFactory(new PropertyValueFactory<>("fechaRetiro"));
        colEstadoHistorico.setCellValueFactory(new PropertyValueFactory<>("nombreEstado"));
        colMedicamento.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getMedicamento().getNombre()));
        colPresentacion.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getMedicamento().getPresentacion()));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colIndicaciones.setCellValueFactory(new PropertyValueFactory<>("indicacion"));
        colDuracion.setCellValueFactory(new PropertyValueFactory<>("duracion"));

        cargarTodasLasRecetasAsync();
    }

    private void cargarTodasLasRecetas()
    {
        cargarTodasLasRecetasAsync();
        tblViewRecetasHistoricoRecetas.getItems().setAll(listaReceta);
    }

    @FXML
    private void buscarHistorico() {
        if (operacionEnProgreso) {
            return;
        }

        String filtro = txtBuscarPacienteIdHistoricoRecetas.getText().trim();
        String estadoSeleccionado = comboEstadoHistorico.getValue();
        buscarRecetasEnBackendAsync(filtro, estadoSeleccionado);
    }


    private int convertirEstadoANumero (String estado) {
        if (Objects.equals(estado, "Confeccionada")) return 1;
        else if (Objects.equals(estado, "Procesada")) return 2;
        else if (Objects.equals(estado, "Lista")) return 3;
        else if (Objects.equals(estado, "Entregada")) return 4;
        else return -1;
    }


    @FXML
    private void verDetallesHistorico(ActionEvent event) {
        if (operacionEnProgreso) {
            return;
        }

        Receta seleccionada = tblViewRecetasHistoricoRecetas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAlerta("Selección requerida", "Debe seleccionar una receta para ver sus detalles.");
            return;
        }

        cargarDetallesRecetaAsync(seleccionada.getId());
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    public void actualizarTabla() {
        cargarTodasLasRecetasAsync();
    }


    public void cargarTodasLasRecetasAsync() {
        if (operacionEnProgreso) return;

        operacionEnProgreso = true;
        progressRecetasHistorico.setVisible(true);

        Async.run(() -> {
                    try {
                        HospitalServiceProxy proxy = new HospitalServiceProxy();
                        if (proxy.conectar()) {
                            SolicitudBackend solicitud = new SolicitudBackend("OBTENER_RECETAS");
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
                        throw new RuntimeException("Error al cargar recetas históricas: " + e.getMessage());
                    }
                },
                recetas -> {
                    operacionEnProgreso = false;
                    progressRecetasHistorico.setVisible(false);
                    this.todasLasRecetas = recetas;
                    listaReceta.setAll(recetas);
                    tblViewRecetasHistoricoRecetas.setItems(listaReceta);
                },
                error -> {
                    operacionEnProgreso = false;
                    progressRecetasHistorico.setVisible(false);
                    mostrarAlerta("Error", "No se pudieron cargar las recetas históricas: " + error.getMessage());
                });
    }

    public void buscarRecetasHistoricoAsync(String filtro, String estadoSeleccionado) {
        operacionEnProgreso = true;
        progressRecetasHistorico.setVisible(true);

        Async.run(() -> {
                    try {
                        List<Receta> recetasFiltradas;

                        if (filtro.isEmpty()) {
                            recetasFiltradas = new ArrayList<>(todasLasRecetas);
                        } else {
                            recetasFiltradas = todasLasRecetas.stream()
                                    .filter(r -> r.getPaciente().getId().toLowerCase().contains(filtro.toLowerCase()) ||
                                            r.getPaciente().getNombre().toLowerCase().contains(filtro.toLowerCase()))
                                    .collect(Collectors.toList());
                        }

                        if (!"Todas".equals(estadoSeleccionado)) {
                            int estadoNum = convertirEstadoANumero(estadoSeleccionado);
                            recetasFiltradas = recetasFiltradas.stream()
                                    .filter(r -> r.getEstado() == estadoNum)
                                    .collect(Collectors.toList());
                        }

                        return recetasFiltradas;
                    } catch (Exception e) {
                        throw new RuntimeException("Error al buscar recetas históricas: " + e.getMessage());
                    }
                },
                recetasFiltradas -> {
                    operacionEnProgreso = false;
                    progressRecetasHistorico.setVisible(false);

                    if (recetasFiltradas.isEmpty()) {
                        mostrarAlerta("Sin resultados", "No se encontraron recetas con los criterios indicados");
                    }
                    tblViewRecetasHistoricoRecetas.getItems().setAll(recetasFiltradas);
                },
                error -> {
                    operacionEnProgreso = false;
                    progressRecetasHistorico.setVisible(false);
                    mostrarAlerta("Error", "Error en búsqueda: " + error.getMessage());
                });
    }

    public void cargarDetallesRecetaAsync(String idReceta) {
        operacionEnProgreso = true;
        progressDetallesHistorico.setVisible(true);
        btnVerDetallesHistorico.setDisable(true);

        Async.run(() -> {
                    try {
                        HospitalServiceProxy proxy = new HospitalServiceProxy();
                        if (proxy.conectar()) {
                            SolicitudBackend solicitud = new SolicitudBackend("OBTENER_DETALLES_RECETA");
                            solicitud.agregarParametro("idReceta", idReceta);
                            RespuestaBackend respuesta = (RespuestaBackend) proxy.enviarSolicitud(solicitud);
                            proxy.desconectar();

                            if (respuesta.isExito()) {
                                return (List<DetalleMedicamento>) respuesta.getDatos();
                            } else {
                                throw new RuntimeException(respuesta.getMensaje());
                            }
                        }
                        throw new RuntimeException("No se pudo conectar al backend");
                    } catch (Exception e) {
                        throw new RuntimeException("Error al cargar detalles de receta: " + e.getMessage());
                    }
                },
                detalles -> {
                    operacionEnProgreso = false;
                    progressDetallesHistorico.setVisible(false);
                    btnVerDetallesHistorico.setDisable(false);

                    if (detalles.isEmpty()) {
                        mostrarAlerta("Sin detalles", "La receta seleccionada no tiene medicamentos asociados");
                    }
                    tableDetallesHistorico.getItems().setAll(detalles);
                },
                error -> {
                    operacionEnProgreso = false;
                    progressDetallesHistorico.setVisible(false);
                    btnVerDetallesHistorico.setDisable(false);
                    mostrarAlerta("Error", "Error al cargar detalles: " + error.getMessage());
                });
    }

    public void buscarRecetasEnBackendAsync(String filtro, String estadoSeleccionado) {
        operacionEnProgreso = true;
        if (progressRecetasHistorico != null) {
            progressRecetasHistorico.setVisible(true);
        }

        Async.run(() -> {
                    try {
                        HospitalServiceProxy proxy = new HospitalServiceProxy();
                        if (proxy.conectar()) {
                            SolicitudBackend solicitud = new SolicitudBackend("OBTENER_RECETAS");
                            RespuestaBackend respuesta = (RespuestaBackend) proxy.enviarSolicitud(solicitud);
                            proxy.desconectar();

                            if (respuesta.isExito()) {
                                List<Receta> recetasFrescas = (List<Receta>) respuesta.getDatos();

                                this.todasLasRecetas = recetasFrescas;

                                List<Receta> recetasFiltradas = recetasFrescas;

                                if (!filtro.isEmpty()) {
                                    recetasFiltradas = recetasFrescas.stream()
                                            .filter(r -> r.getPaciente() != null &&
                                                    (r.getPaciente().getId().toLowerCase().contains(filtro.toLowerCase()) ||
                                                            r.getPaciente().getNombre().toLowerCase().contains(filtro.toLowerCase()) ||
                                                            r.getId().toLowerCase().contains(filtro.toLowerCase())))
                                            .collect(Collectors.toList());
                                }

                                if (!"Todas".equals(estadoSeleccionado)) {
                                    int estadoNum = convertirEstadoANumero(estadoSeleccionado);
                                    recetasFiltradas = recetasFiltradas.stream()
                                            .filter(r -> r.getEstado() == estadoNum)
                                            .collect(Collectors.toList());
                                }

                                return recetasFiltradas;
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
                    if (progressRecetasHistorico != null) {
                        progressRecetasHistorico.setVisible(false);
                    }

                    if (recetasFiltradas.isEmpty()) {
                        mostrarAlerta("Sin resultados", "No se encontraron recetas con los criterios indicados");
                    }
                    listaReceta.setAll(recetasFiltradas);
                    tblViewRecetasHistoricoRecetas.setItems(listaReceta);
                },
                error -> {
                    operacionEnProgreso = false;
                    if (progressRecetasHistorico != null) {
                        progressRecetasHistorico.setVisible(false);
                    }
                    mostrarAlerta("Error", "Error en búsqueda: " + error.getMessage());
                });
    }



}
