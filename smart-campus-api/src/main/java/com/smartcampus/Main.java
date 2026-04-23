package com.smartcampus;

import java.net.URI;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import com.smartcampus.mapper.GlobalExceptionMapper;
import com.smartcampus.mapper.LinkedResourceNotFoundMapper;
import com.smartcampus.mapper.RoomNotEmptyMapper;
import com.smartcampus.mapper.SensorUnavailableMapper;
import com.smartcampus.resource.RoomResource;
import com.smartcampus.resource.SensorReadingResource;
import com.smartcampus.resource.SensorResource;

public class Main {
    public static void main(String[] args) {
        ResourceConfig config = ResourceConfig.forApplication(new AppConfig())
        .register(RoomResource.class)
        .register(SensorResource.class)
        .register(SensorReadingResource.class)
        .register(RoomNotEmptyMapper.class)
        .register(LinkedResourceNotFoundMapper.class)
        .register(SensorUnavailableMapper.class)
        .register(GlobalExceptionMapper.class);


        HttpServer hs = GrizzlyHttpServerFactory.createHttpServer(URI.create("http://localhost:8081/"), config);
        String server = "http://localhost:8081";

        System.out.println("Server is running at " + server);

        try {
            if (java.awt.Desktop.isDesktopSupported() && java.awt.Desktop.getDesktop().isSupported(java.awt.Desktop.Action.BROWSE)) {
                java.awt.Desktop.getDesktop().browse(new URI(server));
            }
    } catch (Exception e) {
        e.printStackTrace();
    }
    }
}