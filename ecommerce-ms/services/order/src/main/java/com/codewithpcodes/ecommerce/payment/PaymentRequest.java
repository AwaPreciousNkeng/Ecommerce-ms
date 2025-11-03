package com.codewithpcodes.ecommerce.payment;

import com.codewithpcodes.ecommerce.customer.CustomerResponse;
import com.codewithpcodes.ecommerce.order.PaymentMethod;

import java.math.BigDecimal;

public record PaymentRequest(
        BigDecimal amount,
        PaymentMethod paymentMethod,
        Integer orderId,
        String orderReference,
        CustomerResponse customer
) {
}
