package org.acme.health;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.health.Readiness;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Readiness
@ApplicationScoped
public class DatabaseHealthCheck implements HealthCheck {

    @Inject
    DataSource defaultDataSource;

    @Inject
    @io.quarkus.agroal.DataSource("consulta")
    DataSource consultaDataSource;

    @Override
    public HealthCheckResponse call() {
        HealthCheckResponseBuilder responseBuilder = HealthCheckResponse.named("Conexões com Bancos de Dados");

        try {
            testConnection(defaultDataSource, "simulacoes-db");
            responseBuilder.withData("simulacoes-db", "OK").up();
        } catch (Exception e) {
            responseBuilder.withData("simulacoes-db", "FALHOU: " + e.getMessage()).down();
        }

        try {
            testConnection(consultaDataSource, "produtos-db");
            responseBuilder.withData("produtos-db", "OK").up();
        } catch (Exception e) {
            responseBuilder.withData("produtos-db", "FALHOU: " + e.getMessage()).down();
        }

        return responseBuilder.build();
    }

    private void testConnection(DataSource dataSource, String dbName) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                statement.execute("SELECT 1");
            }
        }
    }
}