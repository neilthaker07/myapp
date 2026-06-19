package com.demo.myapp.service;

import com.demo.myapp.model.Book;
import com.demo.myapp.model.BookRequest;
import com.demo.myapp.model.BookStatus;
import com.demo.myapp.strategy.BookSortStrategy;
import com.demo.myapp.util.AppLogger;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BookService {

    private final AppLogger logger = AppLogger.getInstance();

    // Simulating a database with a Map
    private final Map<Long, Book> bookDatabase = new HashMap<>();
    private Long nextId = 1L;

    // GET ALL (no sort)
    public List<Book> getAllBooks() {
        logger.info("Fetching all books, count=" + bookDatabase.size());
        return new ArrayList<>(bookDatabase.values());
    }

    // GET ALL with Strategy Pattern — algorithm injected by the caller
    public List<Book> getAllBooks(BookSortStrategy strategy) {
        List<Book> books = new ArrayList<>(bookDatabase.values());
        logger.info("Fetching all books with sort strategy: " + strategy.getClass().getSimpleName());
        return strategy.sort(books);
    }

    // GET ONE
    public Book getBookById(Long id) {
        Book book = bookDatabase.get(id);
        if (book != null) {
            logger.info("Fetching book id=" + id);
        } else {
            logger.warn("Book not found, id=" + id);
        }
        return book;
    }

    // CREATE — Builder Pattern: constructs Book from BookRequest via Book.Builder
    public Book createBook(BookRequest request) {
        logger.info("Creating book, assigned id=" + nextId + ", title=" + request.getTitle());
        Book book = new Book.Builder(request.getTitle(), request.getAuthor())
                .genre(request.getGenre())
                .language(request.getLanguage())
                .build();
        book.setId(nextId);
        bookDatabase.put(nextId, book);
        nextId++;
        return book;
    }

    // UPDATE — Builder Pattern: reconstructs Book from BookRequest, preserving id and state
    public Book updateBook(Long id, BookRequest request) {
        if (bookDatabase.containsKey(id)) {
            logger.info("Updating book id=" + id);
            Book existing = bookDatabase.get(id);
            Book updated = new Book.Builder(request.getTitle(), request.getAuthor())
                    .genre(request.getGenre())
                    .language(request.getLanguage())
                    .build();
            updated.setId(id);
            updated.setState(existing.getState()); // preserve current lifecycle state
            bookDatabase.put(id, updated);
            return updated;
        }
        logger.warn("Update failed — book not found, id=" + id);
        return null;
    }

    // DELETE
    public boolean deleteBook(Long id) {
        boolean removed = bookDatabase.remove(id) != null;
        if (removed) {
            logger.info("Deleted book id=" + id);
        } else {
            logger.warn("Delete failed — book not found, id=" + id);
        }
        return removed;
    }

    // GET STATUS
    public BookStatus getBookStatus(Long id) {
        Book book = bookDatabase.get(id);
        if (book == null) {
            logger.warn("Status check failed — book not found, id=" + id);
            return null;
        }
        return book.getStatus();
    }

    // CHANGE STATE — delegates to the Book's current state object
    public Book changeBookState(Long id, String action) {
        Book book = bookDatabase.get(id);
        if (book == null) {
            logger.warn("State change failed — book not found, id=" + id);
            return null;
        }
        logger.info("State change requested: book id=" + id + ", action=" + action + ", currentStatus=" + book.getStatus());
        switch (action) {
            case "lend"               -> book.lend();
            case "return"             -> book.returnBook();
            case "read-in-library"    -> book.makeAvailableForReading();
            case "available-to-lend"  -> book.makeAvailableForLending();
            case "remove"             -> book.removeFromLibrary();
            default -> throw new IllegalArgumentException("Unknown action: " + action +
                    ". Valid actions: lend, return, read-in-library, available-to-lend, remove");
        }
        logger.info("State changed: book id=" + id + ", newStatus=" + book.getStatus());
        return book;
    }
}