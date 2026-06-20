package com.demo.myapp.repository.entity;

import com.demo.myapp.model.BookGenre;
import com.demo.myapp.model.BookStatus;
import jakarta.persistence.*;

// JPA entity — what gets stored in PostgreSQL
// Kept separate from Book (domain model) so DB concerns don't pollute business logic
@Entity
@Table(name = "books")
public class BookEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @Enumerated(EnumType.STRING)
    private BookGenre genre;

    private String language;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookStatus status = BookStatus.AVAILABLE_TO_LEND;

    public BookEntity() {}

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

    public BookStatus getStatus() { return status; }
    public void setStatus(BookStatus status) { this.status = status; }
}
