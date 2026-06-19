package com.demo.myapp.decorator;

import com.demo.myapp.model.Book;
import com.demo.myapp.model.BookStatus;

import java.util.List;
import java.util.stream.Collectors;

// Concrete Decorator — filters books by availability status, then delegates to the wrapped filter
public class StatusFilter extends BookFilterDecorator {

    private final BookStatus status;

    public StatusFilter(BookStatus status, BookFilter wrapped) {
        super(wrapped);
        this.status = status;
    }

    @Override
    public List<Book> apply(List<Book> books) {
        return wrapped.apply(books).stream()
                .filter(b -> b.getStatus() == status)
                .collect(Collectors.toList());
    }
}
