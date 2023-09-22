package com.denis.casajava.models;

import jakarta.persistence.*;

import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "pricing")
public class Pricing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double defaultPrice = 80.0; // Set the default price to 80.0

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "custom_prices", joinColumns = @JoinColumn(name = "pricing_id"))
    @MapKeyColumn(name = "date")
    @Column(name = "price")
    private Map<String, Double> customPrices = new HashMap<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getDefaultPrice() {
        return defaultPrice;
    }

    public void setDefaultPrice(double defaultPrice) {
        this.defaultPrice = defaultPrice;
    }

    public Map<String, Double> getCustomPrices() {
        return customPrices;
    }

    public void setCustomPrices(Map<String, Double> customPrices) {
        this.customPrices = customPrices;
    }

    public void addCustomPrice(String date, double price) {
        customPrices.put(date, price);
    }
}
