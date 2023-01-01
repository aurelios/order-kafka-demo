package br.com.kafka.order.infrastructure;

import br.com.kafka.order.avro.EventAvro;
import br.com.kafka.order.avro.OrderAvro;
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.boot.web.servlet.context.ServletWebServerInitializedEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

//import static org.springframework.kafka.listener.adapter.RetryingMessageListenerAdapter.CONTEXT_RECORD;

@EnableRetry
@Configuration
public class KafkaConfiguration {

    @Autowired
    private KafkaProperties kafkaProperties;

    public final String LOG_DIED_EVENT_TEMPLATE_CONSUMER = ">>>>>> DiedEvent orderId=\"{}\" orderTotal=\"{}\" topic=\"{}\" partition=\"{}\" offset=\"{}\"";
    private final Logger LOGGER = LoggerFactory.getLogger(KafkaConfiguration.class);

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, EventAvro>> kafkaRetryListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, EventAvro> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(1); // numero threads que o um consumidor pode assumir

        /*factory.setRetryTemplate(retryTemplate());
        factory.setRecoveryCallback(context -> {// APOS chegar no maximo tempo, eh possivel realizar alguma acao.
            handlerRecovery(context);
            return context;
        });*/
        return factory;
    }

    /*private void handlerRecovery(org.springframework.retry.RetryContext context) {
        ConsumerRecord record = (ConsumerRecord) context.getAttribute(CONTEXT_RECORD);
        final EventAvro event = (EventAvro) record.value();
        final OrderAvro newOrder = event.getNewPayload();
        LOGGER.info(LOG_DIED_EVENT_TEMPLATE_CONSUMER, newOrder.getId(), newOrder.getTotal(), record.topic(), record.partition(), record.offset());
    }*/

    public RetryTemplate retryTemplate(){
        final RetryTemplate retryTemplate = new RetryTemplate();
        ExponentialBackOffPolicy exponentialBackOffPolicy = new ExponentialBackOffPolicy();
        exponentialBackOffPolicy.setMultiplier(2); // numero de vezes a ser multiplicado
        exponentialBackOffPolicy.setInitialInterval(1000); // intervalo de tempo de cada retry VS o multiplicado
        exponentialBackOffPolicy.setMaxInterval(20000); // intervalo maximo
        retryTemplate.setBackOffPolicy(exponentialBackOffPolicy);
        SimpleRetryPolicy simpleRetryPolicy = new SimpleRetryPolicy();
        simpleRetryPolicy.setMaxAttempts(5); //maximo de tentativas
        retryTemplate.setRetryPolicy(simpleRetryPolicy);
        return retryTemplate;
    }

    @Bean
    public ConsumerFactory<String, EventAvro> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerProps());
    }

    private Map<String, Object> consumerProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8081");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
        props.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, true); // Permitir leitura da estrutura avro
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, "order-consumer:"+ LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        return props;
    }
}
