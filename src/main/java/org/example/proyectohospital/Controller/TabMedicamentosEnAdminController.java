package org.example.proyectohospital.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.proyectohospital.Logica.GestorMedicamentos;
import org.example.proyectohospital.Logica.Hospital;
import org.example.proyectohospital.Modelo.Medicamento;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class TabMedicamentosEnAdminController implements Initializable{
    @FXML Button btnGuardarMedicamento;
    @FXML Button btnLimpiarCampos;
    @FXML Button btnBorrarMedicamento;
    @FXML Button btnBuscarMedicamento;
    @FXML Button btnMostrarTodosLosMedicamentos;
    @FXML TextField txtNombreMedicamento;
    @FXML TextField txtCodigoMedicamento;
    @FXML TextField txtPresentacionMedicamento;
    @FXML TextField txtBuscarMedicamento;
    @FXML TableView<Medicamento> tbvResultadoBusquedaMedicamento;
    @FXML TableColumn<Medicamento, String> colCodigoMedicamento;
    @FXML TableColumn<Medicamento, String> colNombreMedicamento;
    @FXML TableColumn<Medicamento, String> colPresentacionMedicamento;

    private final GestorMedicamentos gestor = Hospital.getInstance().getGestorMedicamentos();
    private ObservableList<Medicamento> listaMedicamentos = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        colCodigoMedicamento.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNombreMedicamento.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colPresentacionMedicamento.setCellValueFactory(new PropertyValueFactory<>("presentacion"));
        tbvResultadoBusquedaMedicamento.setItems(listaMedicamentos);
    }
    @FXML
    private void guardarMedicamento(){
        String codigo = txtCodigoMedicamento.getText();
        String nombre = txtNombreMedicamento.getText();
        String presentacion = txtPresentacionMedicamento.getText();

        if(codigo.isEmpty() || nombre.isEmpty() || presentacion.isEmpty()){
            mostrarAlerta("Error","Debe llenar todos los campos obligatorios");
            return;
        }

        Medicamento nuevo = new Medicamento(codigo,nombre,presentacion);
        boolean insertado = gestor.insertarMedicamento(nuevo);

        if(!insertado){
            mostrarAlerta("Error", "No se logro guardar este medicamento");
            return;
        }
        limpiarCamposMedicamentos();
    }

    @FXML
    private void limpiarCamposMedicamentos(){
        txtCodigoMedicamento.clear();
        txtNombreMedicamento.clear();
        txtPresentacionMedicamento.clear();
        txtBuscarMedicamento.clear();
    }

    @FXML
    private void buscarMedicamento(){
        String criterio = txtBuscarMedicamento.getText().toLowerCase();

        if(criterio.isEmpty()){
            mostrarAlerta("Error","Ingrese un nombre o codigo valido");
            return;
        }

        List<Medicamento> resultados = gestor.getMedicamentos().stream().filter(p->p.getCodigo().toLowerCase().contains(criterio)||p.getNombre().toLowerCase().contains(criterio)).collect(Collectors.toList());

        listaMedicamentos.setAll(resultados);
    }

    @FXML
    private void borrarMedicamento(){
        Medicamento seleccionado = tbvResultadoBusquedaMedicamento.getSelectionModel().getSelectedItem();
        if(seleccionado != null){
            mostrarAlerta("Error", "Debe seleccionar un medicamento para borrar");
            return;
        }
        gestor.eliminar(seleccionado.getCodigo());
    }

    @FXML
    private void modificarMedicamento(){
        Medicamento seleccionado = tbvResultadoBusquedaMedicamento.getSelectionModel().getSelectedItem();
        if(seleccionado != null){
            mostrarAlerta("Error", "Debe seleccionar un medicamento  para modificar");
            return;
        }
        seleccionado.setCodigo(txtCodigoMedicamento.getText());
        seleccionado.setNombre(txtNombreMedicamento.getText());
        seleccionado.setPresentacion(txtPresentacionMedicamento.getText());
        limpiarCamposMedicamentos();
    }

    @FXML
    public void mostrarTodosLosMedicamentos(){
        listaMedicamentos.setAll(gestor.getMedicamentos());
    }
    private void mostrarAlerta(String titulo, String mensaje){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
