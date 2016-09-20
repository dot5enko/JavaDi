package com.dot5enko.test;

import com.dot5enko.di.Resource;
import com.dot5enko.di.ServiceContainer;
import com.dot5enko.test.mockup.FormatHelper;
import com.dot5enko.test.mockup.Request;

/**
 *
 * @author serhio
 */
public class Main {

    public static void main(String[] args) {

        ServiceContainer sc = ServiceContainer.getInstance();
        
        sc.addResource(Request.class,new Resource());
        
        sc.addLazyResource(FormatHelper.class, () -> {
            return new FormatHelper();
        });
        
        
        
        try {
            
            System.out.println(sc.get(Request.class));
            
            FormatHelper fh = (FormatHelper)sc.get(FormatHelper.class);
            
            System.out.println(fh.toUpper("hello world"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
