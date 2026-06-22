package com.demo.myapp.adapter;

import com.demo.myapp.model.Book;
import com.fasterxml.jackson.annotation.JsonIgnore;

// Adapter Pattern — translates Book (Adaptee) into ExternalBookView (Target)
// Book.java is never modified — the adapter bridges the incompatibility
public class BookToExternalAdapter implements ExternalBookView {

    private final Book book; // the Adaptee

    public BookToExternalAdapter(Book book) {
        this.book = book;
    }

    @JsonIgnore
    public Book getBook() { return book; }

    @Override
    public String getBookId() {
        return String.valueOf(book.getId()); // Long → String
    }

    @Override
    public String getBookName() {
        return book.getTitle(); // title → bookName
    }

    @Override
    public String getWriter() {
        return book.getAuthor(); // author → writer
    }

    @Override
    public String getAvailability() {
        return switch (book.getStatus()) {
            case AVAILABLE_TO_LEND             -> "Available to lend";
            case AVAILABLE_TO_READ_IN_LIBRARY  -> "In-library reading only";
            case LENDED_TO_INDIVIDUALS         -> "Currently lent out";
            case NOT_AVAILABLE_IN_LIBRARY      -> "Not available";
        };
    }
}