package com.demo.myapp.util;

import java.time.LocalDateTime;

// Singleton design pattern
public class AppLogger {

    // Caller side - volatile makes sure that before assigning object, new instance is created
    private static volatile AppLogger instance; // volatile prevents instruction reordering

    private AppLogger() {} // private constructor blocks `new AppLogger()`

    public static AppLogger getInstance() {
        if (instance == null) {                 // first check (no lock — fast path)
            // make sure that only one thread get in
            synchronized (AppLogger.class) {
                // Synchronized is slow, we don't want multiple thread waiting for sync
                // Making multiple threads waiting is computationally expensive.
                // hence 2nd check is needed
                if (instance == null) {         // second check (inside lock — safe)
                    instance = new AppLogger();
                }
            }
        }
        return instance;
    }

    public void info(String message) {
        log("INFO", message);
    }

    public void warn(String message) {
        log("WARN", message);
    }

    private void log(String level, String message) {
        System.out.printf("[%s] [%s] %s%n", LocalDateTime.now(), level, message);
    }
}
