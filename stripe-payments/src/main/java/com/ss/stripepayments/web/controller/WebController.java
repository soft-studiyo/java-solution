package com.ss.stripepayments.web.controller;

import com.ss.stripepayments.form.CheckoutForm;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class WebController {

    @Value("${stripe.public.key}")
    private String stripePublicKey;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("checkoutForm", new CheckoutForm());
        model.addAttribute("stripePublicKey", stripePublicKey);
        return "index";
    }

    @GetMapping("/payment")
    public String paymentMethod(Model model) {
        model.addAttribute("checkoutForm", new CheckoutForm());
        return "card-payment";
    }

    @GetMapping("/subscription")
    public String subscriptionMethod(Model model) {
        model.addAttribute("checkoutForm", new CheckoutForm());
        return "subscription";
    }

    @PostMapping("/")
    public String checkout(@ModelAttribute @Valid CheckoutForm checkoutForm, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "index";
        }
        model.addAttribute("stripePublicKey", stripePublicKey);
        model.addAttribute("amount", checkoutForm.getAmount());
//        model.addAttribute("email", checkoutForm.getEmail());
//        model.addAttribute("featureRequest", checkoutForm.getFeatureRequest());
        return "checkout";
    }
}
