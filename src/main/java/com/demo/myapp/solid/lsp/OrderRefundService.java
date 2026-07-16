package com.demo.myapp.solid.lsp;

import org.springframework.stereotype.Service;

import java.util.List;

// LSP proof: this method treats every Order subtype identically — no
// instanceof, no downcasting, no per-type branch. If a future subtype ever
// needed a type check here, that would be the signal an LSP violation had
// crept into the hierarchy.
@Service
public class OrderRefundService {

    public List<String> refundAll(List<Order> orders) {
        return orders.stream()
                .map(Order::refund)
                .toList();
    }
}
