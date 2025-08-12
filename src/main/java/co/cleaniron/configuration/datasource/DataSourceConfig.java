package co.cleaniron.configuration.datasource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.*;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class DataSourceConfig {

    private volatile boolean isH2Fallback = false;

    @Bean
    @Primary
    public DataSource dataSource(DataSourceProperties props) {
        // --- SQL Server con Hikari, pool mínimo y timeouts cortos ---
        HikariDataSource sql = props.initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();

        // Pool pequeño y sin idles
        sql.setMaximumPoolSize(3);
        sql.setMinimumIdle(0);               // no mantiene conexiones ociosas
        sql.setIdleTimeout(30_000);          // 30s: suelta rápido las idles
        sql.setMaxLifetime(120_000);         // 2min: recicla conexiones “viejas”
        sql.setConnectionTimeout(5_000);     // 5s: si está pausada, falla rápido
        sql.setValidationTimeout(2_000);     // 2s
        sql.setInitializationFailTimeout(0); // arranca aunque no conecte
        sql.setConnectionTestQuery("SELECT 1"); // opcional (JDBC4 isValid también sirve)

        // Asegura timeouts del driver (si no los pusiste en la URL):
        // loginTimeout (seg), socketTimeout (ms)
        sql.addDataSourceProperty("loginTimeout", "5");
        sql.addDataSourceProperty("socketTimeout", "5000");

        try (Connection ignored = sql.getConnection()) {
            System.out.println("✅ Conectado exitosamente a Azure SQL Database");
            isH2Fallback = false;
            return sql;
        } catch (Exception ex) {
            System.err.println("⚠️ SQL Server no disponible, fallback a H2 en memoria: " + ex.getMessage());
            isH2Fallback = true;

            // --- H2 con Hikari (modo MSSQL para máxima compatibilidad) ---
            HikariDataSource h2 = new HikariDataSource();
            h2.setJdbcUrl("jdbc:h2:mem:fallbackdb;MODE=MSSQLServer;CASE_INSENSITIVE_IDENTIFIERS=TRUE;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
            h2.setUsername("sa");
            h2.setPassword("");
            h2.setMaximumPoolSize(3);
            h2.setMinimumIdle(0);
            h2.setIdleTimeout(30_000);
            h2.setMaxLifetime(120_000);
            h2.setConnectionTimeout(5_000);
            h2.setValidationTimeout(2_000);
            h2.setInitializationFailTimeout(0);
            h2.setConnectionTestQuery("SELECT 1");

            return h2;
        }
    }

    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            EntityManagerFactoryBuilder builder,
            DataSource dataSource) {

        Map<String, Object> jpa = new HashMap<>();
        if (isH2Fallback) {
            // H2: genera el esquema solo para correr la app
            jpa.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
            jpa.put("hibernate.hbm2ddl.auto", "create-drop");
            jpa.put("hibernate.show_sql", "false");
            jpa.put("hibernate.format_sql", "true");
            System.out.println("🔧 Hibernate configurado para H2");
        } else {
            // SQL Server
            jpa.put("hibernate.dialect", "org.hibernate.dialect.SQLServerDialect");
            jpa.put("hibernate.hbm2ddl.auto", "update");
            jpa.put("hibernate.jdbc.time_zone", "America/Bogota");
            System.out.println("🔧 Hibernate configurado para SQL Server");
        }

        return builder
                .dataSource(dataSource)
                .packages("co.cleaniron")
                .persistenceUnit("default")
                .properties(jpa)
                .build();
    }
}
