package com.demo.myapp.strategy;

import com.demo.myapp.model.Book;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class SortByTitleStrategy implements BookSortStrategy {

    @Override
    public List<Book> sort(List<Book> books) {
        return books.stream()
                .sorted(Comparator.comparing(b -> b.getTitle().toLowerCase()))
                .collect(Collectors.toList());
    }
}
