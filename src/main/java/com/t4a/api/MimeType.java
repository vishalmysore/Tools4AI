package com.t4a.api;

public enum MimeType {
    JPEG("image/jpeg"),
    PNG("image/png"),
    GIF("image/gif"),
    HTML("text/html"),
    TEXT("text/plain"),
    PDF("application/pdf"),
    MS_WORD("application/msword"),
    OPEN_DOCUMENT_TEXT("application/vnd.oasis.opendocument.text");

    private final String mimeType;

    MimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getMimeType() {
        return mimeType;
    }
}
