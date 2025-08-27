package com.codewithpcodes.ecommerce.order;

import com.codewithpcodes.ecommerce.customer.CustomerClient;
import com.codewithpcodes.ecommerce.exception.BusinessException;
import com.codewithpcodes.ecommerce.kafka.OrderConfirmation;
import com.codewithpcodes.ecommerce.kafka.OrderProducer;
import com.codewithpcodes.ecommerce.orderline.OrderLineRequest;
import com.codewithpcodes.ecommerce.orderline.OrderLineService;
import com.codewithpcodes.ecommerce.payment.PaymentClient;
import com.codewithpcodes.ecommerce.payment.PaymentRequest;
import com.codewithpcodes.ecommerce.product.ProductClient;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper mapper;
    private final CustomerClient customerClient;
    private final ProductClient productClient;
    private final OrderLineService orderlineService;
    private final OrderProducer orderProducer;
    private final PaymentClient paymentClient;


    public Integer createOrder(OrderRequest request) {
        //Check the customer --> Openfeign
        var customer = this.customerClient.findCustomerById(request.customerId())
                .orElseThrow(() -> new BusinessException("Cannot create order. " +
                        "Customer not found with the provided ID:: ." + request.customerId()));

        //Purchase the product --> Using the Product-ms
        var purchasedProducts = this.productClient.purchaseProducts(request.products());

        //Persist the Order
        var order = this.orderRepository.save(mapper.toOrder(request));

        //Persist the order lines
        for (PurchaseRequest purchaseRequest: request.products()) {
            orderlineService.saveOrderLine(
                    new OrderLineRequest(
                            null,
                            order.getId(),
                            purchaseRequest.productId(),
                            purchaseRequest.quantity()
                    )
            );
        }

        //Start the payment process
        var paymentRequest = new PaymentRequest(
                request.amount(),
                request.paymentMethod(),
                order.getId(),
                order.getReference(),
                customer
        );
        paymentClient.requestOrderPayment(paymentRequest);

        orderProducer.sendOrderConfirmation(
                new OrderConfirmation(
                        request.reference(),
                        request.amount(),
                        request.paymentMethod(),
                        customer,
                        purchasedProducts
                ));
        return order.getId();


        //TODO Send the order confirmation --> notification-ms(kafka)
    }

    public List<OrderResponse> findAll() {
        return orderRepository.findAll()
                .stream()
                .map(mapper::fromOrder)
                .collect(Collectors.toList());
    }

    public OrderResponse findById(Integer orderId) {
        return orderRepository.findById(orderId)
                .map(mapper::fromOrder)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Cannot find order with the provided ID %d", orderId)));
    }
}
