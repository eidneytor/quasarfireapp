package com.quasar.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quasar.entities.*;
import com.quasar.services.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;

@RestController
public class QuasarFireController {

    private SatelliteService satelliteService = new SatelliteService();

    public SatelliteService getSatelliteService() { return satelliteService; }
    public void setSatelliteService(SatelliteService value) { satelliteService = value; }

    @RequestMapping(value="/location", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public ResponseEntity<Response> getLocation(float distanceA, float distanceB, float distanceC) {
        //Get position using trilateration
        var position = getSatelliteService().getShipPosition(distanceA, distanceB, distanceC);
        //Get status result
        var status = position != null ? HttpStatus.OK : HttpStatus.NOT_FOUND;
        var message = position == null ? "Unable to get position." : null;
        //Return info + status
        return new ResponseEntity<>(new Response(message, position), status);
    }

    @RequestMapping(value="/message", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public ResponseEntity<Response> getMessage(String messageA, String messageB, String messageC) {
        try {
            var arrayMessageA = getSatelliteService().setMessageStringToArray(messageA);
            var arrayMessageB = getSatelliteService().setMessageStringToArray(messageB);
            var arrayMessageC = getSatelliteService().setMessageStringToArray(messageC);
            //Set with messages max length
            getSatelliteService().setMaxMessageLength(arrayMessageA, arrayMessageB, arrayMessageC);
            //Load list with messages lists
            var messagesList = new ArrayList<ArrayList<String>>();
            getSatelliteService().setMessageToList(messagesList, arrayMessageA);
            getSatelliteService().setMessageToList(messagesList, arrayMessageB);
            getSatelliteService().setMessageToList(messagesList, arrayMessageC);
            //Get message
            var message = getSatelliteService().getShipMessage(messagesList);
            //Get status result
            var status = message.equals("") ? HttpStatus.OK : HttpStatus.NOT_FOUND;
            //Return info & status
            return new ResponseEntity<>(new Response(message, null), status);
        } catch (Exception ex) {
            return new ResponseEntity<>(new Response(ex.getMessage(), null), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/topsecret", method = RequestMethod.POST, consumes = "application/json",
            produces = "application/json")
    @ResponseBody
    public ResponseEntity<Response> setTopSecret(@RequestBody String strSatellites) {
        try {
            //Get satellites object from JSON
            var jsonString = getSatelliteService().JSONMapping(strSatellites);
            var satellitesNet = new ObjectMapper().readValue(jsonString, SatelliteNetwork.class);
            //Get info (coordinate && message) from satellites info
            var info = getSatelliteService().getShipInfo(satellitesNet.getSatellites());
            //Return info & status
            return new ResponseEntity<>(info, info.getStatus() == 200 ? HttpStatus.OK : HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            return new ResponseEntity<>(new Response(ex.getMessage(), null), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/topsecret_split/{satelliteName}", method = RequestMethod.POST, consumes = "application/json",
            produces = "application/json")
    @ResponseBody
    public ResponseEntity<Response> setTopSecretSatellite(@PathVariable("satelliteName") String name,
                                                          @RequestBody String data) {
        try {
            //Get satellite object from JSON
            var jsonString = getSatelliteService().JSONMapping(data);
            var satellite= new ObjectMapper().readValue(jsonString, Satellite.class);
            //Set satellite name from param
            satellite.setName(name);
            //Get response message from adding
            var info = getSatelliteService().setSatelliteToList(name, satellite);
            //Return info & status
            return new ResponseEntity<>(info, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(new Response(ex.getMessage(), null), HttpStatus.BAD_REQUEST);
        }

    }

    @RequestMapping(value="/topsecret_split", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public ResponseEntity<Response> getTopSecretSplit() {
        try {
            //Get info (coordinate && message) from satellites info previously loaded by calling "/topsecret_split/{satelliteName}"
            var info = getSatelliteService().getShipInfo(getSatelliteService().getSatellites());
            //Return info & status
            return new ResponseEntity<>(info, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(new Response(ex.getMessage(), null), HttpStatus.BAD_REQUEST);
        }
    }

    /*Los siguientes son metodos para hacer diferentes pruebas*/
    @RequestMapping(value="/topsecret_split/list", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public ResponseEntity<Response> getTopSecretSplitList() {
        try {
            var data = new StringBuilder();
            for(Satellite satellite: getSatelliteService().getSatellites()) {
                var message = "";
                StringBuilder sbMessage = new StringBuilder();
                if (satellite.getMessage() != null) {
                    for (String s : satellite.getMessage()) sbMessage.append(s + ',');
                    message = sbMessage.toString().trim().substring(0, sbMessage.toString().trim().length()-1);
                }
                data.append("Name: " + satellite.getName());
                data.append(" Distance: ");
                data.append(satellite.getDistanceToShip() != null ? satellite.getDistanceToShip().floatValue() : "0.0");
                data.append(" Message: " + message + " & ");
            }
            return new ResponseEntity<>(new Response(data.toString(), null), HttpStatus.OK);
        }
        catch (Exception ex)
        {
            return new ResponseEntity<>(new Response(ex.getMessage(), null), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value="/topsecret_split/clear", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public ResponseEntity<Response> emptyTopSecretSplit() {
        try {
            setSatelliteService(new SatelliteService());
            return new ResponseEntity<>(new Response("Info from satellites cleared.", null), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(new Response(ex.getMessage(), null), HttpStatus.BAD_REQUEST);
        }
    }
}