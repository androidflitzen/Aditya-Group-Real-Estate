package com.flitzen.adityarealestate_new.Classes;

public class API {
    //public static String baseUrl = "http://jaris.in/ashirvad/android/";
    public static String baseUrl = "http://json.jaris.in/ASHRIVAD/android/";
    public static String login = "App-Login.php?email=%s&password=%s";

    public static String SITES = baseUrl + "Sites.php";
    public static String DEACTIVESITES = baseUrl + "Sites.php";
    public static String TODODEACTIVESITES = baseUrl + "Deactivate-Site.php?";
    public static String TODOACTIVESITES = baseUrl + "Activate-Site.php?";
    public static String TODELETEPLOT = baseUrl + "Delete-plot.php?";

    public static String EDIT_SITE = baseUrl + "Edit-Site.php";
    public static String SITE_PAYMENT = baseUrl + "Site_Payments.php";
    public static String DELETE_SITE_PAYMENT = baseUrl + "Delete-Site-Payments.php";
    public static String PLOTS = baseUrl + "Plots.php";
    public static String PROPERTY = baseUrl + "Properties.php";
    public static String PAYMENTS = baseUrl + "Payments.php";
    public static String PAYMENTS_DELETE = baseUrl + "Delete-Payments.php";
    public static String CUSTOMER = baseUrl + "Customers.php";
    public static String DEACTIVE_CUSTOMER = baseUrl + "Deleted-Customers.php";
    public static String EDIT_CUSTOMER = baseUrl + "Edit-Customer.php";
    public static String CUSTOMER_DELETE = baseUrl + "Delete-Customer.php";
    public static String UPLOAD_KYC = baseUrl + "Upload-kyc.php";
    public static String ASSIGN_CUSTOMER = baseUrl + "Assign-Customer.php";
    public static String UNASSIGN_CUSTOMER = baseUrl + "Unassign-Customer.php";
    public static String UNASSIGN_CUSTOMER_PLOT = baseUrl + "Unassign-Customer-Plot.php";
    public static String REMOVE_CUSTOMER_PLOT = baseUrl + "Empty-Plot.php";
    public static String PLOT_DETAILS = baseUrl + "Plots-Details.php";
    public static String PROPERTY_DETAILS = baseUrl + "Properties-Details.php";
    public static String APP_VERSION = baseUrl + "App-version.php";
    public static String TASKS = baseUrl + "Tasks.php";


    public static String GET_ALL_APPLICANTS = baseUrl + "Get_All_Applicants.php";
    public static String VIEW_PAYMENT = baseUrl + "Loan_EMI_Payment.php";
    public static String EMI_PAYMENT = baseUrl + "Loan_EMI_Payment.php";
    public static String ADD_LOAN_AND_USER = baseUrl + "Admin-AddFile.php";
    public static String GET_TOTAL_LOAN_COUNT = baseUrl + "Admin-Dashboard.php";
    public static String CLOSE_LOAN = baseUrl + "Apply_Loan.php";
    public static String DELETE_LOAN = baseUrl + "Get_All_Applicants.php";
    public static String DELETE_EMI = baseUrl + "Loan_EMI_Payment.php";
    public static String ADD_SITE_FILE = baseUrl + "Site_Payments.php?type=Add_file";
    public static String DELETE_SITE_FILE = baseUrl + "Site_Payments.php?type=delete_file";
    public static String EDIT_PLOT_DETAIL = baseUrl + "Plots.php?type=edit";
    public static String DELETE_PAYMENT = baseUrl + "Delete-Payments.php";
    public static String UPDATE_PAYMENT = baseUrl + "Edit-Payments.php";
    public static String DELETE_PROPERTY = baseUrl + "Properties.php?type=Delete";
    public static String UPDATE_PROPERTY = baseUrl + "Properties.php?type=Edit";
    public static String CUSTOMER_BILL = baseUrl + "Light-Bill.php";
    public static String PRINT_RENT_PAYMENT_URL = baseUrl + "Print-statement.php?property_id=";
    public static String PRINT_PLOT_PAYMENT_URL = baseUrl + "Print-plot-statement.php?plot_id=";
    public static String PRINT_LOAN_PAYMENT_URL = baseUrl + "Print-loan-statement.php?loan_id=";
    public static String SITE_RECEIVE_PAYMENT = baseUrl + "Site_Received_Payments.php";
    public static String SITE_ADD_RECEIVE_PAYMENT = baseUrl + "Site_Received_Payments.php";
    public static String PRINT_SITE_PAYMENT_URL = baseUrl + "Print-site-payment.php?site_id=";
    public static String PRINT_SITE_RECEIVE_PAYMENT_URL = baseUrl + "Print-received-payment.php?site_id=";
    public static String DELETE_RECEIVE_SITE_PAYMENT = baseUrl + "Site_Received_Payments.php";
    public static String PAYMENT_HISTORY_CUSTOMER_URL = baseUrl + "Payment-history-customers.php?property_id=";
    public static String PAYMENT_HISTORY_CUSTOMERS = baseUrl + "Payment-history-customers_transaction.php?";
    public static String DELETE_FINAL_CUSTOMER = baseUrl + "Final-Delete-Customer.php?";

    public static String ADD_TRANSACTION_PAYMENT = baseUrl + "Add-Transaction.php?";
    //public static String All_TRANSACTION=baseUrl+"Transactions.php";

    public static String All_TRANSACTION = baseUrl + "Transactions-all.php";
    public static String All_TRANSACTION_PLOT = baseUrl + "Plote-all.php";

    public static String TRANSACTION_DETAILSPAYMENT = baseUrl + "Transactions-customerwise_payment.php?";
    public static String TRANSACTION_DETAILSRECEIVED = baseUrl + "Transactions-customerwise.php?";


    public static String DELETE_TRANSCATION = baseUrl + "Delete-Transaction.php";
    public static String EDIT_TRANSACTION_PAYMENT = baseUrl + "Edit-Transaction.php?";

    public static String TRANSACTION_CUSTOMERLIST = baseUrl + "Customers.php?";

    public static String TRANSACTION_BOTHPDF = baseUrl + "Print-transaction-all.php?";
    public static String TRANSACTION_RECEIVEDPDF = baseUrl + "Print-transaction.php?";
    public static String TRANSACTION_PAYMENTPDF = baseUrl + "Print-transaction-payment.php?";
    public static String TRANSACTION_DEACTIVECUSTOMERLIST = baseUrl + "Transactions-deactivated.php";
    public static String TRANSACTION_ACTIVECUSTOMERLIST = baseUrl + "Transactions.php";

    public static String TRANSACTION_DEACTIVEDELETECUSTOMER = baseUrl + "Delete-Customer-Transaction.php?";
    public static String REACTIVETED_CUSTOMER = baseUrl + "Activate-Customer-Transaction.php?";

    public static String DEACTIVE_TRANSCUSTOMER = baseUrl + "Deactive-Customer-Transaction.php?";

    public static String ADD_TRANSACTION_CUSTOMERLIST = baseUrl + "Transcation-customers-add.php?";
    public static String TRANS_EDITPROFILE = baseUrl + "Transcation-customers-edit.php?";

    public static String RENTHISTRYDEAVTIVE_DELETE = baseUrl + "Delete-Payment-History.php";

    public static String RENTDEACTIVECUSTOMER_PDFFILE = baseUrl + "Print-deactivated-transaction.php?";


}
