package org.example.proyectohospital.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
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
    @FXML private TextField txtBuscarFarmaceuta;
    @FXML private Button btnBuscarFarmaceuta;
    @FXML private TableColumn <Farmaceuta, String> colNombreFarmaceuta;
    @FXML private TableColumn <Farmaceuta, String> colIDFarmaceuta;
    @FXML private TableView <Farmaceuta> tbvResultadoBusquedaFarmaceuta;
    @FXML private Button btnBorrarFarmaceuta;
    @FXML private Button btnLimpiarCampos;
    @FXML private Button btnGuardarFarmaceuta;
    @FXML private TextField txtNombreFarmaceuta;
    @FXML private TextField txtIdFarmaceuta;

    private final GestorPersonal gestor = Hospital.getInstance().getGestorPersonal();
    private ObservableList<Farmaceuta> listaFarmaceuta = FXCollections.observableArrayList();

    @FXML
    public void mostrarTodosLosFarmaceutas() {
        List<Farmaceuta> farmaceutas = gestor.obtenerPersonalPorTipo("Farmaceuta").stream().map(p->(Farmaceuta)p).collect(Collectors.toList());
        listaFarmaceuta.setAll(farmaceutas);
    }

    @FXML
    public void modificarFarmaceuta(ActionEvent actionEvent) {
        Farmaceuta seleccionado = tbvResultadoBusquedaFarmaceuta.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            mostrarAlerta("Error", "Seleccione un medico ");
        }

        seleccionado.setNombre(txtNombreFarmaceuta.getText());

        limpiarCamposFarmaceutas();
    }


    @FXML
    public void buscarFarmaceuta(ActionEvent actionEvent) {
        String texto = txtBuscarFarmaceuta.getText();

        if (texto.isEmpty()) {
            mostrarTodosLosFarmaceutas();
        }
        List<Farmaceuta> resultados = gestor.obtenerPersonalPorTipo("Farmaceuta").stream().map(p->(Farmaceuta)p).filter(f->f.getId().toLowerCase().contains(texto)||f.getNombre().toLowerCase().contains(texto)).collect(Collectors.toList());

        listaFarmaceuta.setAll(resultados);
    }


    @FXML
    public void borrarFarmaceuta(ActionEvent actionEvent) {
        Farmaceuta seleccionado = tbvResultadoBusquedaFarmaceuta.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            mostrarAlerta("Error", "Debe seleccionar un medico para borrar");
            return;
        }
        gestor.eliminar(seleccionado.getId());
    }

    @FXML
    public void limpiarCamposFarmaceutas() {
        txtNombreFarmaceuta.clear();
        txtIdFarmaceuta.clear();
        txtBuscarFarmaceuta.clear();
    }

    @FXML
    public void guardarFarmaceuta(ActionEvent actionEvent) {
        String idFarmaceuta = txtIdFarmaceuta.getText();
        String nombreFarmaceuta = txtNombreFarmaceuta.getText();

        if(idFarmaceuta.isEmpty() || nombreFarmaceuta.isEmpty()) {
            mostrarAlerta("Error", "Debe llenar todos los campos obligatorios");
            return;
        }

        Farmaceuta nuevo = new Farmaceuta(nombreFarmaceuta, idFarmaceuta, idFarmaceuta);
        boolean insertado = gestor.insertarPersonal(nuevo,false);

        if(!insertado){
            mostrarAlerta("Error", "Ya existe un farmaceuta con ese numero de identificación");
            return;
        }

        mostrarTodosLosFarmaceutas();
        limpiarCamposFarmaceutas();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colNombreFarmaceuta.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colIDFarmaceuta.setCellValueFactory(new PropertyValueFactory<>("id"));

        // Cargar todos al inicio
        tbvResultadoBusquedaFarmaceuta.setItems(listaFarmaceuta);

        mostrarTodosLosFarmaceutas();
    }

    private void mostrarAlerta(String titulo, String mensaje){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
