package com.smartcampus.mapper;

import com.smartcampus.exception.LinkedResourceNotFoundException;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

public class LinkedResourceNotFoundMapper implements ExceptionMapper<LinkedResourceNotFoundException>{

    public Response toResponse(LinkedResourceNotFoundException lrm){
        return Response.status(422).entity(lrm.getMessage()).build();
    }
    
}
