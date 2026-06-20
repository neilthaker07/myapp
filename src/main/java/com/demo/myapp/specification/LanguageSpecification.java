package com.demo.myapp.specification;

import com.demo.myapp.model.Book;

public class LanguageSpecification implements Specification<Book> {

    private final String language;

    public LanguageSpecification(String language) {
        this.language = language;
    }

    @Override
    public boolean isSatisfiedBy(Book book) {
        return book.getLanguage() != null
                && book.getLanguage().equalsIgnoreCase(language);
    }
}
