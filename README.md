# WooCommerce Invoice Generator

This is a handy Java program for the automation of invoice generation and dispatching of WooCommerce order data.

## Invoice Generation

There are three configuration files concerning the invoice generation:

1. `src/main/resources/data/company.json` defines all necessary values of the company that creates the invoice (e.g. company name, tax number, address, etc.)
2. `src/main/resources/data/orders.json` defines the orders exported from WooCommerce for which invoices are to be created (e.g. use [Advanced Order Export For WooCommerce of AlgolPlus](https://de.wordpress.org/plugins/woo-order-export-lite/) for the order export in JSON format)
3. `src/main/resources/data/invoice.json` defines texts and layout of the invoice (e.g. header text, heading, paragraphs and font sizes)

## Invoice Dispatching via Email

The configuration file `notification.json` defines all properties necessary for the email dispatch (e.g. address, password, subject).
The information regarding the connection data to your email-host can be found on its website (e.g. [here](https://support.google.com/mail/answer/7126229) for Gmail).   
