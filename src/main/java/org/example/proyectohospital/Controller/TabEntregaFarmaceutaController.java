package org.example.proyectohospital.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class TabEntregaFarmaceutaController {
    @FXML private TextArea txtDetallesEntregaFarmaceuta;
    @FXML private Button btnEntregarRecetaFarmaceuta;
    @FXML private TableColumn colFechaRetiroEntrega;
    @FXML private TableColumn colPacienteEntrega;
    @FXML private TableColumn colIdEntrega;
    @FXML private TableView tableTabEntregaFarmaceuta;
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
}
