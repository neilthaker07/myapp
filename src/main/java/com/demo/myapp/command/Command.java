package com.demo.myapp.command;

import com.demo.myapp.model.Book;

// Command Pattern — encapsulates a state transition as an object
// execute() performs the action; undo() reverses it
// Book is passed in at call time so the same command class works across all books
public interface Command {
    void execute(Book book);
    void undo(Book book);
    String getDescription();
}
