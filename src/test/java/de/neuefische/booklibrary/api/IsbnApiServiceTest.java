package de.neuefische.booklibrary.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import de.neuefische.booklibrary.model.Book;
import de.neuefische.booklibrary.model.IsbnBook;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static org.junit.jupiter.api.Assertions.assertEquals;

class IsbnApiServiceTest {

    private WireMockServer wireMockServer;
    private IsbnApiService isbnApiService;

    @BeforeEach
    void setUp() {
        wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort());
        wireMockServer.start();

        WebClient webClient = WebClient.builder().baseUrl(wireMockServer.baseUrl()).build();
        isbnApiService = new IsbnApiService(webClient);
    }

    @AfterEach
    void cleanUp(){
        wireMockServer.stop();
    }

    @Test
    void retrieveBookByIsbn() throws JsonProcessingException {
        //GIVEN
        String isbn = "123";
        IsbnBook isbnBook = new IsbnBook("123", "my-book", "me");

        ObjectMapper objectMapper = new ObjectMapper();

        wireMockServer.stubFor(get("/books/" + isbn)
                        .willReturn(okJson(objectMapper.writeValueAsString(isbnBook))));
                //.willReturn(okJson("{\"id\": \"123\", \"title\": \"my-book\"}")));
        //WHEN

        Book actual = isbnApiService.retrieveBookByIsbn(isbn);

        //THEN
        Book expected = new Book(isbn, "my-book");

        assertEquals(expected, actual);
    }

}
