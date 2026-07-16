package com.demo.myapp.solid;

import com.demo.myapp.solid.dip.CheckoutService;
import com.demo.myapp.solid.dip.PaymentGateway;
import com.demo.myapp.solid.dip.SquareGatewayImpl;
import com.demo.myapp.solid.isp.CardGateway;
import com.demo.myapp.solid.isp.CashGateway;
import com.demo.myapp.solid.isp.Chargeable;
import com.demo.myapp.solid.isp.Tokenizable;
import com.demo.myapp.solid.lsp.CardOrder;
import com.demo.myapp.solid.lsp.CashOrder;
import com.demo.myapp.solid.lsp.GiftCardOrder;
import com.demo.myapp.solid.lsp.Order;
import com.demo.myapp.solid.lsp.OrderRefundService;
import com.demo.myapp.solid.ocp.PaymentProcessor;
import com.demo.myapp.solid.srp.OrderProcessor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// One controller, five endpoints — each exercises a single SOLID principle
// through real Spring-managed beans so the principle isn't just a definition,
// it's something you can curl and watch behave.
@RestController
@RequestMapping("/api/solid")
public class SolidController {

    private final OrderProcessor orderProcessor;           // SRP
    private final PaymentProcessor paymentProcessor;       // OCP
    private final OrderRefundService orderRefundService;   // LSP
    private final CardGateway cardGateway;                 // ISP
    private final CashGateway cashGateway;                 // ISP
    private final CheckoutService checkoutService;         // DIP — wired to the @Primary Stripe gateway
    private final SquareGatewayImpl squareGateway;          // DIP — an alternate low-level gateway to swap in

    public SolidController(OrderProcessor orderProcessor,
                            PaymentProcessor paymentProcessor,
                            OrderRefundService orderRefundService,
                            CardGateway cardGateway,
                            CashGateway cashGateway,
                            CheckoutService checkoutService,
                            SquareGatewayImpl squareGateway) {
        this.orderProcessor = orderProcessor;
        this.paymentProcessor = paymentProcessor;
        this.orderRefundService = orderRefundService;
        this.cardGateway = cardGateway;
        this.cashGateway = cashGateway;
        this.checkoutService = checkoutService;
        this.squareGateway = squareGateway;
    }

    // SRP — GET /api/solid/srp?orderId=O-1&subtotal=100.00&loyaltyMember=true
    // OrderProcessor only orchestrates; tax, discount, and receipt formatting
    // each live in their own single-responsibility class (see solid.srp).
    @GetMapping("/srp")
    public ResponseEntity<String> srp(@RequestParam String orderId,
                                       @RequestParam BigDecimal subtotal,
                                       @RequestParam(defaultValue = "false") boolean loyaltyMember) {
        return ResponseEntity.ok(orderProcessor.processOrder(orderId, subtotal, loyaltyMember));
    }

    // OCP — GET /api/solid/ocp?tenderType=card|giftcard|taptopay&amount=42.00
    // PaymentProcessor's source hasn't changed since taptopay was added — new
    // tender types are new @Component classes, never new branches in this method.
    @GetMapping("/ocp")
    public ResponseEntity<Map<String, String>> ocp(@RequestParam String tenderType,
                                                     @RequestParam BigDecimal amount) {
        return ResponseEntity.ok(Map.of("result", paymentProcessor.process(tenderType, amount)));
    }

    // LSP — GET /api/solid/lsp
    // Three different Order subtypes refunded through the exact same
    // polymorphic call — no instanceof, no per-type branch, no thrown
    // UnsupportedOperationException.
    @GetMapping("/lsp")
    public ResponseEntity<List<String>> lsp() {
        List<Order> orders = List.of(
                new CardOrder("O-1", new BigDecimal("25.00")),
                new CashOrder("O-2", new BigDecimal("10.00")),
                new GiftCardOrder("O-3", new BigDecimal("15.00")));
        return ResponseEntity.ok(orderRefundService.refundAll(orders));
    }

    // ISP — GET /api/solid/isp?cash=true|false
    // CashGateway never had to implement Tokenizable — the capability check
    // below works because Tokenizable is its own narrow interface, not one fat
    // method bag every gateway is forced to implement.
    @GetMapping("/isp")
    public ResponseEntity<Map<String, Object>> isp(@RequestParam(defaultValue = "false") boolean cash) {
        Chargeable gateway = cash ? cashGateway : cardGateway;
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("charge", gateway.charge(new BigDecimal("42.00")));
        result.put("token", gateway instanceof Tokenizable tokenizable
                ? tokenizable.tokenize("4111111111111111")
                : "not supported by this gateway");
        return ResponseEntity.ok(result);
    }

    // DIP — GET /api/solid/dip?amount=99.99
    // Same CheckoutService class, two different low-level gateways plugged in:
    // the Stripe path uses the @Primary-injected bean, the Square path proves
    // the dependency can be swapped from the outside without editing
    // CheckoutService's source at all.
    @GetMapping("/dip")
    public ResponseEntity<Map<String, String>> dip(@RequestParam BigDecimal amount) {
        PaymentGateway swappedGateway = squareGateway;
        Map<String, String> result = new LinkedHashMap<>();
        result.put("viaPrimaryStripeGateway", checkoutService.checkout(amount));
        result.put("viaSwappedSquareGateway", new CheckoutService(swappedGateway).checkout(amount));
        return ResponseEntity.ok(result);
    }
}
