package com.dot5enko.test;

import com.dot5enko.di.DependencyInjector;
import com.dot5enko.test.mockup.FormatHelper;

/**
 *
 * @author serhio
 */
public class Main {

    public static void main(String[] args) {

        DependencyInjector di = new DependencyInjector();

        di.addLazyResource(FormatHelper.class, () -> {
            return new FormatHelper();
        });
        
        try {
            FormatHelper fh = (FormatHelper)di.get(FormatHelper.class);
            
            System.out.println(fh.toUpper("hello world"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
