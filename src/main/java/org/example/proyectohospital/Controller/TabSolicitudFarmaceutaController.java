package org.example.proyectohospital.Controller;

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
import org.example.proyectohospital.Logica.Hospital;
import org.example.proyectohospital.Modelo.Receta;
import org.example.proyectohospital.Logica.GestorRecetas;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class TabSolicitudFarmaceutaController implements Initializable {

    @FXML private Button btnAbrirChat;
    @FXML private ProgressIndicator progressRecetas;
    @FXML private TableView<Receta> tableRecetasSolicitud;
    @FXML private TableColumn<Receta, String> colIdSolicitud;
    @FXML private TableColumn<Receta, String> colPacienteSolicitud;
    @FXML private TableColumn<Receta, LocalDate> colFechaRetiroSolicitud;
    @FXML private TableColumn<Receta, String> colEstadoSolicitud;

    @FXML private TextField txtBuscarPacienteSolicitud;
    @FXML private DatePicker dateRetiroSolicitud;
    @FXML private Button btnBuscarSolicitud;
    @FXML private Button btnPonerEnProceso;

    private final GestorRecetas gestorRecetas = Hospital.getInstance().getGestorRecetas();
    private final ObservableList<Receta> listaRecetas = FXCollections.observableArrayList();
    private Receta recetaSeleccionada;

    private boolean operacionEnProgreso = false;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (progressRecetas != null) {
            progressRecetas.setVisible(false);
        }

        inicializarTabla();

        tableRecetasSolicitud.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    recetaSeleccionada = newValue;
                }
        );
    }

    private void inicializarTabla() {
        colIdSolicitud.setCellValueFactory(new PropertyValueFactory<>("id"));
        colPacienteSolicitud.setCellValueFactory(new PropertyValueFactory<>("nombrePaciente"));
        colFechaRetiroSolicitud.setCellValueFactory(new PropertyValueFactory<>("fechaRetiro"));
        colEstadoSolicitud.setCellValueFactory(new PropertyValueFactory<>("nombreEstado"));

        cargarRecetasAsync();

//        listaRecetas.setAll(gestorRecetas.obtenerRecetasPorEstado(1));
//        tableRecetasSolicitud.setItems(listaRecetas);

    }

    public void actualizarTabla() {
//        listaRecetas.setAll(gestorRecetas.obtenerRecetasPorEstado(1));
        cargarRecetasAsync();
    }


