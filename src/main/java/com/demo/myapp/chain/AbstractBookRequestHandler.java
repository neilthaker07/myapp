package com.demo.myapp.chain;

import com.demo.myapp.model.BookRequest;

// Abstract base — manages the next handler reference so subclasses only focus on their own logic
public abstract class AbstractBookRequestHandler implements BookRequestHandler {

    private BookRequestHandler next;

    @Override
    public void setNext(BookRequestHandler next) {
        this.next = next;
    }

    // Subclasses call this after their own check passes
    protected void passToNext(BookRequest request) {
        if (next != null) {
            next.handle(request);
        }
    }
}
