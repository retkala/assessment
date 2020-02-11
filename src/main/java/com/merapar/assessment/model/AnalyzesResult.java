package com.merapar.assessment.model;

import com.google.common.base.Objects;

import java.time.LocalDateTime;


public class AnalyzesResult {

    private LocalDateTime analyseDate;
    private AnalyzesDetails details;

    public AnalyzesResult() {
        details = new AnalyzesDetails();
    }


    public void setAnalyseDate(LocalDateTime analyseDate) {
        this.analyseDate = analyseDate;
    }

    public void setDetails(AnalyzesDetails details) {
        this.details = details;
    }

    public LocalDateTime getAnalyseDate() {
        return analyseDate;
    }

    public AnalyzesDetails getDetails() {
        return details;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AnalyzesResult)) return false;
        AnalyzesResult that = (AnalyzesResult) o;
        return Objects.equal(getAnalyseDate(), that.getAnalyseDate()) &&
                Objects.equal(getDetails(), that.getDetails());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getAnalyseDate(), getDetails());
    }

    @Override
    public String toString() {
        return "AnalyzesResult{" +
                "analyseDate=" + analyseDate +
                ", details=" + details +
                '}';
    }
}
