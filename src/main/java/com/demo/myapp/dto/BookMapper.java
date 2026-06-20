package com.demo.myapp.dto;

import com.demo.myapp.model.Book;

import java.util.List;

// Mapper Pattern — converts Book (domain) → BookResponse (DTO)
// Domain objects stay in the service layer; DTOs cross the API boundary
// Difference from Adapter: Mapper creates a NEW object with copied data;
// Old object can be garbage collected in Mapper
//   Adapter wraps the source and delegates calls to it
public class BookMapper {

    private BookMapper() {} // utility class — no instances

    public static BookResponse toResponse(Book book) {
        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getGenre(),
                book.getLanguage(),
                book.getStatus()
        );
    }

    public static List<BookResponse> toResponseList(List<Book> books) {
        return books.stream()
                .map(BookMapper::toResponse)
                .toList();
    }
}
