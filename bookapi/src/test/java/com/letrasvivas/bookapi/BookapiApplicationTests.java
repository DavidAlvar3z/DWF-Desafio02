package com.letrasvivas.bookapi;

import com.letrasvivas.bookapi.entity.Book;
import jakarta.validation.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("Book API Application Tests")
class BookapiApplicationTests {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Spring Context should load successfully")
    void contextLoads() {
        // Test que verifica que el contexto Spring se carga correctamente
        assertNotNull(validator, "Validator should be initialized");
    }

    @Nested
    @DisplayName("Book Validation Tests")
    class BookValidationTests {

        @Test
        @DisplayName("Valid book with all required fields should pass validation")
        void validBookShouldNotHaveViolations() {
            Book book = new Book("Effective Java", "Joshua Bloch", 2018);

            Set<ConstraintViolation<Book>> violations = validator.validate(book);

            assertTrue(violations.isEmpty(), "Book with valid data should not have violations");
        }

        @Test
        @DisplayName("Valid book with all fields should pass validation")
        void validBookWithAllFieldsShouldNotHaveViolations() {
            Book book = new Book(
                    "Clean Code: A Handbook of Agile Software Craftsmanship",
                    "Robert C. Martin",
                    2008,
                    "Programming",
                    "9780132350884",
                    "A handbook for writing clean, maintainable code",
                    464
            );

            Set<ConstraintViolation<Book>> violations = validator.validate(book);

            assertTrue(violations.isEmpty(), "Book with all valid fields should not have violations");
        }

        @Nested
        @DisplayName("Title Validation Tests")
        class TitleValidationTests {

            @Test
            @DisplayName("Blank title should cause validation violation")
            void blankTitleShouldCauseViolation() {
                Book book = new Book("", "Joshua Bloch", 2020);

                Set<ConstraintViolation<Book>> violations = validator.validate(book);

                assertFalse(violations.isEmpty(), "Blank title should cause violation");
                assertTrue(violations.stream()
                        .anyMatch(v -> v.getPropertyPath().toString().equals("title") &&
                                v.getMessage().equals("Title is mandatory")));
            }

            @Test
            @DisplayName("Null title should cause validation violation")
            void nullTitleShouldCauseViolation() {
                Book book = new Book(null, "Joshua Bloch", 2020);

                Set<ConstraintViolation<Book>> violations = validator.validate(book);

                assertFalse(violations.isEmpty(), "Null title should cause violation");
                assertTrue(violations.stream()
                        .anyMatch(v -> v.getPropertyPath().toString().equals("title")));
            }

            @Test
            @DisplayName("Title exceeding 200 characters should cause validation violation")
            void longTitleShouldCauseViolation() {
                String longTitle = "A".repeat(201); // 201 characters
                Book book = new Book(longTitle, "Joshua Bloch", 2020);

                Set<ConstraintViolation<Book>> violations = validator.validate(book);

                assertFalse(violations.isEmpty(), "Title exceeding 200 characters should cause violation");
                assertTrue(violations.stream()
                        .anyMatch(v -> v.getPropertyPath().toString().equals("title") &&
                                v.getMessage().equals("Title must be between 1 and 200 characters")));
            }
        }

        @Nested
        @DisplayName("Author Validation Tests")
        class AuthorValidationTests {

            @Test
            @DisplayName("Blank author should cause validation violation")
            void blankAuthorShouldCauseViolation() {
                Book book = new Book("Effective Java", "", 2020);

                Set<ConstraintViolation<Book>> violations = validator.validate(book);

                assertFalse(violations.isEmpty(), "Blank author should cause violation");
                assertTrue(violations.stream()
                        .anyMatch(v -> v.getPropertyPath().toString().equals("author") &&
                                v.getMessage().equals("Author is mandatory")));
            }

