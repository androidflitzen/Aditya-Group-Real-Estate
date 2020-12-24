package com.flitzen.adityarealestate_new.Items;

import java.util.ArrayList;

public class Item_Plot_Detail {

    String id;
    String plot_no;
    String site_name;
    String site_address;
    String purchase_price;
    String date_of_purchase;
    String pending_amount;
    String paid_amount;

    ArrayList<Item_Plot_Payment> arrayListPayment = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlot_no() {
        return plot_no;
    }

    public void setPlot_no(String plot_no) {
        this.plot_no = plot_no;
    }

    public String getSite_name() {
        return site_name;
    }

    public void setSite_name(String site_name) {
        this.site_name = site_name;
    }

    public String getSite_address() {
        return site_address;
    }

    public void setSite_address(String site_address) {
        this.site_address = site_address;
    }

    public String getPurchase_price() {
        return purchase_price;
    }

    public void setPurchase_price(String purchase_price) {
        this.purchase_price = purchase_price;
    }

    public String getDate_of_purchase() {
        return date_of_purchase;
    }

    public void setDate_of_purchase(String date_of_purchase) {
        this.date_of_purchase = date_of_purchase;
    }

    public String getPending_amount() {
        return pending_amount;
    }

    public void setPending_amount(String pending_amount) {
        this.pending_amount = pending_amount;
    }

    public String getPaid_amount() {
        return paid_amount;
    }

    public void setPaid_amount(String paid_amount) {
        this.paid_amount = paid_amount;
    }

    public ArrayList<Item_Plot_Payment> getArrayListPayment() {
        return arrayListPayment;
    }

    public void setArrayListPayment(ArrayList<Item_Plot_Payment> arrayListPayment) {
        this.arrayListPayment = arrayListPayment;
    }

    public static class Item_Plot_Payment{
        String  id;
        String amount;
        String remarks;
        String customer_id;
        String customer_name;
        String payment_date;
        String payment_time;
        String payment_attachment;
        String file_type;

        public String getFile_type() {
            return file_type;
        }

        public void setFile_type(String file_type) {
            this.file_type = file_type;
        }

        public String getPayment_attachment() {
            return payment_attachment;
        }

        public void setPayment_attachment(String payment_attachment) {
            this.payment_attachment = payment_attachment;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getRemarks() {
            return remarks;
        }

        public void setRemarks(String remarks) {
            this.remarks = remarks;
        }

        public String getCustomer_id() {
            return customer_id;
        }

        public void setCustomer_id(String customer_id) {
            this.customer_id = customer_id;
        }

        public String getCustomer_name() {
            return customer_name;
        }

        public void setCustomer_name(String customer_name) {
            this.customer_name = customer_name;
        }

        public String getPayment_date() {
            return payment_date;
        }

        public void setPayment_date(String payment_date) {
            this.payment_date = payment_date;
        }

        public String getPayment_time() {
            return payment_time;
        }

        public void setPayment_time(String payment_time) {
            this.payment_time = payment_time;
        }
    }
}