//    @FXML
//    public void buscarReceta(ActionEvent event)
//    {
//        if (operacionEnProgreso) {
//            return;
//        }
//
//        try {
//            String filtro = txtBuscarPacienteSolicitud.getText().trim();
//            LocalDate fechaRetiro = dateRetiroSolicitud.getValue();
//
//            List<Receta> recetas = gestorRecetas.obtenerRecetasPorEstado(1);
//
//            if (filtro.isEmpty() && fechaRetiro == null) {
//                listaRecetas.setAll(gestorRecetas.obtenerRecetasPorEstado(1));
//                return;
//            }
//
//            if (!filtro.isEmpty()) {
//                recetas = recetas.stream()
//                        .filter(r -> r.getId().toLowerCase().contains(filtro.toLowerCase()) ||
//                                r.getNombrePaciente().toLowerCase().contains(filtro.toLowerCase()) ||
//                                (r.getPaciente() != null && r.getPaciente().getId().toLowerCase().contains(filtro.toLowerCase())))
//                        .toList();
//            }
//
//            if (fechaRetiro != null) {
//                recetas = recetas.stream()
//                        .filter(r -> r.getFechaRetiro().equals(fechaRetiro))
//                        .toList();
//            }
//
//            tableRecetasSolicitud.getItems().setAll(recetas);
//        } catch (Exception e) {
//            mostrarAlerta("Error", "No se pudo buscar ninguna receta.");
//        }
//    }

    @FXML
    public void buscarReceta(ActionEvent event) {
        if (operacionEnProgreso) {
            return;
        }

        try {
            String filtro = txtBuscarPacienteSolicitud.getText().trim();
            LocalDate fechaRetiro = dateRetiroSolicitud.getValue();

            if (filtro.isEmpty() && fechaRetiro == null) {
                cargarRecetasAsync();
                return;
            }

            buscarRecetasAsync(filtro, fechaRetiro);

        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo buscar ninguna receta.");
        }
    }

    @FXML
    private void abrirVentanaChat() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/proyectohospital/View/chat-view.fxml"));
            Parent root = loader.load();

            Stage chatStage = new Stage();
            chatStage.setTitle("Chat del Hospital - " + Hospital.getInstance().getUsuarioLogueadoNombre());
            chatStage.setScene(new Scene(root));
            chatStage.setResizable(true);
            chatStage.show();

        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo abrir el chat: " + e.getMessage());
            e.printStackTrace();
        }
    }


    @FXML
    public void ponerEnProceso(ActionEvent event) {
        if (operacionEnProgreso) {
            return;
        }

        try {
            if (recetaSeleccionada == null) {
                mostrarAlerta("Selección requerida", "Debe seleccionar una receta válida");
                return;
            }

            LocalDate fechaRetiroPaciente = dateRetiroSolicitud.getValue();

            if (fechaRetiroPaciente == null) {
                mostrarAlerta("Error", "Debe de indicar la fecha de recepción.");
                return;
            }

            LocalDate fechaRetiroReceta = recetaSeleccionada.getFechaRetiro();
            LocalDate fechaMinima = fechaRetiroReceta.minusDays(3);
            LocalDate fechaMaxima = fechaRetiroReceta.plusDays(3);

            if (fechaRetiroPaciente.isBefore(fechaMinima) || fechaRetiroPaciente.isAfter(fechaMaxima)) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                String mensaje = String.format("La fecha de retiro debe estar entre %s y %s", fechaMinima.format(formatter), fechaMaxima.format(formatter));
                mostrarAlerta("Error", mensaje);
            } else {
//                if (gestorRecetas.actualizarEstadoReceta(recetaSeleccionada.getId(), 2)) {
//                    mostrarAlerta("Éxito", "Receta puesta en proceso correctamente");
//                    listaRecetas.setAll(gestorRecetas.obtenerRecetasPorEstado(1));
//                    actualizarTabla();
//                }
                actualizarEstadoAsync(recetaSeleccionada.getId(), 2);
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo procesar la receta.");
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    //Métodos para hilos (Async).

    public void cargarRecetasAsync()
    {
        if (operacionEnProgreso) {
            return;
        }
        operacionEnProgreso = true;
        progressRecetas.setVisible(true);

        Async.run(
                ()-> {
                    try {
                        return gestorRecetas.obtenerRecetasPorEstado(1);
                    } catch (Exception e) {
                        throw new RuntimeException("Error al cargar recetas: " + e.getMessage());
                    }
                },
                recetasCargadas -> {
                    operacionEnProgreso = false;
                    progressRecetas.setVisible(false);
                    listaRecetas.setAll(recetasCargadas);
                    tableRecetasSolicitud.setItems(listaRecetas);
                },
                error -> {
                    operacionEnProgreso = false;
                    progressRecetas.setVisible(false);
                    mostrarAlerta("Error", "No se pudieron cargar las recetas: " + error.getMessage());
                }
        );
    }

    public void buscarRecetasAsync(String filtro, LocalDate fechaRetiro) {
        operacionEnProgreso = true;
        progressRecetas.setVisible(true);

        Async.run(
                () -> {
                    try {
                        List<Receta> recetas = gestorRecetas.obtenerRecetasPorEstado(1);

                        if (!filtro.isEmpty()) {
                            recetas = recetas.stream()
                                    .filter(r -> r.getId().toLowerCase().contains(filtro.toLowerCase()) ||
                                            r.getNombrePaciente().toLowerCase().contains(filtro.toLowerCase()) ||
                                            (r.getPaciente() != null && r.getPaciente().getId().toLowerCase().contains(filtro.toLowerCase())))
                                    .toList();
                        }

                        if (fechaRetiro != null) {
                            recetas = recetas.stream()
                                    .filter(r -> r.getFechaRetiro().equals(fechaRetiro))
                                    .toList();
                        }

                        return recetas;
                    } catch (Exception e) {
                        throw new RuntimeException("Error al buscar recetas: " + e.getMessage());
                    }
                },
                recetasFiltradas -> {
                    operacionEnProgreso = false;
                    progressRecetas.setVisible(false);
                    tableRecetasSolicitud.getItems().setAll(recetasFiltradas);
                },
                error -> {
                    operacionEnProgreso = false;
                    progressRecetas.setVisible(false);
                    mostrarAlerta("Error", "Error en búsqueda: " + error.getMessage());
                }
        );
    }

    public void actualizarEstadoAsync(String idReceta, int nuevoEstado) {
        operacionEnProgreso = true;
        progressRecetas.setVisible(true);
        btnPonerEnProceso.setDisable(true);

        Async.run(
                () -> {
                    try {
                        boolean actualizado = gestorRecetas.actualizarEstadoReceta(idReceta, nuevoEstado);
                        return actualizado;
                    } catch (Exception e) {
                        throw new RuntimeException("Error al actualizar estado: " + e.getMessage());
                    }
                },
                resultado -> {
                    operacionEnProgreso = false;
                    progressRecetas.setVisible(false);
                    btnPonerEnProceso.setDisable(false);

                    if (resultado) {
                        mostrarAlerta("Éxito", "Receta puesta en proceso correctamente");
                        cargarRecetasAsync();
                    } else {
                        mostrarAlerta("Error", "No se pudo actualizar el estado de la receta");
                    }
                },
                error -> {
                    operacionEnProgreso = false;
                    progressRecetas.setVisible(false);
                    btnPonerEnProceso.setDisable(false);
                    mostrarAlerta("Error", "Error al procesar: " + error.getMessage());
                }
        );
    }


}