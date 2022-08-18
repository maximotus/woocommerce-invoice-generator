package data;

import model.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class WooCommerceDataDeserializer {
    private static final String ORDER_NUMBER_KEY = "order_number";
    private static final String ORDER_DATE_KEY = "order_date";
    private static final String FIRST_NAME_KEY = "billing_first_name";
    private static final String LAST_NAME_KEY = "billing_last_name";
    private static final String STREET_INFORMATION_KEY = "billing_address";
    private static final String STREET_INFORMATION_SPLIT_REGEX = " ";
    private static final int STREET_NAME_INDEX = 0;
    private static final int STREET_NUMBER_INDEX = 1;
    private static final String ZIP_CODE_KEY = "billing_postcode";
    private static final String LOCATION_KEY = "billing_city";
    private static final String COUNTRY_KEY = "billing_country";
    private static final String EMAIL_KEY = "billing_email";
    private static final String PHONE_KEY = "billing_phone";
    private static final String PRODUCTS_KEY = "products";
    private static final String PRODUCT_NAME_KEY = "name";
    private static final String PRODUCT_PRICE_KEY = "item_price";
    private static final String PRODUCT_AMOUNT_KEY = "qty";
    private static final String SHIPPING_PRICE_KEY = "order_shipping";
    private static final DateTimeFormatter WOO_COMMERCE_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final String DEFAULT_SHIPPING_NAME = "Versand";
    private static final int DEFAULT_SHIPPING_AMOUNT = 1;

    private String filePath;
    private JSONArray rawData;
    private List<Order> data;

    public WooCommerceDataDeserializer(String filePath) throws IOException {
        this.filePath = filePath;
        this.rawData = new JSONArray(Files.readString(Path.of(filePath)));
        this.data = new ArrayList<>();
    }

    public void deserialize() {
        for (Object jsonObject : rawData) {
            JSONObject jsonOrder = (JSONObject) jsonObject;
            data.add(deserializeOrder(jsonOrder));
        }
    }

    private Order deserializeOrder(JSONObject jsonOrder) {
        String orderNumber = jsonOrder.getString(ORDER_NUMBER_KEY);
        String orderDate = jsonOrder.getString(ORDER_DATE_KEY);
        Customer customer = deserializeCustomer(jsonOrder);
        List<Product> products = deserializeProducts(jsonOrder);
        return new Order(orderNumber, customer, products, LocalDate.parse(orderDate, WOO_COMMERCE_DATE_FORMAT));
    }

    private Customer deserializeCustomer(JSONObject jsonOrder) {
        String firstName = jsonOrder.getString(FIRST_NAME_KEY);
        String lastName = jsonOrder.getString(LAST_NAME_KEY);
        Address address = deserializeAddress(jsonOrder);
        Contact contact = deserializeContact(jsonOrder);
        int customerId = Customer.UNDEFINED_CUSTOMER_ID;
        Customer customer = new Customer(firstName, lastName, address, contact, customerId);
        customer.setCustomerId(customer.hashCode());
        return customer;
    }

    private Address deserializeAddress(JSONObject jsonOrder) {
        String streetAndStreetNumber = jsonOrder.getString(STREET_INFORMATION_KEY);
        String street = streetAndStreetNumber.split(STREET_INFORMATION_SPLIT_REGEX)[STREET_NAME_INDEX];
        String streetNumber = streetAndStreetNumber.split(STREET_INFORMATION_SPLIT_REGEX)[STREET_NUMBER_INDEX];
        String zipCode = jsonOrder.getString(ZIP_CODE_KEY);
        String location = jsonOrder.getString(LOCATION_KEY);
        String country = jsonOrder.getString(COUNTRY_KEY);
        return new Address(street, streetNumber, zipCode, location, country);
    }

    private Contact deserializeContact(JSONObject jsonCustomer) {
        String email = jsonCustomer.getString(EMAIL_KEY);
        String phoneNumber = jsonCustomer.getString(PHONE_KEY);
        return new Contact(email, phoneNumber);
    }

    private List<Product> deserializeProducts(JSONObject jsonOrder) {
        JSONArray jsonProducts = jsonOrder.getJSONArray(PRODUCTS_KEY);
        List<Product> products = new ArrayList<>();
        for (Object jsonObject : jsonProducts) {
            JSONObject jsonProduct = (JSONObject) jsonObject;
            String name = jsonProduct.getString(PRODUCT_NAME_KEY);
            double price = jsonProduct.getDouble(PRODUCT_PRICE_KEY);
            int amount = Integer.parseInt(jsonProduct.getString(PRODUCT_AMOUNT_KEY));
            products.add(new Product(name, price, amount));
        }
        products.add(deserializeShipping(jsonOrder));
        return products;
    }

    private Product deserializeShipping(JSONObject jsonOrder) {
        double price = jsonOrder.getDouble(SHIPPING_PRICE_KEY);
        return new Product(DEFAULT_SHIPPING_NAME, price, DEFAULT_SHIPPING_AMOUNT);
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public JSONArray getRawData() {
        return rawData;
    }

    public void setRawData(JSONArray rawData) {
        this.rawData = rawData;
    }

    public List<Order> getData() throws NotDeserializedYetException {
        if (data.isEmpty()) throw new NotDeserializedYetException("Call the method deserialize() first.");
        return data;
    }

    public void setData(List<Order> data) {
        this.data = data;
    }
}
