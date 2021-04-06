package com.quasar.services;

import com.quasar.entities.Position;
import com.quasar.entities.Satellite;
import com.quasar.entities.Response;
import org.json.JSONException;
import java.io.IOException;
import java.util.ArrayList;

public interface ISatelliteService {

    Position getShipPosition(Float distanceA, Float distanceB, Float distanceC);
    String getShipMessage(ArrayList<ArrayList<String>> messages);
    void setMaxMessageLength(ArrayList<String> messageA, ArrayList<String> messageB, ArrayList<String> messageC);
    ArrayList<String> setMessageStringToArray(String message);
    void setMessageToList(ArrayList<ArrayList<String>> messagesList, ArrayList<String> message);
    String JSONMapping(String jsonString) throws JSONException;
    Response getShipInfo(ArrayList<Satellite> satellite) throws IOException;
    Response setSatelliteToList(String satelliteName, Satellite satellite);
}
