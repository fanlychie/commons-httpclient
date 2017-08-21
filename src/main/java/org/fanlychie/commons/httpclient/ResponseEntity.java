package org.fanlychie.commons.httpclient;

/**
 * Created by fanzyun on 2017/8/21.
 */
public class ResponseEntity {

    private int statusCode;

    private String content;

    public ResponseEntity() {

    }

    public ResponseEntity(int statusCode, String content) {
        this.statusCode = statusCode;
        this.content = content;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}