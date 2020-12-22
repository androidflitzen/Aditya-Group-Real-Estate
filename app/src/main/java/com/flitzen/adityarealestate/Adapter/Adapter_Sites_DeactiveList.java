package com.flitzen.adityarealestate.Adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;


import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.flitzen.adityarealestate.Classes.API;
import com.flitzen.adityarealestate.Classes.CToast;
import com.flitzen.adityarealestate.Classes.Helper;
import com.flitzen.adityarealestate.Items.Item_Sites_List;
import com.flitzen.adityarealestate.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Adapter_Sites_DeactiveList extends RecyclerView.Adapter<Adapter_Sites_DeactiveList.ViewHolder> {

    ArrayList<Item_Sites_List> itemList = new ArrayList<>();
    Activity context;
    boolean value;
    Adapter_Sites_List.OnItemClickListener mItemClickListener;
    ProgressDialog prd;

    public Adapter_Sites_DeactiveList(Activity context, ArrayList<Item_Sites_List> itemList, Boolean value) {
        this.context = context;
        this.itemList = itemList;
        this.value = value;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_deactive_plotlist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {


        holder.ivPopUp.setChecked(false);
        holder.txt_site_name.setText(itemList.get(position).getSite_name());

        String s = itemList.get(position).getSite_name() + ", " + itemList.get(position).getSite_address();
        SpannableString ss1 = new SpannableString(s);
        ss1.setSpan(new RelativeSizeSpan(1f), 0, itemList.get(position).getSite_name().length(), 0); // set size
        ss1.setSpan(new StyleSpan(Typeface.BOLD), 0, itemList.get(position).getSite_name().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.txt_site_name_1.setText(ss1);



        //holder.txt_site_name_1.setText(itemList.get(position).getSite_name());


        //holder.txt_site_address.setText("Address : " + itemList.get(position).getSite_address());

        holder.txt_site_size.setText("Size (Sq. Yard): " + itemList.get(position).getSite_size());

        if (!itemList.get(position).getSite_price().equals(""))
            holder.txt_site_price.setText(context.getResources().getString(R.string.rupee) + Helper.getFormatPrice(Integer.parseInt(itemList.get(position).getSite_price())));
        else
            holder.txt_site_price.setText("-");

        if (value) {
            holder.txt_site_name.setVisibility(View.VISIBLE);
            holder.txt_site_name_1.setVisibility(View.GONE);
        } else {
            holder.txt_site_name.setVisibility(View.GONE);
            holder.txt_site_name_1.setVisibility(View.VISIBLE);
        }

        holder.view_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mItemClickListener != null)
                    mItemClickListener.onItemClick(position);
            }
        });
        holder.ivPopUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // OpenActiveSite(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Are you sure active site ?");
                builder.setCancelable(true);

                builder.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                toactiveSiteApi(position);
                            }
                        });

                builder.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                holder.ivPopUp.setChecked(false);
                                dialog.cancel();
                            }
                        });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        holder.ivDeleteplot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDeleteplot(position);
            }
        });

    }

    private void openDeleteplot(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Are you sure delete this site ?");
        builder.setCancelable(true);

        builder.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        todeleteplotApi(position);
                    }
                });

        builder.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    private void todeleteplotApi(final int position) {

        showPrd();

        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference();
        databaseReference.child("Sites").child(itemList.get(position).getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    databaseReference.child("Sites").child(itemList.get(position).getKey()).removeValue().addOnCompleteListener(context, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            hidePrd();
                            if(task.isSuccessful()){
                                new CToast(context).simpleToast("Site delete successfully", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                                notifyDataSetChanged();
                            }
                            else {
                                new CToast(context).simpleToast(task.getException().toString(), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                            }

                        }
                    }).addOnFailureListener(context, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            hidePrd();
                            new CToast(context).simpleToast(e.getMessage(), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                        }
                    });
                }
                else {
                    hidePrd();
                    new CToast(context).simpleToast("Site not exist", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                hidePrd();
                new CToast(context).simpleToast(error.getMessage(), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
            }
        });
    }

    private void todeleteplotApi1(final int position) {

        showPrd();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, API.TODELETEPLOT + "site_id="+itemList.get(position).getId(), new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                hidePrd();
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("result") == 1) {
                        new CToast(context).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                        itemList.remove(position);
                        notifyDataSetChanged();
                    } else {
                        new CToast(context).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    new CToast(context).simpleToast("something went wrong.", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hidePrd();
                new CToast(context).simpleToast("something went wrong.", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(stringRequest);


    }

    private void OpenActiveSite(final int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Are you sure active site ?");
        builder.setCancelable(true);

        builder.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        toactiveSiteApi(position);
                    }
                });

        builder.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    private void toactiveSiteApi(final int position) {

        showPrd();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Query query = databaseReference.child("Sites").orderByKey();
        databaseReference.keepSynced(true);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot npsnapshot) {
                hidePrd();
                try {
                    if (npsnapshot.exists()) {
                        for (DataSnapshot dataSnapshot : npsnapshot.getChildren()) {

                            if (dataSnapshot.child("id").getValue().toString().equals(itemList.get(position).getId())) {

                                DatabaseReference cineIndustryRef = databaseReference.child("Sites").child(dataSnapshot.getKey());
                                Map<String, Object> map = new HashMap<>();
                                map.put("status", 1);
                                Task<Void> voidTask = cineIndustryRef.updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        hidePrd();
                                        new CToast(context).simpleToast("Site activate successfully", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                                        notifyDataSetChanged();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        hidePrd();
                                        new CToast(context).simpleToast(e.getMessage(), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                                    }
                                });
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.e("exception   ", e.toString());
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hidePrd();
                Log.e("databaseError   ", databaseError.getMessage());
            }
        });

    }

    private void toactiveSiteApi1(final int position) {

        showPrd();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, API.TODOACTIVESITES + "site_id="+itemList.get(position).getId(), new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                hidePrd();
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("result") == 1) {
                        new CToast(context).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                        itemList.remove(position);
                        notifyDataSetChanged();
                    } else {
                        new CToast(context).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    new CToast(context).simpleToast("something went wrong.", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hidePrd();
                new CToast(context).simpleToast("something went wrong.", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(stringRequest);

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txt_site_name, txt_site_name_1, txt_site_address, txt_site_size, txt_site_price;
        View view_main;
        ImageView plot_soldout,ivDeleteplot;
        SwitchMaterial ivPopUp;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_site_name = (TextView) itemView.findViewById(R.id.txt_site_name);
            txt_site_name_1 = (TextView) itemView.findViewById(R.id.txt_site_name_1);
            txt_site_address = (TextView) itemView.findViewById(R.id.txt_site_address);
            txt_site_size = (TextView) itemView.findViewById(R.id.txt_site_size);
            txt_site_price = (TextView) itemView.findViewById(R.id.txt_site_price);
            plot_soldout = (ImageView) itemView.findViewById(R.id.plot_soldout);
            view_main = itemView.findViewById(R.id.view_main);
            ivPopUp = itemView.findViewById(R.id.ivPopUp);
            ivDeleteplot = itemView.findViewById(R.id.ivDeleteplot);
        }

    }

    public void setOnItemClickListener(final Adapter_Sites_List.OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public interface OnItemClickListener {

        void onItemClick(int position);
    }

    public void showPrd() {
        prd = new ProgressDialog(context);
        prd.setMessage("Please wait...");
        prd.setCancelable(false);
        prd.show();
    }

    public void hidePrd() {
        prd.dismiss();
    }
}
