package com.demo.myapp.observer;

import com.demo.myapp.model.Book;
import com.demo.myapp.model.BookStatus;
import com.demo.myapp.util.AppLogger;

// Concrete Observer 3 — simulates notifying a borrower when a book is lent out
public class LendingNotificationObserver implements BookEventObserver {

    private final AppLogger logger = AppLogger.getInstance();

    @Override
    public void onStateChanged(Book book, BookStatus oldStatus, BookStatus newStatus) {
        if (newStatus == BookStatus.LENDED_TO_INDIVIDUALS) {
            logger.info("NOTIFICATION: \"" + book.getTitle() + "\" has been lent out. Due back in 14 days.");
        }
    }
}
