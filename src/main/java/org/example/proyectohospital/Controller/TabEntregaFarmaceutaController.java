package org.example.proyectohospital.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.proyectohospital.Logica.Hospital;
import org.example.proyectohospital.Modelo.Receta;
import org.example.proyectohospital.Logica.GestorRecetas;


import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class TabEntregaFarmaceutaController implements Initializable {
    @FXML private ProgressIndicator progressRecetasEntrega;
    @FXML private Button btnEntregarRecetaFarmaceuta;
    @FXML private Button btnBuscarTabEntregaFarmaceuta;
    @FXML private TableColumn <Receta, LocalDate> colFechaRetiroEntrega;
    @FXML private TableColumn <Receta, String> colPacienteEntrega;
    @FXML private TableColumn <Receta,String> colIdEntrega;
    @FXML private TableColumn <Receta,String> colEstadoReceta;
    @FXML private TableView <Receta> tableTabEntregaFarmaceuta;
    @FXML private TextField txtIdNombreEntregaFarmaceuta;

    private final GestorRecetas gestorRecetas = Hospital.getInstance().getGestorRecetas();
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
//
//        if (gestorRecetas.actualizarEstadoReceta(seleccionada.getId(), 4)) {
//            mostrarAlerta("Éxito", "Receta entregada correctamente");
//            buscarRecetaParaEntrega(null);
//        }
    }

//    @FXML
//    public void buscarRecetaParaEntrega(ActionEvent actionEvent) {
//        if (operacionEnProgreso) {
//            return;
//        }
//
//        String filtro = txtIdNombreEntregaFarmaceuta.getText().trim();
//        List<Receta> recetas = gestorRecetas.obtenerRecetasPorEstado(3);
//
//        if (!filtro.isEmpty()) {
//            recetas = recetas.stream()
//                    .filter(r -> r.getId().toLowerCase().contains(filtro.toLowerCase()) ||
//                            r.getNombrePaciente().toLowerCase().contains(filtro.toLowerCase()) ||
//                            r.getPaciente().getId().toLowerCase().contains(filtro.toLowerCase())
//                    )
//                    .toList();
//        } else {
//            actualizarTabla();
//        }
//        tableTabEntregaFarmaceuta.getItems().setAll(recetas);
//    }

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

    //Métodos para hilos (Async).

    public void cargarRecetasEntregaAsync() {
        if (operacionEnProgreso) {
            return;
        }

        operacionEnProgreso = true;
        progressRecetasEntrega.setVisible(true);

        Async.run(
                () -> {
                    try {
                        return gestorRecetas.obtenerRecetasPorEstado(3);
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

        Async.run(
                () -> {
                    try {
                        List<Receta> recetas = gestorRecetas.obtenerRecetasPorEstado(3);

                        if (!filtro.isEmpty()) {
                            recetas = recetas.stream()
                                    .filter(r -> r.getId().toLowerCase().contains(filtro.toLowerCase()) ||
                                            r.getNombrePaciente().toLowerCase().contains(filtro.toLowerCase()) ||
                                            r.getPaciente().getId().toLowerCase().contains(filtro.toLowerCase())
                                    )
                                    .toList();
                        }
                        return recetas;
                    } catch (Exception e) {
                        throw new RuntimeException("Error al buscar recetas para entrega: " + e.getMessage());
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
                }
        );
    }

    public void actualizarEstadoEntregaAsync(String idReceta, int nuevoEstado) {
        operacionEnProgreso = true;
        progressRecetasEntrega.setVisible(true);
        btnEntregarRecetaFarmaceuta.setDisable(true);

        Async.run(
                () -> {
                    try {
                        boolean actualizado = gestorRecetas.actualizarEstadoReceta(idReceta, nuevoEstado);
                        return actualizado;
                    } catch (Exception e) {
                        throw new RuntimeException("Error al actualizar estado de entrega: " + e.getMessage());
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
