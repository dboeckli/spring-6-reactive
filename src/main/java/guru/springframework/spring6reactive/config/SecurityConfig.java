package guru.springframework.spring6reactive.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.security.reactive.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private static final List<String> ALLOWED_HEADERS = List.of("*");
    private static final List<String> ALLOWED_METHODS = List.of("POST", "GET", "PUT", "OPTIONS", "DELETE", "PATCH");

    @Value("${security.cors.allowed-origins}")
    private List<String> allowedOrigins;

    @Bean
    @Order(1)
    public SecurityWebFilterChain actuatorSecurityFilterChain(ServerHttpSecurity http) {
        http.securityMatcher
                (EndpointRequest.toAnyEndpoint()).authorizeExchange(authorize -> authorize.anyExchange().permitAll());  // allow actuator endpoints to be accessed without authentication
        return http.build();
    }

    @Bean
    @Order(2)
    SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, CorsConfigurationSource corsConfigurationSource) {
        http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            .authorizeExchange(authorizeExchange -> authorizeExchange
                .pathMatchers(
                    "/favicon.ico",
                    "/v3/api-docs",
                    "/v3/api-docs.yaml",
                    "/v3/api-docs/**", 
                    "/swagger-ui/**", 
                    "/swagger-ui.html").permitAll() // allow Swagger/Openapi endpoints to be accessed without authentication
                //.pathMatchers("/api/v3/customer/**", "/api/v3/beer/**").hasRole("ADMIN") // FOR FUTURE USE ONLY
                .anyExchange().authenticated()
            )
            .oauth2ResourceServer(oAuth2ResourceServerSpec -> oAuth2ResourceServerSpec.jwt(Customizer.withDefaults()));
        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(allowedOrigins);
        configuration.setAllowedMethods(ALLOWED_METHODS);
        configuration.setAllowedHeaders(ALLOWED_HEADERS);
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
