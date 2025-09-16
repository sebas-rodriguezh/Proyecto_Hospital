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

public class CambiarClaveController {

    @FXML private TextField txtIdUsuario;
    @FXML private Button btnAceptarCambio;
    @FXML private Button btnCancelar;
    @FXML private PasswordField pwdPasswordNueva;
    @FXML private PasswordField pwdPasswordVieja;
    @FXML private PasswordField pwdPasswordNuevaConfirmar;
    private final GestorPersonal gestorPersonal = Hospital.getInstance().getGestorPersonal();

    @FXML
    private void aceptarCambioPassword() {
        try
        {
            String user = txtIdUsuario.getText();
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

            Personal verificado = gestorPersonal.getPersonalPorID(user);
            if (verificado == null) {
                mostrarAlerta("Error", "Usuario no encontrado.");
                return;
            }

            if (!verificado.getClave().equals(actual)) {
                mostrarAlerta("Error", "La contraseña actual es incorrecta.");
                return;
            }


            verificado.setClave(nueva);
            Personal exito = gestorPersonal.update(verificado, user);
            if (exito == null) {
                mostrarAlerta("Error", "No se pudo guardar el cambio en el sistema.");
                return;
            } else {
                mostrarAlerta("Éxito", "Contraseña cambiada correctamente.");
            }

            txtIdUsuario.setText("");
            pwdPasswordVieja.clear();
            pwdPasswordNueva.clear();
            pwdPasswordNuevaConfirmar.clear();
        }
        catch (Exception e)
        {
            mostrarAlerta("Error", "No se ha podido modificar la contraseña.");
            return;
        }
    }


    @FXML
    private void cancelarCambio(ActionEvent actionEvent) {
        try {
            mostrarAlerta("Cancelado", "Se canceló el cambio de contraseña.");
            cerrarVentana();

        } catch (Exception e) {
            mostrarAlerta("Error", "Ocurrió un error al cancelar: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void cerrarVentana() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }




    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