            @Test
            @DisplayName("Null author should cause validation violation")
            void nullAuthorShouldCauseViolation() {
                Book book = new Book("Effective Java", null, 2020);

                Set<ConstraintViolation<Book>> violations = validator.validate(book);

                assertFalse(violations.isEmpty(), "Null author should cause violation");
                assertTrue(violations.stream()
                        .anyMatch(v -> v.getPropertyPath().toString().equals("author")));
            }

            @Test
            @DisplayName("Author with only 1 character should cause validation violation")
            void shortAuthorShouldCauseViolation() {
                Book book = new Book("Effective Java", "A", 2020);

                Set<ConstraintViolation<Book>> violations = validator.validate(book);

                assertFalse(violations.isEmpty(), "Author with 1 character should cause violation");
                assertTrue(violations.stream()
                        .anyMatch(v -> v.getPropertyPath().toString().equals("author") &&
                                v.getMessage().equals("Author must be between 2 and 100 characters")));
            }

            @Test
            @DisplayName("Author exceeding 100 characters should cause validation violation")
            void longAuthorShouldCauseViolation() {
                String longAuthor = "A".repeat(101); // 101 characters
                Book book = new Book("Effective Java", longAuthor, 2020);

                Set<ConstraintViolation<Book>> violations = validator.validate(book);

                assertFalse(violations.isEmpty(), "Author exceeding 100 characters should cause violation");
                assertTrue(violations.stream()
                        .anyMatch(v -> v.getPropertyPath().toString().equals("author") &&
                                v.getMessage().equals("Author must be between 2 and 100 characters")));
            }
        }

        @Nested
        @DisplayName("Publication Year Validation Tests")
        class PublicationYearValidationTests {

            @Test
            @DisplayName("Publication year before 1500 should cause validation violation")
            void publicationYearTooLowShouldCauseViolation() {
                Book book = new Book("Ancient Book", "Ancient Author", 1400);

                Set<ConstraintViolation<Book>> violations = validator.validate(book);

                assertFalse(violations.isEmpty(), "Publication year before 1500 should cause violation");
                assertTrue(violations.stream()
                        .anyMatch(v -> v.getPropertyPath().toString().equals("publicationYear") &&
                                v.getMessage().equals("Publication year must be later than 1500")));
            }

            @Test
            @DisplayName("Publication year after 2025 should cause validation violation")
            void publicationYearTooHighShouldCauseViolation() {
                Book book = new Book("Future Book", "Future Author", 3000);

                Set<ConstraintViolation<Book>> violations = validator.validate(book);

                assertFalse(violations.isEmpty(), "Publication year after 2025 should cause violation");
                assertTrue(violations.stream()
                        .anyMatch(v -> v.getPropertyPath().toString().equals("publicationYear") &&
                                v.getMessage().equals("Publication year cannot be greater than 2025")));
            }

            @Test
            @DisplayName("Null publication year should cause validation violation")
            void nullPublicationYearShouldCauseViolation() {
                Book book = new Book("Test Book", "Test Author", null);

                Set<ConstraintViolation<Book>> violations = validator.validate(book);

                assertFalse(violations.isEmpty(), "Null publication year should cause violation");
                assertTrue(violations.stream()
                        .anyMatch(v -> v.getPropertyPath().toString().equals("publicationYear") &&
                                v.getMessage().equals("Publication year is mandatory")));
            }

            @Test
            @DisplayName("Valid boundary years (1500 and 2025) should pass validation")
            void boundaryYearsShouldBeValid() {
                Book book1500 = new Book("Old Book", "Old Author", 1500);
                Book book2025 = new Book("Recent Book", "Recent Author", 2025);

                Set<ConstraintViolation<Book>> violations1500 = validator.validate(book1500);
                Set<ConstraintViolation<Book>> violations2025 = validator.validate(book2025);

                assertTrue(violations1500.isEmpty(), "Year 1500 should be valid");
                assertTrue(violations2025.isEmpty(), "Year 2025 should be valid");
            }
        }

        @Nested
        @DisplayName("ISBN Validation Tests")
        class IsbnValidationTests {

