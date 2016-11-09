package com.dot5enko.server;

import com.dot5enko.di.ServiceContainer;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

abstract public class ConnectionHandler implements Runnable {
    
    protected String uid;
    protected Socket s;
    protected InputStream is;
    protected OutputStream os;
    protected ServiceContainer services;

    protected abstract Request getRequest() throws Exception;

    protected abstract Response action(Request req);
    
    public void setSocket(Socket s) throws IOException {
        this.s = s;
        is = s.getInputStream();
        os = s.getOutputStream();
    }

    @Override
    public void run() {
        try {
            writeResponse(action(getRequest()));
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            try {
                s.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    protected void writeResponse(Response response) throws Throwable {
        os.write(response.getRawResponse().getBytes());
        os.flush();
    }

    protected ConnectionHandler setServiceContainer(ServiceContainer sc) {
        services = sc;
        return this;
    }
}
