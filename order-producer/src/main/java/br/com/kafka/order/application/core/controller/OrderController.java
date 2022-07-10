package br.com.kafka.order.application.core.controller;

import br.com.kafka.order.application.core.request.OrderRequest;
import br.com.kafka.order.application.core.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody final OrderRequest newOrderRequest) {
        orderService.create(newOrderRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
