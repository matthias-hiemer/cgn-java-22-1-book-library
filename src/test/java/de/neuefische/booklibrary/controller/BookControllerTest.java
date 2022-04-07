package de.neuefische.booklibrary.controller;

import de.neuefische.booklibrary.model.Book;
import de.neuefische.booklibrary.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private BookRepository bookRepository;

    private Book book1 = new Book("123", "test-title-1");
    private Book book2 = new Book("1234", "test-title-2");

    @BeforeEach
    public void cleanUp() {

    }

    @Test
    void getAllBooks() {
        //GIVEN
        bookRepository.addBook(book1);
        bookRepository.addBook(book2);

        //WHEN
        List<Book> actual = webTestClient.get()
                .uri("http://localhost:" + port + "/book")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBodyList(Book.class)
                .returnResult()
                .getResponseBody();

        //THEN
        List<Book> expected = List.of(new Book("123", "test-title-1"), book2);

        assertEquals(expected, actual);
    }

    @Test
    void addBook() {
        //GIVEN
        //WHEN

        Book actual = webTestClient.post()
                .uri("/book")
                .bodyValue(book1)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(Book.class)
                .returnResult()
                .getResponseBody();

        //THEN
        Book expected = new Book("123", "test-title-1");
        assertEquals(expected, actual);
    }

    @Test
    void getBookByIsbn_whenInvalidIsbn_thenThrowException() {
        //GIVEN
        //WHEN

        webTestClient.get()
                .uri("/book/invalid-isbn")
                .exchange()
                .expectStatus().is5xxServerError();

        //THEN
    }
}
