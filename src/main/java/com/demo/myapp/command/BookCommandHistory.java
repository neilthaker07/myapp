package com.demo.myapp.command;

import com.demo.myapp.model.Book;
import com.demo.myapp.util.AppLogger;
import org.springframework.stereotype.Service;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Command Pattern — Invoker: records executed commands per book, supports undo and audit log
@Service
public class BookCommandHistory {

    private final Map<Long, Deque<Command>> histories = new HashMap<>();
    private final AppLogger logger = AppLogger.getInstance();

    // Record a command after it has been executed
    public void record(Long bookId, Command command) {
        histories.computeIfAbsent(bookId, k -> new ArrayDeque<>()).push(command);
        logger.info("COMMAND [RECORDED] " + command.getDescription() + " for book id=" + bookId);
    }

    // Undo the last command — mutates the book in place, returns true if something was undone
    public boolean undo(Long bookId, Book book) {
        Deque<Command> history = histories.get(bookId);
        if (history == null || history.isEmpty()) return false;
        Command last = history.pop();
        last.undo(book);
        logger.info("COMMAND [UNDONE] " + last.getDescription() + " for book id=" + bookId);
        return true;
    }

    // Returns audit log — most recent first (stack order)
    public List<String> getHistory(Long bookId) {
        Deque<Command> history = histories.get(bookId);
        if (history == null || history.isEmpty()) return List.of();
        return history.stream().map(Command::getDescription).toList();
    }
}
