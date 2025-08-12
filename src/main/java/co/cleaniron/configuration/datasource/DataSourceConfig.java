package co.cleaniron.configuration.datasource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.datasource.embedded.*;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class DataSourceConfig {

    private boolean isH2Fallback = false;

    @Bean
    @Primary
    public DataSource dataSource(DataSourceProperties props) {
        try {
            // Intentar conexión a SQL Server
            HikariDataSource ds = props.initializeDataSourceBuilder()
                    .type(HikariDataSource.class)
                    .build();

            // Verificar conexión
            try (Connection conn = ds.getConnection()) {
                System.out.println("✅ Conectado exitosamente a Azure SQL Database");
                isH2Fallback = false;
                return ds;
            }
        } catch (Exception ex) {
            System.err.println("⚠️ SQL Server no disponible, fallback a H2 en memoria: " + ex.getMessage());
            isH2Fallback = true;

            // H2 configurado para máxima compatibilidad
            return new EmbeddedDatabaseBuilder()
                    .setType(EmbeddedDatabaseType.H2)
                    .setName("fallbackdb")
                    // Configuración compatible con H2
                    .addScript("classpath:schema-h2.sql") // opcional: script de inicialización
                    .build();
        }
    }

    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            EntityManagerFactoryBuilder builder,
            DataSource dataSource,
            HibernateProperties hibernateProperties) {

        Map<String, Object> jpaProps = new HashMap<>();

        if (isH2Fallback) {
            // Configuración específica para H2
            jpaProps.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
            jpaProps.put("hibernate.hbm2ddl.auto", "create-drop"); // Para testing
            jpaProps.put("hibernate.show_sql", "true");
            jpaProps.put("hibernate.format_sql", "true");
            System.out.println("🔧 Configurando Hibernate para H2");
        } else {
            // Configuración para SQL Server
            jpaProps.put("hibernate.dialect", "org.hibernate.dialect.SQLServerDialect");
            jpaProps.put("hibernate.hbm2ddl.auto", "update");
            jpaProps.put("hibernate.jdbc.time_zone", "America/Bogota");
            System.out.println("🔧 Configurando Hibernate para SQL Server");
        }

        return builder
                .dataSource(dataSource)
                .packages("co.cleaniron") // ajusta según tu paquete base
                .persistenceUnit("default")
                .properties(jpaProps)
                .build();
    }
}