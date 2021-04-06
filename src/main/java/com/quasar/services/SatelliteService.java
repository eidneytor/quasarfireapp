package com.quasar.services;

import com.quasar.entities.Position;
import com.quasar.entities.Response;
import com.quasar.entities.Satellite;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class SatelliteService implements ISatelliteService {

    private ArrayList<Satellite> satellites = new ArrayList<>();
    private int messageMaxLength;

    public Satellite getSatelliteA() {
        return satellites.get(0);
    }
    public Satellite getSatelliteB() {
        return satellites.get(1);
    }
    public Satellite getSatelliteC() {
        return satellites.get(2);
    }

    public ArrayList<Satellite> getSatellites() {
        return satellites;
    }
    public void setSatellites(ArrayList<Satellite> value) {
        this.satellites = value;
    }

    public int getMessageMaxLength() {
        return messageMaxLength;
    }
    public void setMessageMaxLength(int value) {
        this.messageMaxLength = value;
    }

    public SatelliteService() {
        satellites.add(new Satellite(-500F, -200F, "kenobi"));
        satellites.add(new Satellite(100F, -100F, "skywalker"));
        satellites.add(new Satellite(500F, 100F, "sato"));
    }

    //Calculate & return position using trilateration from distances to ship provided
    public Position getShipPosition(Float distanceA, Float distanceB, Float distanceC) {
        try {
            //Get positive position
            var position = tryGetPosition(getSatellites(), distanceA, distanceB, false);
            if (position != null && checkCoordinate(position, distanceA, distanceB, distanceC))
                return position;
            //If positive position isn't correct try with inverted position & check
            var invertedPosition = tryGetPosition(invertCoordinates(), distanceA, distanceB, true);
            //If inverted position  is correct
            if (invertedPosition != null && checkCoordinate(invertedPosition, distanceA, distanceB, distanceC)) {
                //If Y axis is positive, invert this axis
                if (invertedPosition.getY() >= 0)
                    invertedPosition = new Position(-invertedPosition.getX(), -invertedPosition.getY());
                return invertedPosition;
            }
        } catch (Exception ex) { }
        return null;
    }

    //Check if shipPosition is correct
    private boolean checkCoordinate(Position shipPosition, Float distanceA, Float distanceB, Float distanceC) {
        return (Math.round(distanceA) == Math.round(satellites.get(0).getDistanceTo(shipPosition))
                && Math.round(distanceB) == Math.round(satellites.get(1).getDistanceTo(shipPosition))
                && Math.round(distanceC) == Math.round(satellites.get(2).getDistanceTo(shipPosition)));
    }

    //Return new created list of satellites & invert x & y
    private ArrayList<Satellite> invertCoordinates() {
        var satellites = new ArrayList<Satellite>();
        var satelliteA = getSatelliteA();
        var satelliteB = getSatelliteB();
        var satelliteC = getSatelliteC();
        //Invert x & y
        satelliteA.setCoordinate(-satelliteA.getX(), -satelliteA.getY());
        satelliteB.setCoordinate(-satelliteB.getX(), -satelliteB.getY());
        satelliteC.setCoordinate(-satelliteC.getX(), -satelliteC.getY());
        satellites.add(satelliteA);
        satellites.add(satelliteB);
        satellites.add(satelliteC);
        return satellites;
    }

    //With trigonometry get X & Y axis
    //If inverted parameter is true, calculation changes a bit
    private Position tryGetPosition(ArrayList<Satellite> satellites, Float distanceA, Float distanceB, boolean inverted) {
        if (distanceA != null && distanceB != null ) {
            // Calculate distances between A y B
            var distanceBtoA_x = satellites.get(0).getXDistanceTo(satellites.get(1).getX());
            var distanceBtoA_y = satellites.get(0).getYDistanceTo(satellites.get(1).getY());
            var distanceBtoA = satellites.get(0).getDistanceTo(satellites.get(1).getCoordinate());
            var tanDistanceBtoA = Math.atan2(distanceBtoA_y, distanceBtoA_x);
            //Calculate trilateration
            var shipX = (Math.pow(distanceA, 2) - Math.pow(distanceB, 2) + Math.pow(distanceBtoA, 2)) / (distanceBtoA * 2);
            var shipY = Math.pow(distanceA, 2) - Math.pow(shipX, 2);
            float x = 0F, y = 0F;
            //shipY must be positive in order to get the square
            if (shipY > 0F) {
                var distanceShip = (float)Math.sqrt(Math.pow(shipX, 2) + shipY);
                var tanDistanceShip = (float)Math.atan2(Math.sqrt(shipY), shipX);
                //Calculate x & y coordinates inverted or not (changes the calculation)
                if (!inverted) {
                    x = (distanceShip * (float)Math.cos(tanDistanceShip + tanDistanceBtoA) + satellites.get(0).getX());
                    y = (distanceShip * (float)Math.sin(tanDistanceShip + tanDistanceBtoA) + satellites.get(0).getY());
                }
                else {
                    x = (distanceShip * (float)Math.cos(tanDistanceShip - tanDistanceBtoA) + satellites.get(0).getX());
                    y = (distanceShip * (float)Math.sin(-tanDistanceShip + tanDistanceBtoA) + satellites.get(0).getY());
                }
            }
            //Return coordinate
            return new Position(Math.round(x), Math.round(y));
        }
        //Return coordinate
        return null;
    }

    //Return ship message from array of array of strings provided
    public String getShipMessage(ArrayList<ArrayList<String>> messages) {
        var messageDecripted = "";
        try {
            //Set max message length
            setMaxMessageLength(messages.get(0), messages.get(1), messages.get(2));
            //Create new array of messages & equalize out of date messages
            var newArrayMessages = new ArrayList<ArrayList<String>>();
            for (ArrayList<String> mess: messages)
                newArrayMessages.add(completeArrayWithSpaces(mess));
            //Prepare list to join arrays & set words found in messages provided
            var finalMessage = new ArrayList<String>();
            for(var i = 0; i < getMessageMaxLength(); i++)
                finalMessage.add("");
            //Get every position with words found in messages provided
            var position = -1;
            for(String word: finalMessage) {
                position++;
                for (ArrayList<String> message : newArrayMessages) {
                    var newWord = String.valueOf(message.get(position));
                    if (word.equals("") && !newWord.equals("")) finalMessage.set(position, newWord);
                    else if (!word.equals("") && word.equals(newWord)) break;
                }
            }
            //Ignore white spaces from ahead
            StringBuilder sbMessage = new StringBuilder();
            var messageInitialized = false;
            for (String word: finalMessage) {
                messageInitialized = !sbMessage.toString().trim().equals("");
                if (!word.equals("")) {
                    sbMessage.append(word).append(' ');
                    messageDecripted = sbMessage.toString();
                }
                else if (messageInitialized) {
                    messageDecripted = "";
                    break;
                }
            }
            //Return message if it has inicialized & has all words
            if (messageInitialized && !messageDecripted.equals(""))
                messageDecripted = sbMessage.toString().trim();
        } catch (Exception ex) {
            throw ex;
        }
        return messageDecripted;
    }

    //Calculate maximum length of list of messages provided
    public void setMaxMessageLength(ArrayList<String> messageA, ArrayList<String> messageB, ArrayList<String> messageC) {
        try {
            var messageLength = new ArrayList<Integer>();
            messageLength.add(messageA.size());
            messageLength.add(messageB.size());
            messageLength.add(messageC.size());
            //Order to get max value
            Collections.sort(messageLength);
            setMessageMaxLength(messageLength.get(2));
        } catch (Exception ex) {
            setMessageMaxLength(0);
        }
    }

    //Add messages (array of array of strings) to list of messages
    public void setMessageToList(ArrayList<ArrayList<String>> list, ArrayList<String> messages) {
        try {
            //Add array of words to list
            var satellites = new ArrayList<>(messages);
            list.add(completeArrayWithSpaces(satellites));
        } catch (Exception ex) {
        }
    }

    //Convert string to JSON String
    public String JSONMapping(String jsonString) {
        try {
            //Return JSON Object from string
            jsonString = jsonString.replaceAll("\n", "").replaceAll("\r", "");
            return new JSONObject(jsonString).toString();
        } catch (Exception ex) {
            return "";
        }
    }

    //Get ship info (message & coordinate) from satellite info
    public Response getShipInfo(ArrayList<Satellite> satellite) {
        try {
            //Create list of lists & add to array
            var messages = new ArrayList<ArrayList<String>>();
            for(Satellite sat: satellite)
                messages.add(sat.getMessage());
            //Get message
            var message = getShipMessage(messages);
            //Get distances
            var distanceFromA = satellite.get(0).getDistanceToShip();
            var distanceFromB = satellite.get(1).getDistanceToShip();
            var distanceFromC = satellite.get(2).getDistanceToShip();
            //Get coordinate
            var position = getShipPosition(distanceFromA, distanceFromB, distanceFromC);
            //If got message & position
            if (!message.equals("") && position != null)
                return new Response(message, position, 200);
        } catch (Exception ex) {
            return new Response(ex.getMessage(), null, 500);
        }
        //If got not message nor position return info incomplete
        return new Response("Info from satellites not complete or unable to get position or message.", null, 404);
    }

    //Add satellite info to list of satellites
    @Override
    public Response setSatelliteToList(String name, Satellite satellite) {
        //Add message and/or distance to satellite net
        var list = getSatellites();
        for (Satellite sat : list) {
            if (sat.getName().equals(name)) {
                if (sat.getMessage() == null && satellite.getMessage() != null)
                    sat.setMessage(satellite.getMessage());
                if (satellite.getDistanceToShip() != null && satellite.getDistanceToShip() >= 0)
                    sat.setDistanceToShip(satellite.getDistanceToShip());
            }
        }
        setSatellites(list);
        return new Response("Info from satellite '" + name + "' added.", null);
    }

    //Convert string to ArrayList<String>
    public ArrayList<String> setMessageStringToArray(String message) {
        return new ArrayList<>(Arrays.asList(message.split(",", -1)));
    }

    //Complete words array with spaces
    private ArrayList<String> completeArrayWithSpaces(ArrayList<String> words) {
        //Complete words array with spaces
        var wordList = new ArrayList<String>();
        var wordsSize = words != null ? words.size() : 0;
        for (var i = 0; i < getMessageMaxLength() - wordsSize; i++)
            wordList.add("");
        wordList.addAll(words != null ? words : new ArrayList<>());
        return wordList;
    }
}
