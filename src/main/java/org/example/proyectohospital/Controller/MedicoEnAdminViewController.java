package org.example.proyectohospital.Controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.example.proyectohospital.Logica.GestorPersonal;
import org.example.proyectohospital.Logica.Hospital;
import org.example.proyectohospital.Modelo.Medico;
import org.example.proyectohospital.Modelo.Personal;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class MedicoEnAdminViewController implements Initializable {
    @FXML private Button btnMostrarTodosLosMedicos;
    @FXML private Button btnModificarMedico;
    @FXML private TextField txtBuscarMedico;
    @FXML private Button btnBuscarMedico;
    @FXML private TableView<Medico> tbvResultadoBusquedaMedico;
    @FXML private TableColumn<Medico, String> colIDMedico;
    @FXML private TableColumn<Medico, String> colNombreMedico;
    @FXML private TableColumn<Medico, String> colEspecialidadMedico;
    @FXML private Button btnBorrarMedico;
    @FXML private Button btnLimpiarCampos;
    @FXML private Button btnGuardarMedico;
    @FXML private TextField txtEspecialidadMedico;
    @FXML private TextField txtNombreMedico;
    @FXML private TextField txtIdMedico;

    private GestorPersonal gestor = Hospital.getInstance().getGestorPersonal();
    private ObservableList<Medico> listaMedicos = FXCollections.observableArrayList();

    public MedicoEnAdminViewController() {

    }

    @FXML
    public void mostrarTodosLosMedicos() {
        List<Medico> medicos = gestor.obtenerPersonalPorTipo("Medico").stream().map(p-> (Medico)p).toList();
        listaMedicos.setAll(medicos);
    }

    @FXML
    private void modificarMedico(ActionEvent actionEvent) {
        Medico seleccionado = tbvResultadoBusquedaMedico.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            mostrarAlerta("Error", "Seleccione un medico ");
        }

        seleccionado.setNombre(txtNombreMedico.getText());
        seleccionado.setEspecialidad(txtEspecialidadMedico.getText());

        limpiarCamposMedicos();
    }

    @FXML
    private void buscarMedico(ActionEvent actionEvent) {
        String criterio = txtBuscarMedico.getText().toLowerCase();
        if(criterio.isEmpty()){
            mostrarAlerta("Error", "Ingrese un nombre o identificacion valido");
            return;
        }

        List<Medico> fitrados = gestor.obtenerPersonalPorTipo("Medico").stream().map(p->(Medico)p).filter(m-> m.getId().toLowerCase().contains(criterio) || m.getNombre().toLowerCase().contains(criterio)).collect(Collectors.toList());
        listaMedicos.setAll(fitrados);
    }


    @FXML
    private void borrarMedico(ActionEvent actionEvent) {
        Medico seleccionado = tbvResultadoBusquedaMedico.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            mostrarAlerta("Error", "Debe seleccionar un medico para borrar");
            return;
        }
        gestor.eliminar(seleccionado.getId());

    }

    @FXML
    private void limpiarCamposMedicos() {
        txtIdMedico.clear();
        txtNombreMedico.clear();
        txtEspecialidadMedico.clear();
        txtBuscarMedico.clear();
    }

    @FXML
    private void guardarMedico(ActionEvent actionEvent) {
        String idMedico = txtIdMedico.getText();
        String nombreMedico = txtNombreMedico.getText();
        String especialidadMedico = txtEspecialidadMedico.getText();

        if(idMedico.isEmpty() || nombreMedico.isEmpty() || especialidadMedico.isEmpty()) {
            mostrarAlerta("Error","Debe llenar todos los campos obligatorios");
            return;
        }

        Medico medico = new Medico(nombreMedico, idMedico, idMedico,especialidadMedico); //idmedico quedaria como la clave por defecto
        boolean nuevo = gestor.insertarPersonal(medico,false);

        if(!nuevo){
            mostrarAlerta("Error","No se logro insertar el medico");
            return;
        }

        limpiarCamposMedicos();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colIDMedico.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getId()));

        colNombreMedico.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getNombre()));

        colEspecialidadMedico.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getEspecialidad()));
    }

    private void mostrarAlerta(String titulo, String mensaje){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
