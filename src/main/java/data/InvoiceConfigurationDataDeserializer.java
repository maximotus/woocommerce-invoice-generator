package data;

import document.InvoiceConfiguration;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class InvoiceConfigurationDataDeserializer {
    private static final String OUTPUT_PATH_KEY = "outputPath";
    private static final String LOGO_PATH_KEY = "logoPath";
    private static final String LETTERING_PATH_KEY = "letteringPath";
    private static final String SIGNATURE_PATH_KEY = "signaturePath";
    private static final String LOGO_SCALE_PERCENT_KEY = "logoScalePercent";
    private static final String LETTERING_SCALE_PERCENT_KEY = "letteringScalePercent";
    private static final String SIGNATURE_SCALE_PERCENT_KEY = "signatureScalePercent";
    private static final String DATE_FORMAT_READABLE_KEY = "dateFormatReadable";
    private static final String DATE_FORMAT_KEY = "dateFormat";
    private static final String CURRENCY_FORMAT_KEY = "currencyFormat";
    private static final String QUANTITY_FORMAT_KEY = "quantityFormat";
    private static final String HEADER_FONT_SIZE_KEY = "headerFontSize";
    private static final String HEADING_FONT_SIZE_KEY = "headingFontSize";
    private static final String PARAGRAPH_FONT_SIZE_KEY = "paragraphFontSize";
    private static final String FOOTER_FONT_SIZE_KEY = "footerFontSize";
    private static final String CONTENT_WIDTH_KEY = "contentWidth";
    private static final String DEFAULT_SPACING_KEY = "defaultSpacing";
    private static final String LINE_SEPARATOR_WIDTH_KEY = "lineSeparatorWidth";
    private static final String LINE_SEPARATOR_OFFSET_KEY = "lineSeparatorOffset";
    private static final String HEADER_TABLE_PROPORTIONS_KEY = "headerTableProportions";
    private static final String DATA_TABLE_PROPORTIONS_KEY = "dataTableProportions";
    private static final String INNER_TABLE_PROPORTIONS_KEY = "innerDataTableProportions";
    private static final String HEADER_KEY = "header";
    private static final String HEADING_KEY = "heading";
    private static final String PHONE_LABEL_KEY = "phoneLabel";
    private static final String EMAIL_LABEL_KEY = "emailLabel";
    private static final String INVOICE_NUMBER_LABEL_KEY = "invoiceNumberLabel";
    private static final String CUSTOMER_ID_LABEL_KEY = "customerIdLabel";
    private static final String INVOICE_DATE_LABEL_KEY = "invoiceDateLabel";
    private static final String PERFORMANCE_DATE_LABEL_KEY = "performanceDateLabel";
    private static final String PRODUCT_DECLARATION_LABEL_KEY = "productDeclarationLabel";
    private static final String PRODUCT_QUANTITY_LABEL_KEY = "productQuantityLabel";
    private static final String PRODUCT_SINGLE_PRICE_LABEL_KEY = "productSinglePriceLabel";
    private static final String PRODUCT_SUM_PRICE_LABEL_KEY = "productSumPriceLabel";
    private static final String PRODUCTS_SUM_PRICE_LABEL_KEY = "productsSumPriceLabel";
    private static final String IBAN_LABEL_KEY = "ibanLabel";
    private static final String BIC_LABEL_KEY = "bicLabel";
    private static final String BANK_LABEL_KEY = "bankLabel";
    private static final String TAX_NUMBER_LABEL_KEY = "taxNumberLabel";
    private static final String PARAGRAPH_1_KEY = "paragraph1";
    private static final String PARAGRAPH_2_KEY = "paragraph2";
    private static final String PARAGRAPH_3_KEY = "paragraph3";

    private String filePath;
    private JSONObject rawData;
    private InvoiceConfiguration data;

    public InvoiceConfigurationDataDeserializer(String filePath) throws IOException {
        this.filePath = filePath;
        this.rawData = new JSONObject(Files.readString(Path.of(filePath)));
        this.data = null;
    }

    public void deserialize() {
        data = new InvoiceConfiguration(
                rawData.getString(OUTPUT_PATH_KEY),
                rawData.getString(LOGO_PATH_KEY),
                rawData.getString(LETTERING_PATH_KEY),
                rawData.getString(SIGNATURE_PATH_KEY),
                rawData.getInt(LOGO_SCALE_PERCENT_KEY),
                rawData.getInt(LETTERING_SCALE_PERCENT_KEY),
                rawData.getInt(SIGNATURE_SCALE_PERCENT_KEY),
                rawData.getString(DATE_FORMAT_READABLE_KEY),
                rawData.getString(DATE_FORMAT_KEY),
                rawData.getString(CURRENCY_FORMAT_KEY),
                rawData.getString(QUANTITY_FORMAT_KEY),
                rawData.getInt(HEADER_FONT_SIZE_KEY),
                rawData.getInt(HEADING_FONT_SIZE_KEY),
                rawData.getInt(PARAGRAPH_FONT_SIZE_KEY),
                rawData.getInt(FOOTER_FONT_SIZE_KEY),
                rawData.getInt(CONTENT_WIDTH_KEY),
                rawData.getInt(DEFAULT_SPACING_KEY),
                rawData.getInt(LINE_SEPARATOR_WIDTH_KEY),
                rawData.getInt(LINE_SEPARATOR_OFFSET_KEY),
                new int[] {
                        rawData.getJSONArray(HEADER_TABLE_PROPORTIONS_KEY).getInt(0),
                        rawData.getJSONArray(HEADER_TABLE_PROPORTIONS_KEY).getInt(1)
                },
                new int[] {
                        rawData.getJSONArray(DATA_TABLE_PROPORTIONS_KEY).getInt(0),
                        rawData.getJSONArray(DATA_TABLE_PROPORTIONS_KEY).getInt(1)
                },
                new int[] {
                        rawData.getJSONArray(INNER_TABLE_PROPORTIONS_KEY).getInt(0),
                        rawData.getJSONArray(INNER_TABLE_PROPORTIONS_KEY).getInt(1)
                },
                rawData.getString(HEADER_KEY),
                rawData.getString(HEADING_KEY),
                rawData.getString(PHONE_LABEL_KEY),
                rawData.getString(EMAIL_LABEL_KEY),
                rawData.getString(INVOICE_NUMBER_LABEL_KEY),
                rawData.getString(CUSTOMER_ID_LABEL_KEY),
                rawData.getString(INVOICE_DATE_LABEL_KEY),
                rawData.getString(PERFORMANCE_DATE_LABEL_KEY),
                rawData.getString(PRODUCT_DECLARATION_LABEL_KEY),
                rawData.getString(PRODUCT_QUANTITY_LABEL_KEY),
                rawData.getString(PRODUCT_SINGLE_PRICE_LABEL_KEY),
                rawData.getString(PRODUCT_SUM_PRICE_LABEL_KEY),
                rawData.getString(PRODUCTS_SUM_PRICE_LABEL_KEY),
                rawData.getString(IBAN_LABEL_KEY),
                rawData.getString(BIC_LABEL_KEY),
                rawData.getString(BANK_LABEL_KEY),
                rawData.getString(TAX_NUMBER_LABEL_KEY),
                rawData.getString(PARAGRAPH_1_KEY),
                rawData.getString(PARAGRAPH_2_KEY),
                rawData.getString(PARAGRAPH_3_KEY)
        );
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

    public InvoiceConfiguration getData() throws NotDeserializedYetException {
        if (data == null) throw new NotDeserializedYetException("Call the method deserialize() first.");
        return data;
    }

    public void setData(InvoiceConfiguration data) {
        this.data = data;
    }
}
