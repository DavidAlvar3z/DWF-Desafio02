package com.letrasvivas.bookapi.service;

import com.letrasvivas.bookapi.dto.request.CreateBookRequestDTO;
import com.letrasvivas.bookapi.dto.request.UpdateBookRequestDTO;
import com.letrasvivas.bookapi.dto.response.BookResponseDTO;
import com.letrasvivas.bookapi.entity.Book;
import com.letrasvivas.bookapi.exception.ResourceNotFoundException;
import com.letrasvivas.bookapi.exception.DuplicateResourceException;
import com.letrasvivas.bookapi.exception.BusinessValidationException;
import com.letrasvivas.bookapi.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class BookService {

    private final BookRepository bookRepository;

    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    /**
     * Get all books with pagination
     */
    @Transactional(readOnly = true)
    public Page<BookResponseDTO> getAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable)
                .map(this::convertToResponseDTO);
    }

    /**
     * Get book by ID
     */
    @Transactional(readOnly = true)
    public BookResponseDTO getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
        return convertToResponseDTO(book);
    }

    /**
     * Create a new book
     */
    public BookResponseDTO createBook(CreateBookRequestDTO requestDTO) {
        // Check if ISBN already exists (if provided)
        if (requestDTO.getIsbn() != null && !requestDTO.getIsbn().trim().isEmpty()) {
            if (bookRepository.existsByIsbn(requestDTO.getIsbn())) {
                throw new DuplicateResourceException("Book with ISBN " + requestDTO.getIsbn() + " already exists");
            }
        }

        Book book = convertToEntity(requestDTO);
        Book savedBook = bookRepository.save(book);
        return convertToResponseDTO(savedBook);
    }

    /**
     * Update an existing book
     */
    public BookResponseDTO updateBook(Long id, UpdateBookRequestDTO requestDTO) {
        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));

        // Check if ISBN is being changed and if it already exists
        if (requestDTO.getIsbn() != null && !requestDTO.getIsbn().equals(existingBook.getIsbn())) {
            if (bookRepository.existsByIsbn(requestDTO.getIsbn())) {
                throw new DuplicateResourceException("Book with ISBN " + requestDTO.getIsbn() + " already exists");
            }
        }

        updateBookFromDTO(existingBook, requestDTO);
        Book updatedBook = bookRepository.save(existingBook);
        return convertToResponseDTO(updatedBook);
    }

    /**
     * Delete book by ID
     */
    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new ResourceNotFoundException("Book not found with id: " + id);
        }
        bookRepository.deleteById(id);
    }

    /**
     * Soft delete book (mark as unavailable)
     */
    public BookResponseDTO markBookUnavailable(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));

        book.setIsAvailable(false);
        Book updatedBook = bookRepository.save(book);
        return convertToResponseDTO(updatedBook);
    }

    /**
     * Mark book as available
     */
    public BookResponseDTO markBookAvailable(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));

        book.setIsAvailable(true);
        Book updatedBook = bookRepository.save(book);
        return convertToResponseDTO(updatedBook);
    }

    /**
     * Search books by title
     */
    @Transactional(readOnly = true)
    public List<BookResponseDTO> searchBooksByTitle(String title) {
        return bookRepository.findByTitleContainingIgnoreCase(title)
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Search books by author
     */
    @Transactional(readOnly = true)
    public List<BookResponseDTO> searchBooksByAuthor(String author) {
        return bookRepository.findByAuthorContainingIgnoreCase(author)
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Search books by title or author
     */
    @Transactional(readOnly = true)
    public List<BookResponseDTO> searchBooks(String searchTerm) {
        return bookRepository.findByTitleOrAuthorContainingIgnoreCase(searchTerm)
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get books by genre
     */
    @Transactional(readOnly = true)
    public List<BookResponseDTO> getBooksByGenre(String genre) {
        return bookRepository.findByGenre(genre)
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get available books
     */
    @Transactional(readOnly = true)
    public List<BookResponseDTO> getAvailableBooks() {
        return bookRepository.findByIsAvailableTrue()
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get books by publication year range
     */
    @Transactional(readOnly = true)
    public List<BookResponseDTO> getBooksByYearRange(Integer startYear, Integer endYear) {
        return bookRepository.findByPublicationYearBetween(startYear, endYear)
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get classic books (before 1950)
     */
    @Transactional(readOnly = true)
    public List<BookResponseDTO> getClassicBooks() {
        return bookRepository.findClassicBooks()
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get recent books (2020 onwards)
     */
    @Transactional(readOnly = true)
    public List<BookResponseDTO> getRecentBooks() {
        return bookRepository.findRecentBooks()
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Advanced search with multiple criteria
     */
    @Transactional(readOnly = true)
    public Page<BookResponseDTO> searchBooks(String title, String author, String genre,
                                             Integer minYear, Integer maxYear,
                                             Integer minPages, Integer maxPages,
                                             Boolean isAvailable, Pageable pageable) {
        return bookRepository.findBooksWithCriteria(title, author, genre, minYear, maxYear,
                        minPages, maxPages, isAvailable, pageable)
                .map(this::convertToResponseDTO);
    }

    /**
     * Get book by ISBN
     */
    @Transactional(readOnly = true)
    public BookResponseDTO getBookByIsbn(String isbn) {
        Book book = bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with ISBN: " + isbn));
        return convertToResponseDTO(book);
    }

    /**
     * Get most popular genres
     */
    @Transactional(readOnly = true)
    public List<Object[]> getMostPopularGenres() {
        return bookRepository.findMostPopularGenres();
    }

    /**
     * Get most prolific authors
     */
    @Transactional(readOnly = true)
    public List<Object[]> getMostProlificAuthors() {
        return bookRepository.findMostProlificAuthors();
    }

    /**
     * Get similar books
     */
    @Transactional(readOnly = true)
    public List<BookResponseDTO> getSimilarBooks(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + bookId));

        // Extract keywords from title for similarity search
        String[] titleWords = book.getTitle().split(" ");
        String keyword = titleWords.length > 0 ? titleWords[0] : book.getTitle();

        return bookRepository.findSimilarBooks(keyword, bookId)
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Check if ISBN exists
     */
    @Transactional(readOnly = true)
    public boolean isbnExists(String isbn) {
        return bookRepository.existsByIsbn(isbn);
    }

    /**
     * Get book count
     */
    @Transactional(readOnly = true)
    public long getBookCount() {
        return bookRepository.count();
    }

    /**
     * Get available book count
     */
    @Transactional(readOnly = true)
    public long getAvailableBookCount() {
        return bookRepository.findByIsAvailableTrue().size();
    }

    /**
     * Create multiple books at once
     */
    public List<BookResponseDTO> createMultipleBooks(List<CreateBookRequestDTO> requestDTOs) {
        // Validate for duplicate ISBNs in the batch
        List<String> isbns = requestDTOs.stream()
                .map(CreateBookRequestDTO::getIsbn)
                .filter(isbn -> isbn != null && !isbn.trim().isEmpty())
                .collect(Collectors.toList());

        // Check for duplicates in the batch
        if (isbns.size() != isbns.stream().distinct().count()) {
            throw new BusinessValidationException("Duplicate ISBNs found in the batch");
        }

        // Check for existing ISBNs in database
        for (String isbn : isbns) {
            if (bookRepository.existsByIsbn(isbn)) {
                throw new DuplicateResourceException("Book with ISBN " + isbn + " already exists");
            }
        }

        List<Book> books = requestDTOs.stream()
                .map(this::convertToEntity)
                .collect(Collectors.toList());

        List<Book> savedBooks = bookRepository.saveAll(books);
        return savedBooks.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    // ========== PRIVATE HELPER METHODS ==========

    /**
     * Convert CreateBookRequestDTO to Book entity
     */
    private Book convertToEntity(CreateBookRequestDTO requestDTO) {
        Book book = new Book();
        book.setTitle(requestDTO.getTitle());
        book.setAuthor(requestDTO.getAuthor());
        book.setPublicationYear(requestDTO.getPublicationYear());
        book.setGenre(requestDTO.getGenre());
        book.setIsbn(requestDTO.getIsbn());
        book.setDescription(requestDTO.getDescription());
        book.setPageCount(requestDTO.getPageCount());
        book.setIsAvailable(true);
        return book;
    }

    /**
     * Update Book entity from UpdateBookRequestDTO
     */
    private void updateBookFromDTO(Book book, UpdateBookRequestDTO requestDTO) {
        if (requestDTO.getTitle() != null) {
            book.setTitle(requestDTO.getTitle());
        }
        if (requestDTO.getAuthor() != null) {
            book.setAuthor(requestDTO.getAuthor());
        }
        if (requestDTO.getPublicationYear() != null) {
            book.setPublicationYear(requestDTO.getPublicationYear());
        }
        if (requestDTO.getGenre() != null) {
            book.setGenre(requestDTO.getGenre());
        }
        if (requestDTO.getIsbn() != null) {
            book.setIsbn(requestDTO.getIsbn());
        }
        if (requestDTO.getDescription() != null) {
            book.setDescription(requestDTO.getDescription());
        }
        if (requestDTO.getPageCount() != null) {
            book.setPageCount(requestDTO.getPageCount());
        }
        if (requestDTO.getIsAvailable() != null) {
            book.setIsAvailable(requestDTO.getIsAvailable());
        }
    }

    /**
     * Convert Book entity to BookResponseDTO
     */
    private BookResponseDTO convertToResponseDTO(Book book) {
        BookResponseDTO responseDTO = new BookResponseDTO();
        responseDTO.setId(book.getId());
        responseDTO.setTitle(book.getTitle());
        responseDTO.setAuthor(book.getAuthor());
        responseDTO.setPublicationYear(book.getPublicationYear());
        responseDTO.setGenre(book.getGenre());
        responseDTO.setIsbn(book.getIsbn());
        responseDTO.setDescription(book.getDescription());
        responseDTO.setPageCount(book.getPageCount());
        responseDTO.setIsAvailable(book.getIsAvailable());
        responseDTO.setCreatedAt(book.getCreatedAt());
        responseDTO.setUpdatedAt(book.getUpdatedAt());

        // Set computed fields
        responseDTO.setBookAge(book.getBookAge());
        responseDTO.setDisplayTitle(book.getDisplayTitle());

        return responseDTO;
    }
}