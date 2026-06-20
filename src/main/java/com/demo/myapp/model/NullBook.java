package com.demo.myapp.model;

import com.demo.myapp.state.BookState;

// Null Object Pattern — safe stand-in for a missing Book
// Callers check isEmpty() instead of == null; all methods return safe defaults or no-op
// Singleton: only one instance ever needed — there's only one kind of "not found"
public final class NullBook extends Book {

    private static final NullBook INSTANCE = new NullBook();

    private NullBook() {}

    // Eagerly initialized — no lazy/double-checked locking needed since NullBook is
    // lightweight and always needed. JVM class loading guarantees thread safety.
    public static NullBook getInstance() {
        return INSTANCE;
    }

    @Override public boolean isEmpty()      { return true; }

    // Safe defaults — never throws NullPointerException
    @Override public Long        getId()       { return -1L; }
    @Override public String      getTitle()    { return ""; }
    @Override public String      getAuthor()   { return ""; }
    @Override public BookGenre   getGenre()    { return null; }
    @Override public String      getLanguage() { return ""; }
    @Override public BookStatus  getStatus()   { return BookStatus.NOT_AVAILABLE_IN_LIBRARY; }

    // No-op setters — NullBook state cannot be changed
    @Override public void setId(Long id)         {}
    @Override public void setState(BookState s)  {}

    // No-op state transitions — a non-existent book cannot be lent, returned, etc.
    @Override public void lend()                    {}
    @Override public void returnBook()              {}
    @Override public void makeAvailableForReading() {}
    @Override public void makeAvailableForLending() {}
    @Override public void removeFromLibrary()       {}
}
