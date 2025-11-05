module org.example.frontend {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.sql; //Nueva para SQL.
    requires com.zaxxer.hikari; //Nueva para zaxxer.
    //requires com.mysql.cj;
    requires mysql.connector.j;
    requires org.example.proyectohospital;

    opens org.example.frontend to javafx.fxml;
    opens org.example.frontend.Controller to javafx.fxml;

    exports org.example.frontend.Controller;
    //exports org.example.frontend.Modelo;
    exports org.example.frontend.Servicios;

    exports org.example.frontend;
}