            @Test
            @DisplayName("Valid ISBN-10 should pass validation")
            void validIsbn10ShouldPassValidation() {
                Book book = new Book("Test Book", "Test Author", 2020);
                book.setIsbn("0321356683");

                Set<ConstraintViolation<Book>> violations = validator.validate(book);

                assertTrue(violations.isEmpty(), "Valid ISBN-10 should not cause violations");
            }

            @Test
            @DisplayName("Valid ISBN-13 should pass validation")
            void validIsbn13ShouldPassValidation() {
                Book book = new Book("Test Book", "Test Author", 2020);
                book.setIsbn("9780321356680");

                Set<ConstraintViolation<Book>> violations = validator.validate(book);

                assertTrue(violations.isEmpty(), "Valid ISBN-13 should not cause violations");
            }

            @Test
            @DisplayName("Invalid ISBN format should cause validation violation")
            void invalidIsbnShouldCauseViolation() {
                Book book = new Book("Test Book", "Test Author", 2020);
                book.setIsbn("invalid-isbn");

                Set<ConstraintViolation<Book>> violations = validator.validate(book);

                assertFalse(violations.isEmpty(), "Invalid ISBN should cause violation");
                assertTrue(violations.stream()
                        .anyMatch(v -> v.getPropertyPath().toString().equals("isbn") &&
                                v.getMessage().equals("ISBN format is invalid")));
            }

            @Test
            @DisplayName("ISBN exceeding 20 characters should cause validation violation")
            void longIsbnShouldCauseViolation() {
                Book book = new Book("Test Book", "Test Author", 2020);
                book.setIsbn("123456789012345678901"); // 21 characters

                Set<ConstraintViolation<Book>> violations = validator.validate(book);

                assertFalse(violations.isEmpty(), "ISBN exceeding 20 characters should cause violation");
                assertTrue(violations.stream()
                        .anyMatch(v -> v.getPropertyPath().toString().equals("isbn") &&
                                v.getMessage().equals("ISBN cannot exceed 20 characters")));
            }
        }

        @Nested
        @DisplayName("Optional Fields Validation Tests")
        class OptionalFieldsValidationTests {

            @Test
            @DisplayName("Valid genre should pass validation")
            void validGenreShouldPassValidation() {
                Book book = new Book("Test Book", "Test Author", 2020);
                book.setGenre("Fiction");

                Set<ConstraintViolation<Book>> violations = validator.validate(book);

                assertTrue(violations.isEmpty(), "Valid genre should not cause violations");
            }

            @Test
            @DisplayName("Genre exceeding 50 characters should cause validation violation")
            void longGenreShouldCauseViolation() {
                Book book = new Book("Test Book", "Test Author", 2020);
                book.setGenre("A".repeat(51)); // 51 characters

                Set<ConstraintViolation<Book>> violations = validator.validate(book);

                assertFalse(violations.isEmpty(), "Genre exceeding 50 characters should cause violation");
                assertTrue(violations.stream()
                        .anyMatch(v -> v.getPropertyPath().toString().equals("genre") &&
                                v.getMessage().equals("Genre cannot exceed 50 characters")));
            }

            @Test
            @DisplayName("Valid page count should pass validation")
            void validPageCountShouldPassValidation() {
                Book book = new Book("Test Book", "Test Author", 2020);
                book.setPageCount(300);

                Set<ConstraintViolation<Book>> violations = validator.validate(book);

                assertTrue(violations.isEmpty(), "Valid page count should not cause violations");
            }

            @Test
            @DisplayName("Page count less than 1 should cause validation violation")
            void invalidPageCountShouldCauseViolation() {
                Book book = new Book("Test Book", "Test Author", 2020);
                book.setPageCount(0);

                Set<ConstraintViolation<Book>> violations = validator.validate(book);

                assertFalse(violations.isEmpty(), "Page count less than 1 should cause violation");
                assertTrue(violations.stream()
                        .anyMatch(v -> v.getPropertyPath().toString().equals("pageCount") &&
                                v.getMessage().equals("Page count must be at least 1")));
            }

