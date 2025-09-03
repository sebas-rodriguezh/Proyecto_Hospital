package org.example.proyectohospital.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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
    }


    @FXML
    private void cancelarCambio(ActionEvent actionEvent) {
        //Se cancela y vuelve a la pestaña de Login.
    }
}
