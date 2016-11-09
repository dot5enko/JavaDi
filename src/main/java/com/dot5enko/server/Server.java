package com.dot5enko.server;

import com.dot5enko.di.ServiceContainer;
import java.net.ServerSocket;
import java.net.Socket;
import org.bson.Document;

public class Server implements Runnable {

    ServerOnStartEvent startCallback;

    Document options;
    ThreadInitializer initializer;
    protected ServiceContainer sc;

    public Server(Document opts,ThreadInitializer h, ServiceContainer services) {
        options = opts;
        initializer = h;
        sc = services;
    }

    public void start() {
        start(null);
    }

    public void start(ServerOnStartEvent callback) {
        startCallback = callback;
        Thread loopThread = new Thread(this);
        loopThread.start();
    }

    @Override
    public void run() {
        try {
            ServerSocket ss = new ServerSocket(options.getInteger("port"));
            System.out.println("Server started on port " + options.get("port"));
            if (startCallback != null) {
                try {
                    startCallback.fire();
                } catch (java.lang.Exception e) {
                    System.out.println(e.getMessage());
                }
            }
            while (true) {
                
                Socket conn = ss.accept();
                
                ConnectionHandler h = this.initializer.initialize();

                h.setSocket(conn);
                h.setServiceContainer(sc);

                // here could be services initialization
                new Thread(h).start();
            }
        } catch (java.lang.Exception e) {
            System.out.println("Cannot start server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
