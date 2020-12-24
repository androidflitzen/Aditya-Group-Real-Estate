package com.flitzen.adityarealestate_new.Activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.flitzen.adityarealestate_new.Items.CustomerBill;
import com.flitzen.adityarealestate_new.R;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class BillDetailActivity extends AppCompatActivity {
    CustomerBill bill_model;
    ImageView ivBill;
    TextView tvBillMonth,tvBillPrice,tvOtherNote;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_detail);
        initUI();
    }

    private void initUI() {
        getSupportActionBar().setTitle("Bill Detail");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ivBill=findViewById(R.id.ivBill);
        tvBillMonth=findViewById(R.id.tvBillMonth);
        tvBillPrice=findViewById(R.id.tvBillPrice);
        tvOtherNote=findViewById(R.id.tvOtherNote);
        bill_model= (CustomerBill) getIntent().getSerializableExtra("bill_model");
        if (bill_model.getBill_photo()!=null && !bill_model.getBill_photo().equals("")){
            Picasso.with(this)
                    .load(bill_model.getBill_photo())
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .placeholder(R.drawable.img_placeholder)
                    .error(R.drawable.ic_img_not_available)
                    .into(ivBill);
        }else {
            ivBill.setImageResource(R.drawable.ic_img_not_available);
        }

        tvBillMonth.setText(bill_model.getBill_month());
        tvBillPrice.setText("₹"+bill_model.getBill_Rs());
        tvOtherNote.setText(bill_model.getOther_Notes());

        ivBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bill_model.getBill_photo()!=null && !bill_model.getBill_photo().equals("")){
                    startActivity(new Intent(BillDetailActivity.this,Activity_ImageViewer.class)
                            .putExtra("img_url",bill_model.getBill_photo()));
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        overridePendingTransition(0, 0);
        //overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
        return true;
    }
}
