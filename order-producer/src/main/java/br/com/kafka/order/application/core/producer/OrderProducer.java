package br.com.kafka.order.application.core.producer;

import br.com.kafka.order.avro.EventAvro;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class OrderProducer {
    private final Logger logger = LoggerFactory.getLogger(OrderProducer.class);
    private static final String ORDER_TOPIC = "order";
    public static final String LOG_TEMPLATE_PRODUCER = "kafkaProducerEvent(key=\"{}\" topic=\"{}\" partition=\"{}\")";
    @Value("${kafka.publisher.send-timeout}")
    private Integer sendTimeout;
    @Autowired
    private KafkaTemplate<String, EventAvro> kafkaTemplate;

    public void send(EventAvro newOrderEvent){
        try{
            final ProducerRecord<String, EventAvro> record = new ProducerRecord<>(ORDER_TOPIC, newOrderEvent);
            SendResult<String, EventAvro> result = kafkaTemplate.send(record).get(sendTimeout, TimeUnit.SECONDS);

            logger.info(LOG_TEMPLATE_PRODUCER, record.key(), record.topic(), result.getRecordMetadata().partition());
        } catch (Exception e) {
            throw new KafkaException("Não foi possível enviar o evento para o tópico do Kafka", e);
        }
    }
}
