package com.smartcampus.model;

public class Reading {

    private double value;


    public Reading(){
        
    }

    public Reading(double value) {
        this.value = value;
    }

    public double getValue() { 
        return value; 
    }
    public void setValue(double value) { 
        this.value = value; 
    }
}
