package com.demo.myapp.command;

import com.demo.myapp.model.Book;
import com.demo.myapp.model.BookStatus;

public class MakeAvailableForReadingCommand implements Command {

    private final String bookTitle;
    private final BookStatus previousStatus; // saved so undo can restore exactly

    public MakeAvailableForReadingCommand(String bookTitle, BookStatus previousStatus) {
        this.bookTitle = bookTitle;
        this.previousStatus = previousStatus;
    }

    @Override
    public void execute(Book book) { book.makeAvailableForReading(); }

    @Override
    public void undo(Book book) {
        // restore to whatever state the book was in before
        switch (previousStatus) {
            case AVAILABLE_TO_LEND        -> book.makeAvailableForLending();
            case NOT_AVAILABLE_IN_LIBRARY -> book.removeFromLibrary();
            default -> {} // already in a sensible state
        }
    }

    @Override
    public String getDescription() {
        return "Make available for reading: \"" + bookTitle + "\" (was " + previousStatus + ")";
    }
}
