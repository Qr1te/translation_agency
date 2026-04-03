package com.qritiooo.translationagency;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class TranslationAgencyApplication {

    public static void main(String[] args) {
        SpringApplication.run(TranslationAgencyApplication.class, args);
    }

}

