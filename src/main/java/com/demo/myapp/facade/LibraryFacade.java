package com.demo.myapp.facade;

import com.demo.myapp.command.BookCommandHistory;
import com.demo.myapp.command.LendBookCommand;
import com.demo.myapp.command.MakeAvailableForLendingCommand;
import com.demo.myapp.command.MakeAvailableForReadingCommand;
import com.demo.myapp.command.RemoveFromLibraryCommand;
import com.demo.myapp.command.ReturnBookCommand;
import com.demo.myapp.command.Command;
import com.demo.myapp.model.Book;
import com.demo.myapp.model.BookGenre;
import com.demo.myapp.model.BookRequest;
import com.demo.myapp.model.BookStatus;
import com.demo.myapp.model.NullBook;
import com.demo.myapp.observer.AuditLogObserver;
import com.demo.myapp.observer.BookEventPublisher;
import com.demo.myapp.service.BookProjector;
import com.demo.myapp.service.IBookService;
import com.demo.myapp.specification.GenreSpecification;
import com.demo.myapp.specification.LanguageSpecification;
import com.demo.myapp.specification.Specification;
import com.demo.myapp.specification.StatusSpecification;
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

    private final IBookService bookService;
    private final BookEventPublisher publisher;
    private final BookCommandHistory commandHistory;
    private final AppLogger logger = AppLogger.getInstance();

    public LibraryFacade(IBookService bookService, BookEventPublisher publisher,
                         BookCommandHistory commandHistory) {
        this.bookService = bookService;
        this.publisher = publisher;
        this.commandHistory = commandHistory;

        // Named class — audit fires on every transition and may grow more complex
        publisher.register(new AuditLogObserver());

        // Simple one-off observers registered as lambdas — @FunctionalInterface enables this
        publisher.register((book, oldStatus, newStatus) -> {
            if (newStatus == BookStatus.AVAILABLE_TO_LEND)
                logger.info("AVAILABILITY: \"" + book.getTitle() + "\" is now available to borrow!");
        });
        publisher.register((book, oldStatus, newStatus) -> {
            if (newStatus == BookStatus.LENDED_TO_INDIVIDUALS)
                logger.info("NOTIFICATION: \"" + book.getTitle() + "\" has been lent out. Due back in 14 days.");
        });
    }

    public Book addBook(BookRequest request) {
        logger.info("Facade: addBook - title=" + request.getTitle());
        return bookService.createBook(request);
    }

    // Strategy selection lives here — controller never needs to know about concrete strategy classes
    public List<Book> getBooks(String sort) {
        BookSortStrategy strategy = switch (sort) {
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

    // BookProjector — caller supplies the output shape as a lambda or method reference
    public <T> T findBookAs(Long id, BookProjector<T> projector) {
        logger.info("Facade: findBookAs - id=" + id);
        return bookService.getBookById(id, projector);
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

    // Specification Pattern — composes predicates with .and(); supports OR/NOT unlike Decorator chain
    public List<Book> getFilteredBooks(String genre, String language, String status) {
        List<Book> allBooks = bookService.getAllBooks();

        Specification<Book> spec = book -> true; // start: match everything

        if (genre != null && !genre.isBlank()) {
            try {
                spec = spec.and(new GenreSpecification(BookGenre.valueOf(genre.toUpperCase())));
            } catch (IllegalArgumentException ignored) {
                logger.warn("Facade: unknown genre filter value=" + genre + ", skipping");
            }
        }
        if (language != null && !language.isBlank()) {
            spec = spec.and(new LanguageSpecification(language));
        }
        if (status != null && !status.isBlank()) {
            try {
                spec = spec.and(new StatusSpecification(BookStatus.valueOf(status.toUpperCase())));
            } catch (IllegalArgumentException ignored) {
                logger.warn("Facade: unknown status filter value=" + status + ", skipping");
            }
        }

        logger.info("Facade: getFilteredBooks - genre=" + genre + ", language=" + language + ", status=" + status);

        final Specification<Book> finalSpec = spec;
        return allBooks.stream()
                .filter(finalSpec::isSatisfiedBy)
                .toList();
    }

    // Command Pattern — wraps state transition in a Command, records for undo/audit
    public Book transitionState(Long id, String action) {
        logger.info("Facade: transitionState - id=" + id + ", action=" + action);

        Book book = bookService.getBookById(id);
        if (book.isEmpty()) return NullBook.getInstance();
        BookStatus oldStatus = book.getStatus();

        // Build the right command — each knows how to execute AND undo itself
        Command command = switch (action) {
            case "lend"              -> new LendBookCommand(book.getTitle());
            case "return"            -> new ReturnBookCommand(book.getTitle());
            case "read-in-library"   -> new MakeAvailableForReadingCommand(book.getTitle(), oldStatus);
            case "available-to-lend" -> new MakeAvailableForLendingCommand(book.getTitle(), oldStatus);
            case "remove"            -> new RemoveFromLibraryCommand(book.getTitle(), oldStatus);
            default -> throw new IllegalArgumentException("Unknown action: " + action
                    + ". Valid: lend, return, read-in-library, available-to-lend, remove");
        };

        command.execute(book);                    // mutate state in memory
        Book saved = bookService.saveBook(book);  // persist
        commandHistory.record(id, command);       // track for undo/audit
        publisher.notify(saved, oldStatus, saved.getStatus()); // observers

        return saved;
    }

    // Undo the last state transition for a book
    public Book undoLastAction(Long id) {
        logger.info("Facade: undoLastAction - id=" + id);

        Book book = bookService.getBookById(id);
        if (book.isEmpty()) return NullBook.getInstance();
        BookStatus oldStatus = book.getStatus();

        boolean undone = commandHistory.undo(id, book);
        if (!undone) {
            logger.warn("Facade: nothing to undo for book id=" + id);
            return book;
        }

        Book saved = bookService.saveBook(book);
        publisher.notify(saved, oldStatus, saved.getStatus());
        return saved;
    }

    // Audit log — all commands executed on a book, most recent first
    public List<String> getCommandHistory(Long id) {
        return commandHistory.getHistory(id);
    }
}
