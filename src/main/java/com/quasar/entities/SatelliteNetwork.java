package com.quasar.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;

public class SatelliteNetwork {

    @JsonProperty("satellites")
    private ArrayList<Satellite> satellites;

    public ArrayList<Satellite> getSatellites() { return satellites; }
    public void setSatellites(ArrayList<Satellite> value) { satellites = value; }

    public SatelliteNetwork()
    {
        setSatellites(new ArrayList<>());
    }
}