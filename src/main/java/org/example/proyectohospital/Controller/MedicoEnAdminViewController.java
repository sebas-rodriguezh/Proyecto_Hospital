package org.example.proyectohospital.Controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.example.proyectohospital.Logica.GestorPacientes;
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

    public MedicoEnAdminViewController() {

    }

    @FXML
    public void mostrarTodosLosMedicos() {
        try {
            List<Medico> medicos = gestor.obtenerPersonalPorTipo("Medico").stream().map(p-> (Medico)p).toList();
            listaMedicos.setAll(medicos);
        }
        catch (Exception e) {
            mostrarAlerta("Error","Error al cargar medicos." + e.getMessage());
        }
    }

    @FXML
    private void modificarMedico(ActionEvent actionEvent) {
        Medico seleccionado = tbvResultadoBusquedaMedico.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAlerta("Error", "Seleccione un medico");
            return;
        }

        try {
            String nombre = txtNombreMedico.getText().trim();
            String especialidad = txtEspecialidadMedico.getText().trim();

            if (nombre.isEmpty() || especialidad.isEmpty()) {
                mostrarAlerta("Error", "Debe de rellenar todos los campos.");
                return;
            }

            seleccionado.setNombre(nombre);
            seleccionado.setEspecialidad(especialidad);

            gestor.update(seleccionado);

            Medico medicoLogueado = Hospital.getInstance().getMedicoLogueado();
            if (medicoLogueado != null && medicoLogueado.getId().equals(seleccionado.getId())) {
                Hospital.getInstance().setMedicoLogueado(seleccionado);
            }

            mostrarTodosLosMedicos();
            limpiarCamposMedicos();
            mostrarAlerta("Éxito", "Médico modificado correctamente");

        } catch (Exception e) {
            mostrarAlerta("Error", "Error al modificar medico: " + e.getMessage());
            e.printStackTrace();
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
        Medico seleccionado = tbvResultadoBusquedaMedico.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Error", "Debe seleccionar un medico para borrar");
            return;
        }
        try {
            boolean eliminado = gestor.eliminar(seleccionado.getId());
            if (eliminado) {
                mostrarTodosLosMedicos();
                limpiarCamposMedicos();
                mostrarAlerta("Éxito", "Medico eliminado correctamente");

            } else {
                mostrarAlerta("Error", "No se pudo eliminar el medico");
            }
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
        try {
            String idMedico = txtIdMedico.getText();
            String nombreMedico = txtNombreMedico.getText();
            String especialidadMedico = txtEspecialidadMedico.getText();

            if(idMedico.isEmpty() || nombreMedico.isEmpty() || especialidadMedico.isEmpty()) {
                mostrarAlerta("Error","Debe llenar todos los campos obligatorios");
                return;
            }
            Personal nuevo = new Medico(nombreMedico, idMedico, idMedico, especialidadMedico);
            boolean respuestaPacientes = gestorPacientes.existeAlguienConEseID(nuevo.getId());
            boolean insertado = gestor.insertarPersonal(nuevo,respuestaPacientes);

            if(insertado) {
                mostrarTodosLosMedicos();
                limpiarCamposMedicos();
                mostrarAlerta(" Éxito","Médico guardado correctamente.");
            }

            else
            {
                mostrarAlerta("Error", "Ya existe un usuario con ese ID");
            }

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
        mostrarTodosLosMedicos();
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
}