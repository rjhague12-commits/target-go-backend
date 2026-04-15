package com.targetgo.config;

import com.targetgo.models.Product;
import com.targetgo.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.List;

/**
 * Seeds the database with sample products on first startup.
 * Safe to run multiple times — skips if products already exist.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired private ProductRepository productRepository;

    @Override
    public void run(String... args) {
        if (productRepository.count() > 0) return; // already seeded

        List<Product> products = List.of(
            makeProduct("Apple iPhone 15",       "Electronics", new BigDecimal("799.99"), 50,
                "Latest Apple smartphone with A16 chip",
                "https://target.scene7.com/is/image/Target/GUEST_4b8f1799-a4e1-4b1f-a9b5-657ba0892dc6"),
            makeProduct("Samsung 65\" 4K TV",    "Electronics", new BigDecimal("549.99"), 20,
                "65-inch 4K UHD Smart TV with HDR",
                "https://target.scene7.com/is/image/Target/GUEST_7b9e3a9b-7e8d-4b7c-9b3e-3d7e8f7a9b3e"),
            makeProduct("Sony WH-1000XM5",        "Electronics", new BigDecimal("279.99"), 35,
                "Industry-leading noise canceling headphones",
                "https://target.scene7.com/is/image/Target/GUEST_a1b2c3d4"),
            makeProduct("Keurig K-Elite Coffee Maker", "Kitchen", new BigDecimal("129.99"), 40,
                "Single serve K-Cup pod coffee maker",
                "https://target.scene7.com/is/image/Target/GUEST_coffee1"),
            makeProduct("Instant Pot Duo 7-in-1", "Kitchen", new BigDecimal("89.99"), 60,
                "Electric pressure cooker, slow cooker, rice cooker and more",
                "https://target.scene7.com/is/image/Target/GUEST_instantpot"),
            makeProduct("Ninja Air Fryer Pro",    "Kitchen", new BigDecimal("99.99"), 45,
                "4-quart air fryer with 4-in-1 functionality",
                "https://target.scene7.com/is/image/Target/GUEST_airfryer"),
            makeProduct("Levi's 501 Jeans",       "Clothing",   new BigDecimal("59.99"),  80,
                "Classic straight leg jeans",
                "https://target.scene7.com/is/image/Target/GUEST_levis"),
            makeProduct("Nike Air Max 270",        "Clothing",   new BigDecimal("129.99"), 55,
                "Men's running shoes with Air Max cushioning",
                "https://target.scene7.com/is/image/Target/GUEST_nike"),
            makeProduct("Champion Hoodie",         "Clothing",   new BigDecimal("44.99"),  90,
                "Pullover fleece hoodie, multiple colors",
                "https://target.scene7.com/is/image/Target/GUEST_hoodie"),
            makeProduct("Dyson V15 Detect",        "Home",       new BigDecimal("649.99"), 15,
                "Cordless vacuum cleaner with laser dust detection",
                "https://target.scene7.com/is/image/Target/GUEST_dyson"),
            makeProduct("Casper Original Mattress","Home",       new BigDecimal("595.00"), 10,
                "Queen size foam mattress with pressure relief",
                "https://target.scene7.com/is/image/Target/GUEST_casper"),
            makeProduct("Crayola 64 Crayon Box",   "Toys",       new BigDecimal("7.99"),  200,
                "Classic crayon set with built-in sharpener",
                "https://target.scene7.com/is/image/Target/GUEST_crayola"),
            makeProduct("LEGO Star Wars Millennium Falcon", "Toys", new BigDecimal("169.99"), 25,
                "7,541 piece set for ages 16+",
                "https://target.scene7.com/is/image/Target/GUEST_lego"),
            makeProduct("Neutrogena Face Wash",    "Beauty",     new BigDecimal("8.99"), 150,
                "Oil-free acne wash with salicylic acid",
                "https://target.scene7.com/is/image/Target/GUEST_neutrogena"),
            makeProduct("L'Oreal Mascara",         "Beauty",     new BigDecimal("12.49"), 120,
                "Voluminous Original Bold Mascara",
                "https://target.scene7.com/is/image/Target/GUEST_loreal")
        );

        productRepository.saveAll(products);
        System.out.println("✅ Seeded " + products.size() + " products.");
    }

    private Product makeProduct(String name, String category, BigDecimal price,
                                 int stock, String desc, String imageUrl) {
        Product p = new Product();
        p.setName(name);
        p.setCategory(category);
        p.setPrice(price);
        p.setStockQuantity(stock);
        p.setDescription(desc);
        p.setImageUrl(imageUrl);
        return p;
    }
}
