package com.demo.myapp.chain;

import com.demo.myapp.model.BookRequest;

import java.util.function.Supplier;

// Concrete Handler — rejects if the library has reached its maximum book capacity
public class MaxLibrarySizeHandler extends AbstractBookRequestHandler {

    private final Supplier<Integer> bookCountSupplier;
    private final int maxSize;

    public MaxLibrarySizeHandler(Supplier<Integer> bookCountSupplier, int maxSize) {
        this.bookCountSupplier = bookCountSupplier;
        this.maxSize = maxSize;
    }

    @Override
    public void handle(BookRequest request) {
        if (bookCountSupplier.get() >= maxSize) {
            throw new IllegalArgumentException(
                    "Library is at full capacity (" + maxSize + " books). Cannot add more.");
        }
        passToNext(request);
    }
}
