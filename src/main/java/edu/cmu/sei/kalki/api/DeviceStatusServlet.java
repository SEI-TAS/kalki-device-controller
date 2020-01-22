package edu.cmu.sei.kalki.api;

import edu.cmu.sei.kalki.rulebooks.AlertConditionTester;
import edu.cmu.sei.ttg.kalki.models.*;
import org.eclipse.jetty.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

public class DeviceStatusServlet extends DeviceControllerServlet {
    private static Logger logger = Logger.getLogger("device-controller");

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        logger.info("[DeviceStatusServlet] Request received at /iot-interface-api/new-status/");

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

        status.insert();
        logger.info("[DeviceStatusServlet] DeviceStatus inserted:"+status.toString());

        response.setStatus(HttpStatus.OK_200);
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

}
