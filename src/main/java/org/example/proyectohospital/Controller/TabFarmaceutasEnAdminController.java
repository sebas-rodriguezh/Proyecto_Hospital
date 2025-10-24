package org.example.proyectohospital.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
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
        mostrarTodosLosFarmaceutas();
    }


    @FXML
    public void mostrarTodosLosFarmaceutas() {
        try {
//            List<Farmaceuta> farmaceutas = gestor.obtenerPersonalPorTipo("Farmaceuta").stream().map(p->(Farmaceuta)p).collect(Collectors.toList());
//            listaFarmaceuta.setAll(farmaceutas);
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
                mostrarAlerta("Error", "Seleccione un medico ");
                return;
            }

            String id = txtIdFarmaceuta.getText().trim();
            String nombre = txtNombreFarmaceuta.getText().trim();

            if (id.isEmpty() || nombre.isEmpty()) {
                mostrarAlerta("Error", "Complete TODOS los campos");
                return;
            }

            String idOriginal = seleccionado.getId();
            seleccionado.setId(id);
            seleccionado.setNombre(nombre);
            seleccionado.setClave(id);

            modificarFarmaceutaAsync(seleccionado, idOriginal);


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
                        throw new RuntimeException("Error al guardar paciente: " + e.getMessage());
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
                        alert.setContentText("Se pudo insertar el paciente correctamente.");
                        alert.showAndWait();

                    } else {
                        new Alert(Alert.AlertType.ERROR, "Ya existe un paciente con ese ID").showAndWait();
                    }
                },

                error -> {
                    operacionEnProgreso = false;
                    progressFarmaceutas.setVisible(false);
                    btnGuardarFarmaceuta.setDisable(false);
                    new Alert(Alert.AlertType.ERROR, "Ya existe un paciente con ese ID: " + error.getMessage()).showAndWait();
                }
        );
    }

    public void modificarFarmaceutaAsync(Farmaceuta farmaceuta, String idOriginal) {
        operacionEnProgreso = true;
        progressFarmaceutas.setVisible(true);
        btnModificarFarmaceuta.setDisable(true);

        Async.run(() -> {
                    try {
                        if (gestor.existePersonalConEseID(farmaceuta.getId()) || gestorPacientes.existeAlguienConEseID(farmaceuta.getId())) {
                            return false;
                        } else {
                            gestor.update(farmaceuta, idOriginal);
                            return true;
                        }
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
                        new Alert(Alert.AlertType.ERROR, "Ya existe un usuario en el sistema con ese ID").showAndWait();
                    }
                },
                error -> {
                    operacionEnProgreso = false;
                    progressFarmaceutas.setVisible(false);
                    btnModificarFarmaceuta.setDisable(false);
                    new Alert(Alert.AlertType.ERROR, "Error al modificar el farmaceuta.").showAndWait();
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
                        throw new RuntimeException("Error al eliminar paciente: " + e.getMessage());
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
                        alert.setContentText("Se pudo eliminar el paciente correctamente.");
                        alert.showAndWait();

                    } else {
                        new Alert(Alert.AlertType.ERROR, "No se pudo eliminar al paciente: ").showAndWait();
                    }

                },
                error -> {
                    operacionEnProgreso = false;
                    progressFarmaceutas.setVisible(false);
                    btnBorrarFarmaceuta.setDisable(false);
                    new Alert(Alert.AlertType.ERROR, "No se pudo eliminar al paciente: ").showAndWait();
                }
        );
    }



}
