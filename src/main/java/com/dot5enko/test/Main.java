package com.dot5enko.test;

import com.dot5enko.FileHelper;
import com.dot5enko.di.DependencyException;
import com.dot5enko.di.Instantiator;
import com.dot5enko.di.ServiceContainer;
import com.dot5enko.server.Response;
import com.dot5enko.server.Server;
import com.dot5enko.server.protocols.http.HttpHandler;
import com.dot5enko.server.protocols.http.HttpResponse;
import com.dot5enko.test.mockup.*;
import java.util.ArrayList;
import org.bson.Document;

/**
 *
 * @author serhio
 */
public class Main {

    public static void main(String[] args) throws DependencyException {

        ServiceContainer sc = ServiceContainer.getInstance();
        Instantiator manager = Instantiator.getInstance();

        Document config = Document.parse(FileHelper.getFile("config.json"));

        sc.initializeWithConfig(config.get("di", Document.class));

        Server httpServer = new Server(config, () -> {
            return new HttpHandler() {
                @Override
                protected Response action(com.dot5enko.server.Request req) {
                    return new HttpResponse().setContent("hello world");
                }
            };
        }, sc);
        httpServer.start(() -> {
            System.out.println("server started");
        });

        try {

            // Constructor injection example   
            IndexController controller = (IndexController) manager.instantiate(IndexController.class);

            ArrayList cabinetParams = new ArrayList<Object>();
            cabinetParams.add(new String("Sergiy"));

            // getter setter example
            System.out.println(manager.invokeMethod(controller, "cabinetAction", cabinetParams));

            Thread.sleep(2000);

            System.out.println(manager.invokeMethod(controller, "cabinetAction", cabinetParams));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
