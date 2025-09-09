package org.example.proyectohospital.Controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.example.proyectohospital.Modelo.Administrador;
import org.example.proyectohospital.Controller.TabPacientesEnAdminController;
import org.example.proyectohospital.Controller.TabFarmaceutasEnAdminController;
import org.example.proyectohospital.Controller.TabMedicamentosEnAdminController;
import org.example.proyectohospital.Controller.MedicoEnAdminViewController;
import org.example.proyectohospital.Controller.TabDashboardController;

import java.net.URL;
import java.util.ResourceBundle;

public class WindowAdministradorController implements Initializable{
    @FXML TabPane tabPane;
    @FXML private Tab tabMedicamentos;
    @FXML private Tab tabFarmaceutas;
    @FXML private Tab tabMedicos;
    @FXML private Tab tabPacientes;
    @FXML private Tab tabDashboard;

    private MedicoEnAdminViewController medicoController;
    private TabFarmaceutasEnAdminController farmaceutasController;
    private TabMedicamentosEnAdminController medicamentosController;
    private TabPacientesEnAdminController pacientesController;
    private TabDashboardController dashboardController;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tabPane.getSelectionModel().select(tabDashboard);

        tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
            if (newTab == tabMedicos) {
                System.out.println("Se abrio la pestaña de Medicos");
            }else if (newTab == tabPacientes) {
                System.out.println("Se abrio la paciente de Pacientes");
            }
        });
    }

    void setMedicoController(MedicoEnAdminViewController medicoController) {
        this.medicoController = medicoController;
    }

    void setPacientesController(TabPacientesEnAdminController pacientesController) {
        this.pacientesController = pacientesController;
    }

    void setFarmaceutasController(TabFarmaceutasEnAdminController farmaceutasController) {
        this.farmaceutasController = farmaceutasController;
    }

    void setMedicamentosController(TabMedicamentosEnAdminController medicamentosController) {
        this.medicamentosController = medicamentosController;
    }

    void setDashboardController(TabDashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }

    void refrescarTodo(){
        if(medicoController!=null){medicoController.mostrarTodosLosMedicos();}
        if(pacientesController != null){pacientesController.mostrarTodosLosPacientes();}
        if(farmaceutasController != null){farmaceutasController.mostrarTodosLosFarmaceutas();}
        if(medicamentosController != null){medicoController.mostrarTodosLosMedicos();}
        if(dashboardController != null){dashboardController.desplegarDashboard();}
    }
}
