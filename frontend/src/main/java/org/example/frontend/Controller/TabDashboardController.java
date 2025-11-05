package org.example.frontend.Controller;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import org.example.frontend.Modelo.HospitalFrontend;
import org.example.frontend.Servicios.HospitalServiceProxy;
import org.example.proyectohospital.Modelo.DetalleMedicamento;
import org.example.proyectohospital.Modelo.Medicamento;
import org.example.proyectohospital.Modelo.Receta;
import org.example.proyectohospital.shared.RespuestaBackend;
import org.example.proyectohospital.shared.SolicitudBackend;

import java.net.URL;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

public class TabDashboardController implements Initializable {
    @FXML private TableColumn<Medicamento, String> colIMeses;
    @FXML private TableColumn<Medicamento, String> colIMedicamento;
    @FXML private TableView<Medicamento> tbvResultadoBMedicamento;
    @FXML private Button btnDesplegarDashboard;
    @FXML private Button btnAddMedicamento;
    @FXML private ComboBox<Medicamento> comboBoxMedicamentos;
    @FXML private DatePicker dtpHasta;
    @FXML private DatePicker dtpDesde;
    @FXML private LineChart<String,Number> lineChartMedicamentos;
    @FXML private PieChart pieChartRecetas;
    @FXML private CategoryAxis rangoXAxis;
    @FXML private NumberAxis rangoYAxis;
    @FXML private ProgressIndicator progressDashboard;

    private List<Medicamento> medicamentosSeleccionados = new ArrayList<>();
    private List<Medicamento> todosLosMedicamentos = new ArrayList<>();
    private List<Receta> todasLasRecetas = new ArrayList<>();

    private boolean operacionEnProgreso = false;

    @FXML
    public void seleccionMedicamento(ActionEvent actionEvent) {
    }

    @FXML
    public void insertarMedicamentoAlDashboard(ActionEvent actionEvent) {
        Medicamento medicamento = comboBoxMedicamentos.getSelectionModel().getSelectedItem();

        if (medicamento == null) {
            mostrarAlerta("Error", "Debe seleccionar un medicamento");
            return;
        }

        boolean yaExiste = medicamentosSeleccionados.stream()
                .anyMatch(m -> m.getCodigo().equals(medicamento.getCodigo()));

        if (!yaExiste) {
            medicamentosSeleccionados.add(medicamento);
            tbvResultadoBMedicamento.getItems().setAll(medicamentosSeleccionados);
        }

        comboBoxMedicamentos.getSelectionModel().clearSelection();
    }

