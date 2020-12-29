package com.flitzen.adityarealestate_new;

public class FirebaseConstant {
    public static class autoID{
        public static String Auto_ID_TABLE="Auto_ID";

        public static String App_Number="App_Number";
        public static String id="id";
    }

    public static class customers{
        public static String Customers_TABLE="Customers";

        public static String Age="Age";
        public static String Applicant_Name="Applicant_Name";
        public static String Birth_Date="Birth_Date";
        public static String Business_Address="Business_Address";
        public static String Business_Latitude="Business_Latitude";
        public static String Business_Longitude="Business_Longitude";
        public static String Business_Name="Business_Name";
        public static String Business_On_Rent="Business_On_Rent";
        public static String Business_Photo="Business_Photo";
        public static String Business_Photo_2="Business_Photo_2";
        public static String Business_Photo_3="Business_Photo_3";
        public static String Business_Staff="Business_Staff";
        public static String Business_Turnover="Business_Turnover";
        public static String Bussiness_Tax_Return="Bussiness_Tax_Return";
        public static String Company_Address="Company_Address";
        public static String Company_Name="Company_Name";
        public static String Current_Loan="Current_Loan";
        public static String Current_Loan_Details="Current_Loan_Details";
        public static String Date="Date";
        public static String FCM_ID="FCM_ID";
        public static String Family_Details="Family_Details";
        public static String Latitude="Latitude";
        public static String Longitude="Longitude";
        public static String Monthly_Income="Monthly_Income";
        public static String Occupation_Type="Occupation_Type";
        public static String Password="Password";
        public static String Primary_Number="Primary_Number";
        public static String Residence_Photo="Residence_Photo";
        public static String Residence_Photo_2="Residence_Photo_2";
        public static String Residence_Photo_3="Residence_Photo_3";
        public static String Residence_Purchase_Year="Residence_Purchase_Year";
        public static String Residence_Rent="Residence_Rent";
        public static String Residence_Rent_Amount="Residence_Rent_Amount";
        public static String Residence_Rent_Year="Residence_Rent_Year";
        public static String Residence_Type="Residence_Type";
        public static String Secondary_Numbers="Secondary_Numbers";
        public static String Type="Type";
        public static String Vehicle_Detail="Vehicle_Detail";
        public static String address="address";
        public static String city="city";
        public static String contact_no="contact_no";
        public static String contact_no1="contact_no1";
        public static String email="email";
        public static String id="id";
        public static String kyc_aadhar_back="kyc_aadhar_back";
        public static String kyc_aadhar="kyc_aadhar";
        public static String kyc_driving_lic="kyc_driving_lic";
        public static String kyc_other="kyc_other";
        public static String kyc_pancard="kyc_pancard";
        public static String kyc_ration="kyc_ration";
        public static String name="name";
        public static String status="status";
    }

    public static class EMI_Received{
        public static String EMI_Received_TABLE="EMI_Received";

        public static String Customer_Id="Customer_Id";
        public static String EMI_Amount="EMI_Amount";
        public static String EMI_Date="EMI_Date";
        public static String Loan_Id="Loan_Id";
        public static String Loan_Remarks="Loan_Remarks";
        public static String Loan_Type="Loan_Type";
        public static String Type="Type";
        public static String id="id";
    }

    public static class Light_bill{
        public static String Light_bill_TABLE="Light_bill";

        public static String Bill_Rs="Bill_Rs";
        public static String Bill_id="Bill_id";
        public static String Bill_month="Bill_month";
        public static String Bill_photo="Bill_photo";
        public static String Other_Notes="Other_Notes";
        public static String Properties_id="Properties_id";
        public static String created_date="created_date";
    }

    public static class LoanDetails{
        public static String LoanDetails_TABLE="LoanDetails";

