package com.demo.myapp.template;

import com.demo.myapp.model.Book;

import java.util.List;

// Template Method Pattern — defines the skeleton of the report algorithm
// Subclasses implement the three formatting steps; the order never changes
// Other examples - Java servlet
public abstract class BookReportTemplate {

    // Template method — final so no subclass can reorder the steps
    // Important
    public final String generateReport(List<Book> books) {
        return formatHeader()
             + formatBody(books)
             + formatFooter(books);
    }

    protected abstract String formatHeader();
    protected abstract String formatBody(List<Book> books);
    protected abstract String formatFooter(List<Book> books);
}
