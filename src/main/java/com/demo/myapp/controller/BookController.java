package com.demo.myapp.controller;

import com.demo.myapp.adapter.BookToExternalAdapter;
import com.demo.myapp.adapter.ExternalBookView;
import com.demo.myapp.facade.LibraryFacade;
import com.demo.myapp.model.Book;
import com.demo.myapp.model.BookRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final LibraryFacade libraryFacade;

    public BookController(LibraryFacade libraryFacade) {
        this.libraryFacade = libraryFacade;
    }

    // CREATE (POST) - http://localhost:8080/api/books
    // @Valid triggers Bean Validation on BookRequest before the method body runs
    // GlobalExceptionHandler maps MethodArgumentNotValidException → 400, IllegalArgumentException → 400
    @PostMapping
    public ResponseEntity<Book> createBook(@Valid @RequestBody BookRequest request) {
        return new ResponseEntity<>(libraryFacade.addBook(request), HttpStatus.CREATED);
    }

    // READ ALL (GET) - http://localhost:8080/api/books?sort=title|author|id
    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks(
            @RequestParam(required = false, defaultValue = "id") String sort) {
        return ResponseEntity.ok(libraryFacade.getBooks(sort));
    }

    // READ ONE (GET) - http://localhost:8080/api/books/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        Book book = libraryFacade.findBook(id);
        return book != null
                ? ResponseEntity.ok(book)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // UPDATE (PUT) - http://localhost:8080/api/books/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable Long id, @Valid @RequestBody BookRequest request) {
        Book updated = libraryFacade.modifyBook(id, request);
        return updated != null
                ? ResponseEntity.ok(updated)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // DELETE (DELETE) - http://localhost:8080/api/books/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        return libraryFacade.removeBook(id)
                ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // GET STATUS - http://localhost:8080/api/books/{id}/status
    @GetMapping("/{id}/status")
    public ResponseEntity<Map<String, String>> getBookStatus(@PathVariable Long id) {
        var status = libraryFacade.checkStatus(id);
        return status != null
                ? ResponseEntity.ok(Map.of("status", status.name()))
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // CHANGE STATE (PATCH) - http://localhost:8080/api/books/{id}/status?action=lend
    // GlobalExceptionHandler maps IllegalStateException → 409, IllegalArgumentException → 400
    @PatchMapping("/{id}/status")
    public ResponseEntity<Book> changeBookState(@PathVariable Long id, @RequestParam String action) {
        Book updated = libraryFacade.transitionState(id, action);
        return updated != null
                ? ResponseEntity.ok(updated)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // FILTER (GET) - http://localhost:8080/api/books/filter?genre=Java&language=English&status=AVAILABLE_TO_LEND
    @GetMapping("/filter")
    public ResponseEntity<List<Book>> filterBooks(
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(libraryFacade.getFilteredBooks(genre, language, status));
    }

    // EXTERNAL VIEW (GET) - http://localhost:8080/api/books/{id}/external
    @GetMapping("/{id}/external")
    public ResponseEntity<ExternalBookView> getExternalView(@PathVariable Long id) {
        Book book = libraryFacade.findBook(id);
        return book != null
                ? ResponseEntity.ok(new BookToExternalAdapter(book))
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