    @FXML
    public void desplegarDashboard() {
        if (operacionEnProgreso) return;

        LocalDate desde = dtpDesde.getValue();
        LocalDate hasta = dtpHasta.getValue();

        if (desde == null || hasta == null) {
            mostrarAlerta("Error", "Debe seleccionar fechas válidas");
            return;
        }

        if (desde.isAfter(hasta)) {
            mostrarAlerta("Error", "La fecha 'Desde' no puede ser mayor que 'Hasta'");
            return;
        }

        cargarDatosParaDashboard(desde, hasta);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colIMedicamento.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getNombre()));
        colIMeses.setCellValueFactory(cellData -> {
            LocalDate desde = dtpDesde.getValue();
            LocalDate hasta = dtpHasta.getValue();
            if (desde != null && hasta != null) {
                return new SimpleStringProperty(desde + " - " + hasta);
            }
            return new SimpleStringProperty("No definido");
        });

        cargarMedicamentosAsync();
        cargarTodasLasRecetasAsync();

        LocalDate hoy = LocalDate.now();
        dtpHasta.setValue(hoy);
        dtpDesde.setValue(hoy.minusMonths(3));
    }

    private void cargarDatosParaDashboard(LocalDate desde, LocalDate hasta) {
        operacionEnProgreso = true;
        progressDashboard.setVisible(true);
        btnDesplegarDashboard.setDisable(true);

        Async.run(() -> {
                    try {
                        Map<String, Object> resultado = new HashMap<>();
                        resultado.put("desde", desde);
                        resultado.put("hasta", hasta);
                        resultado.put("medicamentosSeleccionados", new ArrayList<>(medicamentosSeleccionados));
                        resultado.put("todasLasRecetas", new ArrayList<>(todasLasRecetas));
                        return resultado;
                    } catch (Exception e) {
                        throw new RuntimeException("Error al procesar datos del dashboard: " + e.getMessage());
                    }
                },
                resultado -> {
                    operacionEnProgreso = false;
                    progressDashboard.setVisible(false);
                    btnDesplegarDashboard.setDisable(false);

                    LocalDate desdeFecha = (LocalDate) resultado.get("desde");
                    LocalDate hastaFecha = (LocalDate) resultado.get("hasta");
                    List<Medicamento> medicamentos = (List<Medicamento>) resultado.get("medicamentosSeleccionados");
                    List<Receta> recetas = (List<Receta>) resultado.get("todasLasRecetas");

                    hacerGraficoLineas(desdeFecha, hastaFecha, medicamentos, recetas);
                    hacerGraficoPastel(desdeFecha, hastaFecha, recetas);
                },
                error -> {
                    operacionEnProgreso = false;
                    progressDashboard.setVisible(false);
                    btnDesplegarDashboard.setDisable(false);
                    mostrarAlerta("Error", "No se pudo generar el dashboard: " + error.getMessage());
                });
    }

    private void cargarMedicamentosAsync() {
        Async.run(() -> {
                    try {
                        HospitalServiceProxy proxy = new HospitalServiceProxy();
                        if (proxy.conectar()) {
                            SolicitudBackend solicitud = new SolicitudBackend("OBTENER_MEDICAMENTOS");
                            RespuestaBackend respuesta = (RespuestaBackend) proxy.enviarSolicitud(solicitud);
                            proxy.desconectar();

                            if (respuesta.isExito()) {
                                return (List<Medicamento>) respuesta.getDatos();
                            } else {
                                throw new RuntimeException(respuesta.getMensaje());
                            }
                        }
                        throw new RuntimeException("No se pudo conectar al backend");
                    } catch (Exception e) {
                        throw new RuntimeException("Error al cargar medicamentos: " + e.getMessage());
                    }
                },
                medicamentos -> {
                    this.todosLosMedicamentos = medicamentos;
                    Platform.runLater(() -> {
                        comboBoxMedicamentos.getItems().clear();
                        comboBoxMedicamentos.getItems().addAll(medicamentos);

                        comboBoxMedicamentos.setCellFactory(listView -> new ListCell<Medicamento>() {
                            @Override
                            protected void updateItem(Medicamento med, boolean empty) {
                                super.updateItem(med, empty);
                                if (empty || med == null) {
                                    setText("");
                                } else {
                                    setText(med.getNombre());
                                }
                            }
                        });

                        comboBoxMedicamentos.setButtonCell(new ListCell<Medicamento>() {
                            @Override
                            protected void updateItem(Medicamento med, boolean empty) {
                                super.updateItem(med, empty);
                                if (empty || med == null) {
                                    setText("");
                                } else {
                                    setText(med.getNombre());
                                }
                            }
                        });
                    });
                },
                error -> {
                    mostrarAlerta("Error", "No se pudieron cargar los medicamentos: " + error.getMessage());
                });
    }

    private void cargarTodasLasRecetasAsync() {
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
                        throw new RuntimeException("Error al cargar recetas: " + e.getMessage());
                    }
                },
                recetas -> {
                    this.todasLasRecetas = recetas;
                },
                error -> {
                    System.err.println("Error al cargar recetas: " + error.getMessage());
                });
    }

    private void hacerGraficoLineas(LocalDate desde, LocalDate hasta, List<Medicamento> medicamentos, List<Receta> recetas) {
        Platform.runLater(() -> {
            lineChartMedicamentos.getData().clear();
            Map<String, Integer> mesesBase = crearMesesBase(desde, hasta);

            for (Medicamento medicamento : medicamentos) {
                XYChart.Series<String, Number> serie = new XYChart.Series<>();
                serie.setName(medicamento.getNombre());
                Map<String, Integer> cantidadPorMes = new LinkedHashMap<>(mesesBase);

                for (Receta receta : recetas) {
                    LocalDate fechaReceta = receta.getFechaPrescripcion();
                    if (fechaReceta.isBefore(desde) || fechaReceta.isAfter(hasta)) {
                        continue;
                    }
                    String mesKey = String.format("%02d-%02d", fechaReceta.getMonthValue(), fechaReceta.getYear() % 100);

                    for (DetalleMedicamento detalle : receta.getDetalleMedicamentos()) {
                        if (detalle.getMedicamento().getCodigo().equals(medicamento.getCodigo())) {
                            cantidadPorMes.put(mesKey, cantidadPorMes.get(mesKey) + detalle.getCantidad());
                        }
                    }
                }

                for (Map.Entry<String, Integer> entry : cantidadPorMes.entrySet()) {
                    serie.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
                }

                lineChartMedicamentos.getData().add(serie);
            }

            rangoXAxis.setLabel("Mes");
            rangoXAxis.setAnimated(false);

            rangoYAxis.setLabel("Cantidad");
            rangoYAxis.setAnimated(false);

            int maximo = 0;
            for (Medicamento medicamento : medicamentos) {
                Map<String, Integer> cantidadPorMes = new LinkedHashMap<>(mesesBase);

                for (Receta receta : recetas) {
                    LocalDate fechaReceta = receta.getFechaPrescripcion();
                    if (fechaReceta.isBefore(desde) || fechaReceta.isAfter(hasta)) continue;

                    String mesKey = String.format("%02d-%02d", fechaReceta.getMonthValue(), fechaReceta.getYear() % 100);
                    for (DetalleMedicamento detalle : receta.getDetalleMedicamentos()) {
                        if (detalle.getMedicamento().getCodigo().equals(medicamento.getCodigo())) {
                            cantidadPorMes.put(mesKey, cantidadPorMes.get(mesKey) + detalle.getCantidad());
                        }
                    }
                }

                for (Integer cantidad : cantidadPorMes.values()) {
                    if (cantidad > maximo) maximo = cantidad;
                }
            }

            if (maximo == 0) {
                rangoYAxis.setUpperBound(10);
            } else {
                rangoYAxis.setUpperBound(maximo + Math.max(3, maximo / 4));
            }
            rangoYAxis.setLowerBound(0);
        });
    }

    private int calcularMaximoParaEjeY(LocalDate desde, LocalDate hasta, List<Medicamento> medicamentos, List<Receta> recetas) {
        int maximo = 0;
        Map<String, Integer> mesesBase = crearMesesBase(desde, hasta);

        for (Medicamento medicamento : medicamentos) {
            Map<String, Integer> cantidadPorMes = new LinkedHashMap<>(mesesBase);

            for (Receta receta : recetas) {
                LocalDate fechaReceta = receta.getFechaPrescripcion();
                if (fechaReceta.isBefore(desde) || fechaReceta.isAfter(hasta)) continue;

                String mesKey = String.format("%02d-%02d", fechaReceta.getMonthValue(), fechaReceta.getYear() % 100);
                for (DetalleMedicamento detalle : receta.getDetalleMedicamentos()) {
                    if (detalle.getMedicamento().getCodigo().equals(medicamento.getCodigo())) {
                        cantidadPorMes.put(mesKey, cantidadPorMes.get(mesKey) + detalle.getCantidad());
                    }
                }
            }

            for (Integer cantidad : cantidadPorMes.values()) {
                if (cantidad > maximo) maximo = cantidad;
            }
        }

        return maximo;
    }

    private void hacerGraficoPastel(LocalDate desde, LocalDate hasta, List<Receta> recetas) {
        Platform.runLater(() -> {
            pieChartRecetas.getData().clear();
            int procesadas = 0;
            int confeccionadas = 0;
            int listas = 0;
            int entregadas = 0;

            for (Receta receta : recetas) {
                LocalDate fechaReceta = receta.getFechaPrescripcion();
                if (fechaReceta.isBefore(desde) || fechaReceta.isAfter(hasta)) {
                    continue;
                }

                int estado = receta.getEstado();
                switch (estado) {
                    case 1: confeccionadas++; break;
                    case 2: procesadas++; break;
                    case 3: listas++; break;
                    case 4: entregadas++; break;
                }
            }

            if (procesadas > 0) {
                pieChartRecetas.getData().add(new PieChart.Data("Procesada (" + procesadas + ")", procesadas));
            }
            if (confeccionadas > 0) {
                pieChartRecetas.getData().add(new PieChart.Data("Confeccionada (" + confeccionadas + ")", confeccionadas));
            }
            if (listas > 0) {
                pieChartRecetas.getData().add(new PieChart.Data("Lista (" + listas + ")", listas));
            }
            if (entregadas > 0) {
                pieChartRecetas.getData().add(new PieChart.Data("Entregada (" + entregadas + ")", entregadas));
            }

            if (pieChartRecetas.getData().isEmpty()) {
                pieChartRecetas.getData().add(new PieChart.Data("Sin datos", 1));
            }
        });
    }

    private Map<String, Integer> crearMesesBase(LocalDate desde, LocalDate hasta) {
        Map<String, Integer> meses = new LinkedHashMap<>();
        YearMonth inicio = YearMonth.from(desde);
        YearMonth fin = YearMonth.from(hasta);
        YearMonth actual = inicio;

        while (!actual.isAfter(fin)) {
            String mesKey = String.format("%02d-%02d", actual.getMonthValue(), actual.getYear() % 100);
            meses.put(mesKey, 0);
            actual = actual.plusMonths(1);
        }

        return meses;
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(titulo);
            alert.setHeaderText(null);
            alert.setContentText(mensaje);
            alert.showAndWait();
        });
    }
}