module org.example.proyectohospital {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.proyectohospital to javafx.fxml;
    exports org.example.proyectohospital;
}