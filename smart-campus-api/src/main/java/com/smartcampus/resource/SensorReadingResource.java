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
    public String addReading(@PathParam("sensorId") String sensorId, Reading reading) {

        for (Room r : RoomResource.getRoomMap().values()) {

            if (r.getSensors() == null) continue;

            for (Sensor s : r.getSensors()) {

                if (s.getId() == null) continue;

                if (s.getId().equals(sensorId)) {

                    if ("MAINTENANCE".equalsIgnoreCase(s.getStatus())) {
                        throw new SensorUnavailableException("Sensor Unavailable");
                    }

                    if (s.getReadings() == null) {
                        s.setReadings(new ArrayList<>());
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
    public List<Reading> getReadings(@PathParam("sensorId") String sensorId) {

        for (Room r : RoomResource.getRoomMap().values()) {

            if (r.getSensors() == null) continue;

            for (Sensor s : r.getSensors()) {

                if (s.getId() == null) continue;

                if (s.getId().equals(sensorId)) {

                    if (s.getReadings() == null) {
                        return new ArrayList<>();
                    }

                    return s.getReadings();
                }
            }
        }

        return new ArrayList<>();
    }
}
