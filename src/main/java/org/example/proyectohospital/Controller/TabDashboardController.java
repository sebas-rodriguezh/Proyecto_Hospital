package org.example.proyectohospital.Controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.proyectohospital.Logica.Hospital;
import org.example.proyectohospital.Modelo.DetalleMedicamento;
import org.example.proyectohospital.Modelo.Medicamento;
import org.example.proyectohospital.Modelo.Receta;
import org.example.proyectohospital.Logica.GestorRecetas;
import org.example.proyectohospital.Logica.GestorMedicamentos;

import java.net.URL;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
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
    private final GestorRecetas gestorRecetas = Hospital.getInstance().getGestorRecetas();
    private final GestorMedicamentos gestorMedicamentos = Hospital.getInstance().getGestorMedicamentos();
    private List<Medicamento> medicamentosSeleccionados = new ArrayList<>();

    @FXML
    public void seleccionMedicamento(ActionEvent actionEvent) {
    }

    @FXML
    public void insertarMedicamentoAlDashboard(ActionEvent actionEvent) {
        Medicamento medicamento = comboBoxMedicamentos.getSelectionModel().getSelectedItem();

        if (medicamento == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setContentText("Debe seleccionar un medicamento");
            alert.showAndWait();
            return;
        }

        boolean yaExiste = false;
        for (Medicamento m : medicamentosSeleccionados) {
            if (m.getCodigo().equals(medicamento.getCodigo())) {
                yaExiste = true;
                break;
            }
        }

        if (!yaExiste) {
            medicamentosSeleccionados.add(medicamento);
            tbvResultadoBMedicamento.getItems().setAll(medicamentosSeleccionados);
        }

        comboBoxMedicamentos.getSelectionModel().clearSelection();
    }

    @FXML
    public void desplegarDashboard() {
        LocalDate desde = dtpDesde.getValue();
        LocalDate hasta = dtpHasta.getValue();

        if (desde == null || hasta == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setContentText("Debe seleccionar fechas válidas");
            alert.showAndWait();
            return;
        }

        if (desde.isAfter(hasta)) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setContentText("La fecha 'Desde' no puede ser mayor que 'Hasta'");
            alert.showAndWait();
            return;
        }

        hacerGraficoLineas(desde, hasta);
        hacerGraficoPastel(desde, hasta);
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

        List<Medicamento> medicamentos = gestorMedicamentos.getMedicamentos();
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
        LocalDate hoy = LocalDate.now();
        dtpHasta.setValue(hoy);
        dtpDesde.setValue(hoy.minusMonths(3));
    }

    private void hacerGraficoLineas(LocalDate desde, LocalDate hasta) {
        lineChartMedicamentos.getData().clear();
        Map<String, Integer> mesesBase = crearMesesBase(desde, hasta);
        for (Medicamento medicamento : medicamentosSeleccionados) {
            XYChart.Series<String, Number> serie = new XYChart.Series<>();
            serie.setName(medicamento.getNombre());
            Map<String, Integer> cantidadPorMes = new LinkedHashMap<>(mesesBase);
            List<Receta> todasLasRecetas = gestorRecetas.getRecetas();
            for (Receta receta : todasLasRecetas) {
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
        for (Medicamento medicamento : medicamentosSeleccionados) {
            Map<String, Integer> cantidadPorMes = new LinkedHashMap<>(mesesBase);
            List<Receta> todasLasRecetas = gestorRecetas.getRecetas();
            for (Receta receta : todasLasRecetas) {
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

    private void hacerGraficoPastel(LocalDate desde, LocalDate hasta) {
        pieChartRecetas.getData().clear();
        int procesadas = 0;
        int confeccionadas = 0;
        int listas = 0;
        int entregadas = 0;

        List<Receta> todasLasRecetas = gestorRecetas.getRecetas();
        for (Receta receta : todasLasRecetas) {
            LocalDate fechaReceta = receta.getFechaPrescripcion();

            if (fechaReceta.isBefore(desde) || fechaReceta.isAfter(hasta)) {
                continue;
            }

            int estado = receta.getEstado();
            if (estado == 1) {
                confeccionadas++;
            } else if (estado == 2) {
                procesadas++;
            } else if (estado == 3) {
                listas++;
            } else if (estado == 4) {
                entregadas++;
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
    }
}