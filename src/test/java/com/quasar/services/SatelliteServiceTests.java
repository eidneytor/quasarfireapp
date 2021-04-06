package com.quasar.services;

import com.quasar.entities.Position;
import com.quasar.entities.Response;
import com.quasar.entities.Satellite;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import java.util.ArrayList;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.*;

public class SatelliteServiceTests
{
    private SatelliteService satelliteService;

    @BeforeEach
    public void setUp() {
        satelliteService = new SatelliteService();
    }

    @Test
    @DisplayName("Get ship position")
    public void testGetShipPosition() {
        Position shipPosition, expectedPosition;
        shipPosition = satelliteService.getShipPosition(360.56F, 424.23F, 860.23F);
        expectedPosition = new Position(-200, -400);
        assertEquals(shipPosition.getX(), expectedPosition.getX(), "X position should be -200.");
        assertEquals(shipPosition.getY(), expectedPosition.getY(), "Y position should be -400.");

        satelliteService = new SatelliteService();
        shipPosition = satelliteService.getShipPosition(905.54F, 360.56F, 412.31F);
        expectedPosition = new Position(400, -300);
        assertEquals(shipPosition.getX(), expectedPosition.getX(), "X position should be 400.");
        assertEquals(shipPosition.getY(), expectedPosition.getY(), "Y position should be -300.");

        satelliteService = new SatelliteService();
        shipPosition = satelliteService.getShipPosition(989.95F, 608.28F, 500F);
        expectedPosition = new Position(200, 500);
        assertEquals(shipPosition.getX(), expectedPosition.getX(), "X position should be 200.");
        assertEquals(shipPosition.getY(), expectedPosition.getY(), "Y position should be 500.");

        satelliteService = new SatelliteService();
        shipPosition = satelliteService.getShipPosition(707.11F, 781.02F, 984.89F);
        expectedPosition = new Position(-400, 500);
        assertEquals(shipPosition.getX(), expectedPosition.getX(), "X position should be -400.");
        assertEquals(shipPosition.getY(), expectedPosition.getY(), "Y position should be 500.");

        satelliteService = new SatelliteService();
        shipPosition = satelliteService.getShipPosition(0F, 360.56F, 412.31F);
        assertNull(shipPosition, "position should be null.");
    }

    @Test
    @DisplayName("Get ship message")
    public void testGetShipMessage() {
        var messages = new ArrayList<ArrayList<String>>();
        var messageA = new ArrayList<>(Arrays.asList("este", "", "", "mensaje", ""));
        var messageB = new ArrayList<>(Arrays.asList("", "es", "", "", "secreto"));
        var messageC = new ArrayList<>(Arrays.asList("este", "", "un", "", ""));
        messages.add(messageA);
        messages.add(messageB);
        messages.add(messageC);
        satelliteService.setMaxMessageLength(messageA, messageB, messageC);
        var expectedValue = "este es un mensaje secreto";
        assertEquals(satelliteService.getShipMessage(messages), expectedValue, "Messages should match.");

        messages = new ArrayList<>();
        messageA = new ArrayList<>(Arrays.asList("este", "", "", "", ""));
        messageB = new ArrayList<>(Arrays.asList("", "es", "", "", "secreto"));
        messageC = new ArrayList<>(Arrays.asList("", "", "", "un", "", "secreto"));
        messages.add(messageA);
        messages.add(messageB);
        messages.add(messageC);
        satelliteService.setMaxMessageLength(messageA, messageB, messageC);
        expectedValue = "";
        assertEquals(satelliteService.getShipMessage(messages), expectedValue, "Messages should match.");
    }

    @Test
    @DisplayName("Se max length of lists of messages")
    public void testSetMaxMessageLength() {
        var messageA = new ArrayList<>(Arrays.asList("este","",""));
        var messageB = new ArrayList<>(Arrays.asList("","", "message"));
        var messageC = new ArrayList<>(Arrays.asList("", "", "es", "message"));
        satelliteService.setMaxMessageLength(messageA, messageB, messageC);
        assertEquals(satelliteService.getMessageMaxLength(), 4, "Values should match.");
    }

