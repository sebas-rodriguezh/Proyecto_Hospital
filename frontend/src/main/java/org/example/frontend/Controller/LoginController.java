package org.example.frontend.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


import org.example.frontend.Modelo.HospitalFrontend;
import org.example.frontend.Servicios.HospitalServiceProxy;
import org.example.proyectohospital.shared.RespuestaBackend;
import org.example.proyectohospital.shared.SolicitudBackend;
import org.example.proyectohospital.Modelo.Medico;
import org.example.proyectohospital.Modelo.Personal;


import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    @FXML private Button btnIngresar;
    @FXML private Button btnCambiarPassword;
    @FXML private Button btnSalir;
    @FXML private PasswordField pwdPassword;
    @FXML private TextField txtIdUsuario;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    @FXML
    private void salirDelaApp(ActionEvent actionEvent) {
        System.exit(0);
    }

    @FXML
    private void iniciarSesion(ActionEvent actionEvent) {
        String id = txtIdUsuario.getText().trim();
        String password = pwdPassword.getText();

        if (id.isEmpty() || password.isEmpty()) {
            mostrarAlerta("Campos vacíos", "Debe ingresar usuario y contraseña.");
            return;
        }

        try {
            HospitalServiceProxy proxy = new HospitalServiceProxy();
            if (proxy.conectar()) {
                SolicitudBackend solicitud = new SolicitudBackend("LOGIN");
                solicitud.agregarParametro("id", id);
                solicitud.agregarParametro("clave", password);

                RespuestaBackend respuesta = (RespuestaBackend) proxy.enviarSolicitud(solicitud);
                proxy.desconectar();

                if (respuesta.isExito()) {
                    Personal personal = (Personal) respuesta.getDatos();
                    HospitalFrontend.getInstance().setPersonalLogueado(personal);

                    mostrarAlerta("Bienvenido", "Bienvenido " + personal.getNombre() + " (" + personal.tipo() + ")");
                    abrirVentanaSegunTipo(personal);
                    cerrarVentanaLogin();
                } else {
                    mostrarAlerta("Error", respuesta.getMensaje());
                    pwdPassword.clear();
                    txtIdUsuario.requestFocus();
                }
            } else {
                mostrarAlerta("Error", "No se pudo conectar con el servidor");
            }

        } catch (Exception e) {
            System.err.println("Error durante el login: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta("Error del sistema", "Error al verificar credenciales: " + e.getMessage());
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

            switch (personal.tipo()) {
                case "Administrador":
                    fxmlPath = "/org/example/frontend/View/WindowAdministrador.fxml";
                    windowTitle = "Sistema Hospital - Administrador: " + personal.getNombre();
                    break;

                case "Medico":
                    fxmlPath = "/org/example/frontend/View/WindowMedico.fxml";
                    windowTitle = "Sistema Hospital - Médico: " + personal.getNombre();

                    if (personal instanceof Medico) {
                        HospitalFrontend.getInstance().setMedicoLogueado((Medico) personal);
                    }

                    break;

                case "Farmaceuta":
                    fxmlPath = "/org/example/frontend/View/WindowFarmaceuta.fxml";
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
            nuevaVentana.setResizable(false);
            nuevaVentana.setMaximized(false);
            nuevaVentana.sizeToScene();
            nuevaVentana.show();

        } catch (IOException e) {
            System.err.println("Error al abrir ventana: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir la ventana: " + e.getMessage());
        }
    }

    @FXML
    private void cambiarPassword(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/frontend/View/CambiarClave.fxml"));
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