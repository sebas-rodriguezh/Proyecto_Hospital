package org.example.frontend.Controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.net.URL;
import java.util.ResourceBundle;

public class WindowFarmaceutaController implements Initializable {

    @FXML private TabPane tabPaneFarmaceuta;
    @FXML private Tab tabSolicitudFarmaceuta;
    @FXML private Tab tabAlistadoFarmaceuta;
    @FXML private Tab tabEntregaFarmaceuta;
    @FXML private Tab tabHistoricoFarmaceuta;
    @FXML private TabSolicitudFarmaceutaController tabSolicitudContentController;
    @FXML private TabAlistadoFarmaceutaController tabAlistadoContentController;
    @FXML private TabEntregaFarmaceutaController tabEntregaContentController;
    @FXML private TabHistoricoRecetasController tabHistoricoContentController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tabPaneFarmaceuta.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldTab, newTab) -> {
                    if (newTab == tabAlistadoFarmaceuta && tabAlistadoContentController != null) {
                        tabAlistadoContentController.actualizarTabla();
                    }

                    if (newTab == tabSolicitudFarmaceuta && tabSolicitudContentController != null) {
                        tabSolicitudContentController.actualizarTabla();
                    }

                    if (newTab == tabEntregaFarmaceuta && tabEntregaContentController != null) {
                        tabEntregaContentController.actualizarTabla();
                    }
                    if (newTab == tabHistoricoFarmaceuta && tabHistoricoContentController != null) {
                        tabHistoricoContentController.actualizarTabla();
                    }
                }
        );
    }
}