package org.example.proyectohospital.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.proyectohospital.Logica.GestorPacientes;
import org.example.proyectohospital.Logica.Hospital;
import org.example.proyectohospital.Modelo.Paciente;

import java.awt.event.ActionEvent;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class TabPacientesEnAdminController {
    @FXML private TextField txtIdPaciente;
    @FXML private TextField txtNombrePaciente;
    @FXML private TextField txtNumeroTelefonoPaciente;
    @FXML private TextField txtBscarPaciente;
    @FXML private DatePicker dtpFecaNacimientoPaciente;
    @FXML private TableView<Paciente> tbvResultadoPaciente;
    @FXML private TableColumn<Paciente,String> colIdPaciente;
    @FXML private TableColumn<Paciente,String> colNombrePaciente;
    @FXML private TableColumn<Paciente,Integer> colTelefonoPaciente;
    @FXML private TableColumn<Paciente, LocalDate> colFecaNacimientoPaciente;
    @FXML private TableColumn<Paciente, LocalDate> colBuscarPaciente;
    @FXML private Button btnGuardarPaciente;
    @FXML private Button btnLimpiarCampos;
    @FXML private Button btnBorrarPaciente;
    @FXML private Button btnModificarPaciente;
    @FXML private Button btnMostrarTodosLosPacientes;
    @FXML private Button btnBuscarPaciente;


    private final GestorPacientes gestor = Hospital.getInstance().getPacientes();
    private ObservableList<Paciente> listaPacientes = FXCollections.observableArrayList();

    public void initialize(URL url, ResourceBundle resourceBundle) {
        colIdPaciente.setCellValueFactory(new PropertyValueFactory<>("idPaciente"));
        colNombrePaciente.setCellValueFactory(new PropertyValueFactory<>("nombrePaciente"));
        colFecaNacimientoPaciente.setCellValueFactory(new PropertyValueFactory<>("fechaNacimiento"));
        colTelefonoPaciente.setCellValueFactory(new PropertyValueFactory<>("telefonoPaciente"));
        tbvResultadoPaciente.setItems(listaPacientes);
    }

    @FXML
    public void mostrarTodosLosPacientes() {
        listaPacientes.setAll(gestor.getPacientes());
    }

    @FXML
    private void limpiarCamposPaciente() {
        txtIdPaciente.clear();
        txtNombrePaciente.clear();
        txtNumeroTelefonoPaciente.clear();
        dtpFecaNacimientoPaciente.setValue(null);
        txtBscarPaciente.clear();
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
        String idPaciente = txtIdPaciente.getText();
        String nombrePaciente = txtNombrePaciente.getText();
        String numeroTelefonoPaciente = txtNumeroTelefonoPaciente.getText();
        LocalDate fechaNacimientoPaciente = dtpFecaNacimientoPaciente.getValue();

        if(idPaciente.isEmpty() || nombrePaciente.isEmpty() || numeroTelefonoPaciente.isEmpty()) {
            mostrarAlerta("Error","Debe llenar todos los campos obligatorios");
        }

        int telefonoPaciente;
        try {
            telefonoPaciente = Integer.parseInt(numeroTelefonoPaciente);
        }catch(NumberFormatException e){
            mostrarAlerta("Error","Debe ingresar un numero de telefono");
            return;
        }

        Paciente nuevo = new Paciente(telefonoPaciente,fechaNacimientoPaciente,nombrePaciente,idPaciente);
        boolean insertado = gestor.insertarPaciente(nuevo,false);

        if(!insertado){
            mostrarAlerta("Error","No se logro guardar el paciente");
            return;
        }

        mostrarTodosLosPacientes();
        limpiarCamposPaciente();

    }
    @FXML
    public void borrarPaciente(javafx.event.ActionEvent actionEvent) {
        Paciente seleccionado = tbvResultadoPaciente.getSelectionModel().getSelectedItem();
        if(seleccionado == null){
            mostrarAlerta("Error", "Debe seleccionar un paciente");
            return;
        }

        gestor.eliminar(seleccionado.getId());
        mostrarTodosLosPacientes();
    }

    @FXML
    public void buscarPaciente(javafx.event.ActionEvent actionEvent) {
        String criterio = txtIdPaciente.getText().toLowerCase();

        if(criterio.isEmpty()){
            mostrarAlerta("Error", "Debe ingresar un numero de telefono valido");
            return;
        }

        List<Paciente> resultados = gestor.getPacientes().stream().filter(p -> p.getNombre().toLowerCase().contains(criterio) || p.getId().toLowerCase().contains(criterio)).collect(Collectors.toList());
        listaPacientes.setAll(resultados);
    }

    @FXML
    public void modificarPaciente(javafx.event.ActionEvent actionEvent) {
        Paciente seleccionado = tbvResultadoPaciente.getSelectionModel().getSelectedItem();
        if(seleccionado == null){
            mostrarAlerta("Error", "Debe seleccionar un paciente");
            return;
        }

        seleccionado.setId(txtIdPaciente.getText());
        seleccionado.setNombre(txtNombrePaciente.getText());
        seleccionado.setFechaNacimiento(dtpFecaNacimientoPaciente.getValue());

        try{
            seleccionado.setTelefono(Integer.parseInt(txtNumeroTelefonoPaciente.getText()));
        }catch(NumberFormatException e){
            mostrarAlerta("Error","Debe ingresar un numero de telefono valido");
            return;
        }
        mostrarTodosLosPacientes();
        limpiarCamposPaciente();
    }
}
