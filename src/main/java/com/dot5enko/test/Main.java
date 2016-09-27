package com.dot5enko.test;

import com.dot5enko.di.AutomaticResourceHandler;
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
        Instantiator manager = Instantiator.getInstance();

        
        // adding resources to service container
        sc.addService("Request", new Request());

        sc.addLazyService("formatHelper", () -> {
            return new FormatHelper();
        },FormatHelper.class);
        
        sc.addLazyService("Mysql", () -> {
            return new MysqlDatabase();
        },MysqlDatabase.class);
        
        sc.addLazyService("db", () -> {
            return new PostgresDatabase();
        },PostgresDatabase.class);
        
        sc.addNotSharedService("different_logger",new AutomaticResourceHandler(Logger.class),Logger.class);
        

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
