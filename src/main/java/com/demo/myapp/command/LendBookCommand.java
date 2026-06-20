package com.demo.myapp.command;

import com.demo.myapp.model.Book;

public class LendBookCommand implements Command {

    private final String bookTitle;

    public LendBookCommand(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    @Override
    public void execute(Book book) { book.lend(); }

    @Override
    public void undo(Book book) { book.returnBook(); } // lend → undo → return

    @Override
    public String getDescription() { return "Lend: \"" + bookTitle + "\""; }
}
