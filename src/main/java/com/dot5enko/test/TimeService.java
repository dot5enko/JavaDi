package com.dot5enko.test;

import com.dot5enko.di.annotation.Service;

@Service("time")
public class TimeService {

    public long currentTime() {
        return System.currentTimeMillis();
    }
}
