package com.demo.myapp.model;

import com.demo.myapp.state.AvailableToLendState;
import com.demo.myapp.state.BookState;

public class Book {
    private Long id;
    private String title;
    private String author;
    private BookGenre genre;
    private String language;

    // The Book is the "context" in GoF State pattern — it holds the current state object
    private BookState state = new AvailableToLendState();

    // Default constructor is required by Spring for JSON mapping
    public Book() {}

    public Book(Long id, String title, String author) {
        this.id = id;
        this.title = title;
        this.author = author;
    }

    // Called by concrete state classes to switch state
    public void setState(BookState state) {
        this.state = state;
    }

    public BookState getState() {
        return state;
    }

    public BookStatus getStatus() {
        return state.getStatus();
    }

    // State transition delegates — callers don't need to know which state is active
    public void lend() { state.lend(this); }
    public void returnBook() { state.returnBook(this); }
    public void makeAvailableForReading() { state.makeAvailableForReading(this); }
    public void makeAvailableForLending() { state.makeAvailableForLending(this); }
    public void removeFromLibrary() { state.removeFromLibrary(this); }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public BookGenre getGenre() { return genre; }
    public void setGenre(BookGenre genre) { this.genre = genre; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    // Builder Pattern — title and author are required; genre and language are optional
    // id and state are excluded: id is server-assigned, state always starts as AvailableToLendState
    public static class Builder {
        private final String title;
        private final String author;
        private BookGenre genre;
        private String language;

        public Builder(String title, String author) {
            this.title = title;
            this.author = author;
        }

        public Builder genre(BookGenre genre) {
            this.genre = genre;
            return this;
        }

        public Builder language(String language) {
            this.language = language;
            return this;
        }

        public Book build() {
            Book book = new Book();
            book.title = this.title;
            book.author = this.author;
            book.genre = this.genre;
            book.language = this.language;
            return book;
        }
    }
}