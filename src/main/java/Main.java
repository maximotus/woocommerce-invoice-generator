import com.itextpdf.text.*;
import data.*;
import document.InvoiceGenerator;
import document.InvoiceConfiguration;
import model.*;
import notification.EmailService;

import javax.mail.MessagingException;
import java.io.*;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    private static final String COMPANY_DATA_FILE_PATH = "src/main/resources/data/company.json";
    private static final String WOO_COMMERCE_DATA_FILE_PATH = "src/main/resources/data/orders.json";
    private static final String INVOICE_CONFIGURATION_DATA_FILE_PATH = "src/main/resources/data/invoice.json";
    private static final String NOTIFICATION_DATA_FILE_PATH = "src/main/resources/data/notification.json";

    private static final String INFO_TEXT_0 = "Invoice Generator by Max Erler (https://www.maxerler.com)";
    private static final String INFO_TEXT_1 = "This is a free tool for semi-automation of invoice generation for small businesses.";
    private static final String INFO_TEXT_2 = "Command line arguments: <PATH_TO_COMPANY_CONFIG> <PATH_TO_ORDERS_CONFIG> <PATH_TO_INVOICE_CONFIG>";
    private static final String INFO_TEXT_3 = "PATH_TO_COMPANY_CONFIG: relative or absolute path to the company configuration file of type .json (e.g. C:/Users/Public/Documents/company.json)";
    private static final String INFO_TEXT_4 = "PATH_TO_ORDERS_CONFIG: relative or absolute path to the orders data file of type .json (e.g. C:/Users/Public/Documents/company.json)";
    private static final String INFO_TEXT_5 = "PATH_TO_INVOICE_CONFIG: relative or absolute path to the invoice configuration file of type .json (e.g. C:/Users/Public/Documents/company.json)";
    private static final String INFO_TEXT_6 = "Example configurations with the required formats can be found in src/main/resources/data";
    private static final String HELP_TEXT = "Pass [h]elp as a command line argument for usage details";

    private static final String[] helpKeys = new String[]{"help", "h", "info", "hel", "he"};

    public static void main(String[] args) {
        Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
        logger.setLevel(Level.ALL);
        logger.info(INFO_TEXT_0);

        if (args.length != 0 && (Arrays.asList(helpKeys).contains(args[0]))) {
            logger.info(INFO_TEXT_1);
            logger.info(INFO_TEXT_2);
            logger.info(INFO_TEXT_3);
            logger.info(INFO_TEXT_4);
            logger.info(INFO_TEXT_5);
            logger.info(INFO_TEXT_6);
            return;
        } else {
            logger.info(HELP_TEXT);
        }

        String[] paths = new String[]{
                COMPANY_DATA_FILE_PATH,
                WOO_COMMERCE_DATA_FILE_PATH,
                INVOICE_CONFIGURATION_DATA_FILE_PATH,
                NOTIFICATION_DATA_FILE_PATH};

        if (args.length == 4) {
            paths = args;
            logger.info("Using the command line arguments as file paths: " + Arrays.toString(paths));
        } else {
            logger.info("Using the default file paths" + Arrays.toString(paths));
        }

        try {
            CompanyDataDeserializer companyDataDeserializer = new CompanyDataDeserializer(paths[0]);
            companyDataDeserializer.deserialize();
            Company company = companyDataDeserializer.getData();

            WooCommerceDataDeserializer wooCommerceDataDeserializer = new WooCommerceDataDeserializer(paths[1]);
            wooCommerceDataDeserializer.deserialize();
            List<Order> orders = wooCommerceDataDeserializer.getData();

            InvoiceConfigurationDataDeserializer invoiceConfigurationDataDeserializer = new InvoiceConfigurationDataDeserializer(paths[2]);
            invoiceConfigurationDataDeserializer.deserialize();
            InvoiceConfiguration config = invoiceConfigurationDataDeserializer.getData();

            NotificationDataDeserializer notificationDataDeserializer = new NotificationDataDeserializer(paths[3]);
            notificationDataDeserializer.deserialize();
            EmailService emailService = notificationDataDeserializer.getData();

            for (Order order : orders) {
                InvoiceGenerator baseDocument = new InvoiceGenerator(config, company, order, LocalDate.now());
                baseDocument.generate();

                String email = order.customer().getContact().email();
                String lastName = order.customer().getLastName();
                String id = baseDocument.getId();
                String fileName = baseDocument.getFileName();
                emailService.sendAttachedMimeMessage(email, lastName, id, fileName);
            }
        } catch (IOException | DocumentException | NotDeserializedYetException | MessagingException e) {
            logger.severe(e.getMessage());
            e.printStackTrace();
        }
    }
}
