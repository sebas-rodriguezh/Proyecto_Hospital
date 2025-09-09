package org.example.proyectohospital.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

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

        // Validación con la base de datos
        if (id.equals("admin") && password.equals("1234")) {
            mostrarAlerta("Bienvenido", "Usted ha ingresado correctamente.");
            // Abrir la ventana principal del sistema
        } else {
            mostrarAlerta("Error", "Usuario o contraseña incorrectos.");
        }

        pwdPassword.clear();
        txtIdUsuario.clear();
    }


    @FXML
    private void cambiarPassword(ActionEvent actionEvent) {
        //LOGICA CAMBIO CONTRASEÑA
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}