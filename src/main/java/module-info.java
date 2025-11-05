module org.example.proyectohospital {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.xml;
    requires jakarta.xml.bind;
    requires java.desktop;
    requires java.sql; //Nueva para SQL.
    requires com.zaxxer.hikari; //Nueva para zaxxer.
    //requires com.mysql.cj;
    requires mysql.connector.j;
    //requires mysql.connector.j;
    requires java.base;

    //opens org.example.backend.Datos to jakarta.xml.bind;
    //exports org.example.backend.Datos;

    opens org.example.proyectohospital to javafx.fxml;
    exports org.example.proyectohospital;
    exports org.example.proyectohospital.Modelo;
    exports org.example.proyectohospital.shared;
    opens org.example.proyectohospital.Modelo to java.base;
    opens org.example.proyectohospital.shared to java.base; // Para serialización
    //exports org.example.frontend.Controller;
    //exports org.example.backend.Modelo;
    //opens org.example.frontend.Controller to javafx.fxml;
}