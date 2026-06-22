package com.demo.rippling;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class RipplingApplication {

    public static void main(String[] args) {
        SpringApplication.run(RipplingApplication.class, args);
        log.info("Rippling service started — listening on port 8081");
    }
}
