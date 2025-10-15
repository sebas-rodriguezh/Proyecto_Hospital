package org.example.proyectohospital.Datos;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

//Lo mismo que teníamos para save y load de xml, pero ahora a escala SQL.
public class DB {
    private static HikariDataSource ds;

    private DB() {}

    //Aquí vamos a configurar la conexión de la aplicación a la base de datos.

    static
    {
        try (InputStream in = DB.class.getClassLoader().getResourceAsStream("db.properties"))
        {
            Properties prop = new Properties();
            prop.load(in); //Traiga las propiedades que escribimos en el archivo de db.properties.

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(prop.getProperty("db.url"));
            config.setUsername(prop.getProperty("db.user"));
            config.setPassword(prop.getProperty("db.password"));

            config.setMaximumPoolSize(Integer.parseInt(prop.getProperty("db.pool.size")));
            config.setPoolName("hotelPool");

            //Configuración opcional, pero recomendada por el profe.
            config.setMinimumIdle(2); //Conexión dure 2 min.
            config.setConnectionTimeout(10000);
            config.setMaxLifetime(1800000);
            ds = new HikariDataSource(config);
        }
        catch (Exception e) {
            throw new RuntimeException("No se pudo iniciar el pool de conexiones", e);
        }
    }

    public static Connection getConnection() throws SQLException
    {
        return ds.getConnection();
    }
}
