package com.letrasvivas.bookapi.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "The title is mandatory")
    private String title;

    @NotBlank(message = "The author is mandatory")
    private String author;

    @Min(value = 1500, message = "The year of publication must be later than 1500.")
    @Max(value = 2025, message = "The year of publication cannot be greater than 2025")
    private int publicationYear;

    public Book() {}

    public Book(String title, String author, int publicationYear) {
        this.title = title;
        this.author = author;
        this.publicationYear = publicationYear;
    }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }

    public void setAuthor(String author) { this.author = author; }

    public int getPublicationYear() { return publicationYear; }

    public void setPublicationYear(int publicationYear) { this.publicationYear = publicationYear; }
}