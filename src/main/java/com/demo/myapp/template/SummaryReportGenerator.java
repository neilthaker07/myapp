package com.demo.myapp.template;

import com.demo.myapp.model.Book;
import com.demo.myapp.model.BookStatus;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// Concrete Template — high-level stats only, no per-book detail
public class SummaryReportGenerator extends BookReportTemplate {

    @Override
    protected String formatHeader() {
        return """
                ==============================
                   LIBRARY SUMMARY REPORT
                ==============================
                """;
    }

    @Override
    protected String formatBody(List<Book> books) {
        Map<BookStatus, Long> countByStatus = books.stream()
                .collect(Collectors.groupingBy(Book::getStatus, Collectors.counting()));

        return String.format("""
                Total Books        : %d
                Available to Lend  : %d
                Lended Out         : %d
                In-Library Reading : %d
                Not Available      : %d
                """,
                books.size(),
                countByStatus.getOrDefault(BookStatus.AVAILABLE_TO_LEND, 0L),
                countByStatus.getOrDefault(BookStatus.LENDED_TO_INDIVIDUALS, 0L),
                countByStatus.getOrDefault(BookStatus.AVAILABLE_TO_READ_IN_LIBRARY, 0L),
                countByStatus.getOrDefault(BookStatus.NOT_AVAILABLE_IN_LIBRARY, 0L));
    }

    @Override
    protected String formatFooter(List<Book> books) {
        return "==============================\n";
    }
}
