package co.cleaniron.configuration.datasource;

import javax.sql.DataSource;
import java.sql.Connection;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.datasource.embedded.*;

import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class DataSourceConfig {

    @Bean
    @Primary
    public DataSource dataSource(DataSourceProperties props) {
        // Primero intento levantar MySQL
        try {
            HikariDataSource mysqlDs = props.initializeDataSourceBuilder()
                    .type(HikariDataSource.class)
                    .build();
            // FORZAR test de conexión al arrancar
            try (Connection conn = mysqlDs.getConnection()) {
                // Si llegamos aquí, MySQL está vivo
            }
            return mysqlDs;
        }
        catch (Exception ex) {
            // Capturamos cualquier fallo (host desconocido, credenciales, timeout…)
            System.err.println("⚠️ MySQL no disponible, cambiando a H2 en memoria: " + ex.getMessage());
            // Fallback a H2 en memoria, modo MySQL
            return new EmbeddedDatabaseBuilder()
                    .setType(EmbeddedDatabaseType.H2)
                    .setName("fallbackdb;MODE=MYSQL;DB_CLOSE_DELAY=-1")
                    .build();
        }
    }
}

