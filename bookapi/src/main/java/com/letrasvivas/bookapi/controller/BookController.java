package com.letrasvivas.bookapi.controller;

import com.letrasvivas.bookapi.dto.request.CreateBookRequestDTO;
import com.letrasvivas.bookapi.dto.request.UpdateBookRequestDTO;
import com.letrasvivas.bookapi.dto.response.BookResponseDTO;
import com.letrasvivas.bookapi.service.BookService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/books")
@Validated
@CrossOrigin(origins = "*", maxAge = 3600)
public class BookController {

    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    // ========== BASIC CRUD OPERATIONS ==========

    /**
     * Get all books with pagination and sorting
     */
    @GetMapping
    public ResponseEntity<Page<BookResponseDTO>> getAllBooks(
            @PageableDefault(size = 10, sort = "title") Pageable pageable) {
        Page<BookResponseDTO> books = bookService.getAllBooks(pageable);
        return ResponseEntity.ok(books);
    }

    /**
     * Get a specific book by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<BookResponseDTO> getBookById(
            @PathVariable @Min(value = 1, message = "ID must be positive") Long id) {
        BookResponseDTO book = bookService.getBookById(id);
        return ResponseEntity.ok(book);
    }

    /**
     * Create a new book
     */
    @PostMapping
    public ResponseEntity<BookResponseDTO> createBook(
            @Valid @RequestBody CreateBookRequestDTO requestDTO) {
        BookResponseDTO createdBook = bookService.createBook(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBook);
    }

    /**
     * Update an existing book
     */
    @PutMapping("/{id}")
    public ResponseEntity<BookResponseDTO> updateBook(
            @PathVariable @Min(value = 1, message = "ID must be positive") Long id,
            @Valid @RequestBody UpdateBookRequestDTO requestDTO) {
        BookResponseDTO updatedBook = bookService.updateBook(id, requestDTO);
        return ResponseEntity.ok(updatedBook);
    }

