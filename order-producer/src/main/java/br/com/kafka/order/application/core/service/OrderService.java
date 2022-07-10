package br.com.kafka.order.application.core.service;

import br.com.kafka.order.application.core.request.OrderRequest;
import br.com.kafka.order.avro.EventAvro;
import br.com.kafka.order.avro.OrderAvro;
import br.com.kafka.order.avro.UserAvro;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class OrderService {

    private final static String ORDER_TOPIC = "order";
    public final String LOG_TEMPLATE_PRODUCER = "kafkaProducerEvent key=\"{}\" topic=\"{}\" partition=\"{}\"";
    private final Logger LOGGER = LoggerFactory.getLogger(OrderService.class);

    @Value("${kafka.publisher.send-timeout}")
    private Integer sendTimeout;

    @Autowired
    private KafkaTemplate<String, EventAvro> kafkaTemplate;

    public void create(final OrderRequest newOrderRequest){
        try{
            LOGGER.info("newOrderRequest={}", newOrderRequest.toString());
            final EventAvro newOrderEvent = convert(newOrderRequest);
            final ProducerRecord<String, EventAvro> record = new ProducerRecord<>(ORDER_TOPIC, newOrderEvent);

            SendResult<String, EventAvro> result = kafkaTemplate.send(record).get(sendTimeout, TimeUnit.SECONDS);
            LOGGER.info(LOG_TEMPLATE_PRODUCER, record.key(), record.topic(), result.getRecordMetadata().partition());
        } catch (Exception e) {
            throw new KafkaException("Não foi possível enviar o evento para o tópico do Kafka", e);
        }
    }

    private EventAvro convert(final OrderRequest newOrderRequest) {
        OrderAvro.Builder newOrderAvro = OrderAvro.newBuilder();
        newOrderAvro.setId(newOrderRequest.getId());
        newOrderAvro.setTotal(String.valueOf(newOrderRequest.getTotal()));
        newOrderAvro.setUser(UserAvro.newBuilder().setLogin(newOrderRequest.getLogin()).build());

        return EventAvro.newBuilder()
                            .setNewPayload(newOrderAvro.build())
                            .build();
    }
}
