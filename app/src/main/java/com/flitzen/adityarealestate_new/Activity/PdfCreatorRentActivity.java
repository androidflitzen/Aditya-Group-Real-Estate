package com.flitzen.adityarealestate_new.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.flitzen.adityarealestate_new.Classes.Helper;
import com.flitzen.adityarealestate_new.Items.Item_Plot_Payment_List;
import com.flitzen.adityarealestate_new.Items.PlotDetailsForPDF;
import com.flitzen.adityarealestate_new.Items.RentDetailsForPDF;
import com.flitzen.adityarealestate_new.R;
import com.flitzen.adityarealestate_new.pdfcreator.activity.PDFCreatorActivity;
import com.flitzen.adityarealestate_new.pdfcreator.utils.PDFUtil;
import com.flitzen.adityarealestate_new.pdfcreator.views.PDFBody;
import com.flitzen.adityarealestate_new.pdfcreator.views.PDFHeaderView;
import com.flitzen.adityarealestate_new.pdfcreator.views.PDFTableView;
import com.flitzen.adityarealestate_new.pdfcreator.views.basic.PDFCustomView;
import com.flitzen.adityarealestate_new.pdfcreator.views.basic.PDFLineSeparatorView;
import com.flitzen.adityarealestate_new.pdfcreator.views.basic.PDFTextView;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class PdfCreatorRentActivity extends PDFCreatorActivity {


    RentDetailsForPDF rentDetailsForPDF;
    ArrayList<Item_Plot_Payment_List> arrayListPlotPayment = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        if(getIntent().hasExtra("RentDetails") && getIntent().hasExtra("paymentList")){
            rentDetailsForPDF= (RentDetailsForPDF) getIntent().getSerializableExtra("RentDetails");
            arrayListPlotPayment= (ArrayList<Item_Plot_Payment_List>) getIntent().getSerializableExtra("paymentList");
        }

        createPDF("test", new PDFUtil.PDFUtilListener() {
            @Override
            public void pdfGenerationSuccess(File savedPDFFile) {
                Toast.makeText(PdfCreatorRentActivity.this, "PDF Created", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void pdfGenerationFailure(Exception exception) {
                Toast.makeText(PdfCreatorRentActivity.this, "PDF NOT Created", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected PDFHeaderView getHeaderView(int pageIndex) {
        /*PDFHeaderView headerView = new PDFHeaderView(getApplicationContext());
        LayoutInflater inflater2 = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view2 = inflater2.inflate(R.layout.header_layout, null);
        PDFCustomView pdfCustomView2=new PDFCustomView(PdfCreatorPlotActivity.this,view2, LinearLayout.LayoutParams.MATCH_PARENT, 90,"4");
        headerView.addView(pdfCustomView2);*/

        return null;
    }

    @Override
    protected PDFBody getBodyViews() {
        PDFBody pdfBody = new PDFBody();

        PDFTextView pdfCompanyNameView = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.H3);
        pdfCompanyNameView.setText("Payment Details");
        pdfBody.addView(pdfCompanyNameView);
        PDFLineSeparatorView lineSeparatorView1 = new PDFLineSeparatorView(getApplicationContext()).setBackgroundColor(Color.WHITE);
        pdfBody.addView(lineSeparatorView1);
        PDFTextView pdfAddressView = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);

        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

        pdfAddressView.setText("as on "+currentDate);
        pdfBody.addView(pdfAddressView);
        pdfCompanyNameView.getView().setGravity(Gravity.CENTER_HORIZONTAL);
        pdfAddressView.getView().setGravity(Gravity.CENTER_HORIZONTAL);


        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.title_layout_for_rent, null);
        PDFCustomView pdfCustomView=new PDFCustomView(PdfCreatorRentActivity.this,view, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT,rentDetailsForPDF,arrayListPlotPayment);
        pdfBody.addView(pdfCustomView);

        String[] textInTable = {"Date", "Customer Name", "Remarks", "Amount"};

        PDFTableView.PDFTableRowView tableHeader = new PDFTableView.PDFTableRowView(getApplicationContext());

        /*for (String s : textInTable) {
            PDFTextView pdfTextView = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
            pdfTextView.setText(s);
            tableHeader.addToRow(pdfTextView);
        }*/

        PDFTableView.PDFTableRowView tableRowView1 = new PDFTableView.PDFTableRowView(getApplicationContext());
       /* for (String s : textInTable) {
            PDFTextView pdfTextView = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
            //pdfTextView.setText("Row 1 : " + s);
            pdfTextView.setText("Row 1 : " + s);
            tableRowView1.addToRow(pdfTextView);
        }*/

        PDFTableView tableView = new PDFTableView(getApplicationContext(), tableHeader, tableRowView1);
        int finalTotalAmount=0;
        for (int i = 0; i < arrayListPlotPayment.size(); i++) {
            // Create 10 rows
            PDFTableView.PDFTableRowView tableRowView = new PDFTableView.PDFTableRowView(getApplicationContext());
            for(int j=0;j<4;j++){
                PDFTextView pdfTextView = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
                if(j==0){

                    arrayListPlotPayment.get(i).getPayment_time();

                    SimpleDateFormat input = new SimpleDateFormat("dd-MM-yyyy");
                    SimpleDateFormat inputT = new SimpleDateFormat("hh:mm:ss");
                    SimpleDateFormat output = new SimpleDateFormat("dd MMMM yyyy");
                    SimpleDateFormat outputT = new SimpleDateFormat("hh:mm a");
                    try {
                        Date oneWayTripDate;
                        Date oneWayTripDateT;
                        oneWayTripDate = input.parse( arrayListPlotPayment.get(i).getPayment_date());  // parse input
                        oneWayTripDateT = inputT.parse( arrayListPlotPayment.get(i).getPayment_time());  // parse input
                        pdfTextView.setText(output.format(oneWayTripDate) +" "+outputT.format(oneWayTripDateT));

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    tableRowView.addToRow(pdfTextView);
                }
                else if(j==1){
                    if(arrayListPlotPayment.get(i).getCustomer_name()!=null){
                        pdfTextView.setText(arrayListPlotPayment.get(i).getCustomer_name());
                        tableRowView.addToRow(pdfTextView);
                    }
                    else {
                        pdfTextView.setText("");
                        tableRowView.addToRow(pdfTextView);
                    }

                }
                else if(j==2){
                    pdfTextView.setText(arrayListPlotPayment.get(i).getRemarks());
                    tableRowView.addToRow(pdfTextView);
                }
                else if(j==3){
                    PDFTextView pdfTextViewR = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P,0);
                    pdfTextViewR.setText("₹"+ Helper.getFormatPrice(Integer.parseInt(arrayListPlotPayment.get(i).getAmount())));
                    finalTotalAmount=finalTotalAmount+Integer.parseInt(arrayListPlotPayment.get(i).getAmount());
                    tableRowView.addToRow(pdfTextViewR);
                }

            }
           /* for (String s : textInTable) {
                PDFTextView pdfTextView = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
                pdfTextView.setText("Row " + (i + 2) + ": " + s);
                tableRowView.addToRow(pdfTextView);
            }*/
            tableView.addRow(tableRowView);
            PDFLineSeparatorView lineSeparatorView3 = new PDFLineSeparatorView(getApplicationContext()).setBackgroundColor(Color.BLACK);
            tableView.addSeparatorRow(lineSeparatorView3);

        }
        pdfBody.addView(tableView);

        LayoutInflater inflaterFinal = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewFinal = inflaterFinal.inflate(R.layout.final_total_layout, null);
        PDFCustomView pdfCustomViewFinal=new PDFCustomView(PdfCreatorRentActivity.this,viewFinal, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT,finalTotalAmount);
        pdfBody.addView(pdfCustomViewFinal);


      /*  LayoutInflater inflater1 = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view1 = inflater1.inflate(R.layout.table_layout, null);
        PDFCustomView pdfCustomView1=new PDFCustomView(PdfCreatorPlotActivity.this,view1, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT,"2");
        pdfBody.addView(pdfCustomView1);*/

       /* LayoutInflater inflater2 = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view2 = inflater2.inflate(R.layout.list_layout, null);
        PDFCustomView pdfCustomView2=new PDFCustomView(PdfCreatorPlotActivity.this,view2, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT,arrayListPlotPayment);
        pdfBody.addView(pdfCustomView2);*/

        return pdfBody;
    }

    @Override
    protected void onNextClicked(final File savedPDFFile) {
        Uri pdfUri = Uri.fromFile(savedPDFFile);

        Intent intentPdfViewer = new Intent(PdfCreatorRentActivity.this, PdfViewerActivity.class);
        intentPdfViewer.putExtra(PdfViewerActivity.PDF_FILE_URI, pdfUri);

        startActivity(intentPdfViewer);
    }
}
