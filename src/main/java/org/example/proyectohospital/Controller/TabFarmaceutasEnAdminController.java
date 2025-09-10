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
import org.example.proyectohospital.Modelo.Personal;
import org.example.proyectohospital.Logica.GestorPersonal;


import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class TabFarmaceutasEnAdminController implements Initializable {
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colNombreFarmaceuta.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colIDFarmaceuta.setCellValueFactory(new PropertyValueFactory<>("id"));
        tbvResultadoBusquedaFarmaceuta.setItems(listaFarmaceuta); //tbv que ve lo que hace la listaObservable.
        mostrarTodosLosFarmaceutas(); //Acá esa lista se carga.
    }


    @FXML
    public void mostrarTodosLosFarmaceutas() {
        try {
            List<Farmaceuta> farmaceutas = gestor.obtenerPersonalPorTipo("Farmaceuta").stream().map(p->(Farmaceuta)p).collect(Collectors.toList());
            listaFarmaceuta.setAll(farmaceutas);
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo cargar la información de los farmaceutas.");
        }
    }

    @FXML
    public void modificarFarmaceuta(ActionEvent actionEvent) {
        Farmaceuta seleccionado = tbvResultadoBusquedaFarmaceuta.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Error", "Seleccione un medico ");
            return;
        }

        try
        {
            String id = txtIdFarmaceuta.getText().trim();
            String nombre = txtNombreFarmaceuta.getText().trim();

            if (id.isEmpty() || nombre.isEmpty()) {
                mostrarAlerta("Error", "Complete TODOS los campos");
                return;
            }

            try {
                String idOriginal = seleccionado.getId();

                seleccionado.setId(id);
                seleccionado.setNombre(nombre);
                seleccionado.setClave(id);


                //Acá se deben de validar las modificaciones.
                if (gestor.existePersonalConEseID(id) || gestorPacientes.existeAlguienConEseID(id)) {
                    mostrarAlerta("Error", "El ID nuevo, ya está registrado en el sistema.");
                    return;
                } else {
                    gestor.update(seleccionado, idOriginal);
                    mostrarTodosLosFarmaceutas();
                    limpiarCamposFarmaceutas();
                    mostrarAlerta("Éxito", "Medicamento modificado correctamente");
                }

            } catch (Exception e) {
                mostrarAlerta("Error", "Error al modificar medicamento: " + e.getMessage());

            }
        }
        catch (Exception e)
        {
            mostrarAlerta("Error", "No se pudo modificar la información del farmaceutas.");
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
        Farmaceuta seleccionado = tbvResultadoBusquedaFarmaceuta.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Error", "Debe seleccionar un farmaceuta para borrar");
            return;
        }
        try
        {
            boolean eliminado = gestor.eliminar(seleccionado.getId());
            if (eliminado) {
                mostrarTodosLosFarmaceutas(); //Se muestra nuevamente todo menos el farmaceuta borrado.
                mostrarAlerta("Éxito", "Medicamento eliminado correctamente");
            } else {
                mostrarAlerta("Error", "No se pudo eliminar el farmaceuta");
            }
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
        try
        {
            String idFarmaceuta = txtIdFarmaceuta.getText();
            String nombreFarmaceuta = txtNombreFarmaceuta.getText();

            if(idFarmaceuta.isEmpty() || nombreFarmaceuta.isEmpty()) {
                mostrarAlerta("Error", "Debe llenar todos los campos obligatorios");
                return;
            }

            Personal nuevo = new Farmaceuta(nombreFarmaceuta, idFarmaceuta, idFarmaceuta);
            boolean respuestaPacientes = gestorPacientes.existeAlguienConEseID(nuevo.getId());
            boolean insertado = gestor.insertarPersonal(nuevo,respuestaPacientes);

            if(insertado) {
                mostrarTodosLosFarmaceutas();
                limpiarCamposFarmaceutas();
                mostrarAlerta(" Éxito","Paciente guardado correctamente.");
            }
            else
            {
                mostrarAlerta("Error", "Ya existe un usuario con ese ID");
            }
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
}
