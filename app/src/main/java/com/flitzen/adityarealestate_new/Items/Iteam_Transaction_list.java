package com.flitzen.adityarealestate_new.Items;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Iteam_Transaction_list {

    @SerializedName("result")
    @Expose
    private Integer result;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("transcations")
    @Expose
    private List<Transcation> transcations = null;

    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Transcation> getTranscations() {
        return transcations;
    }

    public void setTranscations(List<Transcation> transcations) {
        this.transcations = transcations;
    }

    public class Transcation {

        @SerializedName("transaction_id")
        @Expose
        private String transactionId;
        @SerializedName("customer_id")
        @Expose
        private String customerId;
        @SerializedName("customer_name")
        @Expose
        private String customerName;
        @SerializedName("payment_type")
        @Expose
        private String paymentType;
        @SerializedName("transaction_date")
        @Expose
        private String transactionDate;
        @SerializedName("amount")
        @Expose
        private String amount;
        @SerializedName("transaction_note")
        @Expose
        private String transactionNote;

        public String getTransactionId() {
            return transactionId;
        }

        public void setTransactionId(String transactionId) {
            this.transactionId = transactionId;
        }

        public String getCustomerId() {
            return customerId;
        }

        public void setCustomerId(String customerId) {
            this.customerId = customerId;
        }

        public String getCustomerName() {
            return customerName;
        }

        public void setCustomerName(String customerName) {
            this.customerName = customerName;
        }

        public String getPaymentType() {
            return paymentType;
        }

        public void setPaymentType(String paymentType) {
            this.paymentType = paymentType;
        }

        public String getTransactionDate() {
            return transactionDate;
        }

        public void setTransactionDate(String transactionDate) {
            this.transactionDate = transactionDate;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getTransactionNote() {
            return transactionNote;
        }

        public void setTransactionNote(String transactionNote) {
            this.transactionNote = transactionNote;
        }

    }
}
