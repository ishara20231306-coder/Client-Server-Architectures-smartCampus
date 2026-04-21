package com.smartcampus.mapper;

import com.smartcampus.exception.*;
import jakarta.ws.rs.core.*;
import jakarta.ws.rs.ext.*;

@Provider
public class RoomNotEmptyMapper implements ExceptionMapper<RoomNotEmptyException>{
    
    public Response toResponse(RoomNotEmptyException rne){
        
        return Response.status(409).entity(rne.getMessage()).build();
        
    }




}