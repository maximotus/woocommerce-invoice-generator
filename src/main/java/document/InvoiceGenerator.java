package document;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import model.Company;
import model.Order;
import model.Product;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InvoiceGenerator {
    private static final BaseColor PRIMARY_COLOR = BaseColor.BLACK;
    private static final BaseColor SECONDARY_COLOR = BaseColor.GRAY;
    private static final String FONT_FAMILY = FontFactory.HELVETICA;
    private static final String FILE_TYPE = ".pdf";

    private final InvoiceConfiguration config;
    private final Company company;
    private final Order order;
    private final LocalDate invoiceDate;
    private final LocalDate performanceDate;
    private final List<Product> products;

    private final Font headerFont;
    private final Font headingFont;
    private final Font paragraphFont;
    private final Font footerFont;
    private final LineSeparator lineSeparator;
    private final DateTimeFormatter dateFormatReadable;
    private final DecimalFormat currencyFormat;
    private final DecimalFormat quantityFormat;

    private final String id;
    private final String fileName;

    public InvoiceGenerator(InvoiceConfiguration config,
                            Company company,
                            Order order,
                            LocalDate performanceDate) {
        this.config = config;
        this.company = company;
        this.order = order;
        this.invoiceDate = order.orderDate();
        this.performanceDate = performanceDate;
        this.products = order.products();
        String basePath = config.outputPath();

        this.headerFont = FontFactory.getFont(FONT_FAMILY, config.headerFontSize(), PRIMARY_COLOR);
        this.headingFont = FontFactory.getFont(FONT_FAMILY, config.headingFontSize(), PRIMARY_COLOR);
        this.paragraphFont = FontFactory.getFont(FONT_FAMILY, config.paragraphFontSize(), PRIMARY_COLOR);
        this.footerFont = FontFactory.getFont(FONT_FAMILY, config.footerFontSize(), SECONDARY_COLOR);
        this.lineSeparator = new LineSeparator(config.lineSeparatorWidth(), config.contentWidth(),
                PRIMARY_COLOR, Element.ALIGN_BOTTOM, config.lineSeparatorOffset());
        this.dateFormatReadable = DateTimeFormatter.ofPattern(config.dateFormatReadable());
        this.currencyFormat = new DecimalFormat(config.currencyFormat());
        this.quantityFormat = new DecimalFormat(config.quantityFormat());

        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(config.dateFormat());
        this.id = invoiceDate.format(dateFormat) + "-" + order.orderNumber();
        this.fileName = basePath + id + FILE_TYPE;
    }

    public void generate() throws IOException, DocumentException {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(fileName));

        document.open();

        document.add(generateHeader());
        document.add(lineSeparator);
        document.add(generateDataTable());
        document.add(lineSeparator);
        document.add(generateHeading());
        document.add(Chunk.NEWLINE);
        document.add(new Paragraph(config.paragraph1() + " " + order.customer().getLastName() + ",", paragraphFont));
        document.add(Chunk.NEWLINE);
        document.add(new Paragraph(config.paragraph2(), paragraphFont));
        document.add(generateProductTable());
        document.add(new Paragraph(config.paragraph3(), paragraphFont));
        document.add(generateSignature());
        document.add(new Paragraph(company.shareholders().get(0).getFirstName() + " " +
                company.shareholders().get(0).getLastName() + " (" + company.address().location() +
                ", " + invoiceDate.format(dateFormatReadable) + ")", paragraphFont));
        document.add(generateFooter());

        document.close();
    }

    private Paragraph generateHeading() {
        Paragraph heading = new Paragraph(config.heading() + " " + id, headingFont);
        heading.setSpacingBefore(config.defaultSpacing());
        return heading;
    }

    private Table generateHeader() throws DocumentException, IOException {
        Paragraph title = new Paragraph(config.header(), headerFont);
        PdfPCell headingCell = new PdfPCell(title);
        headingCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        headingCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        headingCell.setBorder(0);

        Image lettering = Image.getInstance(config.letteringPath());
        lettering.scalePercent(config.letteringScalePercent());
        PdfPCell letteringCell = new PdfPCell(lettering);
        letteringCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        letteringCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        letteringCell.setBorder(0);

        Image logo = Image.getInstance(config.logoPath());
        logo.scalePercent(config.logoScalePercent());
        PdfPCell logoCell = new PdfPCell(logo);
        logoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        logoCell.setBorder(0);

        Table innerHeader = new Table(2);
        innerHeader.setWidths(config.headerTableProportions());
        innerHeader.addCell(letteringCell);
        innerHeader.addCell(logoCell);
        PdfPCell innerHeaderCell = new PdfPCell(innerHeader);
        innerHeaderCell.setBorder(0);

        Table header = new Table(2);
        header.setWidthPercentage(config.contentWidth());

        header.addCell(headingCell);
        header.addCell(innerHeaderCell);

        return header;
    }

    private Table generateDataTable() throws DocumentException {
        Table table = new Table(2);
        table.setSpacingBefore(config.defaultSpacing());
        table.setWidthPercentage(config.contentWidth());
        table.setWidths(config.dataTableProportions());

        List<PdfPCell> customerInformation = initCustomerInformation();
        List<PdfPCell> companyInformation = initCompanyInformation(paragraphFont);
        PdfPCell emptyCell = initParagraphCell(" ", paragraphFont);

        List<PdfPCell> cells = Arrays.asList(
                customerInformation.get(0), companyInformation.get(0),
                customerInformation.get(1), companyInformation.get(1),
                customerInformation.get(2), companyInformation.get(2),
                customerInformation.get(3), companyInformation.get(3),
                emptyCell, companyInformation.get(4),
                emptyCell, emptyCell,
                emptyCell, generateInnerDataTable());

        table.addAll(cells);
        table.setBorders(0);

        return table;
    }

    private PdfPCell generateInnerDataTable() throws DocumentException {
        Table table = new Table(2);
        table.setWidths(config.innerDataTableProportions());

        PdfPCell companyPhoneALabelCell = initParagraphCell(config.phoneLabel() + " (" + company.shareholders().get(0).getLastName() + "):", paragraphFont);
        PdfPCell companyPhoneBLabelCell = initParagraphCell(config.phoneLabel() + " (" + company.shareholders().get(1).getLastName() + "):", paragraphFont);
        PdfPCell companyMailLabelCell = initParagraphCell(config.emailLabel() + ":", paragraphFont);

        PdfPCell companyPhoneACell = initParagraphCell(company.shareholders().get(0).getContact().phoneNumber(), paragraphFont);
        PdfPCell companyPhoneBCell = initParagraphCell(company.shareholders().get(1).getContact().phoneNumber(), paragraphFont);
        PdfPCell companyMailCell = initParagraphCell(company.shareholders().get(1).getContact().email(), paragraphFont);

        PdfPCell invoiceNumberLabelCell = initParagraphCell(config.invoiceNumberLabel() + ":", paragraphFont);
        PdfPCell customerNumberLabelCell = initParagraphCell(config.customerIdLabel() + ":", paragraphFont);
        PdfPCell invoiceDateLabelCell = initParagraphCell(config.invoiceDateLabel() + ":", paragraphFont);
        PdfPCell performanceDateLabelCell = initParagraphCell(config.performanceDateLabel() + ":", paragraphFont);

        PdfPCell invoiceNumberCell = initParagraphCell(id, paragraphFont);
        PdfPCell customerNumberCell = initParagraphCell(String.valueOf(order.customer().getCustomerId()), paragraphFont);
        PdfPCell invoiceDateCell = initParagraphCell(invoiceDate.format(dateFormatReadable), paragraphFont);
        PdfPCell performanceDateCell = initParagraphCell(performanceDate.format(dateFormatReadable), paragraphFont);

        List<PdfPCell> cells = Arrays.asList(
                companyPhoneALabelCell, companyPhoneACell,
                companyPhoneBLabelCell, companyPhoneBCell,
                companyMailLabelCell, companyMailCell,
                invoiceNumberLabelCell, invoiceNumberCell,
                customerNumberLabelCell, customerNumberCell,
                invoiceDateLabelCell, invoiceDateCell,
                performanceDateLabelCell, performanceDateCell);

        table.addAll(cells);
        table.setBorders(0);

        return new PdfPCell(table);
    }

    private Table generateProductTable() {
        Table table = new Table(4);
        table.setSpacingBefore(config.defaultSpacing());
        table.setSpacingAfter(config.defaultSpacing() - 10);
        table.setWidthPercentage(config.contentWidth());

        PdfPCell declarationHeaderCell = initParagraphCell(config.productDeclarationLabel(), paragraphFont);
        PdfPCell amountHeaderCell = initParagraphCell(config.productQuantityLabel(), paragraphFont);
        PdfPCell singlePriceHeaderCell = initParagraphCell(config.productSinglePriceLabel(), paragraphFont);
        PdfPCell sumPriceHeaderCell = initParagraphCell(config.productSumPriceLabel(), paragraphFont);
        PdfPCell emptyCell = initParagraphCell(" ", paragraphFont);

        List<PdfPCell> cells = new ArrayList<>(Arrays.asList(declarationHeaderCell, amountHeaderCell, singlePriceHeaderCell, sumPriceHeaderCell));

        double sum = 0;
        for (Product product : products) {
            PdfPCell productNameCell = initParagraphCell(product.name(), paragraphFont);
            PdfPCell productAmountCell = initParagraphCell(quantityFormat.format(product.amount()), paragraphFont);
            PdfPCell productPriceCell = initParagraphCell(currencyFormat.format(product.price()), paragraphFont);
            PdfPCell productSumCell = initParagraphCell(currencyFormat.format(product.price() * product.amount()), paragraphFont);
            cells.addAll(Arrays.asList(productNameCell, productAmountCell, productPriceCell, productSumCell));
            sum += product.price() * product.amount();
        }

        cells.addAll(Arrays.asList(emptyCell, emptyCell,
                initParagraphCell(config.productsSumPriceLabel() + ":", paragraphFont),
                initParagraphCell(currencyFormat.format(sum), paragraphFont)));

        table.addAll(cells);
        table.setHeaderRows(1);

        return table;
    }

    private Image generateSignature() throws BadElementException, IOException {
        Image image = Image.getInstance(config.signaturePath());
        image.scalePercent(config.signatureScalePercent());
        return image;
    }

    private Table generateFooter() {
        Table table = new Table(2);
        table.setWidthPercentage(config.contentWidth());
        table.setSpacingBefore(config.defaultSpacing());

        List<PdfPCell> companyInformation = initCompanyInformation(footerFont);
        List<PdfPCell> companyFinancialInformation = initCompanyFinancialInformation();

        for (PdfPCell pdfPCell : companyFinancialInformation) {
            pdfPCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        }

        table.addAll(Arrays.asList(
                companyInformation.get(0), companyFinancialInformation.get(0),
                companyInformation.get(1), companyFinancialInformation.get(1),
                companyInformation.get(2), companyFinancialInformation.get(2),
                companyInformation.get(3), companyFinancialInformation.get(3)
        ));

        table.setBorders(0);

        return table;
    }

    private PdfPCell initParagraphCell(String text, Font font) {
        Paragraph customerNameParagraph = new Paragraph(text, font);
        return new PdfPCell(customerNameParagraph);
    }

    private List<PdfPCell> initCompanyInformation(Font font) {
        PdfPCell companyNameCell = initParagraphCell(company.name(), font);
        PdfPCell companyDeclarationCell = initParagraphCell(company.declaration(), font);
        PdfPCell companyStreetCell = initParagraphCell(company.address().street() + " " + company.address().streetNumber(), font);
        PdfPCell companyLocationCell = initParagraphCell(company.address().zipCode() + " " + company.address().location(), font);
        PdfPCell companyCountryCell = initParagraphCell(company.address().country(), font);
        return Arrays.asList(companyNameCell, companyDeclarationCell, companyStreetCell, companyLocationCell, companyCountryCell);
    }

    private List<PdfPCell> initCustomerInformation() {
        PdfPCell customerNameCell = initParagraphCell(order.customer().getFirstName() + " " + order.customer().getLastName(), paragraphFont);
        PdfPCell customerStreetCell = initParagraphCell(order.customer().getAddress().street() + " " + order.customer().getAddress().streetNumber(), paragraphFont);
        PdfPCell customerLocationCell = initParagraphCell(order.customer().getAddress().zipCode() + " " + order.customer().getAddress().location(), paragraphFont);
        PdfPCell customerCountryCell = initParagraphCell(order.customer().getAddress().country(), paragraphFont);
        return Arrays.asList(customerNameCell, customerStreetCell, customerLocationCell, customerCountryCell);
    }

    private List<PdfPCell> initCompanyFinancialInformation() {
        PdfPCell companyIbanCell = initParagraphCell(config.ibanLabel() + ": " + company.bankAccount().iban(), footerFont);
        PdfPCell companyBicCell = initParagraphCell(config.bicLabel() + ": " + company.bankAccount().bic(), footerFont);
        PdfPCell companyBankNameCell = initParagraphCell(config.bankLabel() + ": " + company.bankAccount().bankName(), footerFont);
        PdfPCell companyTaxNumberCell = initParagraphCell(config.taxNumberLabel() + ": " + company.taxNumber(), footerFont);
        return Arrays.asList(companyIbanCell, companyBicCell, companyBankNameCell, companyTaxNumberCell);
    }

    public String getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }
}
