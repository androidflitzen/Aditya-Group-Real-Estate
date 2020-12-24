package com.flitzen.adityarealestate_new.Activity;


import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.flitzen.adityarealestate_new.Classes.Helper;
import com.flitzen.adityarealestate_new.R;

import java.text.DecimalFormat;

public class Activity_EMI_Calculation extends AppCompatActivity {

    private EditText edtLoanAmount, edtInterest, edtTenure;
    private Button btnCalculate;
    private LinearLayout layout_calculate_detail;
    private TextView txtTotalPayable, txtEmi, txtAmount, txtTotalInterest;
    private Button btn_reset;
    private Button btn_loan_report;
    private RadioButton rdbEmi;
    private RadioButton rdbSimple;

    ///FOR emi CALCULATOR
    private View viewTenure, viewEmiContent;

    DecimalFormat formatter;
    double lAMOUNT, lTENURE, lRATE, TOTAL_PAYABLE, EMI, INTEREST_PAYABLE;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emi_calculation);

        getSupportActionBar().setTitle(Html.fromHtml("Calculator"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        formatter = new DecimalFormat(Helper.DECIMAL_FORMATE);

        edtLoanAmount = (EditText) findViewById(R.id.edt_calculate_enter_amount);
        edtInterest = (EditText) findViewById(R.id.edt_interest_percentage);
        edtTenure = (EditText) findViewById(R.id.edt_calculator_tenure);
        rdbEmi = (RadioButton) findViewById(R.id.rdb_calculator_emi);
        rdbSimple = (RadioButton) findViewById(R.id.rdb_calculator_simple);
        btnCalculate = (Button) findViewById(R.id.btn_calculate);
        btn_reset = (Button) findViewById(R.id.btn_reset);
        btn_loan_report = (Button) findViewById(R.id.btn_loan_report_detail);

        txtTotalPayable = (TextView) findViewById(R.id.txt_calculate_amount_to_pay);
        txtEmi = (TextView) findViewById(R.id.txt_calculate_emi_per_month);
        txtTotalInterest = (TextView) findViewById(R.id.txt_calculate_total_interest);
        txtAmount = (TextView) findViewById(R.id.txt_total_amount);

        viewTenure = findViewById(R.id.view_emi_calculator_tenure);
        viewEmiContent = findViewById(R.id.view_emi_calculator_emi_content);
        layout_calculate_detail = (LinearLayout) findViewById(R.id.calculations_layout);

        /*Navigation_Activity navigation_activity = new Navigation_Activity();
        navigation_activity.home_jump();*/

        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean loanType;
                if (rdbEmi.isChecked()) {
                    loanType = true;
                } else {
                    loanType = false;
                }

                if (TextUtils.isEmpty(edtLoanAmount.getText().toString())) {
                    edtLoanAmount.setError((Html.fromHtml("<font color='#ffffff'>Enter Amount</font>")));
                    edtLoanAmount.requestFocus();
                    return;
                } else if (TextUtils.isEmpty(edtInterest.getText().toString())) {
                    edtInterest.setError((Html.fromHtml("<font color='#ffffff'>Enter Interest</font>")));
                    edtInterest.requestFocus();
                    return;
                } else if (TextUtils.isEmpty(edtTenure.getText().toString())) {
                    if (loanType) {
                        edtTenure.setError((Html.fromHtml("<font color='#ffffff'>Enter Number Months</font>")));
                        edtTenure.requestFocus();
                        return;
                    }
                }
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                CalculateLoan(loanType);
            }

        });

        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtLoanAmount.setText("");
                edtInterest.setText("");
                edtTenure.setText("");
                layout_calculate_detail.setVisibility(View.GONE);
            }
        });

        btn_loan_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Activity_EMI_Calculation.this, Loan_Report_Activity.class);
                i.putExtra("AMOUNT", lAMOUNT);
                i.putExtra("TOTAL", TOTAL_PAYABLE);
                i.putExtra("MONTHS", lTENURE);
                i.putExtra("EMI", EMI);
                i.putExtra("RATE", lRATE);
                startActivity(i);
            }
        });

        edtInterest.addTextChangedListener(new OnTextChange());
        edtLoanAmount.addTextChangedListener(new OnTextChange());
        edtTenure.addTextChangedListener(new OnTextChange());
        rdbEmi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                layout_calculate_detail.setVisibility(View.GONE);
                if (isChecked)
                    viewTenure.setVisibility(View.VISIBLE);
            }
        });
        rdbSimple.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                layout_calculate_detail.setVisibility(View.GONE);
                if (isChecked)
                    viewTenure.setVisibility(View.GONE);
            }
        });

    }

    class OnTextChange implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            layout_calculate_detail.setVisibility(View.GONE);
        }
    }

    public void CalculateLoan(boolean status) {

        layout_calculate_detail.setVisibility(View.VISIBLE);
        lAMOUNT = Double.parseDouble(edtLoanAmount.getText().toString().trim());
        lTENURE = ((edtTenure.getText().toString().trim().length()==0) ? 0.0 : Double.parseDouble(edtTenure.getText().toString().trim()));
        lRATE = Double.parseDouble(edtInterest.getText().toString().trim());

        if (status) {//EMI LOAN

            viewEmiContent.setVisibility(View.VISIBLE);
            btn_loan_report.setVisibility(View.VISIBLE);

            EMI = Helper.GetEMI(lAMOUNT, lRATE, lTENURE);
            TOTAL_PAYABLE = EMI * lTENURE;
            INTEREST_PAYABLE = TOTAL_PAYABLE - lAMOUNT;

            txtTotalInterest.setText(Helper.RUPEE + formatter.format(Helper.getRoundValue(INTEREST_PAYABLE)));
            txtAmount.setText(Helper.RUPEE + formatter.format(lAMOUNT));
            txtTotalPayable.setText(Helper.RUPEE + formatter.format(Helper.getRoundValue(TOTAL_PAYABLE)));
            txtEmi.setText(Helper.RUPEE + formatter.format(Helper.getRoundValue(EMI)));

        } else {// SIMPLE LOAN
            //Formula 1.Find interest = (AMOUNT*RATE) / (100) -------- 2.Find total payable = Find interest + AMOUNT
            viewEmiContent.setVisibility(View.GONE);
            INTEREST_PAYABLE = (lAMOUNT * lRATE) / (100);
            TOTAL_PAYABLE = (INTEREST_PAYABLE * lTENURE) + lAMOUNT;

            //txtTotalInterest.setText(Helper.RUPEE + formatter.format(Helper.getRoundValue(INTEREST_PAYABLE * lTENURE)));
            //txtAmount.setText(Helper.RUPEE + formatter.format(lAMOUNT));
            //txtTotalPayable.setText(Helper.RUPEE + formatter.format(Helper.getRoundValue(TOTAL_PAYABLE)));
            txtEmi.setText(Helper.RUPEE + formatter.format(Helper.getRoundValue(INTEREST_PAYABLE)));

            btn_loan_report.setVisibility(View.GONE);

        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        overridePendingTransition(0, 0);
      //  overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
       // overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
    }
}


