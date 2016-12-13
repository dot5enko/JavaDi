package com.dot5enko.server;

abstract public class Response {

    protected StringBuilder content = new StringBuilder();

    public Response setContent(String c) {
        content.setLength(0);
        content.append(c);
        return this;
    }
    
    public Response put(String c) {
        content.append(c);
        return this;
    }
    

    public String getRawResponse(){
        return content.toString();
    }
}
