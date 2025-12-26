package guru.springframework.spring6reactive.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

@Configuration
@Profile("!test & !it")
@Slf4j
public class H2ConsoleConfiguration {

    private org.h2.tools.Server webServer;

    private org.h2.tools.Server tcpServer;

    private static final String WEB_PORT = "8088";
    private static final String TCP_PORT = "9098";

    @EventListener(ContextRefreshedEvent.class)
    public void start() throws java.sql.SQLException {
        this.webServer = org.h2.tools.Server.createWebServer("-webPort", WEB_PORT, "-tcpAllowOthers").start();
        this.tcpServer = org.h2.tools.Server.createTcpServer("-tcpPort", TCP_PORT, "-tcpAllowOthers").start();
        log.info("### H2Console started on http://localhost:{}. State: {}", webServer.getPort(), webServer.getStatus());
    }

    @EventListener(ContextClosedEvent.class)
    public void stop() {
        this.tcpServer.stop();
        this.webServer.stop();
        log.info("### H2Console Stopped. State: {}", webServer.getStatus());
    }

}
