package de.neuefische.booklibrary.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class IsbnBook {

    private String id;
    private String title;
    private String author;
}
