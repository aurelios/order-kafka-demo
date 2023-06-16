package br.com.kafka.order.infrastructure;

import br.com.kafka.order.avro.EventAvro;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfiguration {

    @Autowired
    private KafkaProperties kafkaProperties;

    @Bean
    public ProducerFactory<String, EventAvro> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public KafkaTemplate<String, EventAvro> kafkaTemplate() {
        return new KafkaTemplate<String, EventAvro>(producerFactory());
    }

    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<String, Object>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092,localhost:9093,localhost:9094");
        props.put(KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8081");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        /*
        See https://kafka.apache.org/documentation/#producerconfigs for more properties
        ack = 0 ------> o producer envia a mensagem e não aguarda por nenhuma notificação de recebimento do servidor.(SEM RETORNO + MAIS PERFORMANCE)
        ack = 1 ------> o LEADER irá salvar o registro em seu local, mas responderá sem aguardar o reconhecimento total de replicamento de todos os servidores.(RETORNO PARCIAL + MÉDIA PERFORMANCE)
        ack = -1/all -> o LEADER salva replica para os FOLLOWERS e só após todos notificarem que foi salvo que o producer é notificado.(RETORNO TOTAL + BAIXA PERFORMANCE)
         */
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        //props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true"); // true = default - kafka descarta uma das mensagens e ainda garante a ordem correta utilizando o timestamp.

        return props;
    }

    @Bean
    public KafkaAdmin admin() {
        Map<String, Object> configs = new HashMap<String, Object>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092,localhost:9093,localhost:9094");
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic orderTopic() {
        return TopicBuilder.name("order")
                .partitions(3)
                .replicas(3)
                .build();
    }

}
