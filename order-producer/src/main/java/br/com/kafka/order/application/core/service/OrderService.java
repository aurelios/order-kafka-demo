package br.com.kafka.order.application.core.service;

import br.com.kafka.order.application.core.producer.OrderProducer;
import br.com.kafka.order.application.core.request.OrderRequest;
import br.com.kafka.order.avro.EventAvro;
import br.com.kafka.order.avro.OrderAvro;
import br.com.kafka.order.avro.UserAvro;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private final Logger logger = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    private OrderProducer orderProducer;

    public void create(final OrderRequest newOrderRequest){
        logger.info("newOrderRequest={}", newOrderRequest.toString());
        final EventAvro newOrderEvent = convert(newOrderRequest);
        orderProducer.send(newOrderEvent);
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
