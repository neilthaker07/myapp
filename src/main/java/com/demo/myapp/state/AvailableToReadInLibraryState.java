package com.demo.myapp.state;

import com.demo.myapp.model.Book;
import com.demo.myapp.model.BookStatus;

public class AvailableToReadInLibraryState implements BookState {

    @Override
    public void lend(Book book) {
        throw new IllegalStateException("Book is restricted to in-library reading and cannot be lent out");
    }

    @Override
    public void returnBook(Book book) {
        throw new IllegalStateException("Book is not currently lent out");
    }

    @Override
    public void makeAvailableForReading(Book book) {
        throw new IllegalStateException("Book is already available for in-library reading");
    }

    @Override
    public void makeAvailableForLending(Book book) {
        book.setState(new AvailableToLendState());
    }

    @Override
    public void removeFromLibrary(Book book) {
        book.setState(new NotAvailableInLibraryState());
    }

    @Override
    public BookStatus getStatus() {
        return BookStatus.AVAILABLE_TO_READ_IN_LIBRARY;
    }
}
