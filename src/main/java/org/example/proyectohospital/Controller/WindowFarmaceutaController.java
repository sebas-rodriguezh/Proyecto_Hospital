package org.example.proyectohospital.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class WindowFarmaceutaController implements Initializable {

    @FXML
    private TabPane tabPaneFarmaceuta;
    @FXML
    private Tab tabSolicitudFarmaceuta;
    @FXML
    private Tab tabAlistadoFarmaceuta;
    @FXML
    private Tab tabEntregaFarmaceuta;
    @FXML
    private Tab tabHistoricoFarmaceuta;

    private TabSolicitudFarmaceutaController solicitudController;
    private TabAlistadoFarmaceutaController alistadoController;
    private TabEntregaFarmaceutaController entregaController;
    private TabHistoricoRecetasController historicoController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Cargar FXML de cada tab y obtener su controller
        try {
            FXMLLoader loaderSolicitud = new FXMLLoader(getClass().getResource("/fxml/TabSolicitudFarmaceuta.fxml"));
            tabSolicitudFarmaceuta.setContent(loaderSolicitud.load());
            solicitudController = loaderSolicitud.getController();

            FXMLLoader loaderAlistado = new FXMLLoader(getClass().getResource("/fxml/TabAlistadoFarmaceuta.fxml"));
            tabAlistadoFarmaceuta.setContent(loaderAlistado.load());
            alistadoController = loaderAlistado.getController();

            FXMLLoader loaderEntrega = new FXMLLoader(getClass().getResource("/fxml/TabEntregaFarmaceuta.fxml"));
            tabEntregaFarmaceuta.setContent(loaderEntrega.load());
            entregaController = loaderEntrega.getController();

            FXMLLoader loaderHistorico = new FXMLLoader(getClass().getResource("/fxml/TabHistoricoRecetas.fxml"));
            tabHistoricoFarmaceuta.setContent(loaderHistorico.load());
            historicoController = loaderHistorico.getController();

        } catch (IOException e) {
            e.printStackTrace();
        }

        // Listener para refrescar contenido al cambiar de tab
        tabPaneFarmaceuta.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab == tabSolicitudFarmaceuta && solicitudController != null) {
                solicitudController.buscarReceta(null); // método que ya existe
            } else if (newTab == tabAlistadoFarmaceuta && alistadoController != null) {
                alistadoController.buscarReceta(null); // método que ya existe
            } else if (newTab == tabEntregaFarmaceuta && entregaController != null) {
                entregaController.buscarRecetaParaEntrega(null); // método que ya existe
            } else if (newTab == tabHistoricoFarmaceuta && historicoController != null) {
                historicoController.refrescarTabla(); // ahora funciona
            }
        });
        // Seleccionar la primera pestaña por defecto
        tabPaneFarmaceuta.getSelectionModel().select(tabSolicitudFarmaceuta);
    }

}
