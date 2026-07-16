package com.demo.myapp.solid.isp;

// ISP: tokenization only makes sense for card-shaped payment instruments.
// Keeping it as its own interface — instead of folding it into one fat
// PaymentGateway contract — is what lets CashGateway simply not implement it.
public interface Tokenizable {
    String tokenize(String rawCardNumber);
}
