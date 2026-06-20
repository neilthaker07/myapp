package com.demo.myapp.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.demo.myapp.model.BookGenre;

// Builder Pattern — DTO for API input; only fields the client can supply (no id, no state)
// Bean Validation annotations handle Layer 1 (format checks) before the service is ever called
public class BookRequest {

    @NotBlank(message = "Title must not be blank")
    @Size(min = 2, max = 200, message = "Title must be between 2 and 200 characters")
    private String title;

    @NotBlank(message = "Author must not be blank")
    @Size(min = 2, max = 100, message = "Author must be between 2 and 100 characters")
    private String author;

    private BookGenre genre; // enum — Jackson rejects unknown values automatically

    @Size(max = 50, message = "Language must not exceed 50 characters")
    private String language;

    public BookRequest() {}

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public BookGenre getGenre() { return genre; }
    public void setGenre(BookGenre genre) { this.genre = genre; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
}
