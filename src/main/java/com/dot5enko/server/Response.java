package com.dot5enko.server;

abstract public class Response {

    protected String content;

    public Response setContent(String c) {
        content = c;
        return this;
    }

    public String getRawResponse(){
        return content;
    }
}
