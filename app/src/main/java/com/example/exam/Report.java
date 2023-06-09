package com.example.exam;


public class Report {
    private String reportId;
    private String title;
    private String content;

    public Report() {
    }

    public Report(String reportId, String title, String content) {
        this.reportId = reportId;
        this.title = title;
        this.content = content;
    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
