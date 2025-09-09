package org.example.proyectohospital.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.proyectohospital.Logica.GestorPersonal;
import org.example.proyectohospital.Logica.Hospital;
import org.example.proyectohospital.Modelo.Personal;

import java.io.IOException;

public class LoginController {
    @FXML
    private Button btnIngresar;
    @FXML
    private Button btnCambiarPassword;
    @FXML
    private Button btnSalir;
    @FXML
    private PasswordField pwdPassword;
    @FXML
    private TextField txtIdUsuario;


    private final GestorPersonal gestorPersonal = Hospital.getInstance().getGestorPersonal();


    @FXML
    private void salirDelaApp(ActionEvent actionEvent) {
        System.exit(0);
    }


    @FXML
    private void iniciarSesion(ActionEvent actionEvent) {
        String id = txtIdUsuario.getText();
        String password = pwdPassword.getText();

        if (id.isEmpty() || password.isEmpty()) {
            mostrarAlerta("Campos vacíos", "Debe ingresar usuario y contraseña.");
            return;
        }

        try {
            Personal personal = gestorPersonal.verificarCredenciales(id, password);

            if (personal != null) {
                mostrarAlerta("Bienvenido", "Bienvenido " + personal.getNombre() + " (" + personal.tipo() + ")");
                abrirVentanaSegunTipo(personal);
                cerrarVentanaLogin();
            } else {
                mostrarAlerta("Error", "Usuario o contraseña incorrectos.");
                pwdPassword.clear();
                txtIdUsuario.clear();
            }

        } catch (Exception e) {
            mostrarAlerta("Error del sistema", "Error al verificar credenciales: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void cerrarVentanaLogin() {
        Stage stage = (Stage) btnIngresar.getScene().getWindow();
        stage.close();
    }

    private void abrirVentanaSegunTipo(Personal personal) {
        try {
            String fxmlPath = "";
            String windowTitle = "";

            // Determinar qué ventana abrir según el tipo de personal
            switch (personal.tipo()) {
                case "Administrador":
                    fxmlPath = "/org/example/proyectohospital/View/WindowAdministrador.fxml";
                    windowTitle = "Sistema Hospital - Administrador: " + personal.getNombre();
                    break;

                case "Medico":
                    fxmlPath = "/org/example/proyectohospital/View/WindowMedico.fxml";
                    windowTitle = "Sistema Hospital - Médico: " + personal.getNombre();
                    break;

                case "Farmaceuta":
                    fxmlPath = "/org/example/proyectohospital/View/WindowFarmaceuta.fxml";
                    windowTitle = "Sistema Hospital - Farmaceuta: " + personal.getNombre();
                    break;

                default:
                    mostrarAlerta("Error", "Tipo de usuario no reconocido: " + personal.tipo());
                    return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Stage nuevaVentana = new Stage();
            nuevaVentana.setTitle(windowTitle);
            nuevaVentana.setScene(new Scene(root));
            nuevaVentana.setMaximized(true);
            nuevaVentana.show();

            Object controller = loader.getController();
            if (controller instanceof WindowAdministradorController) {
                // Aquí puedes pasar información adicional al controlador si es necesario
                System.out.println("Ventana de administrador abierta para: " + personal.getNombre());
            } else if (controller instanceof WindowFarmaceutaController) {
                System.out.println("Ventana de farmaceuta abierta para: " + personal.getNombre());
            }

        } catch (IOException e) {
            mostrarAlerta("Error", "No se pudo abrir la ventana: " + e.getMessage());
            e.printStackTrace();
        }


    }

    @FXML
    private void cambiarPassword(ActionEvent actionEvent) {
        try {
            // Abrir ventana de cambio de contraseña
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/proyectohospital/View/CambiarClave.fxml"));
            Parent root = loader.load();

            Stage ventanaCambio = new Stage();
            ventanaCambio.setTitle("Cambiar Contraseña");
            ventanaCambio.setScene(new Scene(root));
            ventanaCambio.setResizable(false);
            ventanaCambio.showAndWait();

        } catch (IOException e) {
            mostrarAlerta("Error", "No se pudo abrir la ventana de cambio de contraseña: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}