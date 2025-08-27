package com.codewithpcodes.ecommerce.kafka.order;

import com.codewithpcodes.ecommerce.kafka.payment.Customer;
import com.codewithpcodes.ecommerce.kafka.payment.PaymentMethod;
import com.codewithpcodes.ecommerce.kafka.payment.Product;

import java.math.BigDecimal;
import java.util.List;

public record OrderConfirmation(
        String orderReference,
        BigDecimal amount,
        PaymentMethod paymentMethod,
        Customer customer,
        List<Product> products
) {
}
