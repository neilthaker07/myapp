package com.demo.myapp.command;

import com.demo.myapp.model.Book;
import com.demo.myapp.model.BookStatus;

public class RemoveFromLibraryCommand implements Command {

    private final String bookTitle;
    private final BookStatus previousStatus;

    public RemoveFromLibraryCommand(String bookTitle, BookStatus previousStatus) {
        this.bookTitle = bookTitle;
        this.previousStatus = previousStatus;
    }

    @Override
    public void execute(Book book) { book.removeFromLibrary(); }

    @Override
    public void undo(Book book) {
        switch (previousStatus) {
            case AVAILABLE_TO_LEND            -> book.makeAvailableForLending();
            case AVAILABLE_TO_READ_IN_LIBRARY -> book.makeAvailableForReading();
            default -> {}
        }
    }

    @Override
    public String getDescription() {
        return "Remove from library: \"" + bookTitle + "\" (was " + previousStatus + ")";
    }
}
