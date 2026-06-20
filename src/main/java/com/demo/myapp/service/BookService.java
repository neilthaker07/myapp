package com.demo.myapp.service;

import com.demo.myapp.chain.BookRequestHandler;
import com.demo.myapp.chain.DuplicateTitleHandler;
import com.demo.myapp.chain.MaxLibrarySizeHandler;
import com.demo.myapp.model.Book;
import com.demo.myapp.model.BookRequest;
import com.demo.myapp.model.BookStatus;
import com.demo.myapp.model.NullBook;
import com.demo.myapp.repository.BookRepository;
import com.demo.myapp.strategy.BookSortStrategy;
import com.demo.myapp.util.AppLogger;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService implements IBookService {

    private final AppLogger logger = AppLogger.getInstance();
    private final BookRepository bookRepository; // Repository Pattern — swap memory ↔ postgres via @Profile

    // Chain of Responsibility — wired once; suppliers read live from repository
    private final BookRequestHandler validationChain;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
        this.validationChain = buildValidationChain();
    }

    private BookRequestHandler buildValidationChain() {
        DuplicateTitleHandler duplicateHandler = new DuplicateTitleHandler(
                () -> bookRepository.findAll());
        MaxLibrarySizeHandler maxSizeHandler = new MaxLibrarySizeHandler(
                () -> (int) bookRepository.count(), 1000);

        duplicateHandler.setNext(maxSizeHandler);
        return duplicateHandler;
    }

    @Override
    public List<Book> getAllBooks() {
        logger.info("Fetching all books, count=" + bookRepository.count());
        return bookRepository.findAll();
    }

    @Override
    public List<Book> getAllBooks(BookSortStrategy strategy) {
        List<Book> books = bookRepository.findAll();
        logger.info("Fetching all books with sort strategy: " + strategy.getClass().getSimpleName());
        return strategy.sort(books);
    }

    @Override
    public Book getBookById(Long id) {
        return bookRepository.findById(id)
                .map(book -> { logger.info("Fetching book id=" + id); return book; })
                .orElseGet(() -> { logger.warn("Book not found, id=" + id); return NullBook.getInstance(); });
    }

    @Override
    public Book createBook(BookRequest request) {
        validationChain.handle(request);
        logger.info("Creating book, title=" + request.getTitle());
        Book book = new Book.Builder(request.getTitle(), request.getAuthor())
                .genre(request.getGenre())
                .language(request.getLanguage())
                .build();
        return bookRepository.save(book); // repository assigns the id
    }

    @Override
    public Book updateBook(Long id, BookRequest request) {
        return bookRepository.findById(id).map(existing -> {
            logger.info("Updating book id=" + id);
            Book updated = new Book.Builder(request.getTitle(), request.getAuthor())
                    .genre(request.getGenre())
                    .language(request.getLanguage())
                    .build();
            updated.setId(id);
            updated.setState(existing.getState()); // preserve lifecycle state
            return bookRepository.save(updated);
        }).orElseGet(() -> {
            logger.warn("Update failed — book not found, id=" + id);
            return NullBook.getInstance();
        });
    }

    @Override
    public boolean deleteBook(Long id) {
        boolean removed = bookRepository.deleteById(id);
        if (removed) logger.info("Deleted book id=" + id);
        else         logger.warn("Delete failed — book not found, id=" + id);
        return removed;
    }

    @Override
    public BookStatus getBookStatus(Long id) {
        return bookRepository.findById(id).map(Book::getStatus)
                .orElseGet(() -> { logger.warn("Status check failed — book not found, id=" + id); return null; });
    }

    @Override
    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }

    @Override
    public Book changeBookState(Long id, String action) {
        return bookRepository.findById(id).map(book -> {
            logger.info("State change: book id=" + id + ", action=" + action);
            switch (action) {
                case "lend"              -> book.lend();
                case "return"            -> book.returnBook();
                case "read-in-library"   -> book.makeAvailableForReading();
                case "available-to-lend" -> book.makeAvailableForLending();
                case "remove"            -> book.removeFromLibrary();
                default -> throw new IllegalArgumentException("Unknown action: " + action
                        + ". Valid: lend, return, read-in-library, available-to-lend, remove");
            }
            logger.info("State changed: book id=" + id + ", newStatus=" + book.getStatus());
            return bookRepository.save(book); // persist updated state
        }).orElseGet(() -> {
            logger.warn("State change failed — book not found, id=" + id);
            return NullBook.getInstance();
        });
    }
}
