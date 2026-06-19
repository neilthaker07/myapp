package com.demo.myapp.car;

import java.util.LinkedHashMap;
import java.util.Map;

// Abstract Factory Pattern — Creator
// Two factory methods (getEngine + getBody) produce a compatible family of parts per subclass
// Subclasses decide WHAT engine and WHAT body to create — always a matched set
public abstract class CarFactory {

    protected abstract Engine getEngine(); // factory method 1
    protected abstract Body getBody();     // factory method 2 — makes this Abstract Factory

    public Map<String, String> takeOrder() {
        Engine engine = getEngine();
        Body body = getBody();
        Map<String, String> result = new LinkedHashMap<>();
        result.put("step", "takeOrder");
        result.put("engineType", engine.getType());
        result.put("bodyStyle", body.getStyle());
        result.put("message", "Order received for a " + engine.getType() + " " + body.getStyle());
        return result;
    }

    public Map<String, String> buildOrder() {
        Engine engine = getEngine();
        Body body = getBody();
        Map<String, String> result = new LinkedHashMap<>();
        result.put("step", "buildOrder");
        result.put("engineType", engine.getType());
        result.put("engineStart", engine.start());
        result.put("bodyStyle", body.getStyle());
        result.put("bodyMaterial", body.getMaterial());
        result.put("message", "Car built: " + body.getStyle() + " with " + engine.getType() + " engine");
        return result;
    }
}
