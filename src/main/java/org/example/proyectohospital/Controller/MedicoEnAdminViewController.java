package org.example.proyectohospital.Controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.proyectohospital.Logica.GestorPacientes;
import org.example.proyectohospital.Logica.GestorPersonal;
import org.example.proyectohospital.Logica.Hospital;
import org.example.proyectohospital.Modelo.Farmaceuta;
import org.example.proyectohospital.Modelo.Medico;
import org.example.proyectohospital.Modelo.Personal;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class MedicoEnAdminViewController implements Initializable {
    @FXML private Button btnAbrirChat;
    @FXML private ProgressIndicator progressMedicos;
    @FXML private Button btnMostrarTodosLosMedicos;
    @FXML private Button btnModificarMedico;
    @FXML private Button btnBuscarMedico;
    @FXML private Button btnBorrarMedico;
    @FXML private Button btnLimpiarCampos;
    @FXML private Button btnGuardarMedico;
    @FXML private TextField txtBuscarMedico;
    @FXML private TableView<Medico> tbvResultadoBusquedaMedico;
    @FXML private TableColumn<Medico, String> colIDMedico;
    @FXML private TableColumn<Medico, String> colNombreMedico;
    @FXML private TableColumn<Medico, String> colEspecialidadMedico;
    @FXML private TextField txtEspecialidadMedico;
    @FXML private TextField txtNombreMedico;
    @FXML private TextField txtIdMedico;

    private final GestorPersonal gestor = Hospital.getInstance().getGestorPersonal();
    private final GestorPacientes gestorPacientes = Hospital.getInstance().getGestorPacientes();
    private final ObservableList<Medico> listaMedicos = FXCollections.observableArrayList();

    //Hilos.
    private boolean operacionEnProgreso = false;

    public MedicoEnAdminViewController() {

    }

    @FXML
    public void mostrarTodosLosMedicos() {
        try {
//            List<Medico> medicos = gestor.obtenerPersonalPorTipo("Medico").stream().map(p-> (Medico)p).toList();
//            listaMedicos.setAll(medicos);
            cargarMedicosAsync();
        }
        catch (Exception e) {
            mostrarAlerta("Error","Error al cargar medicos." + e.getMessage());
        }
    }

    @FXML
    private void modificarMedico(ActionEvent actionEvent) {
        if (operacionEnProgreso) {
            return;
        }
        try
        {
            Medico seleccionado = tbvResultadoBusquedaMedico.getSelectionModel().getSelectedItem();

            if (seleccionado == null) {
                mostrarAlerta("Error", "Seleccione un medico");
                return;
            }

            String nombre = txtNombreMedico.getText().trim();
            String especialidad = txtEspecialidadMedico.getText().trim();

            if (nombre.isEmpty() || especialidad.isEmpty()) {
                mostrarAlerta("Error", "Debe de rellenar todos los campos.");
                return;
            }

            seleccionado.setNombre(nombre);
            seleccionado.setEspecialidad(especialidad);
            modificarMedicoAsync(seleccionado);


        } catch (Exception e) {
            mostrarAlerta("Error", "Error al modificar medico: " + e.getMessage());
        }
    }

    @FXML
    private void buscarMedico(ActionEvent actionEvent) {
        String criterio = txtBuscarMedico.getText().toLowerCase();
        if(criterio.isEmpty()) {
            mostrarAlerta("Error", "Ingrese un nombre o identificacion valido");
            return;
        }
        try
        {
            List<Medico> resultados = gestor.obtenerPersonalPorTipo("Medico").stream()
                    .map(p -> (Medico) p)
                    .filter(f -> {
                        String textoBusqueda = criterio.toLowerCase().trim();
                        return f.getId().toLowerCase().contains(textoBusqueda) ||
                                f.getNombre().toLowerCase().contains(textoBusqueda);
                    })
                    .collect(Collectors.toList());
            listaMedicos.setAll(resultados);
        }
        catch (Exception e) {
            mostrarAlerta("Error","Error al buscar medico." + e.getMessage());
        }
    }

    @FXML
    private void borrarMedico(ActionEvent actionEvent) {
        if (operacionEnProgreso) {
            return;
        }
        try
        {
            Medico seleccionado = tbvResultadoBusquedaMedico.getSelectionModel().getSelectedItem();
            if (seleccionado == null) {
                mostrarAlerta("Error", "Debe seleccionar un medico para borrar");
                return;
            }
            eliminarMedicoAsync(seleccionado);

        } catch (Exception e) {
            mostrarAlerta("Error", "Error al borrar medico: " + e.getMessage());
        }
    }

    @FXML
    private void limpiarCamposMedicos() {
        txtIdMedico.clear();
        txtNombreMedico.clear();
        txtEspecialidadMedico.clear();
        txtBuscarMedico.clear();
        tbvResultadoBusquedaMedico.getSelectionModel().clearSelection();
        configurarCampoId(true);
    }

    @FXML
    private void guardarMedico(ActionEvent actionEvent) {
        if (operacionEnProgreso) {
            return;
        }

        try {
            String idMedico = txtIdMedico.getText();
            String nombreMedico = txtNombreMedico.getText();
            String especialidadMedico = txtEspecialidadMedico.getText();

            if(idMedico.isEmpty() || nombreMedico.isEmpty() || especialidadMedico.isEmpty()) {
                mostrarAlerta("Error","Debe llenar todos los campos obligatorios");
                return;
            }
            Personal nuevo = new Medico(nombreMedico, idMedico, idMedico, especialidadMedico);
            guardarMedicoAsync(nuevo);
        }
        catch (Exception e) {
            mostrarAlerta("Error","No se logro insertar el medico");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colIDMedico.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getId()));

        colNombreMedico.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getNombre()));

        colEspecialidadMedico.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getEspecialidad()));

        tbvResultadoBusquedaMedico.setItems(listaMedicos);

        tbvResultadoBusquedaMedico.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        configurarCampoId(false);
                        llenarCamposConMedico(newValue);
                    } else {
                        configurarCampoId(true);
                    }
                }
        );

        configurarCampoId(true);
        //mostrarTodosLosMedicos();
        cargarMedicosAsync();
    }

    private void configurarCampoId(boolean editable) {
        txtIdMedico.setEditable(editable);
        txtIdMedico.setFocusTraversable(editable);

        if (editable) {
            txtIdMedico.setStyle("-fx-background-color: white; -fx-text-fill: black;");
        } else {
            txtIdMedico.setStyle("-fx-background-color: #f0f0f0; -fx-text-fill: #666;");
        }
    }

    private void llenarCamposConMedico(Medico medico) {
        txtIdMedico.setText(medico.getId());
        txtNombreMedico.setText(medico.getNombre());
        txtEspecialidadMedico.setText(medico.getEspecialidad());
    }

    private void mostrarAlerta(String titulo, String mensaje){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
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


    //Métodos para hilos (Async).

    public void cargarMedicosAsync() {
        if (operacionEnProgreso) {
            return;
        }

        operacionEnProgreso = true;
        progressMedicos.setVisible(true);

        if (btnMostrarTodosLosMedicos != null) {
            btnMostrarTodosLosMedicos.setDisable(true);
        }

        Async.run(
                () -> {
                    try {
                        List<Medico> medicos = gestor.obtenerPersonalPorTipo("Medico").stream()
                                .map(p -> (Medico) p)
                                .collect(Collectors.toList());
                        return medicos;
                    } catch (Exception e) {
                        throw new RuntimeException("Error al cargar médicos: " + e.getMessage());
                    }
                },
                listaMedicosCargados -> {
                    operacionEnProgreso = false;
                    progressMedicos.setVisible(false);

                    if (btnMostrarTodosLosMedicos != null) {
                        btnMostrarTodosLosMedicos.setDisable(false);
                    }
                    listaMedicos.setAll(listaMedicosCargados);
                },
                error -> {
                    operacionEnProgreso = false;
                    progressMedicos.setVisible(false);

                    if (btnMostrarTodosLosMedicos != null) {
                        btnMostrarTodosLosMedicos.setDisable(false);
                    }
                    mostrarAlerta("Error", "No se pudieron cargar los médicos: " + error.getMessage());
                }
        );
    }

    public void guardarMedicoAsync(Personal medico)
    {
        operacionEnProgreso = true;
        progressMedicos.setVisible(true);
        btnGuardarMedico.setDisable(true);

        Async.run(() -> {
                    try
                    {
                        boolean respuestaPacientes = gestorPacientes.existeAlguienConEseID(medico.getId());
                        boolean insertado = gestor.insertarPersonal(medico, respuestaPacientes);
                        return insertado;
                    }

                    catch (Exception e)
                    {
                        throw new RuntimeException("Error al guardar médico: " + e.getMessage());
                    }
                },
                resultado -> {
                    operacionEnProgreso = false;
                    progressMedicos.setVisible(false);
                    btnGuardarMedico.setDisable(false);

                    if (resultado)
                    {
                        cargarMedicosAsync(); //RECARGO LA TABLA.
                        limpiarCamposMedicos();
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Exito al guardar");
                        alert.setHeaderText(null);
                        alert.setContentText("Se pudo insertar el medico correctamente.");
                        alert.showAndWait();

                    } else {
                        new Alert(Alert.AlertType.ERROR, "Ya existe un usuario con ese ID").showAndWait();
                    }
                },

                error -> {
                    operacionEnProgreso = false;
                    progressMedicos.setVisible(false);
                    btnGuardarMedico.setDisable(false);
                    new Alert(Alert.AlertType.ERROR, "Ya existe un usuario con ese ID: " + error.getMessage()).showAndWait();
                }
        );
    }

    public void modificarMedicoAsync(Medico medico) {
        operacionEnProgreso = true;
        progressMedicos.setVisible(true);
        btnModificarMedico.setDisable(true);

        Async.run(() -> {
                    try {
                        gestor.update(medico);

                        Medico medicoLogueado = Hospital.getInstance().getMedicoLogueado();
                        if (medicoLogueado != null && medicoLogueado.getId().equals(medico.getId())) {
                            Hospital.getInstance().setMedicoLogueado(medico);
                        }

                        return medico;
                    } catch (Exception e) {
                        throw new RuntimeException("Error al modificar médico: " + e.getMessage());
                    }
                },
                resultado -> {
                    operacionEnProgreso = false;
                    progressMedicos.setVisible(false);
                    btnModificarMedico.setDisable(false);

                    cargarMedicosAsync();
                    limpiarCamposMedicos();

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Exito al modificar");
                    alert.setHeaderText(null);
                    alert.setContentText("Se pudo modificar al médico correctamente.");
                    alert.showAndWait();

                },
                error -> {
                    operacionEnProgreso = false;
                    progressMedicos.setVisible(false);
                    btnModificarMedico.setDisable(false);
                    new Alert(Alert.AlertType.ERROR, "No se pudo modificar al médico: " + error.getMessage()).showAndWait();
                }
        );
    }

    public void eliminarMedicoAsync (Medico medico)
    {
        operacionEnProgreso = true;
        progressMedicos.setVisible(true);
        btnBorrarMedico.setDisable(true);

        Async.run(() -> {
                    try {
                        boolean eliminado = gestor.eliminar(medico.getId());
                        return eliminado;
                    } catch (Exception e) {
                        throw new RuntimeException("Error al eliminar médico: " + e.getMessage());
                    }
                },
                resultado -> {
                    operacionEnProgreso = false;
                    progressMedicos.setVisible(false);
                    btnBorrarMedico.setDisable(false);

                    if (resultado) {
                        cargarMedicosAsync();
                        limpiarCamposMedicos();
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Exito al eliminar");
                        alert.setHeaderText(null);
                        alert.setContentText("Se pudo eliminar el médico correctamente.");
                        alert.showAndWait();

                    } else {
                        new Alert(Alert.AlertType.ERROR, "No se pudo eliminar al médico: ").showAndWait();
                    }

                },
                error -> {
                    operacionEnProgreso = false;
                    progressMedicos.setVisible(false);
                    btnBorrarMedico.setDisable(false);
                    new Alert(Alert.AlertType.ERROR, "No se pudo eliminar al médico: ").showAndWait();
                }
        );
    }

}