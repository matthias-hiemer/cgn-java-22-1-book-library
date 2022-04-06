package de.neuefische.booklibrary.api;

import de.neuefische.booklibrary.model.Book;
import de.neuefische.booklibrary.model.IsbnBook;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class IsbnApiService {

    private final WebClient webClient;

    public IsbnApiService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Book retrieveBookByIsbn(String isbn) {
        IsbnBook isbnBook =  webClient.get()
                .uri("/books/" + isbn)
                .retrieve()
                .toEntity(IsbnBook.class)
                .block()
                .getBody();

        return new Book(isbn, isbnBook.getTitle());
    }
}
