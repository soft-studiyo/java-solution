package com.ss.stripepayments.web.controller;

import com.ss.stripepayments.dto.CustomerData;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController("/api")
public class StripPaymentControllerAPI {

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @PostMapping("/customers")
    public CustomerData createCustomer(@RequestBody CustomerData customerData) throws StripeException {
        Stripe.apiKey = stripeApiKey;
        Map<String, Object> params = new HashMap<>();
        params.put("name", customerData.getName());
        params.put("email", customerData.getEmail());
        Customer customer = Customer.create(params);
        customerData.setCustomerId(customer.getId());
        return customerData;
    }
}
