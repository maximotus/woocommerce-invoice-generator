package model;

import java.time.LocalDate;
import java.util.List;

public record Order(String orderNumber, Customer customer, List<Product> products, LocalDate orderDate) {
}
