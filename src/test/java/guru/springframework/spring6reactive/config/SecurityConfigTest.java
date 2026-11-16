package guru.springframework.spring6reactive.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.web.reactive.function.client.WebClient;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SecurityConfigTest {

    @Test
    void createsLazyJwtDecoderForConfiguredIssuer() {
        SecurityConfig securityConfig = new SecurityConfig(new SecurityConfig.AllowedOriginConfig());

        ReactiveJwtDecoder jwtDecoder = securityConfig.jwtDecoder(WebClient.builder(), "http://localhost:1/issuer");

        assertThatThrownBy(() -> jwtDecoder.decode("invalid-token").block()).isInstanceOf(JwtException.class);
    }

}
