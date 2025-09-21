package com.letrasvivas.bookapi;

import com.letrasvivas.bookapi.model.Book;
import jakarta.validation.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BookapiApplicationTests {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void contextLoads() {
        // Test que verifica que el contexto Spring se carga (auto generado)
    }

    @Test
    void validBookShouldNotHaveViolations() {
        Book book = new Book("Effective Java","Joshua Bloch",2018);
        Set<ConstraintViolation<Book>> violations = validator.validate(book);
        assertTrue(violations.isEmpty(), "Book should be valid");
    }

    @Test
    void blankTitleShouldCauseViolation() {
        Book book = new Book("","Author",2020);
        Set<ConstraintViolation<Book>> violations = validator.validate(book);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("title") &&
                        v.getMessage().equals("The title is mandatory")));
    }

    @Test
    void blankAuthorShouldCauseViolation() {
        Book book = new Book("Title","",2020);
        Set<ConstraintViolation<Book>> violations = validator.validate(book);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("author") &&
                        v.getMessage().equals("The author is mandatory")));
    }

    @Test
    void publicationYearTooLowShouldCauseViolation() {
        Book book = new Book("Title","Author",1400);
        Set<ConstraintViolation<Book>> violations = validator.validate(book);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("publicationYear") &&
                        v.getMessage().equals("The year of publication must be later than 1500.")));
    }

    @Test
    void publicationYearTooHighShouldCauseViolation() {
        Book book = new Book("Title","Author",3000);
        Set<ConstraintViolation<Book>> violations = validator.validate(book);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("publicationYear") &&
                        v.getMessage().equals("The year of publication cannot be greater than 2025")));
    }
}