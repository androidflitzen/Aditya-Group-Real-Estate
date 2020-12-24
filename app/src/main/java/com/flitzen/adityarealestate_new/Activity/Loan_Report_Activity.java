package com.flitzen.adityarealestate_new.Activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ListView;


import com.flitzen.adityarealestate_new.Adapter.Adapter_Loan_Report;
import com.flitzen.adityarealestate_new.Items.Items_Loan_Reports;
import com.flitzen.adityarealestate_new.R;

import java.util.ArrayList;

public class Loan_Report_Activity extends AppCompatActivity {

    ArrayList<Items_Loan_Reports> itemArray = new ArrayList<>();
    double lAMOUNT, TOTAL_PAYABLE, lTENURE, EMI, lRATE;
    private Adapter_Loan_Report mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan__report_);

        lAMOUNT = getIntent().getDoubleExtra("AMOUNT", 0.0);
        TOTAL_PAYABLE = getIntent().getDoubleExtra("TOTAL", 0.0);
        lTENURE = getIntent().getDoubleExtra("MONTHS", 0.0);
        EMI = getIntent().getDoubleExtra("EMI", 0.0);
        lRATE = getIntent().getDoubleExtra("RATE", 0.0);

        itemArray.clear();
        double rPrincipalAmount = lAMOUNT;
        double rOverallAmount = TOTAL_PAYABLE;

        for (int i = 0; i <= lTENURE; i++) {
            if (i == 0) {
                Items_Loan_Reports item = new Items_Loan_Reports();
                item.setrMonth(i);
                item.setrAmont(rPrincipalAmount);
                item.setrTotalAmount(rOverallAmount);
                itemArray.add(item);
            } else {
                Items_Loan_Reports item = new Items_Loan_Reports();
                item.setrMonth(i);
                item.setrEMI(EMI);
                double rInterest = (rPrincipalAmount * lRATE) / 100;
                double rPrincipal = EMI - rInterest;
                rPrincipalAmount = rPrincipalAmount - rPrincipal;
                rOverallAmount = rOverallAmount - EMI;
                item.setrInterest(rInterest);
                item.setrPrincipal(rPrincipal);
                item.setrAmont(rPrincipalAmount);
                item.setrTotalAmount(rOverallAmount);
                itemArray.add(item);
            }
        }

        ListView lst = (ListView) findViewById(R.id.listview_loan_report);
        mAdapter = new Adapter_Loan_Report(Loan_Report_Activity.this, itemArray);
        lst.setAdapter(mAdapter);

    }
}
