package com.dot5enko.server;

abstract public class Request {

    String request;

    public Request(String request) throws Exception {
        this.request = request;
    }

    public String getRaw() {
        return this.request;
    }
}
