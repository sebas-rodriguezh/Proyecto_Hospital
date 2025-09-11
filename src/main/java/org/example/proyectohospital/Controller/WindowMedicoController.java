package org.example.proyectohospital.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.example.proyectohospital.Modelo.Medico;

import java.net.URL;
import java.util.ResourceBundle;

public class WindowMedicoController {
//    @FXML private TabPane tabGeneral;
//    @FXML private Tab tabPrescribir;
//    @FXML private Tab tabHistorico;
//    @FXML private Tab tabDashboard;
//    @FXML private Tab tabAcercaDe;
//
//
//    private Medico medicoActual;
//    private TabPrescibirController prescibirController;
//
//    // Método para establecer el médico que inició sesión
//    public void setMedicoActual(Medico medico) {
////        this.medicoActual = medico;
////        System.out.println("Médico logueado: " + medico.getNombre() + " - " + medico.getEspecialidad());
////
////        // Pasar el médico al controlador de prescribir cuando se inicialice
////        if (prescibirController != null) {
////            prescibirController.setMedicoActual(medico);
////        }
////    }
////
////    @Override
////    public void initialize(URL url, ResourceBundle resourceBundle) {
////        System.out.println("WindowMedicoController inicializado");
////
////        // Listener para cuando se selecciona la pestaña de prescribir
////        tabGeneral.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
////            if (newTab == tabPrescribir && prescibirController == null) {
////                cargarControllerPrescribir();
////            }
////        });
////    }
////
////    private void cargarControllerPrescribir() {
////        try {
////            // Cargar el FXML de la pestaña prescribir
////            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/proyectohospital/View/TabPrescibir.fxml"));
////
////            // Si ya hay contenido en la pestaña, obtener su controller
////            if (tabPrescribir.getContent() != null) {
////                // El contenido ya está cargado, buscar el controller
////                Object controller = loader.getController();
////                if (controller instanceof TabPrescibirController) {
////                    prescibirController = (TabPrescibirController) controller;
////                }
////            }
////
////            // Establecer el médico en el controller de prescribir
////            if (prescibirController != null && medicoActual != null) {
////                prescibirController.setMedicoActual(medicoActual);
////            }
////
////        } catch (Exception e) {
////            System.err.println("Error al cargar controller de prescribir: " + e.getMessage());
////        }
////    }
////
////    // Método alternativo si el controller ya está inyectado
////    public void setPrescibirController(TabPrescibirController controller) {
////        this.prescibirController = controller;
////        if (medicoActual != null) {
////            controller.setMedicoActual(medicoActual);
////        }
////    }
}