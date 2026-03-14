package com.example.noteproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class NoteProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(NoteProjectApplication.class, args);
    }

}
