package org.example.proyectohospital.Controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.proyectohospital.Modelo.Farmaceuta;
import org.example.proyectohospital.Modelo.Personal;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

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

    //private Personal persona = new Farmaceuta("Luis","1111","1111");
    //private final GestorPersonal gestor = new GestorPersonal(new XMLPersonalRepository());

    @FXML
    public void mostrarTodosLosFarmaceutas(ActionEvent actionEvent) {
    }

    @FXML
    public void modificarFarmaceuta(ActionEvent actionEvent) {
    }


    @FXML
    public void buscarFarmaceuta(ActionEvent actionEvent) {
        String texto = txtBuscarFarmaceuta.getText();

        if (texto.isEmpty()) {
            //tbvResultadoBusquedaFarmaceuta.setItems(FXCollections.observableArrayList(gestor.obtenerSoloFarmaceutas()));
        } else {
            //List<Farmaceuta> resultados = gestor.buscarFarmaceutasPorNombre(texto);
            //tbvResultadoBusquedaFarmaceuta.setItems(FXCollections.observableArrayList(resultados));

        }
    }


    @FXML
    public void borrarFarmaceuta(ActionEvent actionEvent) {
    }

    @FXML
    public void limpiarCamposFarmaceutas(ActionEvent actionEvent) {
        txtNombreFarmaceuta.clear();
        txtIdFarmaceuta.clear();
        txtBuscarFarmaceuta.clear();
    }

    public void guardarFarmaceuta(ActionEvent actionEvent) {
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
//        colNombreFarmaceuta.setCellValueFactory(new PropertyValueFactory<>("nombre"));
//        colIDFarmaceuta.setCellValueFactory(new PropertyValueFactory<>("id"));
//
//        // Cargar todos al inicio
//        tbvResultadoBusquedaFarmaceuta.setItems(FXCollections.observableArrayList(
//                gestor.obtenerSoloFarmaceutas()
//        ));
    }
}
