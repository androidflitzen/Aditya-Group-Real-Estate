package com.flitzen.adityarealestate_new;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.itextpdf.text.Anchor;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;

public class PageNumeration extends PdfPageEventHelper
{
    Context mContext;
    private static String TAG        = PageNumeration.class.getSimpleName();
    private static Font FONT_FOOTER  = new Font(Font.FontFamily.TIMES_ROMAN,  8, Font.NORMAL, BaseColor.DARK_GRAY);

    PageNumeration(Context mContext)
    {
        this.mContext=mContext;
    }

    @Override
    public void onStartPage(PdfWriter writer, Document document) {
        //super.onStartPage(writer, document);
        try {
            PdfPTable table = new PdfPTable(1);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{100});
            table.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
            table.getDefaultCell().setVerticalAlignment(Element.ALIGN_CENTER);
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

            Drawable d= ContextCompat.getDrawable(mContext, R.drawable.header);
            Bitmap bmp = Bitmap.createBitmap(d.getIntrinsicWidth(), d.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

            Canvas canvas = new Canvas(bmp);
            d.setBounds(0,0,d.getIntrinsicWidth(),d.getIntrinsicHeight());
            d.draw(canvas);

            //Bitmap bmp=((BitmapDrawable) d).getBitmap();
            ByteArrayOutputStream stream=new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG,100,stream);
            Image image = Image.getInstance(stream.toByteArray());
            image.scaleToFit(PageSize.A4);

            PdfPCell cellImage;
            cellImage = new PdfPCell(image);
            cellImage.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellImage.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cellImage.setUseAscender(true);
            cellImage.setBorder(PdfPCell.NO_BORDER);

           // document.setPageSize(PageSize.A4);
            //document.newPage();
            //document.add(image);

            table.addCell(cellImage);

            document.add(table);

        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static  void addEmptyLine(Document document, int number) throws DocumentException
    {
        for (int i = 0; i < number; i++)
        {
            document.add(new Paragraph(" "));
        }
    }

    @Override
    public void onEndPage(PdfWriter writer, Document document)
    {
        try
        {
            PdfPCell cell;
            PdfPTable table = new PdfPTable(1);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{100});

            Drawable d= ContextCompat.getDrawable(mContext, R.drawable.qq2);
          /*  Bitmap bmp = Bitmap.createBitmap(d.getIntrinsicWidth(), d.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bmp);
            d.setBounds(0,0,d.getIntrinsicWidth(),d.getIntrinsicHeight());
            d.draw(canvas);*/
            Bitmap bmp=((BitmapDrawable) d).getBitmap();

            ByteArrayOutputStream stream=new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG,100,stream);
            Image image = Image.getInstance(stream.toByteArray());
            image.scaleToFit(PageSize.A4);

            //2nd Column
            cell = new PdfPCell(image);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(0);
            cell.setPadding(2f);
            table.addCell(cell);
            table.setTotalWidth(document.getPageSize().getWidth()-document.leftMargin()-document.rightMargin());
            table.writeSelectedRows(0,-1,document.leftMargin(),120,writer.getDirectContent());
            //table.writeSelectedRows(0,-1,document.leftMargin(),writer.getPageSize().getBottom(document.bottomMargin()),writer.getDirectContent());
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            Log.e(TAG,ex.toString());
        }
    }
}
