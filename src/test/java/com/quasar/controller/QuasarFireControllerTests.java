package com.quasar.controller;

import com.quasar.entities.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

public class QuasarFireControllerTests {
    public QuasarFireController getNewQuasar() { return new QuasarFireController();  }

    @Test
    @DisplayName("Get ship message")
    public void testGetShipMessage() {
        var result = getNewQuasar().getMessage("este,,,mensaje,", ",es,,,secreto", ",,un,,secreto").getBody();
        assertEquals(result.getMessage(), "este es un mensaje secreto", "Messages should match.");
        result = getNewQuasar().getMessage("este,,,,", ",es,,,", "este,,un,,secreto").getBody();
        assertEquals(result.getMessage(), "", "Message should be empty string.");
        result = getNewQuasar().getMessage("", "", "").getBody();
        assertEquals(result.getMessage(), "", "Message should be empty string.");
    }

    @Test
    @DisplayName("Get ship position")
    public void testGetShipPosition() {
        var location = getNewQuasar().getLocation(989.95F, 608.28F, 500F);
        assertEquals(location.getBody().getPosition().getX(), 200, "X position should be 200.");
        assertEquals(location.getBody().getPosition().getY(), 500, "Y position should be 500.");
        location = getNewQuasar().getLocation(989.95F, 608.28F, 0F);
        var expectedValue = "Unable to get position.";
        assertEquals(location.getBody().getMessage(), expectedValue, "Message should match.");

    }

    @Test
    @DisplayName("Post topsecret Method")
    public void testPostTopsecretMethod() {
        ResponseEntity<Response> response;
        String expectedValue;
        var body = """
                {\r
                "satellites": [\r
                {\r
                "name": "kenobi",\r
                "distance": 989.95,\r
                "message": ["este", "", "", "mensaje", ""]\r
                },\r
                {\r
                "name": "skywalker",\r
                "distance": 608.28,\r
                "message": ["", "es", "", "", "secreto"]\r
                },\r
                {\r
                "name": "sato",\r
                "distance": 500,\r
                "message": ["este", "", "un", "", ""]\r
                }\r
                ]\r
                }""";
        response = getNewQuasar().setTopSecret(body);
        expectedValue = "este es un mensaje secreto";
        assertEquals(response.getBody().getMessage(), expectedValue, "Messages should match.");
        assertEquals(response.getBody().getPosition().getX(), 200, "X position should be 0.");
        assertEquals(response.getBody().getPosition().getY(), 500, "Y position should be 0.");

        body = """
                {\r
                "satellites": [\r
                {\r
                "name": "kenobi",\r
                "distance": 989.95,\r
                "message": ["este", "", "", "mensaje", ""]\r
                },\r
                {\r
                "name": "skywalker",\r
                "distance": 608.28,\r
                },\r
                {\r
                "name": "sato",\r
                "distance": 0,\r
                "message": ["este", "", "un", "", ""]\r
                }\r
                ]\r
                }""";
        response = getNewQuasar().setTopSecret(body);
        assertEquals(response.getStatusCodeValue(), 400, "Status code should match.");

        body = """
                {\r
                "satellites": [\r
                {\r
                "name": "kenobi",\r
                "distance": 989.95,\r
                "message": ["este", "", "", "mensaje", ""]\r
                },\r
                {\r
                "name": "skywalker",\r
                "distance": 608.28,\r
                "message": ["", "es", "", "", "secreto"]\r
                },\r
                {\r
                "name": "sato",\r
                "distance": 0,\r
                "message": ["este", "", "", "", ""]\r
                }\r
                ]\r
                }""";
        response = getNewQuasar().setTopSecret(body);
        assertEquals(response.getStatusCodeValue(), 404, "Status code should match.");
    }

    @Test
    @DisplayName("Post topsecret_split Method with satellite name parameter")
    public void testSetTopSecretSatelliteMethod() {
        var name = "kenobi";
        var body = """
                {\r
                "distance": 100.0,\r
                "message": ["este", "", "", "mensaje", ""]\r
                }""";
        var response = getNewQuasar().setTopSecretSatellite(name, body);
        var expectedValue = "Info from satellite 'kenobi' added.";
        assertEquals(response.getBody().getMessage(), expectedValue, "Message should match.");
    }

    @Test
    @DisplayName("Get topsecret_split Method")
    public void testGetTopSecretSplitMethod() {
        QuasarFireController quasar;
        ArrayList<String> messageA, messageB, messageC;
        ResponseEntity<Response> response;
        String expectedValue;

        quasar = getNewQuasar();
        var bodyA = """
                {\r
                "distance": 989.95,\r
                "message": ["este", "", "", "mensaje", ""]\r
                }""";
        quasar.setTopSecretSatellite("kenobi", bodyA);
        var bodyB = """
                {\r
                "distance": 608.28,\r
                "message": ["", "es", "", "", "secreto"]\r
                }""";
        quasar.setTopSecretSatellite("skywalker", bodyB);
        var bodyC = """
                {\r
                "distance": 500,\r
                "message": ["este", "", "un", "", ""]\r
                }""";
        quasar.setTopSecretSatellite("sato", bodyC);
        messageA = quasar.getSatelliteService().getSatelliteA().getMessage();
        messageB = quasar.getSatelliteService().getSatelliteB().getMessage();
        messageC = quasar.getSatelliteService().getSatelliteC().getMessage();
        quasar.getSatelliteService().setMaxMessageLength(messageA, messageB, messageC);
        response = quasar.getTopSecretSplit();
        expectedValue = "este es un mensaje secreto";
        assertEquals(response.getBody().getMessage(), expectedValue, "Messages should match");
        assertEquals(response.getBody().getPosition().getX(), 200, "X position should be 0");
        assertEquals(response.getBody().getPosition().getY(), 500, "Y position should be 0");
    }

    @Test
    @DisplayName("Get topsecret_split/list Method")
    public void testGetTopSecretSplitListMethod() {
        var quasar = getNewQuasar();
        var bodyA = """
                {\r
                "distance": 100.0,\r
                "message": ["este", "", "", "mensaje", ""]\r
                }""";
        quasar.setTopSecretSatellite("kenobi", bodyA);
        var response = quasar.getTopSecretSplitList();
        var expectedValue = "Name: kenobi Distance: 100.0 Message: este,,,mensaje, & Name: skywalker Distance: 0.0 Message:  & Name: sato Distance: 0.0 Message:  & ";
        assertEquals(response.getBody().getMessage(), expectedValue, "Messages should match.");
    }

    @Test
    @DisplayName("Get topsecret_split/clear Method")
    public void testEmptyTopSecretSplitMethod() {
        var quasar = getNewQuasar();
        var satelliteName = "kenobi";
        var body = """
                {\r
                "distance": 100.0,\r
                "message": ["este", "", "", "mensaje", ""]\r
                }""";
        quasar.setTopSecretSatellite(satelliteName, body);
        var response = quasar.emptyTopSecretSplit();
        var expectedValue = "Info from satellites cleared.";
        assertEquals(response.getBody().getMessage(), expectedValue, "Messages should match.");
    }
}