package com.example.userauth.config;

import net.spy.memcached.MemcachedClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;

@Configuration
public class MemcachedConfig {

    @Value("${memcached.server:localhost:11211}")
    private String memcachedServer;

    @Bean
    public MemcachedClient memcachedClient() throws Exception {
        String[] parts = memcachedServer.split(":");
        String host = parts[0];
        int port = Integer.parseInt(parts[1]);
        return new MemcachedClient(new InetSocketAddress(host, port));
    }
}
