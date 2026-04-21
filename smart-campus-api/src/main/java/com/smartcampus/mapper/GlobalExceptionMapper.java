package com.smartcampus.mapper;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

public class GlobalExceptionMapper implements ExceptionMapper<Throwable>{
    
    public Response toResponse(Throwable ge){
        return Response.status(500).entity(ge.getMessage()).build();
    }
    
}
