package com.demo.myapp.decorator;

import com.demo.myapp.model.Book;

import java.util.List;

// Decorator Pattern — Component interface: every filter (base and decorated) implements this
@FunctionalInterface
public interface BookFilter {
    List<Book> apply(List<Book> books);
}
