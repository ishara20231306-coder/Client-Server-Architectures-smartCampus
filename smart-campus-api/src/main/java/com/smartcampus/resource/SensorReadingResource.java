package com.smartcampus.resource;

import java.util.ArrayList;
import java.util.List;

import com.smartcampus.exception.SensorUnavailableException;
import com.smartcampus.model.Reading;
import com.smartcampus.model.Room;
import com.smartcampus.model.Sensor;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@Path("/sensors/{sensorId}/readings")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)

public class SensorReadingResource {
    
    @POST
    public String addReading(@PathParam("sensorId")String sensorId, Reading reading){
        for(Room r : RoomResource.getRoomMap().values()){
            for(Sensor s : r.getSensors()){
                if(s.getId().equals(sensorId)){
                    if(s.getStatus().equals("MAINTENANCE")){
                        throw new SensorUnavailableException("Sensor Unavailable");
                    }

                    s.setCurrentValue(reading.getValue());
                    s.getReadings().add(reading);

                    return "Reading added";
                }
            }
        }
        return "Sensor not found";
    }

    @GET
    public List<Reading> getReadings(@PathParam("sensorId") String sensorId){
        for(Room r : RoomResource.getRoomMap().values()){
            for(Sensor s : r.getSensors()){


                if(s.getId().equals(sensorId)){
                    return s.getReadings();
                }
            }
        }

        return new ArrayList<>();
    }
}
