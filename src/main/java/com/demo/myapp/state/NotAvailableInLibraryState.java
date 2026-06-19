package com.demo.myapp.state;

import com.demo.myapp.model.Book;
import com.demo.myapp.model.BookStatus;

public class NotAvailableInLibraryState implements BookState {

    @Override
    public void lend(Book book) {
        throw new IllegalStateException("Book is not available in the library");
    }

    @Override
    public void returnBook(Book book) {
        throw new IllegalStateException("Book is not currently lent out");
    }

    @Override
    public void makeAvailableForReading(Book book) {
        book.setState(new AvailableToReadInLibraryState());
    }

    @Override
    public void makeAvailableForLending(Book book) {
        book.setState(new AvailableToLendState());
    }

    @Override
    public void removeFromLibrary(Book book) {
        throw new IllegalStateException("Book is already not available in the library");
    }

    @Override
    public BookStatus getStatus() {
        return BookStatus.NOT_AVAILABLE_IN_LIBRARY;
    }
}
