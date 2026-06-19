package com.demo.myapp.car;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/car")
public class CarController {

    // Factory Method Pattern — controller resolves which Concrete Creator to use,
    // then calls the inherited algorithm methods (takeOrder / buildOrder)
    @GetMapping("/order")
    public ResponseEntity<?> orderCar(@RequestParam String brand) {
        CarFactory factory = switch (brand.toLowerCase()) {
            case "prius" -> new PriusFactory();
            case "tesla" -> new TeslaFactory();
            case "volvo" -> new VolvoFactory();
            default -> null;
        };

        if (factory == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Unknown brand: " + brand + ". Valid: prius, tesla, volvo"));
        }

        // Both calls go through CarFactory's algorithm — which internally calls getEngine()
        // CarFactory never knows whether it got a HybridEngine, ElectricEngine, or GasEngine
        Map<String, Object> response = new java.util.LinkedHashMap<>();
        response.put("brand", brand);
        response.put("takeOrder", factory.takeOrder());
        response.put("buildOrder", factory.buildOrder());
        return ResponseEntity.ok(response);
    }
}
