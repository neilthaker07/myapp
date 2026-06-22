package com.demo.myapp.service;

import com.demo.myapp.model.Book;
import com.demo.myapp.model.BookRequest;
import com.demo.myapp.model.BookStatus;
import com.demo.myapp.strategy.BookSortStrategy;

import java.util.List;

// Proxy Pattern — Subject interface: both BookService (real) and CachingBookServiceProxy implement this
// LibraryFacade depends on this interface, never on the concrete class
public interface IBookService {
    List<Book> getAllBooks();
    List<Book> getAllBooks(BookSortStrategy strategy);
    Book getBookById(Long id);
    <T> T getBookById(Long id, BookProjector<T> projector);
    Book createBook(BookRequest request);
    Book updateBook(Long id, BookRequest request);
    boolean deleteBook(Long id);
    BookStatus getBookStatus(Long id);
    Book changeBookState(Long id, String action);
    Book saveBook(Book book); // persist an already-mutated domain object (used by Command pattern)
}
