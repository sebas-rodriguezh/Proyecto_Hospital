package org.example.proyectohospital.Controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.proyectohospital.Servicios.ChatServiceProxy;
import org.example.proyectohospital.Logica.Hospital;
import org.example.proyectohospital.Modelo.Medico;
import org.example.proyectohospital.Modelo.Personal;

public class ChatController {
    @FXML private TextArea txtAreaMensajes;
    @FXML private TextField txtMensaje;
    @FXML private ListView<String> listUsuarios;
    @FXML private Button btnConectar;
    @FXML private Button btnDesconectar;
    @FXML private Label lblEstado;

    private ChatServiceProxy chatProxy;
    private String usuarioActual;

    @FXML
    public void initialize() {
        usuarioActual = obtenerUsuarioLogueado();
        lblEstado.setText("Usuario: " + usuarioActual);

        // Configurar doble clic en lista de usuarios
        listUsuarios.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                enviarMensajePrivado();
            }
        });
    }

    private String obtenerUsuarioLogueado() {
        try {
            // Obtener el usuario del Hospital
            String usuarioId = Hospital.getInstance().getUsuarioLogueadoId();
            if (usuarioId != null && !usuarioId.trim().isEmpty()) {
                return usuarioId;
            }

            // Si por alguna razón no hay usuario, usar uno temporal
            String usuarioTemporal = "Usuario-" + (System.currentTimeMillis() % 1000);
            System.out.println("Usando usuario temporal: " + usuarioTemporal);
            return usuarioTemporal;

        } catch (Exception e) {
            String usuarioTemporal = "Usuario-" + (System.currentTimeMillis() % 1000);
            System.out.println("Error obteniendo usuario, usando temporal: " + usuarioTemporal);
            return usuarioTemporal;
        }
    }
    @FXML
    private void conectarChat() {
        try {
            chatProxy = new ChatServiceProxy(usuarioActual, this::mostrarMensaje);

            boolean conectado = chatProxy.conectar("localhost", 7000);

            if (conectado) {
                btnConectar.setDisable(true);
                btnDesconectar.setDisable(false);
                lblEstado.setText("Conectado como: " + usuarioActual);
                lblEstado.setStyle("-fx-text-fill: green;");

                // Esperar un poco y solicitar usuarios
                new Thread(() -> {
                    try {
                        Thread.sleep(1000);
                        Platform.runLater(() -> {
                            chatProxy.solicitarUsuariosConectados();
                        });
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }).start();

                mostrarMensaje("[SISTEMA] Conectado al servidor de chat");
            }
        } catch (Exception e) {
            mostrarAlerta("Error de Conexión", "No se pudo conectar al servidor: " + e.getMessage());
        }
    }

    @FXML
    private void desconectarChat() {
        if (chatProxy != null) {
            chatProxy.desconectar();
            chatProxy = null;
        }
        btnConectar.setDisable(false);
        btnDesconectar.setDisable(true);
        lblEstado.setText("Desconectado");
        lblEstado.setStyle("-fx-text-fill: red;");
        listUsuarios.getItems().clear();
        mostrarMensaje("[SISTEMA] Desconectado del chat");
    }

    @FXML
    private void enviarMensaje() {
        if (chatProxy == null) {
            mostrarAlerta("No Conectado", "Debe conectarse primero al chat");
            return;
        }

        String mensaje = txtMensaje.getText().trim();
        if (mensaje.isEmpty()) {
            return;
        }

        chatProxy.enviarMensaje(mensaje);
        txtMensaje.clear();
    }

    @FXML
    private void enviarMensajePrivado() {
        String destinatario = listUsuarios.getSelectionModel().getSelectedItem();
        if (destinatario == null) {
            mostrarAlerta("Seleccione Usuario", "Seleccione un usuario de la lista para enviar mensaje privado");
            return;
        }

        if (chatProxy == null) {
            mostrarAlerta("No Conectado", "Debe estar conectado al chat");
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Mensaje Privado");
        dialog.setHeaderText("Enviar mensaje privado a: " + destinatario);
        dialog.setContentText("Mensaje:");

        dialog.showAndWait().ifPresent(mensaje -> {
            if (!mensaje.trim().isEmpty()) {
                chatProxy.enviarMensajePrivado(destinatario, mensaje.trim());
                mostrarMensaje("[PRIVADO para " + destinatario + "]: " + mensaje.trim());
            }
        });
    }

    @FXML
    private void actualizarUsuarios() {
        if (chatProxy != null) {
            chatProxy.solicitarUsuariosConectados();
        } else {
            mostrarAlerta("No Conectado", "Conéctese al chat primero");
        }
    }

    private void mostrarMensaje(String mensaje) {
        Platform.runLater(() -> {
            txtAreaMensajes.appendText(mensaje + "\n");

            // Auto-scroll al final
            txtAreaMensajes.setScrollTop(Double.MAX_VALUE);

            // Actualizar lista de usuarios si es mensaje del sistema
            if (mensaje.contains("Usuarios conectados:") && chatProxy != null) {
                actualizarListaUsuarios();
            }
        });
    }

    private void actualizarListaUsuarios() {
        if (chatProxy != null) {
            listUsuarios.getItems().clear();
            listUsuarios.getItems().addAll(chatProxy.getUsuariosConectados());
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}