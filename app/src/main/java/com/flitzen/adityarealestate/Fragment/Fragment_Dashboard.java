package com.flitzen.adityarealestate.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.flitzen.adityarealestate.Activity.Activity_Add_Payment_For_Loan;
import com.flitzen.adityarealestate.Activity.Activity_Add_Payment_For_Plots;
import com.flitzen.adityarealestate.Activity.Activity_Add_Payment_For_Rents;
import com.flitzen.adityarealestate.Activity.Activity_Admin_All_LoanApplication;
import com.flitzen.adityarealestate.Activity.Activity_Customer_List;
import com.flitzen.adityarealestate.Activity.Activity_Plot_ActiveList;
import com.flitzen.adityarealestate.Activity.Activity_Rent_List;

import com.flitzen.adityarealestate.Activity.Transaction_Activity;
import com.flitzen.adityarealestate.Classes.SharePref;
import com.flitzen.adityarealestate.R;

public class Fragment_Dashboard extends Fragment {

    View btn_plots, btn_rents, btn_Customer, btn_Load_Application, btn_plots_payment, btn_rents_payment, btn_loan_payment, btn_transaction,btn_Tasks;
    TextView tvTitleLoginName,txtPlot,txtRent,txtLoan,txtTransaction,txtCustomer;
    ImageView imgPlot,imgRent,imgLoan,imgTransaction,imgCustomer;

    Animation animZoomout;
    SharedPreferences sharedPreferences;
    public Fragment_Dashboard() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(Html.fromHtml("<b>" + getResources().getString(R.string.app_name) + "<b>"));

        init(view);
        sharedPreferences = SharePref.getSharePref(getActivity());
        animZoomout = AnimationUtils.loadAnimation(getActivity(), R.anim.zoom_in);

        startanimation();

        btn_plots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                btn_plots.setBackgroundColor(getResources().getColor(R.color.select_card));
                btn_rents.setBackgroundColor(getResources().getColor(R.color.white));
                btn_Customer.setBackgroundColor(getResources().getColor(R.color.white));
                btn_Load_Application.setBackgroundColor(getResources().getColor(R.color.white));
                btn_plots_payment.setBackgroundColor(getResources().getColor(R.color.white));
                btn_rents_payment.setBackgroundColor(getResources().getColor(R.color.white));
                btn_loan_payment.setBackgroundColor(getResources().getColor(R.color.white));
                btn_transaction.setBackgroundColor(getResources().getColor(R.color.white));

