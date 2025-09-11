package org.example.proyectohospital.Controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.proyectohospital.Modelo.DetalleMedicamento;
import org.example.proyectohospital.Modelo.Medicamento;

import java.net.URL;
import java.util.ResourceBundle;

public class ModificarDetalleMedicamentoController implements Initializable {
    @FXML private Button btnGuardar;
    @FXML private Button btnSalir;
    @FXML private Spinner<Integer> spinnerDuracionEnDias;
    @FXML private Spinner<Integer> spinnerCantidad;
    @FXML private TextField txtIndicaciones;
    @FXML private TableColumn<Medicamento, String> colPresentacionMedicamento;
    @FXML private TableColumn<Medicamento, String> colNombreMedicamento;
    @FXML private TableColumn<Medicamento, String> colCodigoMedicamento;
    @FXML private TableView<Medicamento> tbvResultadoMedicamento;
    private static int contadorId = 1;
    private Medicamento medicamento;
    private AgregarMedicamentoRecetaController controllerPadre;

    public void setMedicamento(Medicamento medicamento) {
        this.medicamento = medicamento;
        tbvResultadoMedicamento.setItems(FXCollections.observableArrayList(medicamento));
    }

    public void setControllerPadre(AgregarMedicamentoRecetaController controller) {
        this.controllerPadre = controller;
    }

    @FXML
    private void volverAAnterior(ActionEvent actionEvent) {
        cerrarVentana();
    }

    @FXML
    private void guardarDetalleMedicamento(ActionEvent actionEvent) {
        if (medicamento == null) {
            mostrarAlerta("Error", "Medicamento inválido");
            return;
        }

        int cantidad = spinnerCantidad.getValue();
        int dias = spinnerDuracionEnDias.getValue();
        String indicaciones = txtIndicaciones.getText().trim();

        if (indicaciones.isEmpty()) {
            mostrarAlerta("Error", "Debe ingresar las indicaciones para el medicamento");
            return;
        }

        if (cantidad <= 0 || dias <= 0) {
            mostrarAlerta("Error", "La cantidad y duración deben ser valores positivos");
            return;
        }

        try {
            String idDetalle = String.valueOf(contadorId++);
            DetalleMedicamento detalleMedicamento = new DetalleMedicamento(medicamento, idDetalle, cantidad, dias, indicaciones);

            if (controllerPadre != null) {
                controllerPadre.recibirDetalleMedicamento(detalleMedicamento);
            }

            cerrarVentana();

        } catch (Exception e) {
            mostrarAlerta("Error", "Error al crear el detalle del medicamento: " + e.getMessage());
            e.printStackTrace();
        }
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colCodigoMedicamento.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNombreMedicamento.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colPresentacionMedicamento.setCellValueFactory(new PropertyValueFactory<>("presentacion"));

        spinnerCantidad.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1)
        );

        spinnerDuracionEnDias.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 365, 1)
        );

        spinnerCantidad.setEditable(true);
        spinnerDuracionEnDias.setEditable(true);

        spinnerCantidad.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                spinnerCantidad.getEditor().setText(oldValue);
            }
        });

        spinnerDuracionEnDias.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                spinnerDuracionEnDias.getEditor().setText(oldValue);
            }
        });
    }

    private void cerrarVentana() {
        Stage stage = (Stage) btnSalir.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    public void cargarDatosExistentes(DetalleMedicamento detalle) {
        if (detalle != null) {
            spinnerCantidad.getValueFactory().setValue(detalle.getCantidad());
            spinnerDuracionEnDias.getValueFactory().setValue(detalle.getDuracion());
            txtIndicaciones.setText(detalle.getIndicacion());
        }
    }


}