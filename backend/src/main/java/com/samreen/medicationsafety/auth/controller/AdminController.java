package com.samreen.medicationsafety.auth.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @GetMapping("/status")
    public ResponseEntity<Map<String, String>> status() {

        return ResponseEntity.ok(
                Map.of(
                    "status", "ok",
                    "message", "ADMIN access granted"
                )
        );
    }
}
