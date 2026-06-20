package com.demo.myapp.chain;

import com.demo.myapp.model.Book;
import com.demo.myapp.model.BookRequest;

import java.util.Collection;
import java.util.function.Supplier;

// Concrete Handler 3 — rejects if a book with the same title already exists
// Supplier gives lazy access to the live book collection without coupling to BookService
public class DuplicateTitleHandler extends AbstractBookRequestHandler {

    private final Supplier<Collection<Book>> booksSupplier;

    public DuplicateTitleHandler(Supplier<Collection<Book>> booksSupplier) {
        this.booksSupplier = booksSupplier;
    }

    @Override
    public void handle(BookRequest request) {
        boolean exists = booksSupplier.get().stream()
                .anyMatch(b -> b.getTitle().equalsIgnoreCase(request.getTitle()));
        if (exists) {
            throw new IllegalArgumentException("A book with title '" + request.getTitle() + "' already exists");
        }
        passToNext(request);
    }
}
