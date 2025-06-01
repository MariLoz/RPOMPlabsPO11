package com.example.lab1;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Literature {
    private String name;
    private String info;
    private String bookUrl;
    private String author;

    public Literature(String name, String info, String bookUrl, String author) {
        this.name = name;
        this.info = info;
        this.bookUrl = bookUrl;
        this.author = author;
    }
}
