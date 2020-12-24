package com.flitzen.adityarealestate_new.Classes;

import com.flitzen.adityarealestate_new.CommonModel;
import com.flitzen.adityarealestate_new.Items.Iteam_Transaction_list;
import com.flitzen.adityarealestate_new.UploadPdfModel;
import com.flitzen.adityarealestate_new.service.PDFGETMODEL;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface WebAPI {

    public static String BASE_URL1 = "http://json.jaris.in/ASHRIVAD/android/";

    @GET("Transactions.php")
    Call<Iteam_Transaction_list> getalltransaction();

    @FormUrlEncoded
    @POST("ASHRIVAD/android/Upload-pdf.php")
    Call<UploadPdfModel> Uploadpdf(@Field("p_id") String p_id, @Field("customer_id") String customer_id, @Field("pdf_document") String pdf_document);


    @GET("ASHRIVAD/android/Delete-Rant_documents.php")
    Call<CommonModel> delete(@Query("id") String id);


    @GET("ASHRIVAD/android/Delete-plot_2.php")
    Call<CommonModel> delete_plot(@Query("p_id") String p_id);

    @GET("ASHRIVAD/android/Print-deactivated-transaction.php")
    Call<PDFGETMODEL> Printdeactivated(@Query("property_id") String property_id, @Query("customer_id") String customer_id);


    @GET("ASHRIVAD/android/Print-plot-statement.php")
    Call<PDFGETMODEL> Printplot(@Query("plot_id") String plot_id);
}
