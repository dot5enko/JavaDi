package com.dot5enko.test;

import com.dot5enko.di.AutomaticResourceHandler;
import com.dot5enko.di.DependencyException;
import com.dot5enko.di.Instantiator;
import com.dot5enko.di.ServiceContainer;
import com.dot5enko.test.mockup.*;
import java.util.ArrayList;

/**
 *
 * @author serhio
 */
public class Main {

    public static void main(String[] args) throws DependencyException {

        ServiceContainer sc = ServiceContainer.getInstance();
        Instantiator manager = Instantiator.getInstance();

        // adding resources to service container
        sc.addLazyService("formatHelper", FormatHelper.class);
        sc.addLazyService("Mysql", MysqlDatabase.class);
        sc.addLazyService("db", PostgresDatabase.class);

        sc.addNotSharedService("Request", Request.class);

        sc.addService("logger", manager.instantiate(Logger.class));

        try {

            // Constructor injection example   
            IndexController controller = (IndexController) manager.instantiate(IndexController.class);

            ArrayList cabinetParams = new ArrayList<Object>();
            cabinetParams.add(new String("Sergiy"));

            // getter setter example
            System.out.println(manager.invokeMethod(controller, "cabinetAction", cabinetParams));

            Thread.sleep(1000);

            System.out.println(manager.invokeMethod(controller, "cabinetAction", cabinetParams));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
