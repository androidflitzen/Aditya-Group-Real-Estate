package com.flitzen.adityarealestate_new.service;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PDFGETMODEL {
    @SerializedName("result")
    @Expose
    private Integer result;
    @SerializedName("file")
    @Expose
    private String file;

    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

}
