package org.example.proyectohospital.Controller;

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
import org.example.proyectohospital.Logica.GestorPersonal;
import org.example.proyectohospital.Logica.Hospital;
import org.example.proyectohospital.Modelo.Medico;
import org.example.proyectohospital.Modelo.Personal;
import org.example.proyectohospital.Logica.InicializadorDatos;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    @FXML private Button btnIngresar;
    @FXML private Button btnCambiarPassword;
    @FXML private Button btnSalir;
    @FXML private PasswordField pwdPassword;
    @FXML private TextField txtIdUsuario;

    private final GestorPersonal gestorPersonal = Hospital.getInstance().getGestorPersonal();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("Inicializando LoginController...");

        // Inicializar datos si es necesario
        try {
            InicializadorDatos.inicializarSiEsNecesario();

            // Verificar que tenemos datos
            System.out.println("Personal registrado: " + gestorPersonal.getPersonal().size());
            InicializadorDatos.mostrarPersonalExistente();

        } catch (Exception e) {
            System.err.println("Error durante la inicialización: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta("Error de Inicialización", "Hubo un problema al cargar los datos: " + e.getMessage());
        }
    }

    @FXML
    private void salirDelaApp(ActionEvent actionEvent) {
        System.exit(0);
    }

    @FXML
    private void iniciarSesion(ActionEvent actionEvent) {
        String id = txtIdUsuario.getText().trim();
        String password = pwdPassword.getText();

        System.out.println("=== INTENTO DE LOGIN ===");
        System.out.println("ID ingresado: '" + id + "'");
        System.out.println("Password ingresado: '" + password + "'");

        if (id.isEmpty() || password.isEmpty()) {
            mostrarAlerta("Campos vacíos", "Debe ingresar usuario y contraseña.");
            return;
        }

        try {
            // DEBUG: Buscar específicamente el usuario
            Personal personalPorID = gestorPersonal.getPersonalPorID(id);
            System.out.println("Personal encontrado por ID: " + personalPorID);

            if (personalPorID != null) {
                System.out.println("- Nombre: " + personalPorID.getNombre());
                System.out.println("- Clave almacenada: '" + personalPorID.getClave() + "'");
                System.out.println("- Tipo: " + personalPorID.tipo());
                System.out.println("- ¿Claves coinciden?: " + personalPorID.getClave().equals(password));
            }

            // Intentar verificar credenciales
            Personal personal = gestorPersonal.verificarCredenciales(id, password);
            System.out.println("Resultado verificarCredenciales: " + personal);

            if (personal != null) {
                mostrarAlerta("Bienvenido", "Bienvenido " + personal.getNombre() + " (" + personal.tipo() + ")");
                abrirVentanaSegunTipo(personal);
                cerrarVentanaLogin();
            } else {
                mostrarAlerta("Error", "Usuario o contraseña incorrectos.");

                // DEBUG: Mostrar usuarios disponibles
                System.out.println("\n=== USUARIOS DISPONIBLES ===");
                for (Personal p : gestorPersonal.getPersonal()) {
                    System.out.println("ID: '" + p.getId() + "' | Clave: '" + p.getClave() + "' | Nombre: " + p.getNombre());
                }

                pwdPassword.clear();
                txtIdUsuario.requestFocus();
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

            // Determinar qué ventana abrir según el tipo de personal
            switch (personal.tipo()) {
                case "Administrador":
                    fxmlPath = "/org/example/proyectohospital/View/WindowAdministrador.fxml";
                    windowTitle = "Sistema Hospital - Administrador: " + personal.getNombre();
                    break;

                case "Medico":
                    fxmlPath = "/org/example/proyectohospital/View/WindowMedico.fxml";
                    windowTitle = "Sistema Hospital - Médico: " + personal.getNombre();

                    if (personal instanceof Medico) {
                        Hospital.getInstance().setMedicoLogueado((Medico) personal);
                        System.out.println("✅ Médico guardado en singleton: " + personal.getNombre());
                    }


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

        } catch (IOException e) {
            System.err.println("Error al abrir ventana: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir la ventana: " + e.getMessage());
        }
    }

    @FXML
    private void cambiarPassword(ActionEvent actionEvent) {
        try {
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