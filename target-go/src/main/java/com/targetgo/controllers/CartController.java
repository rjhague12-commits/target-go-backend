package com.targetgo.controllers;

import com.targetgo.models.CartItem;
import com.targetgo.services.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired private CartService cartService;

    // ── GET /api/cart ─────────────────────────────────────────────────
    @GetMapping
    public ResponseEntity<?> getCart(@AuthenticationPrincipal UserDetails userDetails) {
        List<CartItem> items = cartService.getCart(userDetails.getUsername());
        return ResponseEntity.ok(Map.of(
                "items", items,
                "total", cartService.getCartTotal(userDetails.getUsername())
        ));
    }

    // ── POST /api/cart/add ────────────────────────────────────────────
    // Body: { "productId": 1, "quantity": 2 }
    @PostMapping("/add")
    public ResponseEntity<?> addToCart(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, Integer> body) {
        try {
            Long productId = body.get("productId").longValue();
            int quantity = body.getOrDefault("quantity", 1);
            CartItem item = cartService.addToCart(
                    userDetails.getUsername(), productId, quantity);
            return ResponseEntity.ok(item);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ── PUT /api/cart/{itemId} ────────────────────────────────────────
    // Body: { "quantity": 3 }
    @PutMapping("/{itemId}")
    public ResponseEntity<?> updateQuantity(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long itemId,
            @RequestBody Map<String, Integer> body) {
        try {
            CartItem updated = cartService.updateQuantity(
                    userDetails.getUsername(), itemId, body.get("quantity"));
            return ResponseEntity.ok(
                    updated != null ? updated : Map.of("message", "Item removed"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ── DELETE /api/cart/{itemId} ─────────────────────────────────────
    @DeleteMapping("/{itemId}")
    public ResponseEntity<?> removeItem(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long itemId) {
        try {
            cartService.removeFromCart(userDetails.getUsername(), itemId);
            return ResponseEntity.ok(Map.of("message", "Item removed from cart"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ── DELETE /api/cart ──────────────────────────────────────────────
    @DeleteMapping
    public ResponseEntity<?> clearCart(@AuthenticationPrincipal UserDetails userDetails) {
        cartService.clearCart(userDetails.getUsername());
        return ResponseEntity.ok(Map.of("message", "Cart cleared"));
    }
}
