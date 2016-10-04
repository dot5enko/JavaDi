package com.dot5enko.test;

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
        
        sc.initializeWithConfig("/Users/serhio/NetBeansProjects/di/src/main/java/com/dot5enko/test/config/services.json");

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
