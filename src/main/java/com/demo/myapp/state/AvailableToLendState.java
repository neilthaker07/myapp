package com.demo.myapp.state;

import com.demo.myapp.model.Book;
import com.demo.myapp.model.BookStatus;

public class AvailableToLendState implements BookState {

    @Override
    public void lend(Book book) {
        book.setState(new LendedToIndividualsState());
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
        throw new IllegalStateException("Book is already available to lend");
    }

    @Override
    public void removeFromLibrary(Book book) {
        book.setState(new NotAvailableInLibraryState());
    }

    @Override
    public BookStatus getStatus() {
        return BookStatus.AVAILABLE_TO_LEND;
    }
}
