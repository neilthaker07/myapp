package com.demo.myapp.service;

import com.demo.myapp.model.Book;
import com.demo.myapp.model.BookRequest;
import com.demo.myapp.model.BookStatus;
import com.demo.myapp.strategy.BookSortStrategy;
import com.demo.myapp.util.AppLogger;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Proxy Pattern — Caching Proxy: sits in front of BookService, caches getBookById results
// @Primary tells Spring to inject this wherever IBookService is required
// BookService (real subject) is injected by concrete type — no ambiguity
@Primary
@Service
public class CachingBookServiceProxy implements IBookService {

    private final BookService bookService;
    private final Map<Long, Book> cache = new HashMap<>();
    private final AppLogger logger = AppLogger.getInstance();

    public CachingBookServiceProxy(BookService bookService) {
        this.bookService = bookService;
    }

    // CACHED — returns from cache on repeat calls, skips real service
    @Override
    public Book getBookById(Long id) {
        if (cache.containsKey(id)) {
            logger.info("PROXY [CACHE HIT]  getBookById id=" + id);
            return cache.get(id);
        }
        logger.info("PROXY [CACHE MISS] getBookById id=" + id + " — delegating to BookService");
        Book book = bookService.getBookById(id);
        if (book != null) {
            cache.put(id, book);
        }
        return book;
    }

    // CACHE-INVALIDATING — book changed, cached version is stale
    @Override
    public Book updateBook(Long id, BookRequest request) {
        Book updated = bookService.updateBook(id, request);
        if (updated != null) {
            cache.remove(id);
            logger.info("PROXY [INVALIDATED] cache evicted for book id=" + id + " after update");
        }
        return updated;
    }

    @Override
    public boolean deleteBook(Long id) {
        boolean removed = bookService.deleteBook(id);
        if (removed) {
            cache.remove(id);
            logger.info("PROXY [INVALIDATED] cache evicted for book id=" + id + " after delete");
        }
        return removed;
    }

    @Override
    public Book changeBookState(Long id, String action) {
        Book updated = bookService.changeBookState(id, action);
        if (updated != null) {
            cache.remove(id); // state changed — cached copy is stale
            logger.info("PROXY [INVALIDATED] cache evicted for book id=" + id + " after state change");
        }
        return updated;
    }

    // PASS-THROUGH — no caching needed for these operations
    @Override
    public List<Book> getAllBooks() { return bookService.getAllBooks(); }

    @Override
    public List<Book> getAllBooks(BookSortStrategy strategy) { return bookService.getAllBooks(strategy); }

    @Override
    public Book createBook(BookRequest request) { return bookService.createBook(request); }

    @Override
    public BookStatus getBookStatus(Long id) { return bookService.getBookStatus(id); }
}
