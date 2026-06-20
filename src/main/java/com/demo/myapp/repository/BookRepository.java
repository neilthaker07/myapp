package com.demo.myapp.repository;

import com.demo.myapp.model.Book;

import java.util.List;
import java.util.Optional;

// Repository Pattern — abstracts data access; BookService never knows if it's talking to
// an in-memory store or PostgreSQL. Switch implementations via Spring @Profile.
public interface BookRepository {
    Book save(Book book);               // create (id=null) or update (id set)
    Optional<Book> findById(Long id);
    List<Book> findAll();
    boolean deleteById(Long id);
    long count();
    boolean existsByTitle(String title);
}
