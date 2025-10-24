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
import javafx.application.Platform;
import org.example.proyectohospital.Controller.Async;

public class TabPacientesEnAdminController implements Initializable {
    @FXML private ProgressIndicator progressPacientes;
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
    private  final GestorPersonal gestorPersonal = Hospital.getInstance().getPersonal();
    private ObservableList<Paciente> listaPacientes = FXCollections.observableArrayList();

    //Son para evitar problemas de sobre-uso de hilos y multiples consultas a la bd. (Evita el multi-clic)
    private boolean cargaEnProgreso = false;
    private boolean operacionEnProgreso = false;


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
            //listaPacientes.setAll(gestor.getPacientes());
            cargarPacientesAsync();
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
    public void guardarPaciente(javafx.event.ActionEvent actionEvent)
    {
        if (operacionEnProgreso)
        {
            return;
        }

        try
        {
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
            try
            {
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
                //Llamar al Async de guardarPaciente.
                guardarPacienteAsync(nuevo);
            }

            catch (Exception e)
            {
                mostrarAlerta("Error","Error al guardar el paciente: " + e.getMessage());
            }

        } catch (Exception e) {
            mostrarAlerta("Error","Error al guardar el paciente."+ e.getMessage());
        }
    }



    @FXML
    public void borrarPaciente(javafx.event.ActionEvent actionEvent) {
        if (operacionEnProgreso)
        {
            return;
        }

        try
        {
            Paciente seleccionado = tbvResultadoBPaciente.getSelectionModel().getSelectedItem();

            if(seleccionado == null) {
                mostrarAlerta("Error", "Debe seleccionar un paciente");
                return;
            }
            //Llamo a Async
            eliminarPacienteAsync(seleccionado);

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

    public boolean validarCamposParaModificacion(String nombre, String telefono, LocalDate fechaNacimientoPaciente)
    {
        if (nombre.isEmpty() || telefono.isEmpty() || fechaNacimientoPaciente == null) {
            return false;
        }
        return true;
    }

    @FXML
    public void modificarPaciente(javafx.event.ActionEvent actionEvent) {

        if (operacionEnProgreso)
        {
            return;
        }

        try
        {
            Paciente seleccionado = tbvResultadoBPaciente.getSelectionModel().getSelectedItem();
            if(seleccionado == null) {
                mostrarAlerta("Error", "Debe seleccionar un paciente");
                return;
            }
            String nombre = txtNombrePaciente.getText().trim();
            String telefonoStr = txtNumeroTelefonoPaciente.getText().trim();
            LocalDate fechaNacimiento = dtpFechaNacimientoPaciente.getValue();

            if (!validarCamposParaModificacion(nombre, telefonoStr, fechaNacimiento))
            {
                mostrarAlerta("Error", "Todos los campos son obligatorios");
                return;
            }

            int telefono;
            try {
                telefono = Integer.parseInt(telefonoStr);
            } catch(NumberFormatException e) {
                mostrarAlerta("Error", "Teléfono debe ser numérico");
                return;
            }

            seleccionado.setNombre(nombre);
            seleccionado.setTelefono(telefono);
            seleccionado.setFechaNacimiento(fechaNacimiento);

            // Llamar async
            modificarPacienteAsync(seleccionado);

        } catch (Exception e)
        {
            mostrarAlerta("Error", "No se pudo modificar el paciente: " + e.getMessage());
        }
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

    //Métodos para hilos (Async).

    public void cargarPacientesAsync() {
        if (cargaEnProgreso) {
            return;
        }

        cargaEnProgreso = true;
        progressPacientes.setVisible(true);

        if (btnMostrarTodosLosPacientes != null) {
            btnMostrarTodosLosPacientes.setDisable(true);
        }

        Async.run(
                () -> {
                    try {
                        List<Paciente> pacientes = gestor.getPacientes();
                        return pacientes;
                    } catch (Exception e) {
                        throw new RuntimeException("Error al cargar pacientes: " + e.getMessage());
                    }
                },
                listaPacientesCargados -> {
                    cargaEnProgreso = false;
                    progressPacientes.setVisible(false);

                    if (btnMostrarTodosLosPacientes != null) {
                        btnMostrarTodosLosPacientes.setDisable(false);
                    }
                    listaPacientes.setAll(listaPacientesCargados);
                },
                error -> {
                    cargaEnProgreso = false;
                    progressPacientes.setVisible(false);

                    if (btnMostrarTodosLosPacientes != null) {
                        btnMostrarTodosLosPacientes.setDisable(false);
                    }
                }
        );
    }

    public void guardarPacienteAsync(Paciente nuevo)
    {
        operacionEnProgreso = true;
        progressPacientes.setVisible(true);
        btnGuardarPaciente.setDisable(true);

        Async.run(() -> {
            try
            {
                boolean respuestaPersonal = gestorPersonal.existePersonalConEseID(nuevo.getId());
                 boolean insertado = gestor.insertarPaciente(nuevo, respuestaPersonal);
                 return insertado;
            }

            catch (Exception e)
            {
                throw new RuntimeException("Error al guardar paciente: " + e.getMessage());
            }
                },
                resultado -> {
                    operacionEnProgreso = false;
                    progressPacientes.setVisible(false);
                    btnGuardarPaciente.setDisable(false);

                    if (resultado)
                    {
                        cargarPacientesAsync(); //RECARGO LA TABLA.
                        limpiarCamposPaciente();
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Exito al guardar");
                        alert.setHeaderText(null);
                        alert.setContentText("Se pudo insertar el paciente correctamente.");
                        alert.showAndWait();

                    } else {
                        new Alert(Alert.AlertType.ERROR, "Ya existe un paciente con ese ID").showAndWait();
                    }
                },

                error -> {
                    operacionEnProgreso = false;
                    progressPacientes.setVisible(false);
                    btnGuardarPaciente.setDisable(false);
                    new Alert(Alert.AlertType.ERROR, "Ya existe un paciente con ese ID: " + error.getMessage()).showAndWait();
                }
                );
    }

    public void modificarPacienteAsync(Paciente paciente)
    {
        operacionEnProgreso = true;
        progressPacientes.setVisible(true);
        btnGuardarPaciente.setDisable(true);

       Async.run(()-> {
           try
           {
               gestor.update(paciente);
               return paciente;
           }
           catch (Exception e)
           {
               throw new RuntimeException("Error al modificar paciente: " + e.getMessage());
           }
               },
               resultado -> {
                   operacionEnProgreso = false;
                   progressPacientes.setVisible(false);
                   btnGuardarPaciente.setDisable(false);
                   cargarPacientesAsync();
                   limpiarCamposPaciente();

                   //Se puede borrar luego, si no se quiere decir nada.

                   Alert alert = new Alert(Alert.AlertType.INFORMATION);
                   alert.setTitle("Exito al modificar");
                   alert.setHeaderText(null);
                   alert.setContentText("Se pudo modificar el paciente correctamente.");
                   alert.showAndWait();
               },
               error -> {
                   operacionEnProgreso = false;
                   progressPacientes.setVisible(false);
                   btnModificarPaciente.setDisable(false);
                   new Alert(Alert.AlertType.ERROR, "No se pudo modificar al paciente: " + error.getMessage()).showAndWait();
               }
               );
    }

    public void eliminarPacienteAsync (Paciente paciente)
    {
        operacionEnProgreso = true;
        progressPacientes.setVisible(true);
        btnBorrarPaciente.setDisable(true);

        Async.run(() -> {
            try {
                boolean eliminado = gestor.eliminar(paciente.getId());
                return eliminado;
            } catch (Exception e) {
                throw new RuntimeException("Error al eliminar paciente: " + e.getMessage());
            }
                },
            resultado -> {
                operacionEnProgreso = false;
                progressPacientes.setVisible(false);
                btnBorrarPaciente.setDisable(false);

                if (resultado) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Exito al eliminar");
                    alert.setHeaderText(null);
                    alert.setContentText("Se pudo eliminar el paciente correctamente.");
                    alert.showAndWait();
                    cargarPacientesAsync(); // Recargar tabla
                    limpiarCamposPaciente();
                } else {
                    new Alert(Alert.AlertType.ERROR, "No se pudo eliminar al paciente: ").showAndWait();
                }

            },
            error -> {
                operacionEnProgreso = false;
                progressPacientes.setVisible(false);
                btnBorrarPaciente.setDisable(false);
                new Alert(Alert.AlertType.ERROR, "No se pudo eliminar al paciente: ").showAndWait();
            }
                );
    }


}
