package com.demo.myapp.strategy;

import com.demo.myapp.model.Book;

import java.util.List;

public interface BookSortStrategy {
    List<Book> sort(List<Book> books);
}