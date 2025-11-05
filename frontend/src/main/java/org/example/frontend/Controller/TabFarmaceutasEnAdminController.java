package org.example.frontend.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import org.example.frontend.Modelo.HospitalFrontend;
import org.example.frontend.Servicios.HospitalServiceProxy;
import org.example.proyectohospital.shared.RespuestaBackend;
import org.example.proyectohospital.shared.SolicitudBackend;
import org.example.proyectohospital.Modelo.Receta;

import org.example.proyectohospital.Modelo.Farmaceuta;
import org.example.proyectohospital.Modelo.Personal;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class TabFarmaceutasEnAdminController implements Initializable {
    @FXML private Button btnAbrirChat;
    @FXML private ProgressIndicator progressFarmaceutas;
    @FXML private Button btnMostrarTodosLosFarmaceutas;
    @FXML private Button btnModificarFarmaceuta;
    @FXML private Button btnBuscarFarmaceuta;
    @FXML private Button btnBorrarFarmaceuta;
    @FXML private Button btnLimpiarCampos;
    @FXML private Button btnGuardarFarmaceuta;
    @FXML private TextField txtBuscarFarmaceuta;
    @FXML private TableColumn <Farmaceuta, String> colNombreFarmaceuta;
    @FXML private TableColumn <Farmaceuta, String> colIDFarmaceuta;
    @FXML private TableView <Farmaceuta> tbvResultadoBusquedaFarmaceuta;
    @FXML private TextField txtNombreFarmaceuta;
    @FXML private TextField txtIdFarmaceuta;


    private final ObservableList<Farmaceuta> listaFarmaceuta = FXCollections.observableArrayList();
    private List<Farmaceuta> todosLosFarmaceutas = new ArrayList<>();

    private boolean operacionEnProgreso = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colNombreFarmaceuta.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colIDFarmaceuta.setCellValueFactory(new PropertyValueFactory<>("id"));
        tbvResultadoBusquedaFarmaceuta.setItems(listaFarmaceuta);

        tbvResultadoBusquedaFarmaceuta.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        configurarCampoId(false);
                        llenarCamposConFarmaceuta(newValue);
                    } else {
                        configurarCampoId(true);
                    }
                }
        );

        configurarCampoId(true);
        mostrarTodosLosFarmaceutas();
    }

    private void configurarCampoId(boolean editable) {
        txtIdFarmaceuta.setEditable(editable);
        txtIdFarmaceuta.setFocusTraversable(editable);

        if (editable) {
            txtIdFarmaceuta.setStyle("-fx-background-color: white; -fx-text-fill: black;");
        } else {
            txtIdFarmaceuta.setStyle("-fx-background-color: #f0f0f0; -fx-text-fill: #666;");
        }
    }

    private void llenarCamposConFarmaceuta(Farmaceuta farmaceuta) {
        txtIdFarmaceuta.setText(farmaceuta.getId());
        txtNombreFarmaceuta.setText(farmaceuta.getNombre());
    }

    @FXML
    private void abrirVentanaChat() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/frontend/View/chat-view.fxml"));
            Parent root = loader.load();

            Stage chatStage = new Stage();
            chatStage.setTitle("Chat del Hospital - " + HospitalFrontend.getInstance().getUsuarioLogueadoNombre());
            chatStage.setScene(new Scene(root));
            chatStage.setResizable(true);
            chatStage.show();

        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo abrir el chat: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void mostrarTodosLosFarmaceutas() {
        try {
            cargarFarmaceutasAsync();
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo cargar la información de los farmaceutas.");
        }
    }

    @FXML
    public void modificarFarmaceuta(ActionEvent actionEvent) {
        if (operacionEnProgreso) {
            return;
        }

        try
        {
            Farmaceuta seleccionado = tbvResultadoBusquedaFarmaceuta.getSelectionModel().getSelectedItem();
            if (seleccionado == null) {
                mostrarAlerta("Error", "Seleccione un farmacéutico ");
                return;
            }

            String nombre = txtNombreFarmaceuta.getText().trim();

            if (nombre.isEmpty()) {
                mostrarAlerta("Error", "Complete el campo nombre");
                return;
            }

            seleccionado.setNombre(nombre);
            seleccionado.setClave(seleccionado.getId());

            modificarFarmaceutaAsync(seleccionado, seleccionado.getId());


        } catch (Exception e) {
            mostrarAlerta("Error", "Error al modificar farmaceuta: " + e.getMessage());
        }
    }

    @FXML
    public void buscarFarmaceuta(ActionEvent actionEvent) {
        String texto = txtBuscarFarmaceuta.getText().trim();

        if (texto.isEmpty()) {
            cargarFarmaceutasAsync();
            return;
        }

        List<Farmaceuta> resultados = todosLosFarmaceutas.stream()
                .filter(f -> {
                    String textoBusqueda = texto.toLowerCase().trim();
                    return f.getId().toLowerCase().contains(textoBusqueda) ||
                            f.getNombre().toLowerCase().contains(textoBusqueda);
                })
                .collect(Collectors.toList());
        listaFarmaceuta.setAll(resultados);
    }

    @FXML
    public void borrarFarmaceuta(ActionEvent actionEvent) {
        if (operacionEnProgreso) {
            return;
        }

        try
        {
            Farmaceuta seleccionado = tbvResultadoBusquedaFarmaceuta.getSelectionModel().getSelectedItem();
            if (seleccionado == null) {
                mostrarAlerta("Error", "Debe seleccionar un farmaceuta para borrar");
                return;
            }
            eliminarFarmaceutaAsync(seleccionado);
        }
        catch (Exception e) {
            mostrarAlerta("Error", "Error al borrar farmaceuta: " + e.getMessage());
        }
    }

    @FXML
    public void limpiarCamposFarmaceutas() {
        txtNombreFarmaceuta.clear();
        txtIdFarmaceuta.clear();
        txtBuscarFarmaceuta.clear();
        tbvResultadoBusquedaFarmaceuta.getSelectionModel().clearSelection();
        configurarCampoId(true);
    }

    @FXML
    public void guardarFarmaceuta(ActionEvent actionEvent) {
        if (operacionEnProgreso) {
            return;
        }

        try
        {
            String idFarmaceuta = txtIdFarmaceuta.getText();
            String nombreFarmaceuta = txtNombreFarmaceuta.getText();

            if(idFarmaceuta.isEmpty() || nombreFarmaceuta.isEmpty()) {
                mostrarAlerta("Error", "Debe llenar todos los campos obligatorios");
                return;
            }

            Personal nuevo = new Farmaceuta(nombreFarmaceuta, idFarmaceuta, idFarmaceuta);
            guardarFarmaceutaAsync(nuevo);

        }
        catch (Exception e)
        {
            mostrarAlerta("Error", "No se pudo insertar la farmaceuta: " + e.getMessage());
        }
    }

    private void mostrarAlerta(String titulo, String mensaje){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }


    public void cargarFarmaceutasAsync() {
        if (operacionEnProgreso) return;

        operacionEnProgreso = true;
        progressFarmaceutas.setVisible(true);
        if (btnMostrarTodosLosFarmaceutas != null) btnMostrarTodosLosFarmaceutas.setDisable(true);

        Async.run(() -> {
                    try {
                        HospitalServiceProxy proxy = new HospitalServiceProxy();
                        if (proxy.conectar()) {
                            SolicitudBackend solicitud = new SolicitudBackend("OBTENER_FARMACEUTAS");
                            RespuestaBackend respuesta = (RespuestaBackend) proxy.enviarSolicitud(solicitud);
                            proxy.desconectar();

                            if (respuesta.isExito()) {
                                return (List<Farmaceuta>) respuesta.getDatos();
                            } else {
                                throw new RuntimeException(respuesta.getMensaje());
                            }
                        }
                        throw new RuntimeException("No se pudo conectar al backend");
                    } catch (Exception e) {
                        throw new RuntimeException("Error al cargar farmacéuticos: " + e.getMessage());
                    }
                },
                farmaceutas -> {
                    operacionEnProgreso = false;
                    progressFarmaceutas.setVisible(false);
                    if (btnMostrarTodosLosFarmaceutas != null) btnMostrarTodosLosFarmaceutas.setDisable(false);
                    this.todosLosFarmaceutas = farmaceutas;
                    listaFarmaceuta.setAll(farmaceutas);
                },
                error -> {
                    operacionEnProgreso = false;
                    progressFarmaceutas.setVisible(false);
                    if (btnMostrarTodosLosFarmaceutas != null) btnMostrarTodosLosFarmaceutas.setDisable(false);
                    mostrarAlerta("Error", "No se pudieron cargar los farmaceutas: " + error.getMessage());
                });
    }

    public void guardarFarmaceutaAsync(Personal farmaceuta) {
        operacionEnProgreso = true;
        progressFarmaceutas.setVisible(true);
        btnGuardarFarmaceuta.setDisable(true);

        Async.run(() -> {
                    try {
                        HospitalServiceProxy proxy = new HospitalServiceProxy();
                        if (proxy.conectar()) {
                            SolicitudBackend solicitudVerificar = new SolicitudBackend("VERIFICAR_ID_PERSONAL");
                            solicitudVerificar.agregarParametro("id", farmaceuta.getId());
                            RespuestaBackend respuestaVerificar = (RespuestaBackend) proxy.enviarSolicitud(solicitudVerificar);

                            if (respuestaVerificar.isExito() && (Boolean) respuestaVerificar.getDatos()) {
                                proxy.desconectar();
                                return false;
                            }

                            SolicitudBackend solicitudInsertar = new SolicitudBackend("INSERTAR_PERSONAL");
                            solicitudInsertar.agregarParametro("personal", farmaceuta);
                            RespuestaBackend respuestaInsertar = (RespuestaBackend) proxy.enviarSolicitud(solicitudInsertar);
                            proxy.desconectar();

                            return respuestaInsertar.isExito();
                        }
                        return false;
                    } catch (Exception e) {
                        throw new RuntimeException("Error al guardar farmacéutico: " + e.getMessage());
                    }
                },
                resultado -> {
                    operacionEnProgreso = false;
                    progressFarmaceutas.setVisible(false);
                    btnGuardarFarmaceuta.setDisable(false);

                    if (resultado) {
                        cargarFarmaceutasAsync();
                        limpiarCamposFarmaceutas();
                        mostrarAlerta("Éxito", "Se pudo insertar el farmacéutico correctamente.");
                    } else {
                        mostrarAlerta("Error", "Ya existe un usuario con ese ID");
                    }
                },
                error -> {
                    operacionEnProgreso = false;
                    progressFarmaceutas.setVisible(false);
                    btnGuardarFarmaceuta.setDisable(false);
                    mostrarAlerta("Error", "Error al guardar: " + error.getMessage());
                });
    }


    public void modificarFarmaceutaAsync(Farmaceuta farmaceuta, String idOriginal) {
        operacionEnProgreso = true;
        progressFarmaceutas.setVisible(true);
        btnModificarFarmaceuta.setDisable(true);

        Async.run(() -> {
                    try {
                        HospitalServiceProxy proxy = new HospitalServiceProxy();
                        if (proxy.conectar()) {
                            SolicitudBackend solicitud = new SolicitudBackend("ACTUALIZAR_PERSONAL");
                            solicitud.agregarParametro("personal", farmaceuta);
                            RespuestaBackend respuesta = (RespuestaBackend) proxy.enviarSolicitud(solicitud);
                            proxy.desconectar();
                            return respuesta.isExito();
                        }
                        return false;
                    } catch (Exception e) {
                        throw new RuntimeException("Error al modificar farmacéutico: " + e.getMessage());
                    }
                },
                resultado -> {
                    operacionEnProgreso = false;
                    progressFarmaceutas.setVisible(false);
                    btnModificarFarmaceuta.setDisable(false);

                    if (resultado) {
                        cargarFarmaceutasAsync();
                        limpiarCamposFarmaceutas();
                        mostrarAlerta("Éxito", "Se pudo modificar al farmaceuta correctamente.");
                    } else {
                        mostrarAlerta("Error", "Error al modificar el farmacéutico");
                    }
                },
                error -> {
                    operacionEnProgreso = false;
                    progressFarmaceutas.setVisible(false);
                    btnModificarFarmaceuta.setDisable(false);
                    mostrarAlerta("Error", "Error al modificar el farmaceuta: " + error.getMessage());
                });
    }

    public void eliminarFarmaceutaAsync(Farmaceuta farmaceuta) {
        operacionEnProgreso = true;
        progressFarmaceutas.setVisible(true);
        btnBorrarFarmaceuta.setDisable(true);

        Async.run(() -> {
                    try {
                        HospitalServiceProxy proxy = new HospitalServiceProxy();
                        if (proxy.conectar()) {
                            SolicitudBackend solicitud = new SolicitudBackend("ELIMINAR_PERSONAL");
                            solicitud.agregarParametro("id", farmaceuta.getId());
                            RespuestaBackend respuesta = (RespuestaBackend) proxy.enviarSolicitud(solicitud);
                            proxy.desconectar();
                            return respuesta.isExito();
                        }
                        return false;
                    } catch (Exception e) {
                        throw new RuntimeException("Error al eliminar farmacéutico: " + e.getMessage());
                    }
                },
                resultado -> {
                    operacionEnProgreso = false;
                    progressFarmaceutas.setVisible(false);
                    btnBorrarFarmaceuta.setDisable(false);

                    if (resultado) {
                        cargarFarmaceutasAsync();
                        limpiarCamposFarmaceutas();
                        mostrarAlerta("Éxito", "Se pudo eliminar el farmacéutico correctamente.");
                    } else {
                        mostrarAlerta("Error", "No se pudo eliminar al farmacéutico");
                    }
                },
                error -> {
                    operacionEnProgreso = false;
                    progressFarmaceutas.setVisible(false);
                    btnBorrarFarmaceuta.setDisable(false);
                    mostrarAlerta("Error", "No se pudo eliminar al farmacéutico: " + error.getMessage());
                });
    }
}