        public static String Applicantion_Number="Applicantion_Number";
        public static String Approved_Amount="Approved_Amount";
        public static String Approved_Date="Approved_Date";
        public static String Customer_Id="Customer_Id";
        public static String Date_Applied="Date_Applied";
        public static String Interest_Rate="Interest_Rate";
        public static String Loan_Amount="Loan_Amount";
        public static String Loan_Status="Loan_Status";
        public static String Loan_Tenure="Loan_Tenure";
        public static String Loan_Type="Loan_Type";
        public static String Monthly_EMI="Monthly_EMI";
        public static String Original_Amount="Original_Amount";
        public static String Pay_EMI_Date="Pay_EMI_Date";
        public static String Payable_Amount="Payable_Amount";
        public static String Reason_For_Loan="Reason_For_Loan";
        public static String Reject_Remarks="Reject_Remarks";
        public static String id="id";
    }

    public static class Payments{
        public static String Payments_TABLE="Payments";

        public static String amount="amount";
        public static String customer_id="customer_id";
        public static String customer_status="customer_status";
        public static String id="id";
        public static String next_payment_date="next_payment_date";
        public static String payment_attachment="payment_attachment";
        public static String payment_date="payment_date";
        public static String payment_status="payment_status";
        public static String plot_id="plot_id";
        public static String property_id="property_id";
        public static String remarks="remarks";
        public static String rent_status="rent_status";
    }

    public static class Plots{
        public static String Plots_TABLE="Plots";

        public static String customer_id="customer_id";
        public static String date_of_purchase="date_of_purchase";
        public static String id="id";
        public static String plot_no="plot_no";
        public static String purchase_price="purchase_price";
        public static String site_id="site_id";
        public static String size="size";
    }

    public static class Properties{
        public static String Properties_TABLE="Properties";

        public static String address="address";
        public static String customer_id="customer_id";
        public static String hired_since="hired_since";
        public static String id="id";
        public static String property_name="property_name";
        public static String rent="rent";
    }

    public static class Property_History{
        public static String Property_History_TABLE="Property_History";

        public static String customer_id="customer_id";
        public static String hired_end="hired_end";
        public static String hired_since="hired_since";
        public static String id="id";
        public static String property_id="property_id";
        public static String remarks="remarks";
        public static String rent="rent";
    }

    public static class RantDocument{
        public static String RantDocument_TABLE="RantDocument";

        public static String created_at="created_at";
        public static String customer_id="customer_id";
        public static String document="document";
        public static String id="id";
        public static String p_id="p_id";
        public static String status="status";
    }

    public static class SitePayments{
        public static String SitePayments_TABLE="SitePayments";

        public static String amount="amount";
        public static String id="id";
        public static String payment_date="payment_date";
        public static String remarks="remarks";
        public static String site_id="site_id";
    }

    public static class Site_Receive_Payment{
        public static String Site_Receive_Payment_TABLE="Site_Receive_Payment";

        public static String amount="amount";
        public static String id="id";
        public static String payment_date="payment_date";
        public static String remarks="remarks";
        public static String site_id="site_id";
    }

    public static class Sites{
        public static String Sites_TABLE="Sites";

        public static String file="file";
        public static String id="id";
        public static String purchase_price="purchase_price";
        public static String site_address="site_address";
        public static String site_name="site_name";
        public static String size="size";
        public static String status="status";
    }

    public static class Transacation_Customers{
        public static String Transacation_Customers_TABLE="Transacation_Customers";

        public static String address="address";
        public static String city="city";
        public static String contact_no="contact_no";
        public static String contact_no1="contact_no1";
        public static String email="email";
        public static String id="id";
        public static String name="name";
        public static String status="status";
    }

    public static class Transactions{
        public static String Transactions_TABLE="Transactions";

        public static String amount="amount";
        public static String created_at="created_at";
        public static String customer_id="customer_id";
        public static String last_updated="last_updated";
        public static String payment_type="payment_type";
        public static String transaction_date="transaction_date";
        public static String transaction_id="transaction_id";
        public static String transaction_note="transaction_note";

    }
}
