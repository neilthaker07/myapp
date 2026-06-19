package com.demo.myapp.decorator;

import com.demo.myapp.model.Book;

import java.util.List;
import java.util.stream.Collectors;

// Concrete Decorator — filters books by language, then delegates to the wrapped filter
public class LanguageFilter extends BookFilterDecorator {

    private final String language;

    public LanguageFilter(String language, BookFilter wrapped) {
        super(wrapped);
        this.language = language;
    }

    @Override
    public List<Book> apply(List<Book> books) {
        return wrapped.apply(books).stream()
                .filter(b -> language.equalsIgnoreCase(b.getLanguage()))
                .collect(Collectors.toList());
    }
}
