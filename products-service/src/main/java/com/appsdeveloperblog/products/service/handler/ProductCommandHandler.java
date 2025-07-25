package com.appsdeveloperblog.products.service.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.appsdeveloperblog.core.dto.Product;
import com.appsdeveloperblog.core.dto.commands.ReserveProductCommand;
import com.appsdeveloperblog.core.dto.events.ProductReservationFailedEvent;
import com.appsdeveloperblog.core.dto.events.ProductReservedEvent;
import com.appsdeveloperblog.products.service.ProductService;

@Component
@KafkaListener(topics = {"${products.commands.topic.name}"})
public class ProductCommandHandler {
  private final static Logger logger = LoggerFactory.getLogger(ProductCommandHandler.class);

  private final ProductService productService;
  private final KafkaTemplate<String, Object> kafkaTemplate;
  private final String productsEventsTopicName;

  public ProductCommandHandler(
    final ProductService productService,
    final KafkaTemplate<String, Object> kafkaTemplate,
    @Value("${products.events.topic.name}") String productsEventsTopicName) {
      this.productService = productService;
      this.kafkaTemplate = kafkaTemplate;
      this.productsEventsTopicName = productsEventsTopicName;
  }

  @KafkaHandler
  public void handleReserveProductCommand(@Payload ReserveProductCommand reserveProductCommand) {
    try {
      Product reservedProduct = new Product(
            reserveProductCommand.getProductId(),
            reserveProductCommand.getProductQuantity()
      );

      Product foundProduct = productService.findById(reservedProduct.getId());
      reservedProduct.setName(foundProduct.getName());
      reservedProduct.setPrice(foundProduct.getPrice());
            
      reservedProduct = productService.reserve(reservedProduct, reserveProductCommand.getOrderId());

      ProductReservedEvent productReservedEvent = new ProductReservedEvent(
            reserveProductCommand.getProductId(),
            reserveProductCommand.getProductQuantity(),
            reserveProductCommand.getOrderId(),
            reservedProduct.getPrice());

      kafkaTemplate.send(productsEventsTopicName, productReservedEvent);
    } catch (Exception e) {
      logger.error("Failed to reserve product", e);

      ProductReservationFailedEvent productReservationFailedEvent = new ProductReservationFailedEvent(
            reserveProductCommand.getProductId(),
            reserveProductCommand.getOrderId(),
            reserveProductCommand.getProductQuantity());
      kafkaTemplate.send(productsEventsTopicName, productReservationFailedEvent);
    }
  }
}
