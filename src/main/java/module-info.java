module org.example.proyectohospital {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.xml;
    requires jakarta.xml.bind;
    requires org.example.proyectohospital;
    requires java.desktop;

    opens org.example.proyectohospital.Datos to jakarta.xml.bind;
    exports org.example.proyectohospital.Datos;

    opens org.example.proyectohospital to javafx.fxml;
    exports org.example.proyectohospital;
    exports org.example.proyectohospital.Controller;
    exports org.example.proyectohospital.Modelo;
    opens org.example.proyectohospital.Controller to javafx.fxml;
}