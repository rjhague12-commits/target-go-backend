package com.targetgo.services;

import com.targetgo.models.*;
import com.targetgo.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    @Autowired private OrderRepository orderRepository;
    @Autowired private CartItemRepository cartItemRepository;
    @Autowired private OrderItemRepository orderItemRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private PaymentService paymentService;

    // ── Checkout: convert cart → order, run payment ───────────────────
    @Transactional
    public Order checkout(String email, String cardNumber) {
        User user = getUserByEmail(email);
        List<CartItem> cartItems = cartItemRepository.findByUser(user);

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        // Calculate total
        BigDecimal total = cartItems.stream()
                .map(ci -> ci.getProduct().getPrice()
                        .multiply(BigDecimal.valueOf(ci.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Attempt payment
        PaymentService.PaymentResult result = paymentService.processPayment(cardNumber, total);

        // Create the order record regardless (so we track failed attempts too)
        Order order = new Order();
        order.setUser(user);
        order.setTotalAmount(total);
        order.setStatus(result.success ? "PAID" : "FAILED");
        order.setPaymentConfirmation(result.confirmationNumber);
        order = orderRepository.save(order);

        if (result.success) {
            // Snapshot cart items into order items
            List<OrderItem> orderItems = new ArrayList<>();
            for (CartItem ci : cartItems) {
                OrderItem oi = new OrderItem();
                oi.setOrder(order);
                oi.setProduct(ci.getProduct());
                oi.setQuantity(ci.getQuantity());
                oi.setPriceAtPurchase(ci.getProduct().getPrice());
                orderItems.add(oi);

                // Deduct stock
                Product product = ci.getProduct();
                product.setStockQuantity(product.getStockQuantity() - ci.getQuantity());
                productRepository.save(product);
            }
            orderItemRepository.saveAll(orderItems);
            order.setItems(orderItems);

            // Clear cart after successful purchase
            cartItemRepository.deleteByUser(user);
        } else {
            throw new RuntimeException("Payment failed: " + result.message);
        }

        return order;
    }

    // ── Get all orders for a user ─────────────────────────────────────
    public List<Order> getOrderHistory(String email) {
        User user = getUserByEmail(email);
        return orderRepository.findByUserOrderByCreatedAtDesc(user);
    }

    // ── Get a specific order (receipt) ────────────────────────────────
    public Order getOrder(String email, Long orderId) {
        User user = getUserByEmail(email);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        if (!order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }
        return order;
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
