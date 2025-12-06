package com.example.springdemo.service;

import org.springframework.stereotype.Service;

@Service
public class IpfsService {
    
    public String add(String content) throws Exception {
        // Stub implementation - returns a mock CID
        return "Qm" + System.currentTimeMillis();
    }
    
    public String get(String cid) throws Exception {
        // Stub implementation
        return "{}";
    }
}
