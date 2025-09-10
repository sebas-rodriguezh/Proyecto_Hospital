package org.example.proyectohospital.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.proyectohospital.Logica.GestorPacientes;
import org.example.proyectohospital.Logica.GestorPersonal;
import org.example.proyectohospital.Logica.Hospital;
import org.example.proyectohospital.Modelo.Paciente;

import java.awt.event.ActionEvent;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class TabPacientesEnAdminController implements Initializable {
    @FXML private TextField txtIdPaciente;
    @FXML private TextField txtNombrePaciente;
    @FXML private TextField txtNumeroTelefonoPaciente;
    @FXML private TextField txtBuscarPaciente;
    @FXML private DatePicker dtpFechaNacimientoPaciente;
    @FXML private TableView<Paciente> tbvResultadoBPaciente;
    @FXML private TableColumn<Paciente,String> colIDPaciente;
    @FXML private TableColumn<Paciente,String> colNombrePaciente;
    @FXML private TableColumn<Paciente,Integer> colTelefonoPaciente;
    @FXML private TableColumn <Paciente, LocalDate> colFechaNacimientoPaciente;
    @FXML private Button btnGuardarPaciente;
    @FXML private Button btnLimpiarCampos;
    @FXML private Button btnBorrarPaciente;
    @FXML private Button btnModificarPaciente;
    @FXML private Button btnMostrarTodosLosPacientes;
    @FXML private Button btnBuscarPaciente;


    private final GestorPacientes gestor = Hospital.getInstance().getPacientes();
    private  final GestorPersonal gestorPersonal = Hospital.getInstance().getPersonal(); //Se usa para la respuesta Booleana.
    private ObservableList<Paciente> listaPacientes = FXCollections.observableArrayList();

    public void initialize(URL url, ResourceBundle resourceBundle) {
        colIDPaciente.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombrePaciente.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colFechaNacimientoPaciente.setCellValueFactory(new PropertyValueFactory<>("fechaNacimiento"));
        colTelefonoPaciente.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        tbvResultadoBPaciente.setItems(listaPacientes);

        tbvResultadoBPaciente.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        configurarCampoId(false);
                        llenarCamposConPaciente(newValue);
                    } else {
                        configurarCampoId(true);
                    }
                }
        );
        configurarCampoId(true);
        mostrarTodosLosPacientes();
    }

    @FXML
    public void mostrarTodosLosPacientes() {
        try {
            listaPacientes.setAll(gestor.getPacientes());
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al cargar medicamentos: " + e.getMessage());
        }
    }

    @FXML
    private void limpiarCamposPaciente() {
        txtIdPaciente.clear();
        txtNombrePaciente.clear();
        txtNumeroTelefonoPaciente.clear();
        dtpFechaNacimientoPaciente.setValue(null);
        txtBuscarPaciente.clear();
        tbvResultadoBPaciente.getSelectionModel().clearSelection();
        configurarCampoId(true);
    }

    private void mostrarAlerta(String titulo, String mensaje){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }


    @FXML
    public void guardarPaciente(javafx.event.ActionEvent actionEvent) {
        String idPaciente = txtIdPaciente.getText().trim();
        String nombrePaciente = txtNombrePaciente.getText().trim();
        String numeroTelefonoPaciente = txtNumeroTelefonoPaciente.getText().trim();
        LocalDate fechaNacimientoPaciente = dtpFechaNacimientoPaciente.getValue();

        if(idPaciente.isEmpty() || nombrePaciente.isEmpty() || numeroTelefonoPaciente.isEmpty() || fechaNacimientoPaciente == null) {
            mostrarAlerta("Error","Debe llenar todos los campos obligatorios");
            return;
        }

        if (idPaciente.length() > 9 || numeroTelefonoPaciente.length() > 9) {
            mostrarAlerta("Error","Debe llenar todos los campos obligatorios");
            return;
        }
        int telefonoPaciente = 0;
        try {
            try
            {
                telefonoPaciente = Integer.parseInt(numeroTelefonoPaciente);
            }
            catch(NumberFormatException e)
            {
                mostrarAlerta("Error","Debe ingresar un numero de telefono con longitud de 8.");
                return;
            }

            Paciente nuevo = new Paciente(telefonoPaciente, fechaNacimientoPaciente, nombrePaciente, idPaciente);
            boolean respuestaPersonal = gestorPersonal.existePersonalConEseID(nuevo.getId());
            boolean insertado = gestor.insertarPaciente(nuevo, respuestaPersonal);

            if (insertado) {
                mostrarTodosLosPacientes();
                limpiarCamposPaciente();
                mostrarAlerta(" Éxito","Paciente guardado correctamente.");
            } else {
                mostrarAlerta("Error", "Ya existe un paciente con ese ID");
            }
        } catch (Exception e) {
            mostrarAlerta("Error","Error al guardar el paciente.");
        }
    }



    @FXML
    public void borrarPaciente(javafx.event.ActionEvent actionEvent) {
        Paciente seleccionado = tbvResultadoBPaciente.getSelectionModel().getSelectedItem();
        if(seleccionado == null){
            mostrarAlerta("Error", "Debe seleccionar un paciente");
            return;
        }

        try {
            boolean eliminado = gestor.eliminar(seleccionado.getId());
            if (eliminado) {
                mostrarTodosLosPacientes();
                mostrarAlerta("Éxito", "Paciente eliminado correctamente");
            } else {
                mostrarAlerta("Error", "No se pudo eliminar el paciente");
            }

        } catch (Exception e) {
            mostrarAlerta("Error", "Error al eliminar paciente: " + e.getMessage());
        }
    }

    @FXML
    public void buscarPaciente(javafx.event.ActionEvent actionEvent) {
        String criterio = txtBuscarPaciente.getText().toLowerCase().trim();

        if(criterio.isEmpty()) {
            mostrarTodosLosPacientes();
            return;
        }
        try {
            List<Paciente> resultados = gestor.getPacientes().stream().filter(p -> p.getNombre().toLowerCase().contains(criterio) || p.getId().toLowerCase().contains(criterio)).collect(Collectors.toList());
            listaPacientes.setAll(resultados);
        }
        catch (Exception e) {
            mostrarAlerta("Error", "Error al buscar el paciente: " + e.getMessage());
        }
    }

    @FXML
    public void modificarPaciente(javafx.event.ActionEvent actionEvent) {
        Paciente seleccionado = tbvResultadoBPaciente.getSelectionModel().getSelectedItem();
        if(seleccionado == null){
            mostrarAlerta("Error", "Debe seleccionar un paciente");
            return;
        }

        seleccionado.setNombre(txtNombrePaciente.getText());
        seleccionado.setFechaNacimiento(dtpFechaNacimientoPaciente.getValue());

        try
        {
            seleccionado.setTelefono(Integer.parseInt(txtNumeroTelefonoPaciente.getText()));
        }
        catch(NumberFormatException e)
        {
            mostrarAlerta("Error","Debe ingresar un numero de telefono valido");
            return;
        }

        try {
            gestor.update(seleccionado);
            mostrarAlerta("Éxito", "Paciente modificado correctamente");
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al modificar: " + e.getMessage());
        }

        mostrarTodosLosPacientes();
        limpiarCamposPaciente();
    }


    private void configurarCampoId(boolean editable) {
        txtIdPaciente.setEditable(editable);
        txtIdPaciente.setFocusTraversable(editable);

        if (editable) {
            txtIdPaciente.setStyle("-fx-background-color: white; -fx-text-fill: black;");
        } else {
            txtIdPaciente.setStyle("-fx-background-color: #f0f0f0; -fx-text-fill: #666;");
        }
    }

    private void llenarCamposConPaciente(Paciente paciente) {
        txtIdPaciente.setText(paciente.getId());
        txtNombrePaciente.setText(paciente.getNombre());
        txtNumeroTelefonoPaciente.setText(String.valueOf(paciente.getTelefono()));
        dtpFechaNacimientoPaciente.setValue(paciente.getFechaNacimiento());
    }


}
