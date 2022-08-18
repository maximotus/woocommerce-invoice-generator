package model;

import java.util.Objects;

public class Customer extends Person {
    public static final int UNDEFINED_CUSTOMER_ID = 0;
    private int customerId;

    public Customer(String firstName, String lastName, Address address, Contact contact, int customerId) {
        super(firstName, lastName, address, contact);
        this.customerId = customerId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    @Override
    public int hashCode() {
        // turn the sign bit off to get positive integers only
        // Math.abs throws an ArrayOutOfBoundsException if the hash is equal to Integer.MIN_VALUE
        return Objects.hash(firstName + lastName) & 0xfffffff;
    }
}
