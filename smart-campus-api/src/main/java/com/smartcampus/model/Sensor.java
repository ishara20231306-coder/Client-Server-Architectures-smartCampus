package com.smartcampus.model;

import java.util.ArrayList;
import java.util.List;

public class Sensor{
    private String id;
    private String type;
    private String status = "ACTIVE";
    private double currentValue;
    private List<Reading> readings = new ArrayList<>();



    public Sensor(String id, String type) {
        this.id = id;
        this.type = type;
    }

    public String getId() { 
        return id; 
    }
    public void setId(String id) { 
        this.id = id; 
    }

    public String getType() { 
        return type; 
    }
    public void setType(String type) { 
        this.type = type; 
    }

    public double getCurrentValue() { 
        return currentValue; 
    }
    public void setCurrentValue(double currentValue) { 
        this.currentValue = currentValue; 
    }

    public String getStatus() { 
        return status; 
    }
    public void setStatus(String status) { 
        this.status = status; 
    }

    public List<Reading> getReadings() { 
        return readings; 
    }
    public void setReadings(List<Reading> readings) { 
        this.readings = readings; 
    }

}
