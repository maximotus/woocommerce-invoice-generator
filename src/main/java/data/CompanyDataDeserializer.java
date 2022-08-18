package data;

import model.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CompanyDataDeserializer {
    private static final String LABEL_KEY = "label";
    private static final String NAME_KEY = "name";
    private static final String DECLARATION_KEY = "declaration";
    private static final String TAX_NUMBER_KEY = "taxNumber";
    private static final String ADDRESS_KEY = "address";
    private static final String SHAREHOLDERS_KEY = "shareholders";
    private static final String BANK_ACCOUNT_KEY = "bankAccount";
    private static final String STREET_KEY = "street";
    private static final String STREET_NUMBER_KEY = "streetNumber";
    private static final String ZIP_CODE_KEY = "zipCode";
    private static final String LOCATION_KEY = "location";
    private static final String COUNTRY_KEY = "country";
    private static final String FIRST_NAME_KEY = "firstName";
    private static final String LAST_NAME_KEY = "lastName";
    private static final String CONTACT_KEY = "contact";
    private static final String EMAIL_KEY = "email";
    private static final String PHONE_KEY = "phone";
    private static final String IBAN_KEY = "iban";
    private static final String BIC_KEY = "bic";
    private static final String BANK_NAME_KEY = "bankName";

    private String filePath;
    private JSONObject rawData;
    private Company data;

    public CompanyDataDeserializer(String filePath) throws IOException {
        this.filePath = filePath;
        this.rawData = new JSONObject(Files.readString(Path.of(filePath)));
        this.data = null;
    }

    public void deserialize() {
        String label = rawData.getString(LABEL_KEY);
        String name = rawData.getString(NAME_KEY);
        String declaration = rawData.getString(DECLARATION_KEY);
        String taxNumber = rawData.getString(TAX_NUMBER_KEY);
        Address address = deserializeAddress(rawData.getJSONObject(ADDRESS_KEY));
        List<Person> shareholders = deserializeShareholders(rawData.getJSONArray(SHAREHOLDERS_KEY));
        BankAccount bankAccount = deserializeBankAccount(rawData.getJSONObject(BANK_ACCOUNT_KEY));
        this.data = new Company(label, name, declaration, address, shareholders, bankAccount, taxNumber);
    }

    private Address deserializeAddress(JSONObject jsonAddress) {
        String street = jsonAddress.getString(STREET_KEY);
        String streetNumber = jsonAddress.getString(STREET_NUMBER_KEY);
        String zipCode = jsonAddress.getString(ZIP_CODE_KEY);
        String location = jsonAddress.getString(LOCATION_KEY);
        String country = jsonAddress.getString(COUNTRY_KEY);
        return new Address(street, streetNumber, zipCode, location, country);
    }

    private List<Person> deserializeShareholders(JSONArray jsonShareholders) {
        List<Person> shareholders = new ArrayList<>();
        for (Object jsonObject : jsonShareholders) {
            JSONObject jsonShareholder = (JSONObject) jsonObject;
            String firstName = jsonShareholder.getString(FIRST_NAME_KEY);
            String lastName = jsonShareholder.getString(LAST_NAME_KEY);
            Address address = deserializeAddress(jsonShareholder.getJSONObject(ADDRESS_KEY));
            Contact contact = deserializeContact(jsonShareholder.getJSONObject(CONTACT_KEY));
            shareholders.add(new Person(firstName, lastName, address, contact));
        }
        return shareholders;
    }

    private Contact deserializeContact(JSONObject jsonContact) {
        String email = jsonContact.getString(EMAIL_KEY);
        String phone = jsonContact.getString(PHONE_KEY);
        return new Contact(email, phone);
    }

    private BankAccount deserializeBankAccount(JSONObject jsonBankAccount) {
        String iban = jsonBankAccount.getString(IBAN_KEY);
        String bic = jsonBankAccount.getString(BIC_KEY);
        String bankName = jsonBankAccount.getString(BANK_NAME_KEY);
        return new BankAccount(iban, bic, bankName);
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public JSONObject getRawData() {
        return rawData;
    }

    public void setRawData(JSONObject rawData) {
        this.rawData = rawData;
    }

    public Company getData() throws NotDeserializedYetException {
        if (data == null) throw new NotDeserializedYetException("Call the method deserialize() first.");
        return data;
    }

    public void setData(Company data) {
        this.data = data;
    }
}
