package document;

import com.itextpdf.text.pdf.*;

import java.util.List;

public class Table extends PdfPTable {

    public Table(int numColumns) {
        super(numColumns);
    }

    public void addAll(List<PdfPCell> pdfPCells) {
        for (PdfPCell pdfPCell : pdfPCells) {
            addCell(pdfPCell);
        }
    }

    public void setBorders(int width) {
        for (PdfPRow pdfPRow : this.getRows()) {
            for (PdfPCell pdfPCell : pdfPRow.getCells()) {
                pdfPCell.setBorder(width);
            }
        }
    }
}
