package com.demo.myapp.service;

import com.demo.myapp.model.Book;

// Projection pattern — caller decides the output shape (DTO, view, adapter) at the call site.
// Eliminates the need for one service method per return type (getBookAsDto, getBookAsView, etc.)
@FunctionalInterface
public interface BookProjector<T> {
    T project(Book book);
}
