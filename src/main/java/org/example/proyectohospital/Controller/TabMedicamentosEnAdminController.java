package org.example.proyectohospital.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.proyectohospital.Logica.GestorMedicamentos;
import org.example.proyectohospital.Logica.Hospital;
import org.example.proyectohospital.Modelo.Medicamento;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class TabMedicamentosEnAdminController implements Initializable{

    @FXML private Button btnGuardarMedicamento;
    @FXML private Button btnLimpiarCampos;
    @FXML private Button btnBorrarMedicamento;
    @FXML private Button btnBuscarMedicamento;
    @FXML private Button btnMostrarTodosLosMedicamentos;
    @FXML private Button btnModificarMedicamento;

    @FXML private TextField txtNombreMedicamento;
    @FXML private TextField txtCodigoMedicamento;
    @FXML private TextField txtPresentacionMedicamento;
    @FXML private TextField txtBuscarMedicamento;

    @FXML private TableView<Medicamento> tbvResultadoBusquedaMedicamento;
    @FXML private TableColumn<Medicamento, String> colCodigoMedicamento;
    @FXML private TableColumn<Medicamento, String> colNombreMedicamento;
    @FXML private TableColumn<Medicamento, String> colPresentacionMedicamento;

    private final GestorMedicamentos gestor = Hospital.getInstance().getGestorMedicamentos();
    private final ObservableList<Medicamento> listaMedicamentos = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        colCodigoMedicamento.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNombreMedicamento.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colPresentacionMedicamento.setCellValueFactory(new PropertyValueFactory<>("presentacion"));
        tbvResultadoBusquedaMedicamento.setItems(listaMedicamentos);

        tbvResultadoBusquedaMedicamento.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        configurarCampoCodigo(false);
                        llenarCamposConMedicamento(newValue);
                    } else {
                        configurarCampoCodigo(true);
                    }
                }
        );

        configurarCampoCodigo(true);
        mostrarTodosLosMedicamentos();
    }

    @FXML
    private void guardarMedicamento() {
        String codigo = txtCodigoMedicamento.getText().trim();
        String nombre = txtNombreMedicamento.getText().trim();
        String presentacion = txtPresentacionMedicamento.getText().trim();

        if(codigo.isEmpty() || nombre.isEmpty() || presentacion.isEmpty()) {
            mostrarAlerta("Error","Debe llenar todos los campos obligatorios");
            return;
        }

        try {
            Medicamento nuevo = new Medicamento(nombre, presentacion, codigo);
            boolean insertado = gestor.insertarMedicamento(nuevo);

            if (insertado) {
                mostrarTodosLosMedicamentos();
                limpiarCamposMedicamentos();
                mostrarAlerta("Éxito", "Medicamento guardado correctamente");
            } else {
                mostrarAlerta("Error", "Ya existe un medicamento con ese código");
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al guardar medicamento: " + e.getMessage());
        }
    }

    @FXML
    private void modificarMedicamento() {
        Medicamento seleccionado = tbvResultadoBusquedaMedicamento.getSelectionModel().getSelectedItem();
        if(seleccionado == null) {
            mostrarAlerta("Error", "Debe seleccionar un medicamento para modificar");
            return;
        }

        String nombre = txtNombreMedicamento.getText().trim();
        String presentacion = txtPresentacionMedicamento.getText().trim();
        String codigo = txtCodigoMedicamento.getText().trim();

        if (!codigo.equals(seleccionado.getCodigo())) {
            mostrarAlerta("Error", "No se puede modificar el código del medicamento");
            return;
        }

        if (nombre.isEmpty() || presentacion.isEmpty()) {
            mostrarAlerta("Error", "Complete los campos de nombre y presentación");
            return;
        }

        try {
            seleccionado.setNombre(nombre);
            seleccionado.setPresentacion(presentacion);

            gestor.update(seleccionado);
            mostrarTodosLosMedicamentos();
            limpiarCamposMedicamentos();
            mostrarAlerta("Éxito", "Medicamento modificado correctamente");
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al modificar medicamento: " + e.getMessage());
        }
    }

    @FXML
    private void borrarMedicamento() {
        Medicamento seleccionado = tbvResultadoBusquedaMedicamento.getSelectionModel().getSelectedItem();
        if(seleccionado == null){
            mostrarAlerta("Error", "Debe seleccionar un medicamento para borrar.");
            return;
        }

        try {
            boolean eliminado = gestor.eliminar(seleccionado.getCodigo());
            if (eliminado) {
                mostrarTodosLosMedicamentos();
                mostrarAlerta("Éxito", "Medicamento eliminado correctamente");
            } else {
                mostrarAlerta("Error", "No se pudo eliminar el medicamento");
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al borrar medicamento: " + e.getMessage());
        }
    }

    @FXML
    private void buscarMedicamento() {
        String criterio = txtBuscarMedicamento.getText().toLowerCase().trim();

        if(criterio.isEmpty()) {
            mostrarTodosLosMedicamentos();
            return;
        }

        try {
            List<Medicamento> resultados = gestor.getMedicamentos().stream()
                    .filter(p -> p.getCodigo().toLowerCase().contains(criterio) ||
                            p.getNombre().toLowerCase().contains(criterio))
                    .collect(Collectors.toList());

            listaMedicamentos.setAll(resultados);
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al buscar medicamento: " + e.getMessage());
        }
    }

    @FXML
    private void mostrarTodosLosMedicamentos() {
        try {
            listaMedicamentos.setAll(gestor.getMedicamentos());
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al cargar medicamentos: " + e.getMessage());
        }
    }

    @FXML
    private void limpiarCamposMedicamentos(){
        txtCodigoMedicamento.clear();
        txtNombreMedicamento.clear();
        txtPresentacionMedicamento.clear();
        txtBuscarMedicamento.clear();
        tbvResultadoBusquedaMedicamento.getSelectionModel().clearSelection();
        configurarCampoCodigo(true);
    }

    private void configurarCampoCodigo(boolean editable) {
        txtCodigoMedicamento.setEditable(editable);
        txtCodigoMedicamento.setFocusTraversable(editable);

        if (editable) {
            txtCodigoMedicamento.setStyle("-fx-background-color: white; -fx-text-fill: black;");
        } else {
            txtCodigoMedicamento.setStyle("-fx-background-color: #f0f0f0; -fx-text-fill: #666;");
        }
    }

    private void llenarCamposConMedicamento(Medicamento medicamento) {
        txtCodigoMedicamento.setText(medicamento.getCodigo());
        txtNombreMedicamento.setText(medicamento.getNombre());
        txtPresentacionMedicamento.setText(medicamento.getPresentacion());
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}