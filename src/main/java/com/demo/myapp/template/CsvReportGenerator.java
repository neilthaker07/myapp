package com.demo.myapp.template;

import com.demo.myapp.model.Book;

import java.util.List;

// Concrete Template — CSV format for spreadsheet / data export
public class CsvReportGenerator extends BookReportTemplate {

    @Override
    protected String formatHeader() {
        return "id,title,author,genre,language,status\n";
    }

    @Override
    protected String formatBody(List<Book> books) {
        StringBuilder sb = new StringBuilder();
        for (Book book : books) {
            sb.append(String.format("%d,%s,%s,%s,%s,%s\n",
                    book.getId(),
                    escapeCsv(book.getTitle()),
                    escapeCsv(book.getAuthor()),
                    book.getGenre() != null ? book.getGenre().name() : "",
                    book.getLanguage() != null ? escapeCsv(book.getLanguage()) : "",
                    book.getStatus().name()));
        }
        return sb.toString();
    }

    @Override
    protected String formatFooter(List<Book> books) {
        return "# Total records: " + books.size() + "\n";
    }

    // Wrap fields that contain commas or quotes in double-quotes
    private String escapeCsv(String value) {
        if (value.contains(",") || value.contains("\"")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
