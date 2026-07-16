package com.demo.myapp.solid.isp;

import java.math.BigDecimal;

// ISP: a separate narrow capability — kept apart from Chargeable so a
// write-only or charge-only integration (rare, but real) isn't forced to
// implement refunds it will never support.
public interface Refundable {
    String refund(BigDecimal amount);
}
