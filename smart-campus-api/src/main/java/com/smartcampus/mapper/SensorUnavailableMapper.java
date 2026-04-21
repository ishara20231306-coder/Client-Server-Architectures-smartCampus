package com.smartcampus.mapper;

import com.smartcampus.exception.SensorUnavailableException;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

public class SensorUnavailableMapper implements ExceptionMapper<SensorUnavailableException>{
    
    public Response toResponse(SensorUnavailableException sue){
        return Response.status(403).entity(sue.getMessage()).build();
    }
}
