package com.dot5enko.server.protocols.http;

import com.dot5enko.server.ConnectionHandler;
import com.dot5enko.server.Request;
import com.dot5enko.server.Response;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

abstract public class HttpHandler extends ConnectionHandler {

    protected abstract Response action(Request req);

    protected Request getRequest() throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        try {
            while (true) {
                String sLine = br.readLine();
                if (sLine == null || sLine.trim().length() == 0) {
                    break;
                }
                sb.append(sLine + "\n");
            }
            return new HttpRequest(sb.toString());
        } catch (java.lang.Exception e) {
            throw new Exception("Can't parse request: " + e.getMessage());
        }
    }
}
