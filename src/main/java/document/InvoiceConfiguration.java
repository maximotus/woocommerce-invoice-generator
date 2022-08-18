package document;

public record InvoiceConfiguration(String outputPath, String logoPath,
                                   String letteringPath, String signaturePath,
                                   int logoScalePercent, int letteringScalePercent,
                                   int signatureScalePercent, String dateFormatReadable, String dateFormat,
                                   String currencyFormat, String quantityFormat, int headerFontSize,
                                   int headingFontSize, int paragraphFontSize, int footerFontSize, int contentWidth,
                                   int defaultSpacing, int lineSeparatorWidth, int lineSeparatorOffset,
                                   int[] headerTableProportions, int[] dataTableProportions,
                                   int[] innerDataTableProportions, String header, String heading,
                                   String phoneLabel, String emailLabel,
                                   String invoiceNumberLabel, String customerIdLabel,
                                   String invoiceDateLabel, String performanceDateLabel,
                                   String productDeclarationLabel, String productQuantityLabel,
                                   String productSinglePriceLabel, String productSumPriceLabel,
                                   String productsSumPriceLabel, String ibanLabel,
                                   String bicLabel, String bankLabel,
                                   String taxNumberLabel, String paragraph1,
                                   String paragraph2, String paragraph3) {
}
