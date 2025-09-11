package org.example.proyectohospital.Controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.proyectohospital.Logica.GestorMedicamentos;
import org.example.proyectohospital.Logica.Hospital;
import org.example.proyectohospital.Modelo.DetalleMedicamento;
import org.example.proyectohospital.Modelo.Medicamento;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class AgregarMedicamentoRecetaController implements Initializable {
    @FXML private Button btnSalir;
    @FXML private Button btnSeleccionar;
    @FXML private TextField txtValorBuscado;
    @FXML private ComboBox<String> comboBoxFiltro;
    @FXML private TableColumn<Medicamento, String> colPresentacionMedicamento;
    @FXML private TableColumn<Medicamento, String> colNombreMedicamento;
    @FXML private TableColumn<Medicamento, String> colCodigoMedicamento;
    @FXML private TableView<Medicamento> tbvResultadoBPaciente;

    private final GestorMedicamentos gestorMedicamentos = Hospital.getInstance().getGestorMedicamentos();
    private List<Medicamento> todosLosMedicamentos;
    private Medicamento medicamentoSeleccionado;
    private TabPrescibirController controllerPadre;

    public AgregarMedicamentoRecetaController() {
    }

    // Método para establecer la referencia al controller padre
    public void setControllerPadre(TabPrescibirController controller) {
        this.controllerPadre = controller;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Configurar las columnas de la tabla
        colCodigoMedicamento.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNombreMedicamento.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colPresentacionMedicamento.setCellValueFactory(new PropertyValueFactory<>("presentacion"));

        // Configurar ComboBox de filtros
        comboBoxFiltro.setItems(FXCollections.observableArrayList("Código", "Nombre", "Presentación"));
        comboBoxFiltro.setValue("Nombre"); // Valor por defecto

        // Cargar medicamentos desde la lógica de negocio
        cargarMedicamentos();

        // Escuchar cambios en el campo de texto para filtrado en tiempo real
        txtValorBuscado.textProperty().addListener((obs, oldVal, newVal) -> {
            filtrarMedicamentos(newVal);
        });

        // Listener para selección de medicamento
        tbvResultadoBPaciente.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    medicamentoSeleccionado = newValue;
                    btnSeleccionar.setDisable(newValue == null);
                }
        );

        btnSeleccionar.setDisable(true); // Inicialmente deshabilitado
    }

    private void cargarMedicamentos() {
        try {
            todosLosMedicamentos = gestorMedicamentos.getMedicamentos();
            tbvResultadoBPaciente.setItems(FXCollections.observableArrayList(todosLosMedicamentos));
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al cargar medicamentos: " + e.getMessage());
        }
    }

    private void filtrarMedicamentos(String texto) {
        if (texto == null || texto.isBlank()) {
            tbvResultadoBPaciente.setItems(FXCollections.observableArrayList(todosLosMedicamentos));
            return;
        }

        try {
            String filtro = texto.toLowerCase().trim();
            String tipoFiltro = comboBoxFiltro.getValue();

            List<Medicamento> filtrados = todosLosMedicamentos.stream()
                    .filter(m -> {
                        switch (tipoFiltro) {
                            case "Código":
                                return m.getCodigo().toLowerCase().contains(filtro);
                            case "Nombre":
                                return m.getNombre().toLowerCase().contains(filtro);
                            case "Presentación":
                                return m.getPresentacion().toLowerCase().contains(filtro);
                            default:
                                return m.getNombre().toLowerCase().contains(filtro) ||
                                        m.getCodigo().toLowerCase().contains(filtro) ||
                                        m.getPresentacion().toLowerCase().contains(filtro);
                        }
                    })
                    .collect(Collectors.toList());

            tbvResultadoBPaciente.setItems(FXCollections.observableArrayList(filtrados));
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al filtrar medicamentos: " + e.getMessage());
        }
    }

    @FXML
    private void volverAAnterior(ActionEvent actionEvent) {
        cerrarVentana();
    }

    @FXML
    private void seleccionarMedicamento(ActionEvent actionEvent) {
        if (medicamentoSeleccionado == null) {
            mostrarAlerta("Selección requerida", "Debe seleccionar un medicamento.");
            return;
        }

        try {
            // Abrir ventana para configurar detalles del medicamento
            abrirVentanaDetallesMedicamento();
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al abrir detalles del medicamento: " + e.getMessage());
        }
    }

    private void abrirVentanaDetallesMedicamento() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/proyectohospital/View/ModificarDetalleMedicamento.fxml"));
            Parent root = loader.load();

            // Obtener el controller de la ventana de detalles
            ModificarDetalleMedicamentoController detallesController = loader.getController();
            detallesController.setMedicamento(medicamentoSeleccionado);
            detallesController.setControllerPadre(this); // Establecer referencia a este controller

            Stage detallesStage = new Stage();
            detallesStage.setTitle("Configurar Detalles del Medicamento");
            detallesStage.setScene(new Scene(root));
            detallesStage.initModality(Modality.WINDOW_MODAL);
            detallesStage.initOwner(btnSeleccionar.getScene().getWindow());
            detallesStage.setResizable(false);
            detallesStage.showAndWait();

        } catch (Exception e) {
            mostrarAlerta("Error", "Error al abrir ventana de detalles: " + e.getMessage());
        }
    }

    // Método llamado desde ModificarDetalleMedicamentoController cuando se guardan los detalles
    public void recibirDetalleMedicamento(DetalleMedicamento detalleMedicamento) {
        try {
            // Pasar el detalle del medicamento al controller padre
            if (controllerPadre != null) {
                controllerPadre.agregarDetalleMedicamento(detalleMedicamento);
            }
            cerrarVentana();
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al agregar medicamento: " + e.getMessage());
        }
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
}