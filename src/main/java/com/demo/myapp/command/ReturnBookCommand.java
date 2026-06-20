package com.demo.myapp.command;

import com.demo.myapp.model.Book;

public class ReturnBookCommand implements Command {

    private final String bookTitle;

    public ReturnBookCommand(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    @Override
    public void execute(Book book) { book.returnBook(); }

    @Override
    public void undo(Book book) { book.lend(); } // return → undo → lend again

    @Override
    public String getDescription() { return "Return: \"" + bookTitle + "\""; }
}
