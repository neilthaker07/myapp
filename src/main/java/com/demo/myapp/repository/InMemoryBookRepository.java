package com.demo.myapp.repository;

import com.demo.myapp.model.Book;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

// Repository Pattern — in-memory implementation using HashMap
// @Primary makes this the default when no profile is specified
// Existing behaviour is preserved exactly as before
@Primary
@Profile("memory")
@Repository
public class InMemoryBookRepository implements BookRepository {

    private final Map<Long, Book> store = new HashMap<>();
    private Long nextId = 1L;

    // Here is implicit @Transactional - ACID
    @Override
    public Book save(Book book) {
        if (book.getId() == null) {
            book.setId(nextId++); // assign new id on create
        }
        store.put(book.getId(), book);
        return book;
    }

    @Override
    public Optional<Book> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Book> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public boolean deleteById(Long id) {
        return store.remove(id) != null;
    }

    @Override
    public long count() {
        return store.size();
    }

    @Override
    public boolean existsByTitle(String title) {
        return store.values().stream()
                .anyMatch(b -> b.getTitle().equalsIgnoreCase(title));
    }
}
