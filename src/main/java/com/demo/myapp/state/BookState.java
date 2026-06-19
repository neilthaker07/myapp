package com.demo.myapp.state;

import com.demo.myapp.model.Book;
import com.demo.myapp.model.BookStatus;

// State design pattern
public interface BookState {
    void lend(Book book);
    void returnBook(Book book);
    void makeAvailableForReading(Book book);
    void makeAvailableForLending(Book book);
    void removeFromLibrary(Book book);
    BookStatus getStatus();
}