            @Test
            @DisplayName("Page count exceeding 10000 should cause validation violation")
            void highPageCountShouldCauseViolation() {
                Book book = new Book("Test Book", "Test Author", 2020);
                book.setPageCount(10001);

                Set<ConstraintViolation<Book>> violations = validator.validate(book);

                assertFalse(violations.isEmpty(), "Page count exceeding 10000 should cause violation");
                assertTrue(violations.stream()
                        .anyMatch(v -> v.getPropertyPath().toString().equals("pageCount") &&
                                v.getMessage().equals("Page count cannot exceed 10,000")));
            }

            @Test
            @DisplayName("Description exceeding 500 characters should cause validation violation")
            void longDescriptionShouldCauseViolation() {
                Book book = new Book("Test Book", "Test Author", 2020);
                book.setDescription("A".repeat(501)); // 501 characters

                Set<ConstraintViolation<Book>> violations = validator.validate(book);

                assertFalse(violations.isEmpty(), "Description exceeding 500 characters should cause violation");
                assertTrue(violations.stream()
                        .anyMatch(v -> v.getPropertyPath().toString().equals("description") &&
                                v.getMessage().equals("Description cannot exceed 500 characters")));
            }
        }
    }

    @Nested
    @DisplayName("Book Business Logic Tests")
    class BookBusinessLogicTests {

        @Test
        @DisplayName("Book age should be calculated correctly")
        void bookAgeShouldBeCalculatedCorrectly() {
            Book book = new Book("Test Book", "Test Author", 2000);

            Integer bookAge = book.getBookAge();

            assertNotNull(bookAge, "Book age should not be null");
            assertTrue(bookAge >= 20, "Book from 2000 should be at least 20 years old");
        }

        @Test
        @DisplayName("Display title should include publication year")
        void displayTitleShouldIncludeYear() {
            Book book = new Book("Effective Java", "Joshua Bloch", 2018);

            String displayTitle = book.getDisplayTitle();

            assertEquals("Effective Java (2018)", displayTitle);
        }

        @Test
        @DisplayName("Book should be classic if published before 1950")
        void bookShouldBeClassicIfOld() {
            Book book = new Book("Old Book", "Old Author", 1940);

            assertTrue(book.isClassic(), "Book from 1940 should be classic");
        }

        @Test
        @DisplayName("Book should be recent if published in 2020 or later")
        void bookShouldBeRecentIfNew() {
            Book book = new Book("New Book", "New Author", 2023);

            assertTrue(book.isRecent(), "Book from 2023 should be recent");
        }

        @Test
        @DisplayName("Book should be long if it has more than 500 pages")
        void bookShouldBeLongIfManyPages() {
            Book book = new Book("Long Book", "Author", 2020);
            book.setPageCount(600);

            assertTrue(book.isLongBook(), "Book with 600 pages should be long");
        }

        @Test
        @DisplayName("Book summary should be formatted correctly")
        void bookSummaryShouldBeFormatted() {
            Book book = new Book("Test Book", "Test Author", 2020);
            book.setPageCount(300);

            String summary = book.getBookSummary();

            assertEquals("Test Book by Test Author (2020) - 300 pages", summary);
        }

        @Test
        @DisplayName("New book should be available by default")
        void newBookShouldBeAvailableByDefault() {
            Book book = new Book("Test Book", "Test Author", 2020);

            assertTrue(book.getIsAvailable(), "New book should be available by default");
        }

        @Test
        @DisplayName("Book availability can be changed")
        void bookAvailabilityCanBeChanged() {
            Book book = new Book("Test Book", "Test Author", 2020);

            book.markAsUnavailable();
            assertFalse(book.getIsAvailable(), "Book should be unavailable after marking");

            book.markAsAvailable();
            assertTrue(book.getIsAvailable(), "Book should be available after marking");
        }
    }
}