package com.demo.myapp.chain;

import com.demo.myapp.model.BookRequest;

// Chain of Responsibility Pattern — Handler interface
// Each handler either validates and passes to next, or rejects by throwing
public interface BookRequestHandler {
    void handle(BookRequest request);
    void setNext(BookRequestHandler next);
}
