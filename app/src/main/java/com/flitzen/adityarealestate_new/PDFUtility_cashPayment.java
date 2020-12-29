package com.flitzen.adityarealestate_new;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.util.Log;

import androidx.annotation.NonNull;


import com.flitzen.adityarealestate_new.Classes.Helper;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.Barcode128;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PDFUtility_cashPayment {
    private static final String TAG = PDFUtility_cashPayment.class.getSimpleName();
    private static Font FONT_TITLE = new Font(Font.FontFamily.TIMES_ROMAN, 30, Font.BOLD);
    private static Font FONT_SUBTITLE = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.NORMAL);
    private static Font FONT_TOTAL = new Font(Font.FontFamily.TIMES_ROMAN, 15, Font.NORMAL);

    private static Font FONT_CELL = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL);
    private static Font FONT_COLUMN = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD);

    public interface OnDocumentClose {
        void onPDFDocumentClose(File file);
    }

    public static void createPdf(@NonNull Context mContext, OnDocumentClose mCallback, List<String[]> items, @NonNull String filePath, boolean isPortrait, String site_size, String site_address
            , String site_name,int finalAmount) throws Exception {

        if (filePath.equals("")) {
            throw new NullPointerException("PDF File Name can't be null or blank. PDF File can't be created");
        }

        File file = new File(filePath);

        if (file.exists()) {
            file.delete();
            Thread.sleep(50);
        }

        Document document = new Document();
        //   document.setMargins(24f,24f,0f,32f);
        document.setMargins(0f, 0f, 0f, 120f);
        document.setMarginMirroring(false);
        document.setPageSize(isPortrait ? PageSize.A4 : PageSize.A4.rotate());

        PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(filePath));
        pdfWriter.setFullCompression();
        pdfWriter.setPageEvent(new PageNumeration(mContext));

        document.open();

        // setMetaData(document);

        addHeader(mContext, document);
        addEmptyLine(document, 2);
        addDetails(mContext, document, pdfWriter, site_size, site_address, site_name);
        addEmptyLine(document, 2);
        document.add(createDataTable(items));
        addEmptyLine(document, 2);
        addFinalTotle(mContext,document,pdfWriter,finalAmount);
        addEmptyLine(document, 2);

        // document.add(createSignBox());

        document.close();

        try {
            pdfWriter.close();
        } catch (Exception ex) {
            Log.e(TAG, "Error While Closing pdfWriter : " + ex.toString());
        }

        if (mCallback != null) {
            mCallback.onPDFDocumentClose(file);
        }
    }

    private static void addFinalTotle(Context mContext, Document document, PdfWriter pdfWriter, int finalAmount) {

        try {
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(95);
            table.setWidths(new float[]{1.4f,0.8f});
            table.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
            table.getDefaultCell().setVerticalAlignment(Element.ALIGN_RIGHT);
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);


            PdfPCell cell = null;

            {
                /*LEFT TOP LOGO*/

                PdfPTable logoTable = new PdfPTable(1);
                logoTable.setWidthPercentage(100);
                logoTable.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
                logoTable.setHorizontalAlignment(Element.ALIGN_CENTER);
                logoTable.getDefaultCell().setVerticalAlignment(Element.ALIGN_CENTER);

                PdfPCell logoCell = new PdfPCell(new Phrase("  " , FONT_CELL));
                logoCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                logoCell.setBorder(PdfPCell.NO_BORDER);
                logoTable.addCell(logoCell);

                PdfTemplate template = pdfWriter.getDirectContent().createTemplate(120, 1);
                template.setColorFill(BaseColor.WHITE);
                template.rectangle(0, 0, 120, 1);
                template.fill();
                pdfWriter.releaseTemplate(template);
                logoTable.addCell(Image.getInstance(template));


                SimpleDateFormat input = new SimpleDateFormat("yyyy MMM dd");
                SimpleDateFormat output = new SimpleDateFormat("dd MMMM yyyy");

                PdfPCell logoCell2 = new PdfPCell(new Phrase("  " , FONT_CELL));
                logoCell2.setHorizontalAlignment(Element.ALIGN_LEFT);
                logoCell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
                logoCell2.setBorder(PdfPCell.NO_BORDER);

                logoTable.addCell(logoCell2);


                PdfTemplate template2 = pdfWriter.getDirectContent().createTemplate(120, 1);
                template2.setColorFill(BaseColor.WHITE);
                template2.rectangle(0, 0, 120, 1);
                template2.fill();
                pdfWriter.releaseTemplate(template2);
                logoTable.addCell(Image.getInstance(template2));

                cell = new PdfPCell(logoTable);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setUseAscender(true);
                cell.setBorder(PdfPCell.NO_BORDER);
                cell.setPadding(2f);
                table.addCell(cell);

            }

            {
                PdfPTable logoTable = new PdfPTable(1);
                logoTable.setWidthPercentage(100);
                logoTable.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
                logoTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
                logoTable.getDefaultCell().setVerticalAlignment(Element.ALIGN_RIGHT);

                PdfTemplate template = pdfWriter.getDirectContent().createTemplate(35, 0.5f);
                template.setColorFill(BaseColor.BLACK);
                template.rectangle(0, 0, 35, 0.5f);
                template.fill();
                pdfWriter.releaseTemplate(template);
                logoTable.addCell(Image.getInstance(template));

                String total=mContext.getResources().getString(R.string.rupee_for_pdf)+Helper.getFormatPrice(finalAmount);
                PdfPCell logoCell1 = new PdfPCell(new Phrase("Final Total :               "+total, FONT_TOTAL));
                logoCell1.setHorizontalAlignment(Element.ALIGN_LEFT);
                logoCell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
                logoCell1.setBorder(PdfPCell.NO_BORDER);

                logoTable.addCell(logoCell1);

               /* PdfPCell logoCell = new PdfPCell(new Phrase(String.valueOf(mContext.getResources().getString(R.string.rupee)+Helper.getFormatPrice(finalAmount)), FONT_TOTAL));
                logoCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                logoCell.setBorder(PdfPCell.NO_BORDER);

                logoTable.addCell(logoCell);*/

                PdfTemplate template2 = pdfWriter.getDirectContent().createTemplate(35, 0.5f);
                template2.setColorFill(BaseColor.BLACK);
                template2.rectangle(0, 0, 35, 0.5f);
                template2.fill();
                pdfWriter.releaseTemplate(template2);
                logoTable.addCell(Image.getInstance(template2));

                cell = new PdfPCell(logoTable);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setUseAscender(true);
                cell.setBorder(PdfPCell.NO_BORDER);
                cell.setPadding(2f);
                table.addCell(cell);
            }
            document.add(table);
        }
        catch (Exception e){
            System.out.println("=======Exception   "+e.getMessage());
        }
    }

    private static void addEmptyLine(Document document, int number) throws DocumentException {
        for (int i = 0; i < number; i++) {
            document.add(new Paragraph(" "));
        }
    }

    private static void setMetaData(Document document) {
        document.addCreationDate();
        //document.add(new Meta("",""));
        document.addAuthor("RAVEESH G S");
        document.addCreator("RAVEESH G S");
        document.addHeader("DEVELOPER", "RAVEESH G S");
    }

    private static void addHeader(Context mContext, Document document) throws Exception {
        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{100});
        table.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
        table.getDefaultCell().setVerticalAlignment(Element.ALIGN_CENTER);
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

        PdfPCell cell;
        {
            // LEFT TOP LOGO
            cell = new PdfPCell();
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(PdfPCell.NO_BORDER);
            cell.setPadding(8f);
            cell.setUseAscender(true);

            Paragraph temp = new Paragraph("Site Payment", FONT_TITLE);
            temp.setAlignment(Element.ALIGN_CENTER);
            cell.addElement(temp);

            String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
            temp = new Paragraph("as on " + currentDate, FONT_SUBTITLE);
            temp.setAlignment(Element.ALIGN_CENTER);
            cell.addElement(temp);

            table.addCell(cell);
        }

        //Paragraph paragraph=new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN, 2.0f, Font.NORMAL, BaseColor.BLACK));
        //paragraph.add(table);
        //document.add(paragraph);
        document.add(table);
    }

    private static void addDetails(Context mContext, Document document, PdfWriter pdfWriter, String site_size, String site_address, String site_name) throws DocumentException, IOException {

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(95);
        table.setWidths(new float[]{1, 1});
        table.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
        table.getDefaultCell().setVerticalAlignment(Element.ALIGN_CENTER);
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

        PdfPCell cell = null;
        {
            /*LEFT TOP LOGO*/

            PdfPTable logoTable = new PdfPTable(1);
            logoTable.setWidthPercentage(100);
            logoTable.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
            logoTable.setHorizontalAlignment(Element.ALIGN_CENTER);
            logoTable.getDefaultCell().setVerticalAlignment(Element.ALIGN_CENTER);

            PdfPCell logoCell = new PdfPCell(new Phrase("Site Name:  " + site_name, FONT_CELL));
            logoCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            logoCell.setBorder(PdfPCell.NO_BORDER);
            logoTable.addCell(logoCell);

            PdfTemplate template = pdfWriter.getDirectContent().createTemplate(120, 1);
            template.setColorFill(BaseColor.BLACK);
            template.rectangle(0, 0, 120, 1);
            template.fill();
            pdfWriter.releaseTemplate(template);
            logoTable.addCell(Image.getInstance(template));


            SimpleDateFormat input = new SimpleDateFormat("yyyy MMM dd");
            SimpleDateFormat output = new SimpleDateFormat("dd MMMM yyyy");

            PdfPCell logoCell2 = new PdfPCell(new Phrase("Size : " + site_size, FONT_CELL));
            logoCell2.setHorizontalAlignment(Element.ALIGN_LEFT);
            logoCell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
            logoCell2.setBorder(PdfPCell.NO_BORDER);

            logoTable.addCell(logoCell2);


            PdfTemplate template2 = pdfWriter.getDirectContent().createTemplate(120, 1);
            template2.setColorFill(BaseColor.BLACK);
            template2.rectangle(0, 0, 120, 1);
            template2.fill();
            pdfWriter.releaseTemplate(template2);
            logoTable.addCell(Image.getInstance(template2));

            cell = new PdfPCell(logoTable);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setUseAscender(true);
            cell.setBorder(PdfPCell.NO_BORDER);
            cell.setPadding(2f);
            table.addCell(cell);

        }

        /* RIGHT TOP LOGO*/
        {
            PdfPTable logoTable = new PdfPTable(1);
            logoTable.setWidthPercentage(100);
            logoTable.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
            logoTable.setHorizontalAlignment(Element.ALIGN_CENTER);
            logoTable.getDefaultCell().setVerticalAlignment(Element.ALIGN_CENTER);

            PdfPCell logoCell = new PdfPCell(new Phrase("Property Address : " +site_address, FONT_CELL));
            logoCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            logoCell.setBorder(PdfPCell.NO_BORDER);

            logoTable.addCell(logoCell);

            PdfTemplate template = pdfWriter.getDirectContent().createTemplate(120, 1);
            template.setColorFill(BaseColor.BLACK);
            template.rectangle(0, 0, 120, 1);
            template.fill();
            pdfWriter.releaseTemplate(template);
            logoTable.addCell(Image.getInstance(template));

            PdfPCell logoCell1 = new PdfPCell(new Phrase("    ", FONT_CELL));
            logoCell1.setHorizontalAlignment(Element.ALIGN_LEFT);
            logoCell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
            logoCell1.setBorder(PdfPCell.NO_BORDER);
            logoTable.addCell(logoCell1);

            PdfTemplate template1 = pdfWriter.getDirectContent().createTemplate(120, 1);
            template1.setColorFill(BaseColor.WHITE);
            template1.rectangle(0, 0, 120, 1);
            template1.fill();
            pdfWriter.releaseTemplate(template1);
            logoTable.addCell(Image.getInstance(template1));

            cell = new PdfPCell(logoTable);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setUseAscender(true);
            cell.setBorder(PdfPCell.NO_BORDER);
            cell.setPadding(2f);
            table.addCell(cell);
        }

        //Paragraph paragraph=new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN, 2.0f, Font.NORMAL, BaseColor.BLACK));
        //paragraph.add(table);
        //document.add(paragraph);
        document.add(table);
    }

    private static PdfPTable createDataTable(List<String[]> dataTable) throws DocumentException {
        PdfPTable table1 = new PdfPTable(3);
        table1.setWidthPercentage(95);
        table1.setWidths(new float[]{1f, 1f, 1f});
        table1.setHeaderRows(1);
        table1.getDefaultCell().setVerticalAlignment(Element.ALIGN_CENTER);
        table1.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

        PdfPCell cell;
        {
            cell = new PdfPCell(new Phrase("Date", FONT_COLUMN));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPadding(4f);
            table1.addCell(cell);

            cell = new PdfPCell(new Phrase("Remarks", FONT_COLUMN));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPadding(4f);
            table1.addCell(cell);

            cell = new PdfPCell(new Phrase("Amount", FONT_COLUMN));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_RIGHT);
            cell.setPadding(4f);
            table1.addCell(cell);
        }

        float top_bottom_Padding = 8f;
        float left_right_Padding = 4f;
        boolean alternate = false;

        BaseColor lt_gray = new BaseColor(221, 221, 221); //#DDDDDD
        BaseColor cell_color;

        int size = dataTable.size();

        for (int i = 0; i < size; i++) {
            cell_color = alternate ? lt_gray : BaseColor.WHITE;
            String[] temp = dataTable.get(i);

            cell = new PdfPCell(new Phrase(temp[0], FONT_CELL));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPaddingLeft(left_right_Padding);
            cell.setPaddingRight(left_right_Padding);
            cell.setPaddingTop(top_bottom_Padding);
            cell.setPaddingBottom(top_bottom_Padding);
           // cell.setBackgroundColor(cell_color);
            table1.addCell(cell);

            cell = new PdfPCell(new Phrase(temp[1], FONT_CELL));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_LEFT);
            cell.setPaddingLeft(left_right_Padding);
            cell.setPaddingRight(left_right_Padding);
            cell.setPaddingTop(top_bottom_Padding);
            cell.setPaddingBottom(top_bottom_Padding);
           // cell.setBackgroundColor(cell_color);
            table1.addCell(cell);

            cell = new PdfPCell(new Phrase(temp[2], FONT_CELL));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_RIGHT);
            cell.setPaddingLeft(left_right_Padding);
            cell.setPaddingRight(left_right_Padding);
            cell.setPaddingTop(top_bottom_Padding);
            cell.setPaddingBottom(top_bottom_Padding);
           // cell.setBackgroundColor(cell_color);
            table1.addCell(cell);

            alternate = !alternate;
        }

        return table1;
    }

    private static PdfPTable createSignBox() throws DocumentException {
        PdfPTable outerTable = new PdfPTable(1);
        outerTable.setWidthPercentage(100);
        outerTable.getDefaultCell().setBorder(PdfPCell.NO_BORDER);

        PdfPTable innerTable = new PdfPTable(2);
        {
            innerTable.setWidthPercentage(100);
            innerTable.setWidths(new float[]{1, 1});
            innerTable.getDefaultCell().setBorder(PdfPCell.NO_BORDER);

            //ROW-1 : EMPTY SPACE
            PdfPCell cell = new PdfPCell();
            cell.setBorder(PdfPCell.NO_BORDER);
            cell.setFixedHeight(60);
            innerTable.addCell(cell);

            //ROW-2 : EMPTY SPACE
            cell = new PdfPCell();
            cell.setBorder(PdfPCell.NO_BORDER);
            cell.setFixedHeight(60);
            innerTable.addCell(cell);

            //ROW-3 : Content Left Aligned
            cell = new PdfPCell();
            Paragraph temp = new Paragraph(new Phrase("Signature of Supervisor", FONT_SUBTITLE));
            cell.addElement(temp);

            temp = new Paragraph(new Phrase("( RAVEESH G S )", FONT_SUBTITLE));
            temp.setPaddingTop(4f);
            temp.setAlignment(Element.ALIGN_LEFT);
            cell.addElement(temp);

            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorder(PdfPCell.NO_BORDER);
            cell.setPadding(4f);
            innerTable.addCell(cell);

            //ROW-4 : Content Right Aligned
            cell = new PdfPCell(new Phrase("Signature of Staff ", FONT_SUBTITLE));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setBorder(PdfPCell.NO_BORDER);
            cell.setPadding(4f);
            innerTable.addCell(cell);
        }

        PdfPCell signRow = new PdfPCell(innerTable);
        signRow.setHorizontalAlignment(Element.ALIGN_LEFT);
        signRow.setBorder(PdfPCell.NO_BORDER);
        signRow.setPadding(4f);

        outerTable.addCell(signRow);

        return outerTable;
    }

    private static Image getImage(byte[] imageByte, boolean isTintingRequired) throws Exception {
        Paint paint = new Paint();
        if (isTintingRequired) {
            paint.setColorFilter(new PorterDuffColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN));
        }
        Bitmap input = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
        Bitmap output = Bitmap.createBitmap(input.getWidth(), input.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        canvas.drawBitmap(input, 0, 0, paint);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        output.compress(Bitmap.CompressFormat.PNG, 100, stream);
        Image image = Image.getInstance(stream.toByteArray());
        image.setWidthPercentage(80);
        return image;
    }

    private static Image getBarcodeImage(PdfWriter pdfWriter, String barcodeText) {
        Barcode128 barcode = new Barcode128();
        //barcode.setBaseline(-1); //BARCODE TEXT ABOVE
        barcode.setFont(null);
        barcode.setCode(barcodeText);
        barcode.setCodeType(Barcode128.CODE128);
        barcode.setTextAlignment(Element.ALIGN_BASELINE);
        return barcode.createImageWithBarcode(pdfWriter.getDirectContent(), BaseColor.BLACK, null);
    }
}
