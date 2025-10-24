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
    @FXML private ProgressIndicator progressMedicamentos;
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

    private boolean modoEdicion = false;
    private DetalleMedicamento detalleOriginal = null;
    private boolean operacionEnProgreso = false;


    public void setModoEdicion(DetalleMedicamento detalleAEditar) {
        this.modoEdicion = true;
        this.detalleOriginal = detalleAEditar;
        this.medicamentoSeleccionado = detalleAEditar.getMedicamento();
        btnSeleccionar.setDisable(false);
        tbvResultadoBPaciente.getSelectionModel().select(detalleAEditar.getMedicamento());
    }


    public AgregarMedicamentoRecetaController() {
    }

    public void setControllerPadre(TabPrescibirController controller) {
        this.controllerPadre = controller;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colCodigoMedicamento.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNombreMedicamento.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colPresentacionMedicamento.setCellValueFactory(new PropertyValueFactory<>("presentacion"));

        comboBoxFiltro.setItems(FXCollections.observableArrayList("Código", "Nombre", "Presentación"));
        comboBoxFiltro.setValue("Nombre");

        txtValorBuscado.textProperty().addListener((obs, oldVal, newVal) -> {
            filtrarMedicamentos(newVal);
        });

        tbvResultadoBPaciente.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    medicamentoSeleccionado = newValue;
                    btnSeleccionar.setDisable(newValue == null);
                }
        );

        btnSeleccionar.setDisable(true);
        cargarMedicamentosAsync();
    }

//    private void cargarMedicamentos() {
//        try {
//            todosLosMedicamentos = gestorMedicamentos.getMedicamentos();
//            tbvResultadoBPaciente.setItems(FXCollections.observableArrayList(todosLosMedicamentos));
//        } catch (Exception e) {
//            mostrarAlerta("Error", "Error al cargar medicamentos: " + e.getMessage());
//        }
//    }


    private void cargarMedicamentosAsync() {
        if (operacionEnProgreso) return;

        operacionEnProgreso = true;
        if (progressMedicamentos != null) progressMedicamentos.setVisible(true);

        Async.run(() -> {
                    try {
                        return gestorMedicamentos.getMedicamentos();
                    } catch (Exception e) {
                        throw new RuntimeException("Error al cargar medicamentos: " + e.getMessage());
                    }
                },
                medicamentos -> {
                    operacionEnProgreso = false;
                    if (progressMedicamentos != null) progressMedicamentos.setVisible(false);
                    todosLosMedicamentos = medicamentos;
                    tbvResultadoBPaciente.setItems(FXCollections.observableArrayList(medicamentos));
                },
                error -> {
                    operacionEnProgreso = false;
                    if (progressMedicamentos != null) progressMedicamentos.setVisible(false);
                    mostrarAlerta("Error", "No se pudieron cargar los medicamentos: " + error.getMessage());
                }
        );
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
            abrirVentanaDetallesMedicamento();
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al abrir detalles del medicamento: " + e.getMessage());
        }
    }

    private void abrirVentanaDetallesMedicamento() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/proyectohospital/View/ModificarDetalleMedicamento.fxml"));
            Parent root = loader.load();

            ModificarDetalleMedicamentoController detallesController = loader.getController();
            detallesController.setMedicamento(medicamentoSeleccionado);
            detallesController.setControllerPadre(this);

            if (modoEdicion && detalleOriginal != null) {
                detallesController.cargarDatosExistentes(detalleOriginal);
            }

            Stage detallesStage = new Stage();
            detallesStage.setTitle(modoEdicion ? "Modificar Detalles del Medicamento" : "Configurar Detalles del Medicamento");
            detallesStage.setScene(new Scene(root));
            detallesStage.initModality(Modality.WINDOW_MODAL);
            detallesStage.initOwner(btnSeleccionar.getScene().getWindow());
            detallesStage.setResizable(false);
            detallesStage.showAndWait();

        } catch (Exception e) {
            mostrarAlerta("Error", "Error al abrir ventana de detalles: " + e.getMessage());
        }
    }



    public void recibirDetalleMedicamento(DetalleMedicamento detalleMedicamento) {
        try {
            if (modoEdicion && detalleOriginal != null) {
                detalleMedicamento.setIdDetalle(detalleOriginal.getIdDetalle());
            }

            if (controllerPadre != null) {
                controllerPadre.agregarDetalleMedicamento(detalleMedicamento);
            }
            cerrarVentana();
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al procesar medicamento: " + e.getMessage());
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