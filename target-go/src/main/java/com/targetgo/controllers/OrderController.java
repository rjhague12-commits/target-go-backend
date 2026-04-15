package com.targetgo.controllers;

import com.targetgo.models.Order;
import com.targetgo.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired private OrderService orderService;

    // ── POST /api/orders/checkout ─────────────────────────────────────
    // Body: { "cardNumber": "4111111111111111" }
    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, String> body) {
        try {
            Order order = orderService.checkout(
                    userDetails.getUsername(),
                    body.get("cardNumber"));
            return ResponseEntity.ok(order);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ── GET /api/orders ───────────────────────────────────────────────
    @GetMapping
    public ResponseEntity<List<Order>> getOrderHistory(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(orderService.getOrderHistory(userDetails.getUsername()));
    }

    // ── GET /api/orders/{id} (receipt) ────────────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        try {
            Order order = orderService.getOrder(userDetails.getUsername(), id);
            return ResponseEntity.ok(order);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
