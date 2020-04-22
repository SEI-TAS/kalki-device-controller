package edu.cmu.sei.kalki.dc.api;

import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;

public abstract class DeviceControllerServlet extends HttpServlet {

    protected JSONObject parseRequestBody(HttpServletRequest request, HttpServletResponse response) throws ServletException {
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

}
