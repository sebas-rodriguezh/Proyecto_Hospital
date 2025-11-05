module org.example.backend {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.sql; //Nueva para SQL.
    requires com.zaxxer.hikari; //Nueva para zaxxer.
    //requires com.mysql.cj;
    requires mysql.connector.j;
    requires org.example.proyectohospital;

    exports org.example.backend.Servicios;
    exports org.example.backend.Logica;
    exports org.example.backend.Datos;
    // org.example.proyectohospital.Modelo;

    opens org.example.backend to javafx.fxml;
    exports org.example.backend;

}