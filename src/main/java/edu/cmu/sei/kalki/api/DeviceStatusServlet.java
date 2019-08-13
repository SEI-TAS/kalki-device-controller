package edu.cmu.sei.kalki.api;

import edu.cmu.sei.kalki.rulebooks.AlertConditionTester;
import edu.cmu.sei.ttg.kalki.models.*;
import org.eclipse.jetty.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DeviceStatusServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        System.out.println("Request received at /api/new-status/");

        // read body of request
        JSONObject requestBody = parseRequestBody(request, response);

        // convert JSON to Device
        DeviceStatus status;
        try {
            status = parseDeviceStatus(requestBody);
        }
        catch (JSONException e){
            throw new ServletException("Error parsing device JSON: " + e.getMessage());
        }

        System.out.println(status.toString());
        status.insert();
        AlertConditionTester.testDeviceStatus(status);

        response.setStatus(HttpStatus.OK_200);
    }

    private JSONObject parseRequestBody(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        JSONObject requestBody;
        try {
            String bodyLine;
            StringBuilder jsonBody = new StringBuilder();
            BufferedReader bodyReader = request.getReader();
            while((bodyLine = bodyReader.readLine()) != null) {
                jsonBody.append(bodyLine);
            }
            requestBody = new JSONObject(jsonBody.toString());
        }
        catch (JSONException e) {
            throw new ServletException("Error parsing body JSON of request: " + e.getMessage());
        }
        catch (IOException e) {
            throw new ServletException("Error parsing body of request: " + e.getMessage());
        }

        return requestBody;
    }

    private DeviceStatus parseDeviceStatus(JSONObject statusData) throws JSONException {
        Timestamp timestamp = new Timestamp(statusData.getLong("timestamp"));
        Map<String, String> attributes = parseStatusAttributes(statusData.getJSONObject("attributes"));
        int deviceId = statusData.getInt("deviceId");

        return new DeviceStatus(deviceId, attributes, timestamp);
    }

    private Map<String, String> parseStatusAttributes(JSONObject attributeData){
        Map<String, String> attributes = new HashMap<>();
        Iterator<String> keys = attributeData.keys();
        while(keys.hasNext()){
            String key = keys.next();
            String value = attributeData.getString(key);
            attributes.put(key,value);
        }
        return attributes;
    }

    private void testStatus(int deviceId){

    }
}
