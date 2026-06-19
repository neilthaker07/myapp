package com.demo.myapp.decorator;

import com.demo.myapp.model.Book;

import java.util.List;
import java.util.stream.Collectors;

// Concrete Decorator — filters books by genre, then delegates to the wrapped filter
public class GenreFilter extends BookFilterDecorator {

    private final String genre;

    public GenreFilter(String genre, BookFilter wrapped) {
        super(wrapped);
        this.genre = genre;
    }

    @Override
    public List<Book> apply(List<Book> books) {
        return wrapped.apply(books).stream()
                .filter(b -> genre.equalsIgnoreCase(b.getGenre()))
                .collect(Collectors.toList());
    }
}
