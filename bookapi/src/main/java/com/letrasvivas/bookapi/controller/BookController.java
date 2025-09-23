package com.letrasvivas.bookapi.controller;

import com.letrasvivas.bookapi.model.Book;
import com.letrasvivas.bookapi.service.BookService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/books")
public class BookController {

    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    /**
     * Get all books with pagination.
     */
    @GetMapping
    public ResponseEntity<Page<Book>> getAllBooks(Pageable pageable) {
        return ResponseEntity.ok(bookService.getAllBooks(pageable));
    }

    /**
     * Create a single book with validation.
     */
    @PostMapping
    public ResponseEntity<Book> createBook(@Valid @RequestBody Book book) {
        return ResponseEntity.ok(bookService.saveBook(book));
    }

    /**
     * Create multiple books at once.
     */
    @PostMapping("/bulk")
    public ResponseEntity<List<Book>> createMultipleBooks(@Valid @RequestBody List<Book> books) {
        return ResponseEntity.ok(bookService.saveAllBooks(books));
    }

    /**
     * Search books by title (case-insensitive).
     */
    @GetMapping("/search")
    public ResponseEntity<List<Book>> searchBooks(@RequestParam String title) {
        return ResponseEntity.ok(bookService.findBooksByTitle(title));
    }

    /**
     * Get a book by its ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    /**
     * Update an existing book.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(
            @PathVariable Long id,
            @Valid @RequestBody Book updatedBookData) {

        Book existingBook = bookService.getBookById(id);
        existingBook.setTitle(updatedBookData.getTitle());
        existingBook.setAuthor(updatedBookData.getAuthor());
        existingBook.setPublicationYear(updatedBookData.getPublicationYear());

        Book updatedBook = bookService.saveBook(existingBook);
        return ResponseEntity.ok(updatedBook);
    }

    /**
     * Delete a book by its ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBookById(id);
        return ResponseEntity.noContent().build();
    }
}