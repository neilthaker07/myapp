package com.demo.myapp.observer;

import com.demo.myapp.model.Book;
import com.demo.myapp.model.BookStatus;

// Observer Pattern — Observer interface all concrete observers must implement
// @FunctionalInterface allows registering one-off observers as lambdas instead of named classes
@FunctionalInterface
public interface BookEventObserver {
    void onStateChanged(Book book, BookStatus oldStatus, BookStatus newStatus);
}
