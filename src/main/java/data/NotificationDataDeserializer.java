package data;

import notification.EmailService;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;

public class NotificationDataDeserializer {
    private static final String EMAIL_KEY = "email";
    private static final String ADDRESS_KEY = "address";
    private static final String PASSWORD_KEY = "password";
    private static final String HOST_KEY = "host";
    private static final String PORT_KEY = "port";
    private static final String TLS_KEY = "tls";
    private static final String AUTH_KEY = "auth";
    private static final String DEBUG_KEY = "debug";
    private static final String PROTOCOL_KEY = "protocol";
    private static final String TRUST_KEY = "trust";
    private static final String FROM_KEY = "from";
    private static final String SUBJECT_KEY = "subject";
    private static final String SALUTATION_KEY = "salutation";
    private static final String MESSAGE_KEY = "message";
    private static final String GREETINGS_KEY = "greetings";
    private static final String SIGNATURE_KEY = "signature";

    private final JSONObject rawData;
    private EmailService data;

    public NotificationDataDeserializer(String filePath) throws IOException {
        this.rawData = new JSONObject(Files.readString(Path.of(filePath)));
        this.data = null;
    }

    public void deserialize() throws UnsupportedEncodingException {
        JSONObject email = rawData.getJSONObject(EMAIL_KEY);
        String address = email.getString(ADDRESS_KEY);
        String password = email.getString(PASSWORD_KEY);
        String host = email.getString(HOST_KEY);
        String port = email.getString(PORT_KEY);
        boolean tls = email.getBoolean(TLS_KEY);
        boolean auth = email.getBoolean(AUTH_KEY);
        boolean debug = email.getBoolean(DEBUG_KEY);
        String protocol = email.getString(PROTOCOL_KEY);
        String trust = email.getString(TRUST_KEY);
        String from = email.getString(FROM_KEY);
        String subject = email.getString(SUBJECT_KEY);
        String salutation = email.getString(SALUTATION_KEY);
        String message = email.getString(MESSAGE_KEY);
        String greetings = email.getString(GREETINGS_KEY);
        String signature = email.getString(SIGNATURE_KEY);
        this.data = new EmailService(address, password, host, port, tls, auth, debug, protocol, trust, from, subject,
                salutation, message, greetings, signature);
    }

    public EmailService getData() throws NotDeserializedYetException {
        if (data == null) throw new NotDeserializedYetException("Call the method deserialize() first.");
        return data;
    }
}
