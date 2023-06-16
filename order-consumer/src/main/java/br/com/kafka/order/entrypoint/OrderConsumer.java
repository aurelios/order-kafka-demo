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

    private static final String ORDER_TOPIC = "order";
    private final Logger logger = LoggerFactory.getLogger(OrderConsumer.class);
    public static final String LOG_SUCCESS_TEMPLATE_CONSUMER = "OrderConsumerEvent(thread={} orderId=\"{}\" orderTotal=\"{}\" topic=\"{}\" partition=\"{}\" offset=\"{}\")";
    public static final String LOG_ERROR_TEMPLATE_CONSUMER = "OrderConsumerEvent ERROR (orderId=\"{}\" topic=\"{}\" partition=\"{}\" offset=\"{}\")";

    /** Nao recomendado
     * @KafkaListener(id = "thing2", topicPartitions =
     *         { @TopicPartition(topic = "topic1", partitions = { "0", "1" }),
     *           @TopicPartition(topic = "topic2", partitions = "0",
     *              partitionOffsets = @PartitionOffset(partition = "1", initialOffset = "100"))
     *         })
     */
    @KafkaListener(topics = ORDER_TOPIC, containerFactory = "kafkaRetryListenerContainerFactory", groupId = "order")
    public void consume(ConsumerRecord<String, EventAvro> recordOrder) {

        final EventAvro event = recordOrder.value();
        final OrderAvro newOrder = event.getNewPayload();

        logger.info(LOG_SUCCESS_TEMPLATE_CONSUMER,Thread.currentThread().getId(), recordOrder.key(), newOrder.getTotal(), recordOrder.topic(), recordOrder.partition(), recordOrder.offset());

        try {
            if(new BigDecimal(newOrder.getTotal()).doubleValue() > 1000){
                throw new IllegalArgumentException("Não é permitido pedidos com total maior que 1000");
            }
            //logger.info(String.format("OrderConsumerEvent(pedido=%s no total=%s para o usuario=%s) processado com sucesso!", newOrder.getId(), newOrder.getTotal(), newOrder.getUser().getLogin()));
        } catch (Exception e) {
            logger.error(LOG_ERROR_TEMPLATE_CONSUMER, recordOrder.key(), recordOrder.topic(), recordOrder.partition(), recordOrder.offset());
            throw e;
        }
    }
}
