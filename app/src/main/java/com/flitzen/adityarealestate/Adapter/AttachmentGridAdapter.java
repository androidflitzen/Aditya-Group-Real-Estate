package com.flitzen.adityarealestate.Adapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.flitzen.adityarealestate.Activity.Activity_ImageViewer;
import com.flitzen.adityarealestate.Classes.API;
import com.flitzen.adityarealestate.Classes.CToast;
import com.flitzen.adityarealestate.Items.Items_Attachment;
import com.flitzen.adityarealestate.R;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;

public class AttachmentGridAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater inflater;
    ArrayList<Items_Attachment> attachmenList;
    ProgressDialog prd;
    // Constructor
    public AttachmentGridAdapter(Context c, ArrayList<Items_Attachment> attachmenList) {
        mContext = c;
        inflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
        this.attachmenList=attachmenList;
    }

    public int getCount() {
        return attachmenList.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView==null){
            convertView = inflater.inflate(R.layout.list_attachment_item, null);
            viewHolder=new ViewHolder();
            viewHolder.ivAttachment=(ImageView) convertView.findViewById(R.id.ivAttachment);
            viewHolder.ivDeleteAttachment=(ImageView) convertView.findViewById(R.id.ivDeleteAttachment);
            convertView.setTag(viewHolder);
        }else {
            viewHolder= (ViewHolder) convertView.getTag();
        }
        Picasso.with(mContext)
                .load(attachmenList.get(position).getFile())
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .placeholder(R.drawable.img_placeholder)
                .error(R.drawable.ic_doc)
                .into(viewHolder.ivAttachment);

        viewHolder.ivDeleteAttachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteDialog(position);
            }
        });

        viewHolder.ivAttachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext,Activity_ImageViewer.class);
                intent.putExtra("img_url",attachmenList.get(position).getFile());
                mContext.startActivity(intent);
            }
        });
        return convertView;
    }

    class ViewHolder {
        ImageView ivAttachment,ivDeleteAttachment;
    }

    private void showDeleteDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("Delete this Attachment ?");
        builder.setCancelable(true);

        builder.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteAttachmenAPI(position);
                    }
                });

        builder.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder.create();
        alert11.show();
    }

    private void deleteAttachmenAPI(final int position) {
        showPrd();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, API.TASKS + "?type=Delete_image&id="+attachmenList.get(position).getId(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                hidePrd();
                try {

                    JSONObject jsonObject = new JSONObject(response);
                   // Log.e("Response Task complete", response);

                    if (jsonObject.getInt("result") == 1) {

                        new CToast(mContext).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                        attachmenList.remove(position);
                        notifyDataSetChanged();

                    } else {
                        new CToast(mContext).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    hidePrd();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hidePrd();
            }
        }) {
            /*@Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("type", "Complete");
                params.put("id", Aditya.ID);
                return params;
            }*/
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue queue = Volley.newRequestQueue(mContext);
        queue.add(stringRequest);
    }

    public void showPrd() {
        prd = new ProgressDialog(mContext);
        prd.setMessage("Please wait...");
        prd.setCancelable(false);
        prd.show();
    }

    public void hidePrd() {
        prd.dismiss();
    }
}