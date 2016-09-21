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

        
        // adding resources to service container
        
        sc.addResource(Request.class, new Request());

        sc.addLazyResource(FormatHelper.class, () -> {
            return new FormatHelper();
        });
        
        sc.addLazyResource(MysqlDatabase.class, () -> {
            return new MysqlDatabase();
        });
        
        sc.addLazyResource(PostgresDatabase.class, () -> {
            return new PostgresDatabase();
        });
        
        sc.addLazyResource(Logger.class,() -> {
            return manager.instantiate(Logger.class);
        });
        

        try {

            // Constructor injection example   
            IndexController controller = (IndexController) manager.instantiate(IndexController.class);

            
            ArrayList cabinetParams = new ArrayList<Object>();
            cabinetParams.add(new String("Sergiy"));
            
            // getter setter example
            System.out.println(manager.invokeMethod(controller,"cabinetAction",cabinetParams));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
