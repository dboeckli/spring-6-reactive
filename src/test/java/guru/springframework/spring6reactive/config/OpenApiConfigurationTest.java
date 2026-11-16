package guru.springframework.spring6reactive.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.info.BuildProperties;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

class OpenApiConfigurationTest {

    private OpenApiConfiguration configuration;

    @BeforeEach
    void setUp() {
        Properties properties = new Properties();
        properties.setProperty("name", "spring-6-reactive");
        properties.setProperty("version", "1.0.0");
        configuration = new OpenApiConfiguration(new BuildProperties(properties));
        ReflectionTestUtils.setField(configuration, "authorizationUrl", "http://auth.example/oauth2/auth");
        ReflectionTestUtils.setField(configuration, "tokenUrl", "http://auth.example/oauth2/token");
        ReflectionTestUtils.setField(configuration, "refreshUrl", "http://auth.example/oauth2/refresh-token");
    }

    @Test
    void appliesBuildInfoAndUpdatesOAuthUrls() {
        OAuthFlows clientCredentialsFlows = new OAuthFlows().clientCredentials(new OAuthFlow());
        SecurityScheme oauthScheme = new SecurityScheme().type(SecurityScheme.Type.OAUTH2)
            .flows(clientCredentialsFlows);
        SecurityScheme oauthSchemeWithoutClientCredentials = new SecurityScheme().type(SecurityScheme.Type.OAUTH2)
            .flows(new OAuthFlows());
        SecurityScheme httpScheme = new SecurityScheme().type(SecurityScheme.Type.HTTP);

        Components components = new Components().addSecuritySchemes("oauth", oauthScheme)
            .addSecuritySchemes("oauthWithoutClientCredentials", oauthSchemeWithoutClientCredentials)
            .addSecuritySchemes("http", httpScheme);
        OpenAPI openApi = new OpenAPI().info(new Info()).components(components);

        configuration.customerGlobalHeaderOpenApiCustomizer().customise(openApi);

        assertThat(openApi.getInfo().getTitle()).isEqualTo("spring-6-reactive");
        assertThat(openApi.getInfo().getVersion()).isEqualTo("1.0.0");
        assertThat(clientCredentialsFlows.getClientCredentials().getAuthorizationUrl())
            .isEqualTo("http://auth.example/oauth2/auth");
        assertThat(clientCredentialsFlows.getClientCredentials().getTokenUrl())
            .isEqualTo("http://auth.example/oauth2/token");
        assertThat(clientCredentialsFlows.getClientCredentials().getRefreshUrl())
            .isEqualTo("http://auth.example/oauth2/refresh-token");
    }

}
