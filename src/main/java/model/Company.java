package model;

import java.util.List;

public record Company(String label, String name, String declaration, Address address, List<Person> shareholders, BankAccount bankAccount, String taxNumber) {
}
