package com.demo.myapp.facade;

import com.demo.myapp.decorator.BookFilter;
import com.demo.myapp.decorator.GenreFilter;
import com.demo.myapp.decorator.LanguageFilter;
import com.demo.myapp.decorator.NoOpFilter;
import com.demo.myapp.decorator.StatusFilter;
import com.demo.myapp.model.Book;
import com.demo.myapp.model.BookRequest;
import com.demo.myapp.model.BookStatus;
import com.demo.myapp.model.NullBook;
import com.demo.myapp.observer.AuditLogObserver;
import com.demo.myapp.observer.AvailabilityObserver;
import com.demo.myapp.observer.BookEventPublisher;
import com.demo.myapp.observer.LendingNotificationObserver;
import com.demo.myapp.service.IBookService;
import com.demo.myapp.strategy.BookSortStrategy;
import com.demo.myapp.strategy.SortByAuthorStrategy;
import com.demo.myapp.strategy.SortByIdStrategy;
import com.demo.myapp.strategy.SortByTitleStrategy;
import com.demo.myapp.util.AppLogger;
import org.springframework.stereotype.Service;

import java.util.List;

// Facade Pattern — single simplified entry point that hides BookService, AppLogger, and strategy selection
@Service
public class LibraryFacade {

    // Client doesn't know it's a proxy. Example - Spring AOP, lazy loading, auth
    private final IBookService bookService; // holds proxy — LibraryFacade never knows
    private final BookEventPublisher publisher;
    private final AppLogger logger = AppLogger.getInstance();

    public LibraryFacade(IBookService bookService, BookEventPublisher publisher) {
        this.bookService = bookService;
        this.publisher = publisher;

        // Register all observers — the publisher doesn't know their concrete types
        publisher.register(new AuditLogObserver());
        publisher.register(new AvailabilityObserver());
        publisher.register(new LendingNotificationObserver());
    }

    public Book addBook(BookRequest request) {
        logger.info("Facade: addBook - title=" + request.getTitle());
        return bookService.createBook(request);
    }

    // Strategy selection lives here — controller never needs to know about concrete strategy classes
    public List<Book> getBooks(String sort) {
        BookSortStrategy strategy = switch (sort) {
            // Factory Method deciding which strategy to create
            // Factory answers 'what do I build?';
            // Strategy answers 'how do I run?
            case "title"  -> new SortByTitleStrategy();
            case "author" -> new SortByAuthorStrategy();
            default       -> new SortByIdStrategy();
        };
        logger.info("Facade: getBooks - sort=" + sort);
        return bookService.getAllBooks(strategy);
    }

    public Book findBook(Long id) {
        logger.info("Facade: findBook - id=" + id);
        return bookService.getBookById(id);
    }

    public Book modifyBook(Long id, BookRequest request) {
        logger.info("Facade: modifyBook - id=" + id);
        return bookService.updateBook(id, request);
    }

    public boolean removeBook(Long id) {
        logger.info("Facade: removeBook - id=" + id);
        return bookService.deleteBook(id);
    }

    public BookStatus checkStatus(Long id) {
        logger.info("Facade: checkStatus - id=" + id);
        return bookService.getBookStatus(id);
    }

    // Decorator Pattern — builds filter chain dynamically from whichever params are provided
    public List<Book> getFilteredBooks(String genre, String language, String status) {
        List<Book> allBooks = bookService.getAllBooks();

        BookFilter filter = new NoOpFilter(); // base: returns everything
        if (genre != null && !genre.isBlank()) {
            filter = new GenreFilter(genre, filter);
        }
        if (language != null && !language.isBlank()) {
            filter = new LanguageFilter(language, filter);
        }
        if (status != null && !status.isBlank()) {
            try {
                filter = new StatusFilter(BookStatus.valueOf(status.toUpperCase()), filter);
            } catch (IllegalArgumentException ignored) {
                logger.warn("Facade: unknown status filter value=" + status + ", skipping");
            }
        }
        logger.info("Facade: getFilteredBooks - genre=" + genre + ", language=" + language + ", status=" + status);
        return filter.apply(allBooks);
    }

    // IllegalStateException / IllegalArgumentException bubble up — controller maps them to HTTP status codes
    public Book transitionState(Long id, String action) {
        logger.info("Facade: transitionState - id=" + id + ", action=" + action);

        // Capture old status before transition — required for observer notification
        Book book = bookService.getBookById(id);
        if (book.isEmpty()) return NullBook.getInstance(); // Null Object — no null check needed downstream
        BookStatus oldStatus = book.getStatus();

        Book updated = bookService.changeBookState(id, action);

        // Observer Pattern — notify all registered observers after the state changes
        publisher.notify(updated, oldStatus, updated.getStatus());

        return updated;
    }
}
