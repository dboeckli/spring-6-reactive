package guru.springframework.spring6reactive.config;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.security.autoconfigure.actuate.web.reactive.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;

import static guru.springframework.spring6reactive.controller.BeerController.BEER_PATH;
import static guru.springframework.spring6reactive.controller.CustomerController.CUSTOMER_PATH;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

    private static final List<String> ALLOWED_HEADERS = List.of("*");
    private static final List<String> ALLOWED_METHODS = List.of("POST", "GET", "PUT", "OPTIONS", "DELETE", "PATCH");

    public static final String READ_SCOPE = "SCOPE_message.read";
    public static final String WRITE_SCOPE = "SCOPE_message.write";

    private final AllowedOriginConfig allowedOriginConfig;

    @PostConstruct
    public void init() {
        log.info("### Allowed origins: {}", allowedOriginConfig);
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    SecurityWebFilterChain springSecurityActuator(ServerHttpSecurity http, CorsConfigurationSource corsConfigurationSource) {
        return http
            .securityMatcher(EndpointRequest.toAnyEndpoint())
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
            .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
            .authorizeExchange(exchange -> exchange.anyExchange().permitAll())
            .build();
    }

    @Bean
    @Order(Ordered.LOWEST_PRECEDENCE)
    SecurityWebFilterChain springSecurity(ServerHttpSecurity http, CorsConfigurationSource corsConfigurationSource) {
        return http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            .headers(headers -> headers.frameOptions(ServerHttpSecurity.HeaderSpec.FrameOptionsSpec::disable))
            .authorizeExchange(exchange -> exchange
                .pathMatchers(
                    "/favicon.ico",
                    "/v3/api-docs",
                    "/v3/api-docs.yaml",
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html"
                ).permitAll()

                .pathMatchers(HttpMethod.GET, CUSTOMER_PATH + "/**").hasAuthority(READ_SCOPE)
                .pathMatchers(HttpMethod.POST, CUSTOMER_PATH + "/**").hasAuthority(WRITE_SCOPE)
                .pathMatchers(HttpMethod.PUT, CUSTOMER_PATH + "/**").hasAuthority(WRITE_SCOPE)
                .pathMatchers(HttpMethod.PATCH, CUSTOMER_PATH + "/**").hasAuthority(WRITE_SCOPE)
                .pathMatchers(HttpMethod.DELETE, CUSTOMER_PATH + "/**").hasAuthority(WRITE_SCOPE)
                .pathMatchers(HttpMethod.GET, BEER_PATH + "/**").hasAuthority(READ_SCOPE)
                .pathMatchers(HttpMethod.POST, BEER_PATH + "/**").hasAuthority(WRITE_SCOPE)
                .pathMatchers(HttpMethod.PUT, BEER_PATH + "/**").hasAuthority(WRITE_SCOPE)
                .pathMatchers(HttpMethod.PATCH, BEER_PATH + "/**").hasAuthority(WRITE_SCOPE)
                .pathMatchers(HttpMethod.DELETE, BEER_PATH + "/**").hasAuthority(WRITE_SCOPE)
                .anyExchange().authenticated()
            )
            .oauth2ResourceServer(oAuth2 -> oAuth2.jwt(Customizer.withDefaults()))
            .build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(allowedOriginConfig.getAllowedOrigins());
        configuration.setAllowedMethods(ALLOWED_METHODS);
        configuration.setAllowedHeaders(ALLOWED_HEADERS);
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Component
    @ConfigurationProperties(prefix = "security.cors")
    @Data
    public static class AllowedOriginConfig {
        private List<String> allowedOrigins;
    }
}