    @Test
    @DisplayName("Set satellite to list")
    public void testSetMessageToList() {
        var list = new ArrayList<ArrayList<String>>();
        var message = new ArrayList<String>();
        message.add("este");
        message.add("");
        message.add("");
        message.add("mensaje");
        satelliteService.setMessageToList(list, message);
        assertEquals(list.size(), 1, "List count should be 1.");
    }

    @Test
    @DisplayName("JSON Mapping")
    public void testJSONMapping() throws JSONException {
        var jsonString = """
                {\r
                "distance": 100.0,\r
                "message": ["este", "", "", "mensaje", ""]\r
                }""";
        var result = satelliteService.JSONMapping(jsonString);
        assertEquals(result, new JSONObject(jsonString.replaceAll("\n", "").replaceAll("\r", "")).toString(), "Result should match");
        result = satelliteService.JSONMapping("");
        assertEquals(result, "", "Result should match.");
    }

    @Test
    @DisplayName("Get ship message & position correctly")
    public void getShipInfo() {
        ArrayList<Satellite> list;
        ArrayList<String> messageA, messageB, messageC;
        Satellite satelliteA, satelliteB, satelliteC;
        String expectedValue;
        Response shipInfo;
        list = new ArrayList<Satellite>();
        satelliteA = satelliteService.getSatelliteA();
        messageA = new ArrayList<>(Arrays.asList("este", "", "", "mensaje", ""));
        satelliteA.setDistanceToShip(989.95F);
        satelliteA.setMessage(messageA);
        list.add(satelliteA);
        satelliteB = satelliteService.getSatelliteB();
        messageB = new ArrayList<>(Arrays.asList("", "es", "", "", "secreto"));
        satelliteB.setDistanceToShip(608.28F);
        satelliteB.setMessage(messageB);
        list.add(satelliteB);
        satelliteC = satelliteService.getSatelliteC();
        messageC = new ArrayList<>(Arrays.asList("este", "", "un", "", ""));
        satelliteC.setDistanceToShip(500F);
        satelliteC.setMessage(messageC);
        list.add(satelliteC);
        satelliteService.setMaxMessageLength(messageA, messageB, messageC);
        expectedValue = "este es un mensaje secreto";
        shipInfo = satelliteService.getShipInfo(list);
        assertEquals(shipInfo.getMessage(), expectedValue, "Message should match.");
        assertEquals(shipInfo.getPosition().getX(),200,"X position should match.");
        assertEquals(shipInfo.getPosition().getY(),500,"Info should match.");

        satelliteService = new SatelliteService();
        list = new ArrayList<>();
        list.add(satelliteA);
        satelliteB.setDistanceToShip(null);
        satelliteB.setMessage(null);
        list.add(satelliteB);
        satelliteC.setDistanceToShip(null);
        satelliteC.setMessage(null);
        list.add(satelliteC);
        shipInfo = satelliteService.getShipInfo(list);
        expectedValue = "Info from satellites not complete or unable to get position or message.";
        assertEquals(shipInfo.getMessage(), expectedValue, "Message should match.");
    }

    @Test
    @DisplayName("Set satellite to list correctly")
    public void testSetSatelliteToList() {
        var kenobi = new Satellite();
        kenobi.setName("kenobi");
        kenobi.setMessage(new ArrayList<>(Arrays.asList("este", "", "", "mensaje", "")));
        kenobi.setDistanceToShip(100.0F);
        var response = satelliteService.setSatelliteToList("kenobi", kenobi);
        var expectedValue = "Info from satellite 'kenobi' added.";
        assertEquals(response.getMessage(), expectedValue, "Message should match.");
        assertEquals(satelliteService.getSatellites().get(0).getDistanceToShip(), 100.0F, "Distance should match.");
    }

    @Test
    @DisplayName("Set message string to array of string containing words")
    public void testSetMessageStringToArray() {
        var result = satelliteService.setMessageStringToArray("este,,,mensaje,");
        var expectedValue = new ArrayList<>(Arrays.asList("este", "", "", "mensaje", ""));
        assertEquals(result.size(), expectedValue.size(), "Result should match");
    }
}
