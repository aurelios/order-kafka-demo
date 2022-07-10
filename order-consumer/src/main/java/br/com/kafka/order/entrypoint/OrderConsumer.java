package br.com.kafka.order.entrypoint;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import br.com.kafka.order.avro.EventAvro;
import br.com.kafka.order.avro.OrderAvro;

import java.math.BigDecimal;

@Component
public class OrderConsumer {

    private final static String ORDER_TOPIC = "order";
    private final Logger LOGGER = LoggerFactory.getLogger(OrderConsumer.class);
    public final String LOG_SUCCESS_TEMPLATE_CONSUMER = "kafkaConsumerEvent orderId=\"{}\" orderTotal=\"{}\" topic=\"{}\" partition=\"{}\" offset=\"{}\"";
    public final String LOG_ERROR_TEMPLATE_CONSUMER = "kafkaConsumerEvent orderId=\"{}\" topic=\"{}\" partition=\"{}\" offset=\"{}\"";

    /** Nao recomendado
     * @KafkaListener(id = "thing2", topicPartitions =
     *         { @TopicPartition(topic = "topic1", partitions = { "0", "1" }),
     *           @TopicPartition(topic = "topic2", partitions = "0",
     *              partitionOffsets = @PartitionOffset(partition = "1", initialOffset = "100"))
     *         })
     */
    @KafkaListener(topics = ORDER_TOPIC, containerFactory = "kafkaRetryListenerContainerFactory", groupId = "order")
    public void listenPerson(ConsumerRecord<String, EventAvro> record) {

        final EventAvro event = record.value();
        final OrderAvro newOrder = event.getNewPayload();

        LOGGER.info(LOG_SUCCESS_TEMPLATE_CONSUMER, record.key(), newOrder.getTotal(), record.topic(), record.partition(), record.offset());

        try {
            if(new BigDecimal(newOrder.getTotal()).doubleValue() > 1000){
                throw new IllegalArgumentException("Nao eh permitido pedidos maiores que 1000");
            }
            LOGGER.info(String.format("kafkaConsumerEvent Pedido=%s no total=%s para o usuario=%s processado com sucesso!", newOrder.getId(), newOrder.getTotal(), newOrder.getUser().getLogin()));
        } catch (Exception e) {
            LOGGER.error(LOG_ERROR_TEMPLATE_CONSUMER, record.key(), record.topic(), record.partition(), record.offset());
            throw e;
        }
    }
}
