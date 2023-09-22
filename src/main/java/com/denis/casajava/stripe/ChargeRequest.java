package com.denis.casajava.stripe;

import lombok.Data;

@Data
public class ChargeRequest {

    public enum Currency {
        EUR;
    }
    private String description;
    private int amount;
    private Currency currency;
    private String stripeEmail;
    private String stripeToken;
}