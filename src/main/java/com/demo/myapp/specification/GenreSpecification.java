package com.demo.myapp.specification;

import com.demo.myapp.model.Book;
import com.demo.myapp.model.BookGenre;

public class GenreSpecification implements Specification<Book> {

    private final BookGenre genre;

    public GenreSpecification(BookGenre genre) {
        this.genre = genre;
    }

    @Override
    public boolean isSatisfiedBy(Book book) {
        return book.getGenre() == genre;
    }
}
