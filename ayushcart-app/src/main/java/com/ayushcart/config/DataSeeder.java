package com.ayushcart.config;

import com.ayushcart.entity.Category;
import com.ayushcart.entity.Product;
import com.ayushcart.entity.Role;
import com.ayushcart.entity.User;
import com.ayushcart.repository.CategoryRepository;
import com.ayushcart.repository.ProductRepository;
import com.ayushcart.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Inserts an admin, a demo customer and sample products on startup so the app
 * is usable immediately. It only inserts what is missing, so it is safe with MySQL too.
 * Turn it off with app.seed.enabled=false.
 */
@Component
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true")
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminEmail;
    private final String adminPassword;

    public DataSeeder(UserRepository userRepository, CategoryRepository categoryRepository,
                      ProductRepository productRepository, PasswordEncoder passwordEncoder,
                      @Value("${app.seed.admin-email}") String adminEmail,
                      @Value("${app.seed.admin-password}") String adminPassword) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
    }

    @Override
    @Transactional
    public void run(String... args) {
        createUserIfMissing("AyushCart Admin", adminEmail, adminPassword, Role.ADMIN);
        createUserIfMissing("Demo Customer", "customer@ayushcart.com", "Customer@123", Role.CUSTOMER);

        if (productRepository.count() > 0) {
            return;
        }

        Map<String, Category> c = new LinkedHashMap<>();
        for (String[] row : new String[][]{
                {"Electronics", "Phones, audio and accessories"},
                {"Fashion", "Clothing and footwear"},
                {"Home & Kitchen", "Cookware, storage and decor"},
                {"Books", "Fiction, non-fiction and study guides"},
                {"Sports", "Fitness and outdoor gear"}}) {
            c.put(row[0], categoryRepository.save(new Category(row[0], row[1])));
        }

        Object[][] products = {
                {"Wireless Earbuds Pro", "Bluetooth 5.3 earbuds with active noise cancellation and 30-hour battery with case.", "2499", 40, "Electronics"},
                {"Smartwatch Fit 2", "AMOLED display, heart-rate and SpO2 tracking, 7-day battery.", "3999", 25, "Electronics"},
                {"20000mAh Power Bank", "Fast-charging power bank with USB-C PD 22.5W output.", "1499", 60, "Electronics"},
                {"Mechanical Keyboard", "Hot-swappable switches, RGB backlight, compact 75% layout.", "4299", 4, "Electronics"},
                {"Cotton Kurta", "Breathable pure cotton kurta, regular fit.", "899", 50, "Fashion"},
                {"Running Shoes", "Lightweight mesh upper with cushioned sole for daily runs.", "2199", 30, "Fashion"},
                {"Denim Jacket", "Classic blue denim jacket with button front.", "1799", 3, "Fashion"},
                {"Canvas Backpack", "25L water-resistant backpack with laptop sleeve.", "1199", 45, "Fashion"},
                {"Non-stick Tawa", "28cm granite-coated tawa, induction compatible.", "749", 70, "Home & Kitchen"},
                {"Steel Water Bottle", "1L insulated bottle, keeps drinks cold for 24 hours.", "599", 100, "Home & Kitchen"},
                {"Pressure Cooker 5L", "Stainless steel pressure cooker with safety valve.", "2299", 20, "Home & Kitchen"},
                {"Clean Code", "Robert C. Martin's handbook of agile software craftsmanship.", "650", 35, "Books"},
                {"Data Structures in Java", "Concepts and problems for placement preparation.", "499", 50, "Books"},
                {"Atomic Habits", "James Clear on building good habits and breaking bad ones.", "399", 80, "Books"},
                {"Yoga Mat 6mm", "Anti-slip TPE yoga mat with carry strap.", "699", 55, "Sports"},
                {"Cricket Bat (Kashmir Willow)", "Full-size Kashmir willow bat for leather and tennis balls.", "1899", 12, "Sports"},
                {"Adjustable Dumbbells", "Pair of dumbbells adjustable from 2.5kg to 10kg.", "3499", 8, "Sports"},
        };
        for (Object[] p : products) {
            productRepository.save(new Product((String) p[0], (String) p[1], new BigDecimal((String) p[2]),
                    (Integer) p[3], null, c.get((String) p[4])));
        }
        log.info("Seeded {} categories and {} products", c.size(), products.length);
    }

    private void createUserIfMissing(String name, String email, String rawPassword, Role role) {
        if (!userRepository.existsByEmail(email)) {
            userRepository.save(new User(name, email, passwordEncoder.encode(rawPassword), role));
            log.info("Created {} account: {}", role, email);
        }
    }
}
