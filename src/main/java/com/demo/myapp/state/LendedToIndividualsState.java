package com.demo.myapp.state;

import com.demo.myapp.model.Book;
import com.demo.myapp.model.BookStatus;

public class LendedToIndividualsState implements BookState {

    @Override
    public void lend(Book book) {
        throw new IllegalStateException("Book is already lent out");
    }

    @Override
    public void returnBook(Book book) {
        book.setState(new AvailableToLendState());
    }

    @Override
    public void makeAvailableForReading(Book book) {
        throw new IllegalStateException("Book must be returned before changing its availability");
    }

    @Override
    public void makeAvailableForLending(Book book) {
        throw new IllegalStateException("Book must be returned before changing its availability");
    }

    @Override
    public void removeFromLibrary(Book book) {
        throw new IllegalStateException("Book must be returned before removing it from the library");
    }

    @Override
    public BookStatus getStatus() {
        return BookStatus.LENDED_TO_INDIVIDUALS;
    }
}
