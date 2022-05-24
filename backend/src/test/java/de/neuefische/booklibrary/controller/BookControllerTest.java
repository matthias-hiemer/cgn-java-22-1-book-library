package de.neuefische.booklibrary.controller;


import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import de.neuefische.booklibrary.model.Book;
import de.neuefische.booklibrary.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

@WireMockTest(httpPort = 8484)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private WebTestClient testClient;

    @Autowired
    private BookRepository bookRepository;

    @BeforeEach
    public void cleanUp() {
        bookRepository.deleteAll();
    }

    @Test
    void getAllBooks() {
        //GIVEN
        Book book1 = new Book("1234", "test-title");
        bookRepository.addBook(book1);

        //WHEN

        List<Book> actual = testClient.get()
                .uri("/book")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBodyList(Book.class)
                .returnResult()
                .getResponseBody();

        //THEN
        List<Book> expected = List.of(new Book("1234", "test-title"));
        assertEquals(expected, actual);
    }


    @Test
    void addBookByIsbn() {
        //GIVEN
        String isbn = "978-3-8362-8745-6";
        String jsonResponse = "  {\n" +
                "    \"id\": \"978-3-8362-8745-6\",\n" +
                "    \"title\": \"Java ist auch eine Insel!\",\n" +
                "    \"author\": \"Christian Ullenboom\"\n" +
                "  }";

        stubFor(get("/BookApi/books/"+isbn).willReturn(okJson(jsonResponse)));

        //WHEN
        Book actual = testClient.put()
                .uri("http://localhost:" + port + "/book/" + isbn)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(Book.class)
                .returnResult()
                .getResponseBody();

        //THEN

        Book expected = new Book("978-3-8362-8745-6", "Java ist auch eine Insel!");
        assertEquals(expected, actual);
    }

    @Test
    void addBook() {
        //GIVEN
        Book book = new Book("1234", "test-title");

        //WHEN
        Book actual = testClient.post()
                .uri("http://localhost:" + port + "/book")
                .bodyValue(book)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(Book.class)
                .returnResult()
                .getResponseBody();

        //THEN

        Book expected = new Book("1234", "test-title");
        assertEquals(expected, actual);
    }
}
