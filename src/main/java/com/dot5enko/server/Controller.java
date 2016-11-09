package com.dot5enko.server;

import com.dot5enko.di.ServiceContainer;
import java.net.Socket;

abstract public class Controller {

    protected Socket s;
    protected Request in;
    protected Response out;
    protected ServiceContainer services;

    public Controller(Socket s, Request in, Response out,ServiceContainer services) {
        this.s = s;
        this.in = in;
        this.out = out;
        this.services = services;
    }

}
