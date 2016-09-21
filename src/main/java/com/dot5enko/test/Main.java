package com.dot5enko.test;

import com.dot5enko.di.Instantiator;
import com.dot5enko.di.ServiceContainer;
import com.dot5enko.test.mockup.*;
import java.util.ArrayList;

/**
 *
 * @author serhio
 */
public class Main {

    public static void main(String[] args) {

        ServiceContainer sc = ServiceContainer.getInstance();
        Instantiator manager = new Instantiator();

        sc.addResource(Request.class, new Request());

        sc.addLazyResource(FormatHelper.class, () -> {
            return new FormatHelper();
        });

        try {

            // Controller injection example 
            IndexController controller = (IndexController) manager.instantiate(IndexController.class);

            // service container example
            System.out.println("\nIndexController.indexAction manual");
            System.out.println(controller.indexAction((Request) sc.get("com.dot5enko.test.mockup.Request")));


            // getter setter injection example
            System.out.println("\nIndexController.indexAction:");
            System.out.println(manager.invokeMethod(controller,"indexAction",Request.class));
            
            // example with hybrid injection
            ArrayList cabinetParams = new ArrayList<Object>();
            cabinetParams.add(new String("Sergiy"));
            
            System.out.println("\nIndexController.cabinetAction:");
            System.out.println(manager.invokeMethod(controller,"cabinetAction",cabinetParams,Request.class,String.class));
            

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
