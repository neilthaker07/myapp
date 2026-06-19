package com.demo.myapp.observer;

import com.demo.myapp.model.Book;
import com.demo.myapp.model.BookStatus;
import com.demo.myapp.util.AppLogger;

// Concrete Observer 2 — alerts when a book becomes available to borrow (e.g. notifying a waitlist)
public class AvailabilityObserver implements BookEventObserver {

    private final AppLogger logger = AppLogger.getInstance();

    @Override
    public void onStateChanged(Book book, BookStatus oldStatus, BookStatus newStatus) {
        if (newStatus == BookStatus.AVAILABLE_TO_LEND) {
            logger.info("AVAILABILITY: \"" + book.getTitle() + "\" is now available to borrow!");
        }
    }
}
