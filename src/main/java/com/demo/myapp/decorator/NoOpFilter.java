package com.demo.myapp.decorator;

import com.demo.myapp.model.Book;

import java.util.List;

// Decorator Pattern — Concrete Component (base): returns all books unchanged
// Every filter chain starts here and decorators wrap around it
public class NoOpFilter implements BookFilter {

    @Override
    public List<Book> apply(List<Book> books) {
        return books;
    }
}
