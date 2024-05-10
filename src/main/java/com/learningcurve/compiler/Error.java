package com.learningcurve.compiler;

public class Error {
    private String errorMessage;
    private String lineContent;
    private Integer lineNumber;

    public Error(String errorMessage, String lineContent, Integer lineNumber) {
        this.errorMessage = errorMessage;
        this.lineContent = lineContent;
        this.lineNumber = lineNumber;
    }

    // Getter para errorMessage
    public String getErrorMessage() {
        return errorMessage;
    }

    // Getter para lineContent
    public String getLineContent() {
        return lineContent;
    }

    // Getter para lineNumber
    public Integer getLineNumber() {
        return lineNumber;
    }
}