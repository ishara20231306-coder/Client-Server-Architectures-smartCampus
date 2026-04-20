package com.smartcampus.resource;

import com.smartcampus.model.*;
import com.smartcampus.exception.LinkedResourceNotFoundException;

import jakarta.validation.constraints.Positive;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.*;

@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)

public class SensorResource {
    @POST
    public String addSensor(@QueryParam("roomId")String roomId, Sensor sensor){
        Room room = RoomResource.getRoomMap().get(roomId);

        if(room == null){
            throw new LinkedResourceNotFoundException("Room not found");
        }

        room.getSensors().add(sensor);
        return "Sensor added";
    }

    @GET
    public List<Sensor> getSensors(@QueryParam("type")String type){
        List<Sensor> result = new ArrayList<>();

        for(Room r : RoomResource.getRoomMap().values()){
            for(Sensor s : r.getSensors()){


                if(type == null|| s.getType().equals(type)){
                    result.add(s);
                }
            }
        }

        return result;
    }
}
