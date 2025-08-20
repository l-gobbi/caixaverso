package org.acme.menssageria.facade;

import com.azure.messaging.eventhubs.EventData;
import com.azure.messaging.eventhubs.EventHubClientBuilder;
import com.azure.messaging.eventhubs.EventHubProducerClient;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.Collections;

@Slf4j
@ApplicationScoped
public class EnviarMensagemEventHubFacade {

    private EventHubProducerClient eventHubProducerClient;

    @ConfigProperty(name = "eventhub.connection-string")
    String connectionString;

    @PostConstruct
    void init() {
        if (connectionString == null || connectionString.isEmpty()) {
            throw new IllegalStateException(("Dados de conexão para o event hub não configurados."));
        }
        eventHubProducerClient = new EventHubClientBuilder()
                .connectionString(connectionString)
                .buildProducerClient();
    }

    public void executar(String json) {
        EventData eventData = new EventData(json);
        eventHubProducerClient.send(Collections.singletonList(eventData));
    }
}
