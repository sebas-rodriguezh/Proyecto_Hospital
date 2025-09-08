package org.example.proyectohospital.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;

public class CambiarClaveController {

    @FXML private Button btnAceptarCambio;
    @FXML private Button btnCancelar;
    @FXML private PasswordField pwdPasswordNueva;
    @FXML private PasswordField pwdPasswordVieja;
    @FXML private PasswordField pwdPasswordNuevaConfirmar;

    @FXML
    private void aceptarCambioPassword(ActionEvent actionEvent) {
        String actual = pwdPasswordVieja.getText();
        String nueva = pwdPasswordNueva.getText();
        String confirmar = pwdPasswordNuevaConfirmar.getText();

        if (actual.isEmpty() || nueva.isEmpty() || confirmar.isEmpty()) {
            mostrarAlerta("Campos vacíos", "Debe completar todos los campos.");
            return;
        }

        if (!nueva.equals(confirmar)) {
            mostrarAlerta("Error", "La nueva contraseña y la confirmación no coinciden.");
            return;
        }

     //LOGICA CAMBIAR CONTRASEÑA

        // Limpiar campos
        pwdPasswordVieja.clear();
        pwdPasswordNueva.clear();
        pwdPasswordNuevaConfirmar.clear();
    }


    @FXML
    private void cancelarCambio(ActionEvent actionEvent) {
        //Se cancela y vuelve a la pestaña de Login.
        mostrarAlerta("Cancelado", "Se canceló el cambio de contraseña.");

    }


    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
