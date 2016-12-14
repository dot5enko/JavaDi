package com.dot5enko.test;

import com.dot5enko.FileHelper;
import com.dot5enko.di.DependencyException;
import com.dot5enko.di.ServiceContainer;
import com.dot5enko.server.Server;
import com.dot5enko.server.protocols.http.HttpHandler;
import org.bson.Document;

/**
 *
 * @author serhio
 */
public class Main {

    public static void main(String[] args) throws DependencyException, Exception {
        
        Document config = Document.parse(FileHelper.getFile("config.json"));
        Document appConfig = config.get("app", Document.class);

        ServiceContainer sc = ServiceContainer
                .getInstance()
                .initializeWithConfig(config.get("di", Document.class));
        
        Server httpServer = new Server(config, () -> {
            return new HttpHandler(appConfig.getString("controllersPackage"));
        }, sc);

        httpServer.start();
    }
}
