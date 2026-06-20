package com.demo.myapp.template;

import com.demo.myapp.model.Book;

import java.util.List;

// Concrete Template — full details for every book
public class DetailedReportGenerator extends BookReportTemplate {

    @Override
    protected String formatHeader() {
        return """
                ==============================
                   DETAILED BOOK REPORT
                ==============================
                """;
    }

    @Override
    protected String formatBody(List<Book> books) {
        if (books.isEmpty()) return "  No books found.\n\n";

        StringBuilder sb = new StringBuilder();
        for (Book book : books) {
            sb.append(String.format("""
                    [%d] %s
                        Author   : %s
                        Genre    : %s
                        Language : %s
                        Status   : %s
                    """,
                    book.getId(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getGenre() != null ? book.getGenre() : "-",
                    book.getLanguage() != null ? book.getLanguage() : "-",
                    book.getStatus()));
        }
        return sb.toString();
    }

    @Override
    protected String formatFooter(List<Book> books) {
        return "--- Total: " + books.size() + " book(s) ---\n";
    }
}
