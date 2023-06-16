package br.com.kafka.order.infrastructure;

import br.com.kafka.order.avro.EventAvro;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.retry.annotation.EnableRetry;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;


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
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
        return factory;
    }

    @Bean
    public ConsumerFactory<String, EventAvro> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerProps());
    }

    private Map<String, Object> consumerProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092,localhost:9093,localhost:9094");
        props.put(KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8081");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
        props.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, true); // Permitir leitura da estrutura avro
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, "order-consumer:"+ LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        return props;
    }
}
