package com.targetgo.controllers;

import com.targetgo.models.Product;
import com.targetgo.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired private ProductRepository productRepository;

    // ── GET /api/products ─────────────────────────────────────────────
    @GetMapping
    public List<Product> getAllProducts(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category) {

        if (search != null && !search.isBlank()) {
            return productRepository.findByNameContainingIgnoreCase(search);
        }
        if (category != null && !category.isBlank()) {
            return productRepository.findByCategory(category);
        }
        return productRepository.findAll();
    }

    // ── GET /api/products/{id} ────────────────────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable Long id) {
        return productRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
