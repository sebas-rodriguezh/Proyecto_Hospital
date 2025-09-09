package org.example.proyectohospital.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.proyectohospital.Modelo.Medicamento;

import java.net.URL;
import java.util.ResourceBundle;

public class ModificarDetalleMedicamentoController implements Initializable {
    @FXML private Button btnSalir;
    @FXML private Button btnGuardar;
    @FXML private Spinner<Integer> spinnerDuracionEnDias;
    @FXML private Spinner<Integer> spinnerCantidad;
    @FXML private TextField txtIndicaciones;
    @FXML private TableColumn <Medicamento, String> colPresentacionMedicamento;
    @FXML private TableColumn <Medicamento, String> colNombreMedicamento;
    @FXML private TableColumn <Medicamento, String> colCodigoMedicamento;
    @FXML private TableView <Medicamento> tbvResultadoMedicamento;

    private Medicamento medicamento; // referencia al medicamento actual

    public void setMedicamento(Medicamento medicamento) {
        this.medicamento = medicamento;
        tbvResultadoMedicamento.getItems().setAll(medicamento);
    }


    @FXML
    private void volverAAnterior(ActionEvent actionEvent) {
        Stage stage = (Stage) btnSalir.getScene().getWindow();
        stage.close();
    }

    @FXML
    private  void guardarDetalleMedicamento(ActionEvent actionEvent) {
        if(medicamento == null){
            mostrarAlerta("Error","Medicamento invalido");
            return;
        }

        int cantidad = spinnerCantidad.getValue();
        int dias = spinnerDuracionEnDias.getValue();
        String indicaciones  = txtIndicaciones.getText();

        String resumen =  String.format("Medicamento: %s (%s)\nPresentacion: %s\nCantidad: %d\nDuracion: %d\nndicaciones: %s",
                medicamento.getNombre(),
                medicamento.getCodigo(),
                medicamento.getPresentacion(),
                cantidad,
                dias,
                indicaciones
        );

        mostrarAlerta("Detalle medicamento guardado",resumen);

        Stage stage = (Stage) btnGuardar.getScene().getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Configurar columnas
        colCodigoMedicamento.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNombreMedicamento.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colPresentacionMedicamento.setCellValueFactory(new PropertyValueFactory<>("presentacion"));

        // Configurar spinners
        spinnerDuracionEnDias.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 30, 1)
        );
        spinnerCantidad.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 30, 1)
        );

        spinnerDuracionEnDias.setEditable(false);
        spinnerCantidad.setEditable(false);

    }

    private void mostrarAlerta(String titulo, String mensaje){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

}
