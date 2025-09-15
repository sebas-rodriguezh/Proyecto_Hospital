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

    // Lista simple de medicamentos seleccionados
    private List<Medicamento> medicamentosSeleccionados = new ArrayList<>();

    @FXML
    public void seleccionMedicamento(ActionEvent actionEvent) {
        // Método simple para manejar selección
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

        // Revisar si ya existe
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

        // Hacer los gráficos
        hacerGraficoLineas(desde, hasta);
        hacerGraficoPastel(desde, hasta);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Configurar tabla simple
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

        // Cargar medicamentos al ComboBox
        List<Medicamento> medicamentos = gestorMedicamentos.getMedicamentos();
        comboBoxMedicamentos.getItems().addAll(medicamentos);

        // Mostrar nombres en ComboBox
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

        // Fechas por defecto
        LocalDate hoy = LocalDate.now();
        dtpHasta.setValue(hoy);
        dtpDesde.setValue(hoy.minusMonths(3));
    }

    private void hacerGraficoLineas(LocalDate desde, LocalDate hasta) {
        // Limpiar gráfico
        lineChartMedicamentos.getData().clear();

        // Crear meses base como en el código refinado
        Map<String, Integer> mesesBase = crearMesesBase(desde, hasta);

        // Por cada medicamento seleccionado
        for (Medicamento medicamento : medicamentosSeleccionados) {

            // Crear una serie (línea) para este medicamento
            XYChart.Series<String, Number> serie = new XYChart.Series<>();
            serie.setName(medicamento.getNombre());

            // Copiar meses base (todos iniciando en 0)
            Map<String, Integer> cantidadPorMes = new LinkedHashMap<>(mesesBase);

            // Contar medicamentos por mes
            List<Receta> todasLasRecetas = gestorRecetas.getRecetas();
            for (Receta receta : todasLasRecetas) {
                LocalDate fechaReceta = receta.getFechaPrescripcion();

                // Verificar si está en el rango
                if (fechaReceta.isBefore(desde) || fechaReceta.isAfter(hasta)) {
                    continue;
                }

                // Formato igual al del mapa
                String mesKey = String.format("%02d-%02d", fechaReceta.getMonthValue(), fechaReceta.getYear() % 100);

                // Buscar medicamento en esta receta
                for (DetalleMedicamento detalle : receta.getDetalleMedicamentos()) {
                    if (detalle.getMedicamento().getCodigo().equals(medicamento.getCodigo())) {
                        cantidadPorMes.put(mesKey, cantidadPorMes.get(mesKey) + detalle.getCantidad());
                    }
                }
            }

            // Agregar todos los puntos en orden (como el código refinado)
            for (Map.Entry<String, Integer> entry : cantidadPorMes.entrySet()) {
                serie.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
            }

            lineChartMedicamentos.getData().add(serie);
        }

        // Configurar ejes como el código refinado
        rangoXAxis.setLabel("Mes");
        rangoXAxis.setAnimated(false);

        rangoYAxis.setLabel("Cantidad");
        rangoYAxis.setAnimated(false);

        // Calcular límite del eje Y
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
            meses.put(mesKey, 0); // Inicializar con 0
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
                procesadas++;
            } else if (estado == 2) {
                confeccionadas++;
            } else if (estado == 3) {
                listas++;
            } else if (estado == 4) {
                entregadas++;
            }
        }

        // Agregar datos al gráfico
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

        // Si no hay datos
        if (pieChartRecetas.getData().isEmpty()) {
            pieChartRecetas.getData().add(new PieChart.Data("Sin datos", 1));
        }
    }
}