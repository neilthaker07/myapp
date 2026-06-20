package com.demo.myapp.dto;

import com.demo.myapp.model.BookGenre;
import com.demo.myapp.model.BookStatus;

// DTO — defines exactly what the API exposes to clients
// Java record: immutable, auto-generates constructor, getters, equals, hashCode, toString
// No 'state' field — BookState is an internal domain concern, never exposed via API
public record BookResponse(
        Long id,
        String title,
        String author,
        BookGenre genre,
        String language,
        BookStatus status
) {}
