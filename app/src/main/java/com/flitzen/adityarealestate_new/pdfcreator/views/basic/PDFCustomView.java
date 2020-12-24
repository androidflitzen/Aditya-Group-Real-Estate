package com.flitzen.adityarealestate_new.pdfcreator.views.basic;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.flitzen.adityarealestate_new.Classes.Helper;
import com.flitzen.adityarealestate_new.Items.CashPaymentDetailsForPDF;
import com.flitzen.adityarealestate_new.Items.Item_Plot_Payment_List;
import com.flitzen.adityarealestate_new.Items.Items_View_EMI;
import com.flitzen.adityarealestate_new.Items.LoanDetailsForPDF;
import com.flitzen.adityarealestate_new.Items.PlotDetailsForPDF;
import com.flitzen.adityarealestate_new.Items.RentDetailsForPDF;
import com.flitzen.adityarealestate_new.R;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class PDFCustomView extends PDFView implements Serializable {

    private PDFCustomView(Context context) {
        super(context);
    }

    public PDFCustomView(Context context, View view, int width, int height, String s) {
        super(context);

        if (s.equals("1")) {

            TextView txtDOP = view.findViewById(R.id.txtDOP);
            TextView txtPendingAmount = view.findViewById(R.id.txtPendingAmount);
            TextView txtPlotNo = view.findViewById(R.id.txtPlotNo);
            TextView txtPurchasePrice = view.findViewById(R.id.txtPurchasePrice);
            TextView txtSiteName = view.findViewById(R.id.txtSiteName);
            TextView txtSize = view.findViewById(R.id.txtSize);

            txtDOP.setText("Date");
            txtPendingAmount.setText("000000");
            txtPlotNo.setText("10");
            txtPurchasePrice.setText("4444");
            txtSiteName.setText("Test");
            txtSize.setText("0.5");

        }
       /* else if (s.equals("3")) {


            MyListData[] myListData = new MyListData[] {
                    new MyListData("Email"),
                    new MyListData("Info"),
                    new MyListData("Delete"),
                    new MyListData("Dialer"),
                    new MyListData("Alert"),
                    new MyListData("Map"),

            };

            RecyclerView recyclerView=view.findViewById(R.id.rel);
            MyListAdapter adapter = new MyListAdapter(myListData);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(adapter);
        }
*/


        view.setLayoutParams(new LinearLayout.LayoutParams(width, height, 0));

        super.setView(view);
    }

    public PDFCustomView(Context context, View view, int width, int height, PlotDetailsForPDF plotDetailsForPDF, ArrayList<Item_Plot_Payment_List> arrayListPlotPayment) {
        super(context);

        TextView txtDOP = view.findViewById(R.id.txtDOP);
        TextView txtPendingAmount = view.findViewById(R.id.txtPendingAmount);
        TextView txtPlotNo = view.findViewById(R.id.txtPlotNo);
        TextView txtPurchasePrice = view.findViewById(R.id.txtPurchasePrice);
        TextView txtSiteName = view.findViewById(R.id.txtSiteName);
        TextView txtSize = view.findViewById(R.id.txtSize);
        // TextView txtDateMain=view.findViewById(R.id.txtDateMain);

        SimpleDateFormat input = new SimpleDateFormat("yyyy MMM dd");
        SimpleDateFormat output = new SimpleDateFormat("dd MMMM yyyy");

        try {
            Date oneWayTripDate;
            oneWayTripDate = input.parse(plotDetailsForPDF.getDOP());  // parse input
            txtDOP.setText(output.format(oneWayTripDate));

        } catch (ParseException e) {
            e.printStackTrace();
        }

        // txtDOP.setText(plotDetailsForPDF.getDOP());
        txtPendingAmount.setText(Helper.getFormatPrice(Integer.parseInt(plotDetailsForPDF.getPendingAmount())));
        txtPlotNo.setText(plotDetailsForPDF.getPlotNo());
        txtPurchasePrice.setText(Helper.getFormatPrice(Integer.parseInt(plotDetailsForPDF.getPurchasePrice())));
        txtSiteName.setText(plotDetailsForPDF.getSiteName());
        txtSize.setText(plotDetailsForPDF.getSize());
        //String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        //txtDateMain.setText(currentDate);

      /*  RecyclerView recyclerView=view.findViewById(R.id.rel);
        MyListAdapter adapter = new MyListAdapter(arrayListPlotPayment);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);*/

        view.setLayoutParams(new LinearLayout.LayoutParams(width, height, 0));

        super.setView(view);
    }

    public PDFCustomView(Context context, View view, int width, int height, RentDetailsForPDF rentDetailsForPDF, ArrayList<Item_Plot_Payment_List> arrayListPlotPayment) {
        super(context);

        TextView txtAddress = view.findViewById(R.id.txtAddress);
        TextView txtSince = view.findViewById(R.id.txtSince);
        TextView txtPropertyName = view.findViewById(R.id.txtPropertyName);
        TextView txtRent = view.findViewById(R.id.txtRent);

        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat output = new SimpleDateFormat("dd MMMM yyyy");

        try {
            Date oneWayTripDate;
            Date oneWayTripDateT;
            oneWayTripDate = input.parse(rentDetailsForPDF.getHiredSince());  // parse input
            txtSince.setText(output.format(oneWayTripDate));

        } catch (ParseException e) {
            e.printStackTrace();
        }

        txtAddress.setText(rentDetailsForPDF.getAddress());
        //txtSince.setText(rentDetailsForPDF.getHiredSince());
        txtPropertyName.setText(rentDetailsForPDF.getPropertyName());
        txtRent.setText("₹" + Helper.getFormatPrice(Integer.parseInt(rentDetailsForPDF.getRent())) + "/- per month");
        view.setLayoutParams(new LinearLayout.LayoutParams(width, height, 0));

        super.setView(view);
    }

    public PDFCustomView(Context context, View view, int width, int height,String customerName,String phoneNo) {
        super(context);

        TextView txtCustomerName = view.findViewById(R.id.txtCustomerName);
        TextView txtPhoneNo = view.findViewById(R.id.txtPhoneNo);

        txtCustomerName.setText(customerName);
        txtPhoneNo.setText(phoneNo);

        view.setLayoutParams(new LinearLayout.LayoutParams(width, height, 0));

        super.setView(view);
    }

    public PDFCustomView(Context context, View view, int width, int height, CashPaymentDetailsForPDF cashPaymentDetailsForPDF) {
        super(context);

        TextView txtAddress = view.findViewById(R.id.txtAddress);
        TextView txtSize = view.findViewById(R.id.txtSize);
        TextView txtSiteName = view.findViewById(R.id.txtSiteName);

        txtAddress.setText(cashPaymentDetailsForPDF.getAddress());
        txtSize.setText(cashPaymentDetailsForPDF.getSize()+"Sq yard");
        txtSiteName.setText(cashPaymentDetailsForPDF.getSiteName());
        view.setLayoutParams(new LinearLayout.LayoutParams(width, height, 0));

        super.setView(view);
    }

    public PDFCustomView(Context context, View view, int width, int height, LoanDetailsForPDF loanDetailsForPDF, ArrayList<Items_View_EMI> itemArray_EMI) {
        super(context);

        TextView txtApplicationNumber = view.findViewById(R.id.txtApplicationNumber);
        TextView txtLoanAmount = view.findViewById(R.id.txtLoanAmount);
        TextView txtCustomerName = view.findViewById(R.id.txtCustomerName);
        TextView txtApprovedAmount = view.findViewById(R.id.txtApprovedAmount);
        TextView txtDate = view.findViewById(R.id.txtDate);

        TextView pending = view.findViewById(R.id.pending);
        TextView deposit = view.findViewById(R.id.deposit);
        TextView int1 = view.findViewById(R.id.int1);
        TextView loan = view.findViewById(R.id.loan);


        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat output = new SimpleDateFormat("dd MMMM yyyy");

       /* try {
            Date oneWayTripDate;
            Date oneWayTripDateT;
            oneWayTripDate = input.parse(rentDetailsForPDF.getHiredSince());  // parse input
            txtDate.setText(output.format(oneWayTripDate));

        } catch (ParseException e) {
            e.printStackTrace();
        }*/

        //txtDate.setText(output.format(loanDetailsForPDF.getDateApplied()));
        txtDate.setText(loanDetailsForPDF.getDateApplied());
        txtApplicationNumber.setText(loanDetailsForPDF.getApplicationNo());
        txtCustomerName.setText(loanDetailsForPDF.getCustomerName());
        txtLoanAmount.setText("₹" + Helper.getFormatPrice(Integer.parseInt(loanDetailsForPDF.getLoanAmount())));
        txtApprovedAmount.setText("₹" + Helper.getFormatPrice(Integer.parseInt(loanDetailsForPDF.getApprovedAmount())));
        pending.setText("₹" + Helper.getFormatPrice(Integer.parseInt(loanDetailsForPDF.getPendingAmount())));
        deposit.setText("₹" + Helper.getFormatPrice(Integer.parseInt(loanDetailsForPDF.getDeposit())));
        int1.setText("₹" + Helper.getFormatPrice(Integer.parseInt(loanDetailsForPDF.getInterest())));
        loan.setText("₹" + Helper.getFormatPrice(Integer.parseInt(loanDetailsForPDF.getLoanAmount())));

       /* txtLoanAmount.setText("₹0");
        txtApprovedAmount.setText("₹0");
        pending.setText("₹0");
        deposit.setText("₹0");
        int1.setText("₹0");
        loan.setText("₹0");*/

        view.setLayoutParams(new LinearLayout.LayoutParams(width, height, 0));

        super.setView(view);
    }

    public PDFCustomView(Context context, View view, int width, int height, int finalTotalAmount) {
        super(context);

        TextView txtFinalTotal = view.findViewById(R.id.txtFinalTotal);
        txtFinalTotal.setText(Helper.getFormatPrice(finalTotalAmount));
        view.setLayoutParams(new LinearLayout.LayoutParams(width, height, 0));

        super.setView(view);
    }

    @Override
    public View getView() {
        return super.getView();
    }
}
