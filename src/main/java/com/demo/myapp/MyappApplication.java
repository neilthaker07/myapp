package com.demo.myapp;

import com.demo.myapp.util.AppLogger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MyappApplication {

	public static void main(String[] args) {
//		./mvnw spring-boot:run
		AppLogger.getInstance().info("Application starting up");
		SpringApplication.run(MyappApplication.class, args);
	}

}
