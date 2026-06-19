package com.demo.myapp.observer;

import com.demo.myapp.model.Book;
import com.demo.myapp.model.BookStatus;
import com.demo.myapp.util.AppLogger;

// Concrete Observer 1 — records a full audit trail of every state transition
public class AuditLogObserver implements BookEventObserver {

    private final AppLogger logger = AppLogger.getInstance();

    @Override
    public void onStateChanged(Book book, BookStatus oldStatus, BookStatus newStatus) {
        logger.info("AUDIT: book id=" + book.getId()
                + " \"" + book.getTitle() + "\""
                + " [" + oldStatus + "] → [" + newStatus + "]");
    }
}
