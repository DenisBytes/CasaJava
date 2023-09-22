package com.denis.casajava.services;

import com.denis.casajava.models.Pricing;
import com.denis.casajava.repositories.PricingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;


@Service
public class PricingService {

    @Autowired
    private PricingRepository pricingRepository;

    // Get the one and only Pricing object (assuming Pricing ID is always 1)
    public Pricing getOrCreatePricing() {
        Optional<Pricing> pricingOptional = pricingRepository.findById(1L); // Assuming ID 1 for the single Pricing entity

        if (pricingOptional.isPresent()) {
            return pricingOptional.get();
        } else {
            // Create a new Pricing entity with default values
            Pricing pricing = new Pricing();
            pricing.setDefaultPrice(80); // Set the default price here
            return pricingRepository.save(pricing);
        }
    }

    // Get the default price
    public double getDefaultPrice() {
        Pricing pricing = pricingRepository.findById(1L).orElse(new Pricing()); // Assuming Pricing ID is always 1
        return pricing.getDefaultPrice();
    }

    // Get custom prices map
    public Map<String, Double> getCustomPrices() {
        Pricing pricing = pricingRepository.findById(1L).orElse(new Pricing()); // Assuming Pricing ID is always 1
        return pricing.getCustomPrices();
    }

    public void setDefaultPrice(double defaultPrice) {
        Pricing pricing = pricingRepository.findById(1L).orElse(new Pricing()); // Assuming Pricing ID is always 1
        pricing.setDefaultPrice(defaultPrice);
        pricingRepository.save(pricing);
    }

    public void addCustomPrice(String date, double price) {
        Pricing pricing = pricingRepository.findById(1L).orElse(new Pricing()); // Assuming Pricing ID is always 1
        pricing.getCustomPrices().put(date,price);
        pricingRepository.save(pricing);
    }

    public void deleteCustomPrice(String date) {
        Pricing pricing = pricingRepository.findById(1L).orElse(new Pricing());
         if (pricing.getCustomPrices().containsKey(date)) {
             pricing.getCustomPrices().remove(date);
             pricingRepository.save(pricing);
         } else {
             throw new IllegalArgumentException("Custom price not found for the given date");
         }
    }

}