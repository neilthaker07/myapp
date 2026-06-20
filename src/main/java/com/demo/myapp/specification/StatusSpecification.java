package com.demo.myapp.specification;

import com.demo.myapp.model.Book;
import com.demo.myapp.model.BookStatus;

public class StatusSpecification implements Specification<Book> {

    private final BookStatus status;

    public StatusSpecification(BookStatus status) {
        this.status = status;
    }

    @Override
    public boolean isSatisfiedBy(Book book) {
        return book.getStatus() == status;
    }
}
