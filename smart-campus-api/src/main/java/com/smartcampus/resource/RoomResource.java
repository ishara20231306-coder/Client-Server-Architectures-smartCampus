package com.smartcampus.resource;

import com.smartcampus.model.Room;
import com.smartcampus.exception.RoomNotEmptyException;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.*;

@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)

public class RoomResource {
    private static Map<String, Room> rooms = new HashMap<>();

    public static Map<String, Room> getRoomMap(){
        return rooms;
    }

    @GET
    public Collection<Room> getAll(){
        return rooms.values();
    }

    @POST
    public Room addRoom(Room room) {

    System.out.println("POST CALLED");
    System.out.println("ROOM ID = " + room.getId());
    System.out.println("ROOM NAME = " + room.getName());

    rooms.put(room.getId(), room);

    System.out.println("MAP SIZE = " + rooms.size());

    return room;
    }

    @GET
    @Path("/{id}")
    public Room getRoom(@PathParam("id") String id){
        return rooms.get(id);
    }

    @DELETE
    @Path("/{id}")
    public String deleteRoom(@PathParam("id")String id){
        Room room = rooms.get(id);
        if(room == null)
            return "Room not found";
        if(!room.getSensors().isEmpty()){
            throw new RoomNotEmptyException("Room has sensors");
        }

        rooms.remove(id);
        return "Deleted";
    }

    

    
}
