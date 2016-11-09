package com.dot5enko.server.protocols.http;

import com.dot5enko.server.Response;

public class HttpResponse extends Response {
    
    @Override
    public String getRawResponse() {
        String value = "HTTP/1.1 200 OK\r\n"
                + "Server: TRGServer\r\n"
                + "Content-Type: text/html\r\n"
                + "Content-Length: " + this.content.length() + "\r\n"
                + "Connection: close\r\n\r\n";
        return value + this.content;
    }
}
