package com.demo.myapp.chain;

import com.demo.myapp.model.BookRequest;

// Concrete Handler 1 — rejects if title is blank, otherwise passes along
public class TitleValidationHandler extends AbstractBookRequestHandler {

    @Override
    public void handle(BookRequest request) {
        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw new IllegalArgumentException("Title must not be blank");
        }
        passToNext(request);
    }
}
