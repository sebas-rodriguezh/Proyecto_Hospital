package org.example.proyectohospital.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.proyectohospital.Modelo.Receta;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class TabHistoricoRecetasController implements Initializable {
    // Tabla principal de recetas
    @FXML private TableView<Receta> tblViewRecetasHistoricoRecetas;
    @FXML private TableColumn<Receta, String> colIdHistorico;
    @FXML private TableColumn<Receta, String> colPacienteHistorico;
    @FXML private TableColumn <Receta, LocalDate> colFechaNacimientoPaciente;
    @FXML private TableColumn<Receta, LocalDate> colFechaConfeccionHistorico;
    @FXML private TableColumn<Receta, LocalDate> colFechaRetiroHistorico;
    @FXML private TableColumn<Receta, String> colEstadoHistorico;

    // Tabla de detalles de la receta seleccionada
    @FXML private TableView<Receta> tableDetallesHistorico;
    @FXML private TableColumn<Receta, String> colMedicamento;
    @FXML private TableColumn<Receta, String> colPresentacion;
    @FXML private TableColumn<Receta, Integer> colCantidad;
    @FXML private TableColumn<Receta, String> colIndicaciones;
    @FXML private TableColumn<Receta, Integer> colDuracion;

    // Campos de búsqueda y filtro
    @FXML private TextField txtBuscarPacienteIdHistoricoRecetas;
    @FXML private ComboBox<String> comboEstadoHistorico;
    @FXML private Button btnBuscarHistorico;
    @FXML private Button btnVerDetallesHistorico;

    @FXML
    private void btnBuscarHistorico() {
        String filtro = txtBuscarPacienteIdHistoricoRecetas.getText().trim();
        String estado = comboEstadoHistorico.getValue();

        if(filtro.isEmpty() && (estado == null || estado.isEmpty())) {
            mostrarAlerta("Búsqueda inválida", "Ingrese un paciente, ID o estado para filtrar.");
            return;
        }

        //LOGICA FILTRAR RECETAS SEGUN PACIENTE ID O ESTADO

    }

    @FXML
    private void btnVerDetallesHistorico(ActionEvent event) {
        Receta seleccionada = tblViewRecetasHistoricoRecetas.getSelectionModel().getSelectedItem();
        if(seleccionada == null) {
            mostrarAlerta("Selección requerida", "Debe seleccionar una receta para ver sus detalles.");
            return;
        }

        // LÓGICA PARA MOSTRAR LOS DETALLES DE LA RECETA EN tableDetallesHistorico
    }

    @FXML
    private void ordenarRecetasHistoricoRecetas(){
        //ORDENAR TABLA
    }

    @FXML
    private void ordenarDetallesHistorico() {
        //ORDENAR TABLA
    }



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colIdHistorico.setCellValueFactory(new PropertyValueFactory<>("id"));
        colPacienteHistorico.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colFechaNacimientoPaciente.setCellValueFactory(new PropertyValueFactory<>("fechaNacimiento"));
        colFechaConfeccionHistorico.setCellValueFactory(new PropertyValueFactory<>("fechaConfeccion"));
        colFechaRetiroHistorico.setCellValueFactory(new PropertyValueFactory<>("fechaRetiro"));
        colEstadoHistorico.setCellValueFactory(new PropertyValueFactory<>("estado"));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        colFechaNacimientoPaciente.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate fecha, boolean empty) {
                super.updateItem(fecha, empty);
                setText(empty || fecha == null ? "" : fecha.format(formatter));
            }
        });
        colFechaConfeccionHistorico.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate fecha, boolean empty) {
                super.updateItem(fecha, empty);
                setText(empty || fecha == null ? "" : fecha.format(formatter));
            }
        });
        colFechaRetiroHistorico.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate fecha, boolean empty) {
                super.updateItem(fecha, empty);
                setText(empty || fecha == null ? "" : fecha.format(formatter));
            }
        });

        colMedicamento.setCellValueFactory(new PropertyValueFactory<>("medicamento"));
        colPresentacion.setCellValueFactory(new PropertyValueFactory<>("presentacion"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colIndicaciones.setCellValueFactory(new PropertyValueFactory<>("indicaciones"));
        colDuracion.setCellValueFactory(new PropertyValueFactory<>("duracion"));
    }


    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
