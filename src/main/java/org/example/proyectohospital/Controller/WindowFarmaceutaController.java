package org.example.proyectohospital.Controller;


import  javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;


public class WindowFarmaceutaController {
    @FXML private TabPane tabPaneFarmaceuta;
    @FXML private Tab tabSolicitud;
    @FXML private Tab tabAlistado;
    @FXML private Tab tabEntrega;
    @FXML private Tab tabHistorico;

    @FXML
    private void initialize() {
        System.out.println("WindowFarmaceuta cargado.");
    }

}
