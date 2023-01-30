package com.ss.stripepayments;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StripePaymentsApplication {

	@Value("${stripe.api.key}")
	private String stripeApiKey;

	@PostConstruct
	public void setup() {
		// This is your test secret API key.
		Stripe.apiKey = stripeApiKey;
	}

	public static void main(String[] args) {
		SpringApplication.run(StripePaymentsApplication.class, args);
	}

}
