package com.dot5enko.test.mockup;

import com.dot5enko.di.annotation.Service;
import com.dot5enko.di.annotation.service.Lazy;
import com.dot5enko.di.annotation.service.Shared;
import java.util.Date;

@Service
@Shared(false)
public class Request {
    
    private Date rTime;
    private String remoteAddr = "127.0.0.1";
    private String userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.116 Safari/537.36 OPR/39.0.2256.71";
    
    public Request() {
        this.rTime = new Date();
    }
    
    public Date getRequestTime() {
        return this.rTime;
    }
    
    public String getUserAgent() {
        return this.userAgent;
    }
    
    public String getRemoteAdress() {
        return this.remoteAddr;
    }
    
    
}
