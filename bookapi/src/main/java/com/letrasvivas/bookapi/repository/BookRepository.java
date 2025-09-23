package com.letrasvivas.bookapi.repository;

import com.letrasvivas.bookapi.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    // ========== BASIC SEARCH METHODS ==========

    /**
     * Find books by title containing search term (case insensitive)
     */
    List<Book> findByTitleContainingIgnoreCase(String title);

    /**
     * Find books by author containing search term (case insensitive)
     */
    List<Book> findByAuthorContainingIgnoreCase(String author);

    /**
     * Find books by genre (exact match)
     */
    List<Book> findByGenre(String genre);

    /**
     * Find book by ISBN (unique)
     */
    Optional<Book> findByIsbn(String isbn);

    /**
     * Check if ISBN exists
     */
    boolean existsByIsbn(String isbn);

    // ========== AVAILABILITY METHODS ==========

    /**
     * Find all available books
     */
    List<Book> findByIsAvailableTrue();

    /**
     * Find all unavailable books
     */
    List<Book> findByIsAvailableFalse();

    // ========== YEAR-BASED METHODS ==========

    /**
     * Find books by publication year range
     */
    List<Book> findByPublicationYearBetween(Integer startYear, Integer endYear);

    /**
     * Find books by specific publication year
     */
    List<Book> findByPublicationYear(Integer year);

    /**
     * Find books published before a specific year
     */
    List<Book> findByPublicationYearLessThan(Integer year);

    /**
     * Find books published after a specific year
     */
    List<Book> findByPublicationYearGreaterThan(Integer year);

    // ========== COMBINED SEARCH METHODS ==========

    /**
     * Find books by title OR author containing search term (case insensitive)
     */
    @Query("SELECT b FROM Book b WHERE " +
            "LOWER(b.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(b.author) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Book> findByTitleOrAuthorContainingIgnoreCase(@Param("searchTerm") String searchTerm);

    /**
     * Find books by genre and availability
     */
    List<Book> findByGenreAndIsAvailable(String genre, Boolean isAvailable);

    /**
     * Find books by author and publication year range
     */
    List<Book> findByAuthorContainingIgnoreCaseAndPublicationYearBetween(
            String author, Integer startYear, Integer endYear);

    // ========== CUSTOM QUERIES ==========

    /**
     * Find classic books (published before 1950)
     */
    @Query("SELECT b FROM Book b WHERE b.publicationYear < 1950 ORDER BY b.publicationYear")
    List<Book> findClassicBooks();

    /**
     * Find recent books (published in 2020 or later)
     */
    @Query("SELECT b FROM Book b WHERE b.publicationYear >= 2020 ORDER BY b.publicationYear DESC")
    List<Book> findRecentBooks();

    /**
     * Find long books (more than specified page count)
     */
    @Query("SELECT b FROM Book b WHERE b.pageCount > :minPages ORDER BY b.pageCount DESC")
    List<Book> findLongBooks(@Param("minPages") Integer minPages);

    /**
     * Find books by page count range
     */
    List<Book> findByPageCountBetween(Integer minPages, Integer maxPages);

    // ========== ADVANCED SEARCH WITH CRITERIA ==========

    /**
     * Advanced search with multiple optional criteria
     */
    @Query("SELECT b FROM Book b WHERE " +
            "(:title IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
            "(:author IS NULL OR LOWER(b.author) LIKE LOWER(CONCAT('%', :author, '%'))) AND " +
            "(:genre IS NULL OR b.genre = :genre) AND " +
            "(:minYear IS NULL OR b.publicationYear >= :minYear) AND " +
            "(:maxYear IS NULL OR b.publicationYear <= :maxYear) AND " +
            "(:minPages IS NULL OR b.pageCount >= :minPages) AND " +
            "(:maxPages IS NULL OR b.pageCount <= :maxPages) AND " +
            "(:isAvailable IS NULL OR b.isAvailable = :isAvailable)")
    Page<Book> findBooksWithCriteria(
            @Param("title") String title,
            @Param("author") String author,
            @Param("genre") String genre,
            @Param("minYear") Integer minYear,
            @Param("maxYear") Integer maxYear,
            @Param("minPages") Integer minPages,
            @Param("maxPages") Integer maxPages,
            @Param("isAvailable") Boolean isAvailable,
            Pageable pageable);

    // ========== SIMILARITY SEARCH ==========

    /**
     * Find similar books based on title keywords (excluding the book itself)
     */
    @Query("SELECT b FROM Book b WHERE " +
            "b.id != :excludeId AND " +
            "(LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "b.genre = (SELECT b2.genre FROM Book b2 WHERE b2.id = :excludeId)) " +
            "ORDER BY b.publicationYear DESC")
    List<Book> findSimilarBooks(@Param("keyword") String keyword, @Param("excludeId") Long excludeId);

    /**
     * Find books by same author (excluding the book itself)
     */
    @Query("SELECT b FROM Book b WHERE b.author = :author AND b.id != :excludeId ORDER BY b.publicationYear DESC")
    List<Book> findBooksBySameAuthor(@Param("author") String author, @Param("excludeId") Long excludeId);

    /**
     * Find books in same genre (excluding the book itself)
     */
    @Query("SELECT b FROM Book b WHERE b.genre = :genre AND b.id != :excludeId ORDER BY b.publicationYear DESC")
    List<Book> findBooksInSameGenre(@Param("genre") String genre, @Param("excludeId") Long excludeId);

    // ========== STATISTICS AND ANALYTICS ==========

    /**
     * Get most popular genres with book count
     */
    @Query("SELECT b.genre, COUNT(b) FROM Book b WHERE b.genre IS NOT NULL " +
            "GROUP BY b.genre ORDER BY COUNT(b) DESC")
    List<Object[]> findMostPopularGenres();

    /**
     * Get most prolific authors with book count
     */
    @Query("SELECT b.author, COUNT(b) FROM Book b " +
            "GROUP BY b.author ORDER BY COUNT(b) DESC")
    List<Object[]> findMostProlificAuthors();

    /**
     * Get books count by publication year
     */
    @Query("SELECT b.publicationYear, COUNT(b) FROM Book b " +
            "GROUP BY b.publicationYear ORDER BY b.publicationYear DESC")
    List<Object[]> findBooksCountByYear();

    /**
     * Get average page count by genre
     */
    @Query("SELECT b.genre, AVG(b.pageCount) FROM Book b WHERE b.genre IS NOT NULL AND b.pageCount IS NOT NULL " +
            "GROUP BY b.genre ORDER BY AVG(b.pageCount) DESC")
    List<Object[]> findAveragePageCountByGenre();

    // ========== TOP/RANKING QUERIES ==========

    /**
     * Find top N books by page count
     */
    @Query("SELECT b FROM Book b WHERE b.pageCount IS NOT NULL ORDER BY b.pageCount DESC")
    List<Book> findTopBooksByPageCount(Pageable pageable);

    /**
     * Find oldest books
     */
    @Query("SELECT b FROM Book b ORDER BY b.publicationYear ASC")
    List<Book> findOldestBooks(Pageable pageable);

    /**
     * Find newest books
     */
    @Query("SELECT b FROM Book b ORDER BY b.publicationYear DESC")
    List<Book> findNewestBooks(Pageable pageable);

    /**
     * Find recently added books (by creation date)
     */
    @Query("SELECT b FROM Book b ORDER BY b.createdAt DESC")
    List<Book> findRecentlyAddedBooks(Pageable pageable);

    // ========== VALIDATION QUERIES ==========

    /**
     * Check if title exists (for validation)
     */
    boolean existsByTitleIgnoreCase(String title);

    /**
     * Check if title and author combination exists
     */
    boolean existsByTitleIgnoreCaseAndAuthorIgnoreCase(String title, String author);

    /**
     * Count books by author
     */
    long countByAuthorIgnoreCase(String author);

    /**
     * Count books by genre
     */
    long countByGenre(String genre);

    /**
     * Count available books
     */
    long countByIsAvailableTrue();

    /**
     * Count books in year range
     */
    long countByPublicationYearBetween(Integer startYear, Integer endYear);

    // ========== BULK OPERATIONS ==========

    /**
     * Find books for bulk update (by IDs)
     */
    List<Book> findByIdIn(List<Long> ids);

    /**
     * Find books by multiple ISBNs
     */
    List<Book> findByIsbnIn(List<String> isbns);

    /**
     * Find books by multiple genres
     */
    List<Book> findByGenreIn(List<String> genres);

    /**
     * Find books by multiple authors
     */
    List<Book> findByAuthorIn(List<String> authors);
}