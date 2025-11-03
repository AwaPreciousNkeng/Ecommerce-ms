package com.codewithpcodes.ecommerce.kafka;

import com.codewithpcodes.ecommerce.customer.CustomerResponse;
import com.codewithpcodes.ecommerce.order.PaymentMethod;
import com.codewithpcodes.ecommerce.product.PurchaseResponse;

import java.math.BigDecimal;
import java.util.List;

public record OrderConfirmation(
        String orderReference,
        BigDecimal totalAmount,
        PaymentMethod paymentMethod,
        CustomerResponse customer,
        List<PurchaseResponse> products
) {
}
