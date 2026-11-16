package guru.springframework.spring6reactive.config;

import io.opentelemetry.api.OpenTelemetry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OpenTelemetryAppenderInitializerTest {

    @Mock
    private ObjectProvider<OpenTelemetry> openTelemetryProvider;

    @Test
    void doesNothingWhenNoOpenTelemetryBeanIsAvailable() {
        when(openTelemetryProvider.getIfAvailable()).thenReturn(null);

        new OpenTelemetryAppenderInitializer(openTelemetryProvider).afterPropertiesSet();

        verify(openTelemetryProvider).getIfAvailable();
    }

}
