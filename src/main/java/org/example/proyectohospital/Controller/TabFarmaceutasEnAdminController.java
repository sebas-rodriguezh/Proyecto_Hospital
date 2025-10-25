package org.example.proyectohospital.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.proyectohospital.Logica.GestorPacientes;
import org.example.proyectohospital.Logica.Hospital;
import org.example.proyectohospital.Modelo.Farmaceuta;
import org.example.proyectohospital.Modelo.Medico;
import org.example.proyectohospital.Modelo.Paciente;
import org.example.proyectohospital.Modelo.Personal;
import org.example.proyectohospital.Logica.GestorPersonal;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class TabFarmaceutasEnAdminController implements Initializable {
    @FXML private Button btnAbrirChat;
    @FXML private ProgressIndicator progressFarmaceutas;
    @FXML private Button btnMostrarTodosLosFarmaceutas;
    @FXML private Button btnModificarFarmaceuta;
    @FXML private Button btnBuscarFarmaceuta;
    @FXML private Button btnBorrarFarmaceuta;
    @FXML private Button btnLimpiarCampos;
    @FXML private Button btnGuardarFarmaceuta;
    @FXML private TextField txtBuscarFarmaceuta;
    @FXML private TableColumn <Farmaceuta, String> colNombreFarmaceuta;
    @FXML private TableColumn <Farmaceuta, String> colIDFarmaceuta;
    @FXML private TableView <Farmaceuta> tbvResultadoBusquedaFarmaceuta;
    @FXML private TextField txtNombreFarmaceuta;
    @FXML private TextField txtIdFarmaceuta;

    private final GestorPersonal gestor = Hospital.getInstance().getGestorPersonal();
    private final GestorPacientes gestorPacientes = Hospital.getInstance().getGestorPacientes();
    private final ObservableList<Farmaceuta> listaFarmaceuta = FXCollections.observableArrayList();

    //Hilos.
    private boolean operacionEnProgreso = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colNombreFarmaceuta.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colIDFarmaceuta.setCellValueFactory(new PropertyValueFactory<>("id"));
        tbvResultadoBusquedaFarmaceuta.setItems(listaFarmaceuta);

        // Configurar el listener para la selección de la tabla
        tbvResultadoBusquedaFarmaceuta.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        configurarCampoId(false);
                        llenarCamposConFarmaceuta(newValue);
                    } else {
                        configurarCampoId(true);
                    }
                }
        );

        configurarCampoId(true);
        mostrarTodosLosFarmaceutas();
    }

    private void configurarCampoId(boolean editable) {
        txtIdFarmaceuta.setEditable(editable);
        txtIdFarmaceuta.setFocusTraversable(editable);

        if (editable) {
            txtIdFarmaceuta.setStyle("-fx-background-color: white; -fx-text-fill: black;");
        } else {
            txtIdFarmaceuta.setStyle("-fx-background-color: #f0f0f0; -fx-text-fill: #666;");
        }
    }

    private void llenarCamposConFarmaceuta(Farmaceuta farmaceuta) {
        txtIdFarmaceuta.setText(farmaceuta.getId());
        txtNombreFarmaceuta.setText(farmaceuta.getNombre());
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

    @FXML
    public void mostrarTodosLosFarmaceutas() {
        try {
            cargarFarmaceutasAsync();
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo cargar la información de los farmaceutas.");
        }
    }

    @FXML
    public void modificarFarmaceuta(ActionEvent actionEvent) {
        if (operacionEnProgreso) {
            return;
        }

        try
        {
            Farmaceuta seleccionado = tbvResultadoBusquedaFarmaceuta.getSelectionModel().getSelectedItem();
            if (seleccionado == null) {
                mostrarAlerta("Error", "Seleccione un farmacéutico ");
                return;
            }

            // Para modificar, NO usamos el ID del campo de texto porque no es editable
            // Usamos el ID del objeto seleccionado directamente
            String nombre = txtNombreFarmaceuta.getText().trim();

            if (nombre.isEmpty()) {
                mostrarAlerta("Error", "Complete el campo nombre");
                return;
            }

            seleccionado.setNombre(nombre);
            seleccionado.setClave(seleccionado.getId()); // Mantener la misma clave que el ID

            modificarFarmaceutaAsync(seleccionado, seleccionado.getId());


        } catch (Exception e) {
            mostrarAlerta("Error", "Error al modificar farmaceuta: " + e.getMessage());
        }
    }

    @FXML
    public void buscarFarmaceuta(ActionEvent actionEvent) {
        String texto = txtBuscarFarmaceuta.getText().trim();

        if (texto.isEmpty()) {
            mostrarTodosLosFarmaceutas();
            return;
        }
        try {
            List<Farmaceuta> resultados = gestor.obtenerPersonalPorTipo("Farmaceuta").stream()
                    .map(p -> (Farmaceuta) p)
                    .filter(f -> {
                        String textoBusqueda = texto.toLowerCase().trim();
                        return f.getId().toLowerCase().contains(textoBusqueda) ||
                                f.getNombre().toLowerCase().contains(textoBusqueda);
                    })
                    .collect(Collectors.toList());
            listaFarmaceuta.setAll(resultados);
        }
        catch (Exception e)
        {
            mostrarAlerta("Error", "Error al buscar farmaceuta: " + e.getMessage());
        }
    }

    @FXML
    public void borrarFarmaceuta(ActionEvent actionEvent) {
        if (operacionEnProgreso) {
            return;
        }

        try
        {
            Farmaceuta seleccionado = tbvResultadoBusquedaFarmaceuta.getSelectionModel().getSelectedItem();
            if (seleccionado == null) {
                mostrarAlerta("Error", "Debe seleccionar un farmaceuta para borrar");
                return;
            }
            eliminarFarmaceutaAsync(seleccionado);
        }
        catch (Exception e) {
            mostrarAlerta("Error", "Error al borrar farmaceuta: " + e.getMessage());
        }
    }

    @FXML
    public void limpiarCamposFarmaceutas() {
        txtNombreFarmaceuta.clear();
        txtIdFarmaceuta.clear();
        txtBuscarFarmaceuta.clear();
        tbvResultadoBusquedaFarmaceuta.getSelectionModel().clearSelection();
        configurarCampoId(true);
    }

    @FXML
    public void guardarFarmaceuta(ActionEvent actionEvent) {
        if (operacionEnProgreso) {
            return;
        }

        try
        {
            String idFarmaceuta = txtIdFarmaceuta.getText();
            String nombreFarmaceuta = txtNombreFarmaceuta.getText();

            if(idFarmaceuta.isEmpty() || nombreFarmaceuta.isEmpty()) {
                mostrarAlerta("Error", "Debe llenar todos los campos obligatorios");
                return;
            }

            Personal nuevo = new Farmaceuta(nombreFarmaceuta, idFarmaceuta, idFarmaceuta);
            guardarFarmaceutaAsync(nuevo);

        }
        catch (Exception e)
        {
            mostrarAlerta("Error", "No se pudo insertar la farmaceuta: " + e.getMessage());
        }
    }

    private void mostrarAlerta(String titulo, String mensaje){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    //Métodos para hilos (Async).

    public void cargarFarmaceutasAsync() {
        if (operacionEnProgreso) {
            return;
        }

        operacionEnProgreso = true;
        progressFarmaceutas.setVisible(true);

        if (btnMostrarTodosLosFarmaceutas != null) {
            btnMostrarTodosLosFarmaceutas.setDisable(true);
        }

        Async.run(
                () -> {
                    try {
                        List<Farmaceuta> farmaceutas = gestor.obtenerPersonalPorTipo("Farmaceuta").stream()
                                .map(p -> (Farmaceuta) p)
                                .collect(Collectors.toList());
                        return farmaceutas;
                    } catch (Exception e) {
                        throw new RuntimeException("Error al cargar farmacéuticos: " + e.getMessage());
                    }
                },
                listaFarmaceutasCargados -> {
                    operacionEnProgreso = false;
                    progressFarmaceutas.setVisible(false);

                    if (btnMostrarTodosLosFarmaceutas != null) {
                        btnMostrarTodosLosFarmaceutas.setDisable(false);
                    }
                    listaFarmaceuta.setAll(listaFarmaceutasCargados);
                },
                error -> {
                    operacionEnProgreso = false;
                    progressFarmaceutas.setVisible(false);

                    if (btnMostrarTodosLosFarmaceutas != null) {
                        btnMostrarTodosLosFarmaceutas.setDisable(false);
                    }
                    new Alert(Alert.AlertType.ERROR, "No se pudieron cargar los farmaceutas: " + error.getMessage()).showAndWait();
                }
        );
    }

    public void guardarFarmaceutaAsync(Personal farmaceuta)
    {
        operacionEnProgreso = true;
        progressFarmaceutas.setVisible(true);
        btnGuardarFarmaceuta.setDisable(true);

        Async.run(() -> {
                    try
                    {
                        boolean respuestaPacientes = gestorPacientes.existeAlguienConEseID(farmaceuta.getId());
                        boolean insertado = gestor.insertarPersonal(farmaceuta, respuestaPacientes);
                        return insertado;
                    }

                    catch (Exception e)
                    {
                        throw new RuntimeException("Error al guardar farmacéutico: " + e.getMessage());
                    }
                },
                resultado -> {
                    operacionEnProgreso = false;
                    progressFarmaceutas.setVisible(false);
                    btnGuardarFarmaceuta.setDisable(false);

                    if (resultado)
                    {
                        cargarFarmaceutasAsync(); //RECARGO LA TABLA.
                        limpiarCamposFarmaceutas();
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Exito al guardar");
                        alert.setHeaderText(null);
                        alert.setContentText("Se pudo insertar el farmacéutico correctamente.");
                        alert.showAndWait();

                    } else {
                        new Alert(Alert.AlertType.ERROR, "Ya existe un usuario con ese ID").showAndWait();
                    }
                },

                error -> {
                    operacionEnProgreso = false;
                    progressFarmaceutas.setVisible(false);
                    btnGuardarFarmaceuta.setDisable(false);
                    new Alert(Alert.AlertType.ERROR, "Ya existe un usuario con ese ID: " + error.getMessage()).showAndWait();
                }
        );
    }

    public void modificarFarmaceutaAsync(Farmaceuta farmaceuta, String idOriginal) {
        operacionEnProgreso = true;
        progressFarmaceutas.setVisible(true);
        btnModificarFarmaceuta.setDisable(true);

        Async.run(() -> {
                    try {
                        // No necesitamos verificar si existe el ID porque no estamos cambiando el ID
                        // Solo estamos modificando el nombre
                        gestor.update(farmaceuta);
                        return true;
                    } catch (Exception e) {
                        throw new RuntimeException("Error al modificar farmacéutico: " + e.getMessage());
                    }
                },
                resultado -> {
                    operacionEnProgreso = false;
                    progressFarmaceutas.setVisible(false);
                    btnModificarFarmaceuta.setDisable(false);

                    if (resultado) {
                        cargarFarmaceutasAsync();
                        limpiarCamposFarmaceutas();
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Exito al modificar");
                        alert.setHeaderText(null);
                        alert.setContentText("Se pudo modificar al farmaceuta correctamente.");
                        alert.showAndWait();

                    } else {
                        new Alert(Alert.AlertType.ERROR, "Error al modificar el farmacéutico").showAndWait();
                    }
                },
                error -> {
                    operacionEnProgreso = false;
                    progressFarmaceutas.setVisible(false);
                    btnModificarFarmaceuta.setDisable(false);
                    new Alert(Alert.AlertType.ERROR, "Error al modificar el farmaceuta: " + error.getMessage()).showAndWait();
                }
        );
    }

    public void eliminarFarmaceutaAsync (Farmaceuta farmaceuta)
    {
        operacionEnProgreso = true;
        progressFarmaceutas.setVisible(true);
        btnBorrarFarmaceuta.setDisable(true);

        Async.run(() -> {
                    try {
                        boolean eliminado = gestor.eliminar(farmaceuta.getId());
                        return eliminado;
                    } catch (Exception e) {
                        throw new RuntimeException("Error al eliminar farmacéutico: " + e.getMessage());
                    }
                },
                resultado -> {
                    operacionEnProgreso = false;
                    progressFarmaceutas.setVisible(false);
                    btnBorrarFarmaceuta.setDisable(false);

                    if (resultado) {
                        cargarFarmaceutasAsync(); // Recargar tabla
                        limpiarCamposFarmaceutas();
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Exito al eliminar");
                        alert.setHeaderText(null);
                        alert.setContentText("Se pudo eliminar el farmacéutico correctamente.");
                        alert.showAndWait();

                    } else {
                        new Alert(Alert.AlertType.ERROR, "No se pudo eliminar al farmacéutico: ").showAndWait();
                    }

                },
                error -> {
                    operacionEnProgreso = false;
                    progressFarmaceutas.setVisible(false);
                    btnBorrarFarmaceuta.setDisable(false);
                    new Alert(Alert.AlertType.ERROR, "No se pudo eliminar al farmacéutico: ").showAndWait();
                }
        );
    }
}