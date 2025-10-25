package org.example.proyectohospital.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.proyectohospital.Logica.GestorMedicamentos;
import org.example.proyectohospital.Logica.Hospital;
import org.example.proyectohospital.Modelo.Medicamento;
import org.example.proyectohospital.Modelo.Paciente;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class TabMedicamentosEnAdminController implements Initializable{

    @FXML private Button btnAbrirChat;
    @FXML private ProgressIndicator progressMedicamentos;
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

    //Hilos.
    private boolean operacionEnProgreso = false;

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
        if (operacionEnProgreso)
        {
            return;
        }
        try
        {
            String codigo = txtCodigoMedicamento.getText().trim();
            String nombre = txtNombreMedicamento.getText().trim();
            String presentacion = txtPresentacionMedicamento.getText().trim();

            if(codigo.isEmpty() || nombre.isEmpty() || presentacion.isEmpty()) {
                mostrarAlerta("Error","Debe llenar todos los campos obligatorios");
                return;
            }

            Medicamento nuevo = new Medicamento(nombre, presentacion, codigo);
            guardarMedicamentoAsync(nuevo);
        } catch (Exception e) {
            mostrarAlerta("Error","No se pudo guardar el medicamento.");
            return;
        }
    }

    @FXML
    private void modificarMedicamento() {
        if (operacionEnProgreso) {
            return;
        }
        try
        {
            Medicamento seleccionado = tbvResultadoBusquedaMedicamento.getSelectionModel().getSelectedItem();
            if(seleccionado == null)
            {
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
                modificarMedicamentoAsync(seleccionado);

            } catch (Exception e) {
                mostrarAlerta("Error", "Error al modificar medicamento: " + e.getMessage());
            }

        }
        catch (Exception e)
        {
            mostrarAlerta("Error", "Error al modificar medicamento: " + e.getMessage());
        }
    }

    @FXML
    private void borrarMedicamento() {
        if (operacionEnProgreso)
        {
            return;
        }
        try
        {
            Medicamento seleccionado = tbvResultadoBusquedaMedicamento.getSelectionModel().getSelectedItem();
            if(seleccionado == null) {
                mostrarAlerta("Error", "Debe seleccionar un medicamento para borrar.");
                return;
            }
            eliminarMedicamentoAsync(seleccionado);

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
            //listaMedicamentos.setAll(gestor.getMedicamentos());
            cargarMedicamentosAsync();
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

    @FXML
    private void abrirVentanaChat() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/proyectohospital/View/chat-view.fxml"));
            Parent root = loader.load();

            Stage chatStage = new Stage();
            chatStage.setTitle("Chat del Hospital - " + Hospital.getInstance().getUsuarioLogueadoNombre());
            chatStage.setScene(new Scene(root));
            chatStage.setResizable(true);
            chatStage.show();

        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo abrir el chat: " + e.getMessage());
            e.printStackTrace();
        }
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

    //Métodos para hilos (Async).

    public void cargarMedicamentosAsync() {
        if (operacionEnProgreso) {
            return;
        }

        operacionEnProgreso = true;
        progressMedicamentos.setVisible(true);

        if (btnMostrarTodosLosMedicamentos != null) {
            btnMostrarTodosLosMedicamentos.setDisable(true);
        }

        Async.run(
                () -> {
                    try {
                        List<Medicamento> medicamentos = gestor.getMedicamentos();
                        return medicamentos;
                    } catch (Exception e) {
                        throw new RuntimeException("Error al cargar pacientes: " + e.getMessage());
                    }
                },
                listaMedicamentosCargados -> {
                    operacionEnProgreso = false;
                    progressMedicamentos.setVisible(false);

                    if (btnMostrarTodosLosMedicamentos != null) {
                        btnMostrarTodosLosMedicamentos.setDisable(false);
                    }
                    listaMedicamentos.setAll(listaMedicamentosCargados);
                },
                error -> {
                    operacionEnProgreso = false;
                    progressMedicamentos.setVisible(false);

                    if (btnMostrarTodosLosMedicamentos != null) {
                        btnMostrarTodosLosMedicamentos.setDisable(false);
                    }
                }
        );
    }

    public void guardarMedicamentoAsync(Medicamento nuevo)
    {
        operacionEnProgreso = true;
        progressMedicamentos.setVisible(true);
        btnGuardarMedicamento.setDisable(true);

        Async.run(() -> {
                    try
                    {
                        boolean insertado = gestor.insertarMedicamento(nuevo);
                        return insertado;
                    }

                    catch (Exception e)
                    {
                        throw new RuntimeException("Error al guardar paciente: " + e.getMessage());
                    }
                },
                resultado -> {
                    operacionEnProgreso = false;
                    progressMedicamentos.setVisible(false);
                    btnGuardarMedicamento.setDisable(false);

                    if (resultado)
                    {
                        cargarMedicamentosAsync(); //RECARGO LA TABLA.
                        limpiarCamposMedicamentos();
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Exito al guardar");
                        alert.setHeaderText(null);
                        alert.setContentText("Se pudo insertar al medicamento correctamente.");
                        alert.showAndWait();

                    } else {
                        new Alert(Alert.AlertType.ERROR, "Ya existe un medicamento con ese código").showAndWait();
                    }
                },

                error -> {
                    operacionEnProgreso = false;
                    progressMedicamentos.setVisible(false);
                    btnGuardarMedicamento.setDisable(false);
                    new Alert(Alert.AlertType.ERROR, "No se pudo guardar: " + error.getMessage()).showAndWait();
                }
        );
    }

    public void modificarMedicamentoAsync(Medicamento medicamento)
    {
        operacionEnProgreso = true;
        progressMedicamentos.setVisible(true);
        btnModificarMedicamento.setDisable(true);

        Async.run(()-> {
                    try
                    {
                        gestor.update(medicamento);
                        return medicamento;
                    }
                    catch (Exception e)
                    {
                        throw new RuntimeException("Error al modificar medicamento: " + e.getMessage());
                    }
                },
                resultado -> {
                    operacionEnProgreso = false;
                    progressMedicamentos.setVisible(false);
                    btnModificarMedicamento.setDisable(false);
                    cargarMedicamentosAsync();
                    limpiarCamposMedicamentos();

                    //Se puede borrar luego, si no se quiere decir nada.

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Exito al modificar");
                    alert.setHeaderText(null);
                    alert.setContentText("Se pudo modificar al medicamento correctamente.");
                    alert.showAndWait();
                },
                error -> {
                    operacionEnProgreso = false;
                    progressMedicamentos.setVisible(false);
                    btnModificarMedicamento.setDisable(false);
                    new Alert(Alert.AlertType.ERROR, "No se pudo modificar al medicamento: " + error.getMessage()).showAndWait();
                }
        );
    }

    public void eliminarMedicamentoAsync (Medicamento medicamento)
    {
        operacionEnProgreso = true;
        progressMedicamentos.setVisible(true);
        btnBorrarMedicamento.setDisable(true);

        Async.run(() -> {
                    try {
                        boolean eliminado = gestor.eliminar(medicamento.getCodigo());
                        return eliminado;
                    } catch (Exception e) {
                        throw new RuntimeException("Error al eliminar paciente: " + e.getMessage());
                    }
                },
                resultado -> {
                    operacionEnProgreso = false;
                    progressMedicamentos.setVisible(false);
                    btnBorrarMedicamento.setDisable(false);

                    if (resultado) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Exito al eliminar");
                        alert.setHeaderText(null);
                        alert.setContentText("Se pudo eliminar al medicamento correctamente.");
                        alert.showAndWait();
                        cargarMedicamentosAsync(); // Recargar tabla
                        limpiarCamposMedicamentos();
                    } else {
                        new Alert(Alert.AlertType.ERROR, "No se pudo eliminar al paciente: ").showAndWait();
                    }

                },
                error -> {
                    operacionEnProgreso = false;
                    progressMedicamentos.setVisible(false);
                    btnBorrarMedicamento.setDisable(false);
                    new Alert(Alert.AlertType.ERROR, "No se pudo eliminar al paciente: ").showAndWait();
                }
        );
    }

}