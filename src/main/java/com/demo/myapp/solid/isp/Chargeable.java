package com.demo.myapp.solid.isp;

import java.math.BigDecimal;

// ISP: a narrow, single-capability interface — every payment gateway can charge.
public interface Chargeable {
    String charge(BigDecimal amount);
}
