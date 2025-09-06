package org.example.proyectohospital.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.example.proyectohospital.Modelo.Receta;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class TabEntregaFarmaceutaController implements Initializable {
    @FXML private TextArea txtDetallesEntregaFarmaceuta;
    @FXML private Button btnEntregarRecetaFarmaceuta;
    @FXML private TableColumn <Receta, LocalDate> colFechaRetiroEntrega;
    @FXML private TableColumn <Receta, String> colPacienteEntrega;
    @FXML private TableColumn <Receta,String> colIdEntrega;
    @FXML private TableView <Receta> tableTabEntregaFarmaceuta;
    @FXML private Button btnBuscarTabEntregaFarmaceuta;
    @FXML private TextField txtIdNombreEntregaFarmaceuta;

    @FXML
    public void entregarReceta(ActionEvent actionEvent) {
    }

    @FXML
    public void tableTabEntregaFarmaceuta(SortEvent<TableView> tableViewSortEvent) {
    }

    @FXML
    public void buscarRecetaParaEntrega(ActionEvent actionEvent) {
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
