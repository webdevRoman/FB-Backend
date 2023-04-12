package ru.rgrabelnikov.fbbackend.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

// todo remove
@RestController
@RequestMapping("/api/controller")
public class MainController {

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public Flux<String> list() {
        return Flux.just("1", "2");
    }
}