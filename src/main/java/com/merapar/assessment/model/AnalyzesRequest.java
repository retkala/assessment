package com.merapar.assessment.model;

public class AnalyzesRequest {
    private String url;

    public AnalyzesRequest() {}

    public AnalyzesRequest(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
