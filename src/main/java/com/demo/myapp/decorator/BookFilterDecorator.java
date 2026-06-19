package com.demo.myapp.decorator;

import com.demo.myapp.model.Book;

import java.util.List;

// Decorator Pattern — abstract base for all concrete decorators; wraps another BookFilter
public abstract class BookFilterDecorator implements BookFilter {

    protected final BookFilter wrapped;

    public BookFilterDecorator(BookFilter wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public List<Book> apply(List<Book> books) {
        return wrapped.apply(books); // delegate by default; subclasses add behavior
    }
}