                imgPlot.setColorFilter(ContextCompat.getColor(getActivity(), R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
                txtPlot.setTextColor(getResources().getColor(R.color.white));


                startActivity(new Intent(getContext(), Activity_Plot_ActiveList.class));
                getActivity().overridePendingTransition(0, 0);
                //getActivity().overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
            }
        });

        btn_rents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                btn_plots.setBackgroundColor(getResources().getColor(R.color.white));
                btn_rents.setBackgroundColor(getResources().getColor(R.color.select_card));
                btn_Customer.setBackgroundColor(getResources().getColor(R.color.white));
                btn_Load_Application.setBackgroundColor(getResources().getColor(R.color.white));
                btn_plots_payment.setBackgroundColor(getResources().getColor(R.color.white));
                btn_rents_payment.setBackgroundColor(getResources().getColor(R.color.white));
                btn_loan_payment.setBackgroundColor(getResources().getColor(R.color.white));
                btn_transaction.setBackgroundColor(getResources().getColor(R.color.white));

                imgRent.setColorFilter(ContextCompat.getColor(getActivity(), R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
                txtRent.setTextColor(getResources().getColor(R.color.white));


                btn_rents.setBackgroundColor(getResources().getColor(R.color.select_card));
                startActivity(new Intent(getContext(), Activity_Rent_List.class));
                getActivity().overridePendingTransition(0, 0);
                //getActivity().overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
            }
        });

        btn_Customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                btn_plots.setBackgroundColor(getResources().getColor(R.color.white));
                btn_rents.setBackgroundColor(getResources().getColor(R.color.white));
                btn_Customer.setBackgroundColor(getResources().getColor(R.color.select_card));
                btn_Load_Application.setBackgroundColor(getResources().getColor(R.color.white));
                btn_plots_payment.setBackgroundColor(getResources().getColor(R.color.white));
                btn_rents_payment.setBackgroundColor(getResources().getColor(R.color.white));
                btn_loan_payment.setBackgroundColor(getResources().getColor(R.color.white));
                btn_transaction.setBackgroundColor(getResources().getColor(R.color.white));

                imgCustomer.setColorFilter(ContextCompat.getColor(getActivity(), R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
                txtCustomer.setTextColor(getResources().getColor(R.color.white));


                btn_Customer.setBackgroundColor(getResources().getColor(R.color.select_card));
                startActivity(new Intent(getContext(), Activity_Customer_List.class));
                getActivity().overridePendingTransition(0, 0);
               // getActivity().overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
            }
        });

        btn_Load_Application.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                btn_plots.setBackgroundColor(getResources().getColor(R.color.white));
                btn_rents.setBackgroundColor(getResources().getColor(R.color.white));
                btn_Customer.setBackgroundColor(getResources().getColor(R.color.white));
                btn_Load_Application.setBackgroundColor(getResources().getColor(R.color.select_card));
                btn_plots_payment.setBackgroundColor(getResources().getColor(R.color.white));
                btn_rents_payment.setBackgroundColor(getResources().getColor(R.color.white));
                btn_loan_payment.setBackgroundColor(getResources().getColor(R.color.white));
                btn_transaction.setBackgroundColor(getResources().getColor(R.color.white));

                imgLoan.setColorFilter(ContextCompat.getColor(getActivity(), R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
                txtLoan.setTextColor(getResources().getColor(R.color.white));


                btn_Load_Application.setBackgroundColor(getResources().getColor(R.color.select_card));
                startActivity(new Intent(getContext(), Activity_Admin_All_LoanApplication.class));
                getActivity().overridePendingTransition(0, 0);
                //getActivity().overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
            }
        });

       /* btn_Tasks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), Activity_Tasks_List.class));
                getActivity().overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
            }
        }); */

        btn_plots_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                btn_plots.setBackgroundColor(getResources().getColor(R.color.white));
                btn_rents.setBackgroundColor(getResources().getColor(R.color.white));
                btn_Customer.setBackgroundColor(getResources().getColor(R.color.white));
                btn_Load_Application.setBackgroundColor(getResources().getColor(R.color.white));
                btn_plots_payment.setBackgroundColor(getResources().getColor(R.color.select_card));
                btn_rents_payment.setBackgroundColor(getResources().getColor(R.color.white));
                btn_loan_payment.setBackgroundColor(getResources().getColor(R.color.white));
                btn_transaction.setBackgroundColor(getResources().getColor(R.color.white));

              /*  imgPlot.setColorFilter(ContextCompat.getColor(getActivity(), R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
                txtPlot.setTextColor(getResources().getColor(R.color.white));*/



                btn_plots_payment.setBackgroundColor(getResources().getColor(R.color.select_card));
                startActivity(new Intent(getContext(), Activity_Add_Payment_For_Plots.class));
                getActivity().overridePendingTransition(0, 0);
                //getActivity().overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
            }
        });

        btn_rents_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                btn_plots.setBackgroundColor(getResources().getColor(R.color.white));
                btn_rents.setBackgroundColor(getResources().getColor(R.color.white));
                btn_Customer.setBackgroundColor(getResources().getColor(R.color.white));
                btn_Load_Application.setBackgroundColor(getResources().getColor(R.color.white));
                btn_plots_payment.setBackgroundColor(getResources().getColor(R.color.white));
                btn_rents_payment.setBackgroundColor(getResources().getColor(R.color.select_card));
                btn_loan_payment.setBackgroundColor(getResources().getColor(R.color.white));
                btn_transaction.setBackgroundColor(getResources().getColor(R.color.white));

              /*  imgPlot.setColorFilter(ContextCompat.getColor(getActivity(), R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
                txtPlot.setTextColor(getResources().getColor(R.color.white));
*/

                btn_rents_payment.setBackgroundColor(getResources().getColor(R.color.select_card));
                startActivity(new Intent(getContext(), Activity_Add_Payment_For_Rents.class));
                getActivity().overridePendingTransition(0, 0);
                //getActivity().overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
            }
        });

        btn_loan_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                btn_plots.setBackgroundColor(getResources().getColor(R.color.white));
                btn_rents.setBackgroundColor(getResources().getColor(R.color.white));
                btn_Customer.setBackgroundColor(getResources().getColor(R.color.white));
                btn_Load_Application.setBackgroundColor(getResources().getColor(R.color.white));
                btn_plots_payment.setBackgroundColor(getResources().getColor(R.color.white));
                btn_rents_payment.setBackgroundColor(getResources().getColor(R.color.white));
                btn_loan_payment.setBackgroundColor(getResources().getColor(R.color.select_card));
                btn_transaction.setBackgroundColor(getResources().getColor(R.color.white));

             /*   imgPlot.setColorFilter(ContextCompat.getColor(getActivity(), R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
                txtPlot.setTextColor(getResources().getColor(R.color.white));*/

                btn_loan_payment.setBackgroundColor(getResources().getColor(R.color.select_card));
                startActivity(new Intent(getContext(), Activity_Add_Payment_For_Loan.class));
                getActivity().overridePendingTransition(0, 0);
                //getActivity().overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
            }
        });

        btn_transaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                btn_plots.setBackgroundColor(getResources().getColor(R.color.white));
                btn_rents.setBackgroundColor(getResources().getColor(R.color.white));
                btn_Customer.setBackgroundColor(getResources().getColor(R.color.white));
                btn_Load_Application.setBackgroundColor(getResources().getColor(R.color.white));
                btn_plots_payment.setBackgroundColor(getResources().getColor(R.color.white));
                btn_rents_payment.setBackgroundColor(getResources().getColor(R.color.white));
                btn_loan_payment.setBackgroundColor(getResources().getColor(R.color.white));
                btn_transaction.setBackgroundColor(getResources().getColor(R.color.select_card));

                imgTransaction.setColorFilter(ContextCompat.getColor(getActivity(), R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
                txtTransaction.setTextColor(getResources().getColor(R.color.white));


                btn_transaction.setBackgroundColor(getResources().getColor(R.color.select_card));
                startActivity(new Intent(getContext(), Transaction_Activity.class));
                getActivity().overridePendingTransition(0, 0);
              //  getActivity().overridePendingTransition(R.anim.feed_in, R.anim.feed_out);

            }
        });



        return view;
    }

    private void init(View view) {

        btn_plots = view.findViewById(R.id.btn_plots);
        btn_rents = view.findViewById(R.id.btn_rents);
        btn_Customer = view.findViewById(R.id.btn_Customer);
        btn_Load_Application = view.findViewById(R.id.btn_Load_Application);
        btn_plots_payment = view.findViewById(R.id.btn_plots_payment);
        btn_rents_payment = view.findViewById(R.id.btn_rents_payment);
        btn_loan_payment = view.findViewById(R.id.btn_loan_payment);
       //  btn_Tasks = view.findViewById(R.id.btn_Tasks);
        tvTitleLoginName = view.findViewById(R.id.tvTitleLoginName);
        btn_transaction = view.findViewById(R.id.btn_transaction);

        imgPlot = view.findViewById(R.id.imgPlot);
        txtPlot = view.findViewById(R.id.txtPlot);
        imgRent = view.findViewById(R.id.imgRent);
        txtRent = view.findViewById(R.id.txtRent);
        imgLoan = view.findViewById(R.id.imgLoan);
        txtLoan = view.findViewById(R.id.txtLoan);
        imgTransaction = view.findViewById(R.id.imgTransaction);
        txtTransaction = view.findViewById(R.id.txtTransaction);
        imgCustomer = view.findViewById(R.id.imgCustomer);
        txtCustomer = view.findViewById(R.id.txtCustomer);


    }

    public void startanimation() {

     /* ObjectAnimator scaleAnim = ObjectAnimator.ofFloat(btn_plots, "scaleX", 1.0f, 2.0f);
scaleAnim.setDuration(3000);
scaleAnim.setRepeatCount(ValueAnimator.INFINITE);
scaleAnim.setRepeatMode(ValueAnimator.REVERSE);
scaleAnim.start();

ObjectAnimator scaleAnim1 = ObjectAnimator.ofFloat(btn_rents, "scaleX", 1.0f, 2.0f);
scaleAnim1.setDuration(3000);
scaleAnim1.setRepeatCount(ValueAnimator.INFINITE);
scaleAnim1.setRepeatMode(ValueAnimator.REVERSE);
scaleAnim1.start();

ObjectAnimator scaleAnim2 = ObjectAnimator.ofFloat(btn_Customer, "scaleX", 1.0f, 2.0f);
scaleAnim2.setDuration(3000);
scaleAnim2.setRepeatCount(ValueAnimator.INFINITE);
scaleAnim2.setRepeatMode(ValueAnimator.REVERSE);
scaleAnim2.start();*/


     //   tvTitleLoginName.setText("Welcome "+sharedPreferences.getString(SharePref.userName,"").toLowerCase());

        tvTitleLoginName.setText("Welcome To Aditya Group");

        btn_plots.setAnimation(animZoomout);
        btn_rents.setAnimation(animZoomout);
        btn_Customer.setAnimation(animZoomout);
        btn_Load_Application.setAnimation(animZoomout);
        btn_plots_payment.setAnimation(animZoomout);
        btn_rents_payment.setAnimation(animZoomout);
        // btn_Tasks.setAnimation(animZoomout);
        btn_loan_payment.setAnimation(animZoomout);
        btn_transaction.setAnimation(animZoomout);
    }
}