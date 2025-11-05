package org.example.frontend.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.frontend.Modelo.HospitalFrontend;
import org.example.frontend.Servicios.HospitalServiceProxy;
import org.example.proyectohospital.shared.RespuestaBackend;
import org.example.proyectohospital.shared.SolicitudBackend;
import org.example.proyectohospital.Modelo.Personal;

public class CambiarClaveController {

    @FXML private TextField txtIdUsuario;
    @FXML private Button btnAceptarCambio;
    @FXML private Button btnCancelar;
    @FXML private PasswordField pwdPasswordNueva;
    @FXML private PasswordField pwdPasswordVieja;
    @FXML private PasswordField pwdPasswordNuevaConfirmar;

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

            cambiarClaveAsync(user, actual, nueva);
        }
        catch (Exception e)
        {
            mostrarAlerta("Error", "No se ha podido modificar la contraseña.");
            return;
        }
    }

    private void cambiarClaveAsync(String usuario, String claveActual, String nuevaClave) {
        btnAceptarCambio.setDisable(true);

        Async.run(() -> {
                    try {
                        HospitalServiceProxy proxy = new HospitalServiceProxy();
                        if (proxy.conectar()) {
                            SolicitudBackend solicitud = new SolicitudBackend("CAMBIAR_CLAVE");
                            solicitud.agregarParametro("id", usuario);
                            solicitud.agregarParametro("claveActual", claveActual);
                            solicitud.agregarParametro("nuevaClave", nuevaClave);

                            RespuestaBackend respuesta = (RespuestaBackend) proxy.enviarSolicitud(solicitud);
                            proxy.desconectar();
                            return respuesta.isExito();
                        }
                        return false;
                    } catch (Exception e) {
                        throw new RuntimeException("Error al cambiar clave: " + e.getMessage());
                    }
                },
                resultado -> {
                    btnAceptarCambio.setDisable(false);

                    if (resultado) {
                        mostrarAlerta("Éxito", "Contraseña cambiada correctamente.");
                        limpiarCampos();
                    } else {
                        mostrarAlerta("Error", "No se pudo cambiar la contraseña. Verifique los datos.");
                    }
                },
                error -> {
                    btnAceptarCambio.setDisable(false);
                    mostrarAlerta("Error", "Error al cambiar contraseña: " + error.getMessage());
                });
    }

    private void limpiarCampos() {
        txtIdUsuario.setText("");
        pwdPasswordVieja.clear();
        pwdPasswordNueva.clear();
        pwdPasswordNuevaConfirmar.clear();
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
