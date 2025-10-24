package org.example.proyectohospital.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import org.example.proyectohospital.Logica.Hospital;
import org.example.proyectohospital.Modelo.Receta;
import org.example.proyectohospital.Logica.GestorRecetas;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class TabAlistadoFarmaceutaController implements Initializable {

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
    private final GestorRecetas gestorRecetas = Hospital.getInstance().getGestorRecetas();
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
        //listaRecetas.setAll(gestorRecetas.obtenerRecetasPorEstado(2));
    }

//    @FXML
//    public void buscarReceta(ActionEvent event) {
//        String filtro = txtBuscarPacienteSolicitud.getText().trim();
//
//        List<Receta> recetas = gestorRecetas.obtenerRecetasPorEstado(2);
//
//        if (!filtro.isEmpty()) {
//            recetas = recetas.stream()
//                    .filter(r -> r.getId().toLowerCase().contains(filtro.toLowerCase()) ||
//                            r.getNombrePaciente().toLowerCase().contains(filtro.toLowerCase()))
//                    .toList();
//        }
//
//        tableRecetasAlistado.getItems().setAll(recetas);
//    }

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


//    @FXML
//    public void alistarMedicamentoSeleccionado(ActionEvent actionEvent) {
//        Receta seleccionada = tableRecetasAlistado.getSelectionModel().getSelectedItem();
//        if (seleccionada == null) {
//            mostrarAlerta("Selección requerida", "Debe seleccionar una receta válida");
//            return;
//        }
//
//        if (gestorRecetas.actualizarEstadoReceta(seleccionada.getId(), 3)) {
//            mostrarAlerta("Éxito", "Receta alistada correctamente");
//            buscarReceta(null);
//        }
//    }
//

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

    //Métodos para hilos (Async).

    public void cargarRecetasAlistadoAsync() {
        if (operacionEnProgreso) return;

        operacionEnProgreso = true;
        progressRecetasAlistado.setVisible(true);

        Async.run(
                () -> {
                    try {
                        return gestorRecetas.obtenerRecetasPorEstado(2);
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

        Async.run(
                () -> {
                    try {
                        List<Receta> recetas = gestorRecetas.obtenerRecetasPorEstado(2);

                        if (!filtro.isEmpty()) {
                            recetas = recetas.stream()
                                    .filter(r -> r.getId().toLowerCase().contains(filtro.toLowerCase()) ||
                                            r.getNombrePaciente().toLowerCase().contains(filtro.toLowerCase()))
                                    .toList();
                        }
                        return recetas;
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
                }
        );
    }

    public void actualizarEstadoAlistadoAsync(String idReceta, int nuevoEstado) {
        operacionEnProgreso = true;
        progressRecetasAlistado.setVisible(true);
        btnPonerEnLista.setDisable(true);

        Async.run(
                () -> {
                    try {
                        return gestorRecetas.actualizarEstadoReceta(idReceta, nuevoEstado);
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