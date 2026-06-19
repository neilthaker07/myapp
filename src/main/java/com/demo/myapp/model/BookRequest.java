package com.demo.myapp.model;

// Builder Pattern — DTO for API input; only fields the client can supply (no id, no state)
// Keeps Jackson deserialization separate from the Book domain object
public class BookRequest {
    private String title;
    private String author;
    private String genre;
    private String language;

    public BookRequest() {}

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
}
