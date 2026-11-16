package guru.springframework.spring6reactive.config.health;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.Status;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import com.sun.net.httpserver.HttpServer;

import static org.assertj.core.api.Assertions.assertThat;

class AuthServerHealthIndicatorTest {

    private HttpServer server;

    @AfterEach
    void stopServer() {
        if (server != null) {
            server.stop(0);
        }
    }

    @Test
    void reportsUpWhenAuthServerReportsUp() throws IOException {
        String baseUrl = startServer(200, "{\"status\":\"UP\"}");

        Health health = new AuthServerHealthIndicator(baseUrl).health();

        assertThat(health.getStatus()).isEqualTo(Status.UP);
    }

    @Test
    void reportsDownWhenAuthServerDoesNotReportUp() throws IOException {
        String baseUrl = startServer(200, "{\"status\":\"DOWN\"}");

        Health health = new AuthServerHealthIndicator(baseUrl).health();

        assertThat(health.getStatus()).isEqualTo(Status.DOWN);
    }

    @Test
    void reportsDownWhenAuthServerReturnsNoBody() throws IOException {
        String baseUrl = startServer(204, null);

        Health health = new AuthServerHealthIndicator(baseUrl).health();

        assertThat(health.getStatus()).isEqualTo(Status.DOWN);
    }

    @Test
    void reportsDownWithExceptionWhenAuthServerIsUnreachable() {
        Health health = new AuthServerHealthIndicator("http://localhost:1").health();

        assertThat(health.getStatus()).isEqualTo(Status.DOWN);
    }

    private String startServer(int status, String body) throws IOException {
        server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        server.createContext("/actuator/health", exchange -> {
            byte[] payload = body == null ? new byte[0] : body.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(status, payload.length == 0 ? -1 : payload.length);
            if (payload.length > 0) {
                try (OutputStream responseBody = exchange.getResponseBody()) {
                    responseBody.write(payload);
                }
            }
            exchange.close();
        });
        server.start();
        return "http://localhost:" + server.getAddress().getPort();
    }

}
