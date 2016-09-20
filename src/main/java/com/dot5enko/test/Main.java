package com.dot5enko.test;

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

        sc.addLazyResource(FormatHelper.class, () -> {
            return new FormatHelper();
        });
        
        sc.addResource(Request.class, args);
        
        try {
            FormatHelper fh = (FormatHelper)sc.get(FormatHelper.class);
            
            System.out.println(fh.toUpper("hello world"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
