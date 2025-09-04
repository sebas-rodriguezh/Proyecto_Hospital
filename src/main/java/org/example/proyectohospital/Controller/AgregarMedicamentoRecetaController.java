package org.example.proyectohospital.Controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.proyectohospital.Modelo.Medicamento;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;


public class AgregarMedicamentoRecetaController implements Initializable {
    @FXML private Button btnSalir;
    @FXML private Button btnSeleccionar;
    @FXML private TextField txtValorBuscado;
    @FXML private ComboBox<String> comboBoxFiltro;
    @FXML private TableColumn<Medicamento, String> colPresentacionMedicamento;
    @FXML private TableColumn<Medicamento, String> colNombreMedicamento;
    @FXML private TableColumn<Medicamento, String> colCodigoMedicamento;
    @FXML private TableView<Medicamento> tbvResultadoBPaciente;

    private List<Medicamento> todosLosMedicamentos = new ArrayList<>();


    public AgregarMedicamentoRecetaController() {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colCodigoMedicamento.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNombreMedicamento.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colPresentacionMedicamento.setCellValueFactory(new PropertyValueFactory<>("presentacion"));

        // Simulación: cargar medicamentos desde la lógica
        // todosLosMedicamentos = gestorMedicamentos.obtenerTodos(); // return repo.obtenerTodos();
        //tbvResultadoBPaciente.setItems(FXCollections.observableArrayList(todosLosMedicamentos));

        tbvResultadoBPaciente.getItems().setAll(todosLosMedicamentos); //Este se cambia por la linea de arriba.

        // Escuchar cambios en el campo de texto
        txtValorBuscado.textProperty().addListener((obs, oldVal, newVal) -> {
            filtrarMedicamentos(newVal);
        });
    }

    private void filtrarMedicamentos(String texto) {
        if (texto == null || texto.isBlank()) {
            tbvResultadoBPaciente.getItems().setAll(todosLosMedicamentos); //Se cambia con la linea de abajo.
            //tbvResultadoBPaciente.setItems(FXCollections.observableArrayList(todosLosMedicamentos));
            return;
        }

        String filtro = texto.toLowerCase();

        List<Medicamento> filtrados = todosLosMedicamentos.stream()
                .filter(m -> m.getNombre().toLowerCase().contains(filtro) ||
                        m.getCodigo().toLowerCase().contains(filtro) ||
                        m.getPresentacion().toLowerCase().contains(filtro))
                .toList();

        tbvResultadoBPaciente.getItems().setAll(filtrados);
    }


    @FXML
    private void volverAAnterior(ActionEvent actionEvent) {
    }


    @FXML
    private void seleccionarMedicamento(ActionEvent actionEvent){
        Medicamento seleccionado = tbvResultadoBPaciente.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            // Aquí puedes pasar el medicamento a otra vista o crear un DetalleMedicamento
        }
    }
}