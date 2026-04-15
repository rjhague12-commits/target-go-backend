package com.targetgo.services;

import com.targetgo.models.*;
import com.targetgo.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class CartService {

    @Autowired private CartItemRepository cartItemRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private UserRepository userRepository;

    // ── Get all items in a user's cart ────────────────────────────────
    public List<CartItem> getCart(String email) {
        User user = getUserByEmail(email);
        return cartItemRepository.findByUser(user);
    }

    // ── Get cart total ────────────────────────────────────────────────
    public BigDecimal getCartTotal(String email) {
        return getCart(email).stream()
                .map(item -> item.getProduct().getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // ── Add item to cart (or increase quantity if already present) ────
    @Transactional
    public CartItem addToCart(String email, Long productId, int quantity) {
        User user = getUserByEmail(email);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found: " + productId));

        if (product.getStockQuantity() < quantity) {
            throw new RuntimeException("Not enough stock available");
        }

        // If item already in cart, just increase quantity
        CartItem item = cartItemRepository
                .findByUserAndProductId(user, productId)
                .orElse(null);

        if (item != null) {
            item.setQuantity(item.getQuantity() + quantity);
        } else {
            item = new CartItem();
            item.setUser(user);
            item.setProduct(product);
            item.setQuantity(quantity);
        }

        return cartItemRepository.save(item);
    }

    // ── Update quantity of a specific cart item ───────────────────────
    @Transactional
    public CartItem updateQuantity(String email, Long cartItemId, int newQuantity) {
        User user = getUserByEmail(email);
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        if (!item.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        if (newQuantity <= 0) {
            cartItemRepository.delete(item);
            return null;
        }

        item.setQuantity(newQuantity);
        return cartItemRepository.save(item);
    }

    // ── Remove a specific item from cart ─────────────────────────────
    @Transactional
    public void removeFromCart(String email, Long cartItemId) {
        User user = getUserByEmail(email);
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        if (!item.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        cartItemRepository.delete(item);
    }

    // ── Clear the entire cart ─────────────────────────────────────────
    @Transactional
    public void clearCart(String email) {
        User user = getUserByEmail(email);
        cartItemRepository.deleteByUser(user);
    }

    // ── Helper ────────────────────────────────────────────────────────
    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
    }
}
