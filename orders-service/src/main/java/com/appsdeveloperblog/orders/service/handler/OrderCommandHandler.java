package com.appsdeveloperblog.orders.service.handler;

import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.appsdeveloperblog.core.dto.commands.ApproveOrderCommand;
import com.appsdeveloperblog.orders.service.OrderService;

@Component
@KafkaListener(topics = "${orders.commands.topic.name}")
public class OrderCommandHandler {
    private final OrderService orderService;

    public OrderCommandHandler(
      final OrderService orderService) {
        this.orderService = orderService;
    }

    @KafkaHandler
    public void handleOrderCommand(@Payload ApproveOrderCommand approveOrderCommand) {
        orderService.approveOrder(approveOrderCommand.getOrderId());      
    }
}