    /**
     * Delete a book by ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(
            @PathVariable @Min(value = 1, message = "ID must be positive") Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    // ========== BULK OPERATIONS ==========

    /**
     * Create multiple books at once
     */
    @PostMapping("/bulk")
    public ResponseEntity<List<BookResponseDTO>> createMultipleBooks(
            @Valid @RequestBody List<CreateBookRequestDTO> requestDTOs) {
        List<BookResponseDTO> createdBooks = bookService.createMultipleBooks(requestDTOs);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBooks);
    }

    // ========== AVAILABILITY MANAGEMENT ==========

    /**
     * Mark book as unavailable (soft delete)
     */
    @PatchMapping("/{id}/unavailable")
    public ResponseEntity<BookResponseDTO> markBookUnavailable(
            @PathVariable @Min(value = 1, message = "ID must be positive") Long id) {
        BookResponseDTO book = bookService.markBookUnavailable(id);
        return ResponseEntity.ok(book);
    }

    /**
     * Mark book as available
     */
    @PatchMapping("/{id}/available")
    public ResponseEntity<BookResponseDTO> markBookAvailable(
            @PathVariable @Min(value = 1, message = "ID must be positive") Long id) {
        BookResponseDTO book = bookService.markBookAvailable(id);
        return ResponseEntity.ok(book);
    }

    /**
     * Get all available books
     */
    @GetMapping("/available")
    public ResponseEntity<List<BookResponseDTO>> getAvailableBooks() {
        List<BookResponseDTO> availableBooks = bookService.getAvailableBooks();
        return ResponseEntity.ok(availableBooks);
    }

    // ========== SEARCH OPERATIONS ==========

    /**
     * Search books by title
     */
    @GetMapping("/search/title")
    public ResponseEntity<List<BookResponseDTO>> searchBooksByTitle(
            @RequestParam @NotBlank(message = "Title search term cannot be blank") String title) {
        List<BookResponseDTO> books = bookService.searchBooksByTitle(title);
        return ResponseEntity.ok(books);
    }

    /**
     * Search books by author
     */
    @GetMapping("/search/author")
    public ResponseEntity<List<BookResponseDTO>> searchBooksByAuthor(
            @RequestParam @NotBlank(message = "Author search term cannot be blank") String author) {
        List<BookResponseDTO> books = bookService.searchBooksByAuthor(author);
        return ResponseEntity.ok(books);
    }

    /**
     * General search (title or author)
     */
    @GetMapping("/search")
    public ResponseEntity<List<BookResponseDTO>> searchBooks(
            @RequestParam @NotBlank(message = "Search term cannot be blank") String q) {
        List<BookResponseDTO> books = bookService.searchBooks(q);
        return ResponseEntity.ok(books);
    }

    /**
     * Advanced search with multiple criteria
     */
    @GetMapping("/search/advanced")
    public ResponseEntity<Page<BookResponseDTO>> advancedSearch(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) Integer minYear,
            @RequestParam(required = false) Integer maxYear,
            @RequestParam(required = false) Integer minPages,
            @RequestParam(required = false) Integer maxPages,
            @RequestParam(required = false) Boolean isAvailable,
            @PageableDefault(size = 10, sort = "title") Pageable pageable) {

        Page<BookResponseDTO> books = bookService.searchBooks(
                title, author, genre, minYear, maxYear,
                minPages, maxPages, isAvailable, pageable);
        return ResponseEntity.ok(books);
    }

    // ========== CATEGORY AND CLASSIFICATION ==========

    /**
     * Get books by genre
     */
    @GetMapping("/genre/{genre}")
    public ResponseEntity<List<BookResponseDTO>> getBooksByGenre(
            @PathVariable @NotBlank(message = "Genre cannot be blank") String genre) {
        List<BookResponseDTO> books = bookService.getBooksByGenre(genre);
        return ResponseEntity.ok(books);
    }

    /**
     * Get books by publication year range
     */
    @GetMapping("/year-range")
    public ResponseEntity<List<BookResponseDTO>> getBooksByYearRange(
            @RequestParam @Min(value = 1500, message = "Start year must be after 1500") Integer startYear,
            @RequestParam @Min(value = 1500, message = "End year must be after 1500") Integer endYear) {
        List<BookResponseDTO> books = bookService.getBooksByYearRange(startYear, endYear);
        return ResponseEntity.ok(books);
    }

    /**
     * Get classic books (before 1950)
     */
    @GetMapping("/classics")
    public ResponseEntity<List<BookResponseDTO>> getClassicBooks() {
        List<BookResponseDTO> classicBooks = bookService.getClassicBooks();
        return ResponseEntity.ok(classicBooks);
    }

    /**
     * Get recent books (2020 onwards)
     */
    @GetMapping("/recent")
    public ResponseEntity<List<BookResponseDTO>> getRecentBooks() {
        List<BookResponseDTO> recentBooks = bookService.getRecentBooks();
        return ResponseEntity.ok(recentBooks);
    }

    // ========== ISBN OPERATIONS ==========

    /**
     * Get book by ISBN
     */
    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<BookResponseDTO> getBookByIsbn(
            @PathVariable @Pattern(
                    regexp = "^(97(8|9))?\\d{9}(\\d|X)$",
                    message = "Invalid ISBN format"
            ) String isbn) {
        BookResponseDTO book = bookService.getBookByIsbn(isbn);
        return ResponseEntity.ok(book);
    }

    /**
     * Check if ISBN exists
     */
    @GetMapping("/isbn/{isbn}/exists")
    public ResponseEntity<Map<String, Boolean>> checkIsbnExists(
            @PathVariable @Pattern(
                    regexp = "^(97(8|9))?\\d{9}(\\d|X)$",
                    message = "Invalid ISBN format"
            ) String isbn) {
        boolean exists = bookService.isbnExists(isbn);
        return ResponseEntity.ok(Map.of("exists", exists));
    }

    // ========== RECOMMENDATIONS ==========

    /**
     * Get similar books
     */
    @GetMapping("/{id}/similar")
    public ResponseEntity<List<BookResponseDTO>> getSimilarBooks(
            @PathVariable @Min(value = 1, message = "ID must be positive") Long id) {
        List<BookResponseDTO> similarBooks = bookService.getSimilarBooks(id);
        return ResponseEntity.ok(similarBooks);
    }

    // ========== STATISTICS AND ANALYTICS ==========

    /**
     * Get most popular genres
     */
    @GetMapping("/analytics/genres/popular")
    public ResponseEntity<List<Object[]>> getMostPopularGenres() {
        List<Object[]> genres = bookService.getMostPopularGenres();
        return ResponseEntity.ok(genres);
    }

    /**
     * Get most prolific authors
     */
    @GetMapping("/analytics/authors/prolific")
    public ResponseEntity<List<Object[]>> getMostProlificAuthors() {
        List<Object[]> authors = bookService.getMostProlificAuthors();
        return ResponseEntity.ok(authors);
    }

    /**
     * Get book count
     */
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getBookCount() {
        long totalBooks = bookService.getBookCount();
        long availableBooks = bookService.getAvailableBookCount();

        return ResponseEntity.ok(Map.of(
                "totalBooks", totalBooks,
                "availableBooks", availableBooks,
                "unavailableBooks", totalBooks - availableBooks
        ));
    }

    // ========== HEALTH CHECK ==========

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "BookAPI",
                "timestamp", java.time.Instant.now().toString()
        ));
    }

    // ========== ERROR HANDLING EXAMPLES ==========

    /**
     * Example endpoint demonstrating parameter validation
     */
    @GetMapping("/validate-params")
    public ResponseEntity<Map<String, String>> validateParams(
            @RequestParam @Min(value = 1, message = "Page must be positive") Integer page,
            @RequestParam @Min(value = 1, message = "Size must be positive") Integer size) {
        return ResponseEntity.ok(Map.of(
                "message", "Parameters are valid",
                "page", page.toString(),
                "size", size.toString()
        ));
    }
}