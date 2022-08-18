package notification;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

public class EmailService {
    private static final String LINE_BREAK = "<br/>";
    private static final String DOUBLE_LINE_BREAK = "<br/><br/>";
    private static final String CONTENT_TYPE = "text/html; charset=utf-8";

    private final Session session;
    private final InternetAddress from;
    private final String subject;
    private final String salutation;
    private final String message;
    private final String greetings;
    private final String signature;

    public EmailService(String address, String password, String host, String port, boolean tls, boolean auth,
                        boolean debug, String protocol, String trust, String from, String subject, String salutation,
                        String message, String greetings, String signature) throws UnsupportedEncodingException {
        this.from = new InternetAddress(address, from);
        this.subject = subject;
        this.salutation = salutation;
        this.message = message;
        this.greetings = greetings;
        this.signature = signature;

        Properties properties = new Properties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);
        properties.put("mail.smtp.starttls.enable", tls);
        properties.put("mail.smtp.auth", auth);
        properties.put("mail.debug", debug);
        properties.put("mail.smtp.ssl.protocols", protocol);
        properties.put("mail.smtp.ssl.trust", trust);

        this.session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(address, password);
            }
        });
    }

    public void sendAttachedMimeMessage(String to, String lastName, String id, String attachmentPath) throws MessagingException, IOException {
        Message message = new MimeMessage(session);

        message.setFrom(from);
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));

        String subject = this.subject + id;
        message.setSubject(subject);

        String salutation = this.salutation + lastName;
        String msg = salutation + "," + DOUBLE_LINE_BREAK + this.message + DOUBLE_LINE_BREAK + greetings + LINE_BREAK + signature;

        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent(msg, CONTENT_TYPE);

        MimeBodyPart attachmentBodyPart = new MimeBodyPart();
        attachmentBodyPart.attachFile(new File(attachmentPath));

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(mimeBodyPart);
        multipart.addBodyPart(attachmentBodyPart);

        message.setContent(multipart);

        Transport.send(message);
    }
}
