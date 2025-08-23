module org.example.proyectohospital {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.xml;


    opens org.example.proyectohospital to javafx.fxml;
    exports org.example.proyectohospital;
}