package com.demo.myapp.adapter;

// Adapter Pattern — Target interface: the shape an external system expects
public interface ExternalBookView {
    String getBookId();
    String getBookName();
    String getWriter();
    String getAvailability();
}