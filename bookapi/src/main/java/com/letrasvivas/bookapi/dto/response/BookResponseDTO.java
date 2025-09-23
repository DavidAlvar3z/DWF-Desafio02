// BookResponseDTO.java
package com.letrasvivas.bookapi.dto.response;

import java.time.LocalDateTime;

public class BookResponseDTO {

    private Long id;

    private String title;

    private String author;

    private Integer publicationYear;

    private String genre;

    private String isbn;

    private String description;

    private Integer pageCount;

    private Boolean isAvailable;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // Computed fields
    private Integer bookAge;

    private String displayTitle;

    // Default constructor
    public BookResponseDTO() {}

    // Constructor with essential fields
    public BookResponseDTO(Long id, String title, String author, Integer publicationYear, Boolean isAvailable) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.publicationYear = publicationYear;
        this.isAvailable = isAvailable;
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

    public Integer getBookAge() {
        return bookAge;
    }

    public void setBookAge(Integer bookAge) {
        this.bookAge = bookAge;
    }

    public String getDisplayTitle() {
        return displayTitle;
    }

    public void setDisplayTitle(String displayTitle) {
        this.displayTitle = displayTitle;
    }

    // Utility methods
    @Override
    public String toString() {
        return "BookResponseDTO{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", publicationYear=" + publicationYear +
                ", genre='" + genre + '\'' +
                ", isbn='" + isbn + '\'' +
                ", pageCount=" + pageCount +
                ", isAvailable=" + isAvailable +
                ", bookAge=" + bookAge +
                '}';
    }
}