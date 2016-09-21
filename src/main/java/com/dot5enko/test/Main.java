package com.dot5enko.test;

import com.dot5enko.di.Instantiator;
import com.dot5enko.di.ServiceContainer;
import com.dot5enko.test.mockup.*;

/**
 *
 * @author serhio
 */
public class Main {

    public static void main(String[] args) {

        ServiceContainer sc = ServiceContainer.getInstance();

        sc.addResource(Request.class, new Request());

        sc.addLazyResource(FormatHelper.class, () -> {
            return new FormatHelper();
        });

        Instantiator manager = new Instantiator();

        try {
            
            // Controller injection example 
            IndexController controller = (IndexController) manager.instantiate(IndexController.class);
            
            // service container example
            System.out.println(controller.indexAction((Request)sc.get("com.dot5enko.test.mockup.Request")));
            
            // getter setter injection example
//            manager.invokeMethod(controller,"indexAction");
            

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
