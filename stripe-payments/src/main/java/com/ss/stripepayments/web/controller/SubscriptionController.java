package com.ss.stripepayments.web.controller;

import com.google.gson.JsonSyntaxException;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.model.checkout.Session;
import com.stripe.net.ApiResource;
import com.stripe.net.Webhook;
import com.stripe.param.PriceListParams;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
public class SubscriptionController {

    final String YOUR_DOMAIN = "http://localhost:8080";

    @Value("${stripe.webhook.secret}")
    private String endpointSecret;

    @PostMapping("/create-checkout-session")
    public String createCheckoutSession(@RequestParam("lookup_key") String lookupKey) throws StripeException {

            PriceListParams priceParams = PriceListParams.builder().addLookupKeys(lookupKey).build();
            PriceCollection prices = Price.list(priceParams);

            SessionCreateParams params = SessionCreateParams.builder()
                    .addLineItem(
                            SessionCreateParams.LineItem.builder().setPrice(prices.getData().get(0).getId()).setQuantity(1L).build())
                    .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                    .setSuccessUrl(YOUR_DOMAIN + "/success.html?session_id={CHECKOUT_SESSION_ID}")
                    .setCancelUrl(YOUR_DOMAIN + "/cancel.html")
                    .build();
            return "success";

    }

    @PostMapping("/create-portal-session")
    public String createPortalSession(@RequestParam("session-id") String sessionId) throws StripeException {

            // For demonstration purposes, we're using the Checkout session to retrieve the
            // customer ID.
            // Typically this is stored alongside the authenticated user in your database.
            // Deserialize request from our front end.
            Session checkoutSession = Session.retrieve(sessionId);

            String customer = checkoutSession.getCustomer();
            // Authenticate your user.
            com.stripe.param.billingportal.SessionCreateParams params = new com.stripe.param.billingportal.SessionCreateParams.Builder()
                    .setReturnUrl(YOUR_DOMAIN).setCustomer(customer).build();

            com.stripe.model.billingportal.Session portalSession = com.stripe.model.billingportal.Session.create(params);



            return "#";

    }

    @PostMapping("/webhook")
    public String webhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
            Event event = null;
            try {
                event = ApiResource.GSON.fromJson(payload, Event.class);
            } catch (JsonSyntaxException e) {
                // Invalid payload
                System.out.println("⚠️  Webhook error while parsing basic request.");
                return "";
            }
            if (endpointSecret != null && sigHeader != null) {
                // Only verify the event if you have an endpoint secret defined.
                // Otherwise use the basic event deserialized with GSON.
                try {
                    event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
                } catch (SignatureVerificationException e) {
                    // Invalid signature
                    System.out.println("⚠️  Webhook error while validating signature.");
//                    response.status(400);
                    return "";
                }
            }
            // Deserialize the nested object inside the event
            EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
            StripeObject stripeObject = null;
            if (dataObjectDeserializer.getObject().isPresent()) {
                stripeObject = dataObjectDeserializer.getObject().get();
            } else {
                // Deserialization failed, probably due to an API version mismatch.
                // Refer to the Javadoc documentation on `EventDataObjectDeserializer` for
                // instructions on how to handle this case, or return an error here.
            }
            // Handle the event
            Subscription subscription = null;
            switch (event.getType()) {
                case "customer.subscription.deleted":
                    subscription = (Subscription) stripeObject;
                    // Then define and call a function to handle the event
                    // customer.subscription.deleted
                    // handleSubscriptionTrialEnding(subscription);
                case "customer.subscription.trial_will_end":
                    subscription = (Subscription) stripeObject;
                    // Then define and call a function to handle the event
                    // customer.subscription.trial_will_end
                    // handleSubscriptionDeleted(subscriptionDeleted);
                case "customer.subscription.created":
                    subscription = (Subscription) stripeObject;
                    // Then define and call a function to handle the event
                    // customer.subscription.created
                    // handleSubscriptionCreated(subscription);
                case "customer.subscription.updated":
                    subscription = (Subscription) stripeObject;
                    // Then define and call a function to handle the event
                    // customer.subscription.updated
                    // handleSubscriptionUpdated(subscription);
                    // ... handle other event types
                default:
                    System.out.println("Unhandled event type: " + event.getType());
            }
//            response.status(200);
            return "#";
    }
}
