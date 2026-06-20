package com.demo.myapp.command;

import com.demo.myapp.model.Book;
import com.demo.myapp.model.BookStatus;

public class MakeAvailableForLendingCommand implements Command {

    private final String bookTitle;
    private final BookStatus previousStatus;

    public MakeAvailableForLendingCommand(String bookTitle, BookStatus previousStatus) {
        this.bookTitle = bookTitle;
        this.previousStatus = previousStatus;
    }

    @Override
    public void execute(Book book) { book.makeAvailableForLending(); }

    @Override
    public void undo(Book book) {
        switch (previousStatus) {
            case AVAILABLE_TO_READ_IN_LIBRARY -> book.makeAvailableForReading();
            case NOT_AVAILABLE_IN_LIBRARY     -> book.removeFromLibrary();
            default -> {}
        }
    }

    @Override
    public String getDescription() {
        return "Make available for lending: \"" + bookTitle + "\" (was " + previousStatus + ")";
    }
}
