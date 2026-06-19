package com.demo.myapp.observer;

import com.demo.myapp.model.Book;
import com.demo.myapp.model.BookStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

// Observer Pattern — Subject: maintains the observer list and fires notifications
@Service
public class BookEventPublisher {

    private final List<BookEventObserver> observers = new ArrayList<>();

    // observers need to register first. Then Publisher will let observers know
    public void register(BookEventObserver observer) {
        observers.add(observer);
    }

    public void unregister(BookEventObserver observer) {
        observers.remove(observer);
    }

    public void notify(Book book, BookStatus oldStatus, BookStatus newStatus) {
        for (BookEventObserver observer : observers) {
            observer.onStateChanged(book, oldStatus, newStatus);
        }
    }
}
