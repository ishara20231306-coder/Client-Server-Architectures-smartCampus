package com.smartcampus.model;

import java.util.ArrayList;
import java.util.List;

public class Room {
    private String id;
    private String name;
    private int capacity;
    private List<Sensor> sensors = new ArrayList<>();

    public Room(String id, String name, int capacity){
        this.id = id;
        this.name = name;
        this.capacity = capacity;
    }

}
