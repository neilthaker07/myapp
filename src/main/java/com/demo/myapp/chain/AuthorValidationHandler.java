package com.demo.myapp.chain;

import com.demo.myapp.model.BookRequest;

// Concrete Handler 2 — rejects if author is blank, otherwise passes along
public class AuthorValidationHandler extends AbstractBookRequestHandler {

    @Override
    public void handle(BookRequest request) {
        if (request.getAuthor() == null || request.getAuthor().isBlank()) {
            throw new IllegalArgumentException("Author must not be blank");
        }
        passToNext(request);
    }
}
