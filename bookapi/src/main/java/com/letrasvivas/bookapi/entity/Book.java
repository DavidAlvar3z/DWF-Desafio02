package com.letrasvivas.bookapi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.time.Year;

@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is mandatory")
    @Size(min = 1, max = 200, message = "Title must be between 1 and 200 characters")
    @Column(nullable = false, length = 200)
    private String title;

    @NotBlank(message = "Author is mandatory")
    @Size(min = 2, max = 100, message = "Author must be between 2 and 100 characters")
    @Column(nullable = false, length = 100)
    private String author;

    @NotNull(message = "Publication year is mandatory")
    @Min(value = 1500, message = "Publication year must be later than 1500")
    @Max(value = 2025, message = "Publication year cannot be greater than 2025")
    @Column(name = "publication_year", nullable = false)
    private Integer publicationYear;

    @Size(max = 50, message = "Genre cannot exceed 50 characters")
    @Column(length = 50)
    private String genre;

    @Size(max = 20, message = "ISBN cannot exceed 20 characters")
    @Pattern(regexp = "^(97(8|9))?\\d{9}(\\d|X)$", message = "ISBN format is invalid")
    @Column(unique = true, length = 20)
    private String isbn;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    @Column(length = 500)
    private String description;

    @Min(value = 1, message = "Page count must be at least 1")
    @Max(value = 10000, message = "Page count cannot exceed 10,000")
    @Column(name = "page_count")
    private Integer pageCount;

    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Default constructor
    public Book() {}

    // Constructor with required fields
    public Book(String title, String author, Integer publicationYear) {
        this.title = title;
        this.author = author;
        this.publicationYear = publicationYear;
        this.isAvailable = true;
    }

    // Constructor with all main fields
    public Book(String title, String author, Integer publicationYear, String genre, String isbn, String description, Integer pageCount) {
        this.title = title;
        this.author = author;
        this.publicationYear = publicationYear;
        this.genre = genre;
        this.isbn = isbn;
        this.description = description;
        this.pageCount = pageCount;
        this.isAvailable = true;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Integer getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(Integer publicationYear) {
        this.publicationYear = publicationYear;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPageCount() {
        return pageCount;
    }

    public void setPageCount(Integer pageCount) {
        this.pageCount = pageCount;
    }

    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Computed fields (business logic)

    /**
     * Calculate the age of the book in years
     */
    public Integer getBookAge() {
        if (publicationYear == null) {
            return null;
        }
        return Year.now().getValue() - publicationYear;
    }

    /**
     * Get formatted display title
     */
    public String getDisplayTitle() {
        if (title == null) {
            return null;
        }
        if (publicationYear != null) {
            return title + " (" + publicationYear + ")";
        }
        return title;
    }

    /**
     * Check if book is classic (published before 1950)
     */
    public boolean isClassic() {
        return publicationYear != null && publicationYear < 1950;
    }

    /**
     * Check if book is recent (published in 2020 or later)
     */
    public boolean isRecent() {
        return publicationYear != null && publicationYear >= 2020;
    }

    /**
     * Check if book is a long book (more than 500 pages)
     */
    public boolean isLongBook() {
        return pageCount != null && pageCount > 500;
    }

    /**
     * Get book summary for display
     */
    public String getBookSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append(title).append(" by ").append(author);
        if (publicationYear != null) {
            summary.append(" (").append(publicationYear).append(")");
        }
        if (pageCount != null) {
            summary.append(" - ").append(pageCount).append(" pages");
        }
        return summary.toString();
    }

    // Utility methods
    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", publicationYear=" + publicationYear +
                ", genre='" + genre + '\'' +
                ", isbn='" + isbn + '\'' +
                ", pageCount=" + pageCount +
                ", isAvailable=" + isAvailable +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Book book = (Book) obj;
        return id != null && id.equals(book.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    // Business validation methods

    /**
     * Validate if the book data is complete for display
     */
    public boolean isCompleteForDisplay() {
        return title != null && !title.trim().isEmpty() &&
                author != null && !author.trim().isEmpty() &&
                publicationYear != null;
    }

    /**
     * Mark book as unavailable
     */
    public void markAsUnavailable() {
        this.isAvailable = false;
    }

    /**
     * Mark book as available
     */
    public void markAsAvailable() {
        this.isAvailable = true;
    }
}