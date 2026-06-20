package com.demo.myapp.repository;

import com.demo.myapp.model.Book;
import com.demo.myapp.model.BookStatus;
import com.demo.myapp.repository.entity.BookEntity;
import com.demo.myapp.state.AvailableToLendState;
import com.demo.myapp.state.AvailableToReadInLibraryState;
import com.demo.myapp.state.BookState;
import com.demo.myapp.state.LendedToIndividualsState;
import com.demo.myapp.state.NotAvailableInLibraryState;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// Repository Pattern — PostgreSQL implementation via Spring Data JPA
// Maps between Book (domain model with State pattern) and BookEntity (JPA entity)
// Activated by: spring.profiles.active=postgres in application.properties
@Profile("postgres")
@Repository
public class PostgresBookRepository implements BookRepository {

    private final JpaBookEntityRepository jpa;

    public PostgresBookRepository(JpaBookEntityRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Book save(Book book) {
        BookEntity entity = toEntity(book);
        BookEntity saved = jpa.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Book> findById(Long id) {
        return jpa.findById(id).map(this::toDomain);
    }

    @Override
    public List<Book> findAll() {
        return jpa.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public boolean deleteById(Long id) {
        if (!jpa.existsById(id)) return false;
        jpa.deleteById(id);
        return true;
    }

    @Override
    public long count() {
        return jpa.count();
    }

    @Override
    public boolean existsByTitle(String title) {
        return jpa.existsByTitleIgnoreCase(title);
    }

    // Maps domain Book → JPA entity for persistence
    private BookEntity toEntity(Book book) {
        BookEntity entity = new BookEntity();
        entity.setId(book.getId());
        entity.setTitle(book.getTitle());
        entity.setAuthor(book.getAuthor());
        entity.setGenre(book.getGenre());
        entity.setLanguage(book.getLanguage());
        entity.setStatus(book.getStatus()); // persist BookStatus enum, not the BookState object
        return entity;
    }

    // Maps JPA entity → domain Book, reconstructing the correct BookState from persisted BookStatus
    private Book toDomain(BookEntity entity) {
        Book book = new Book.Builder(entity.getTitle(), entity.getAuthor())
                .genre(entity.getGenre())
                .language(entity.getLanguage())
                .build();
        book.setId(entity.getId());
        book.setState(stateFrom(entity.getStatus())); // rebuild State object from persisted enum
        return book;
    }

    // Reconstructs the GoF State object from the persisted BookStatus enum value
    private BookState stateFrom(BookStatus status) {
        return switch (status) {
            case AVAILABLE_TO_LEND            -> new AvailableToLendState();
            case AVAILABLE_TO_READ_IN_LIBRARY -> new AvailableToReadInLibraryState();
            case LENDED_TO_INDIVIDUALS        -> new LendedToIndividualsState();
            case NOT_AVAILABLE_IN_LIBRARY     -> new NotAvailableInLibraryState();
        };
    }
}
