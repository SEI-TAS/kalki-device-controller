package edu.cmu.sei.kalki.dc.api;

import edu.cmu.sei.kalki.db.models.StageLog;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

public class StageLogServlet extends DeviceControllerServlet {
    private static Logger logger = Logger.getLogger("device-controller");

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        logger.info("[StageLogServlet] Request received at /device-controller-api/new-stage-log");

        // read body of request
        JSONObject requestBody = parseRequestBody(request, response);

        // convert JSON to StageLog
        StageLog stageLog;
        try {
            stageLog = parseStageLog(requestBody);
        }
        catch (JSONException e) {
            throw new ServletException("Error parsing StageLog JSON: " + e.getMessage());
        }

        stageLog.insert();
        logger.info("[StageLogServlet] StageLog inserted: "+stageLog.toString());
    }

    private StageLog parseStageLog(JSONObject logData) throws JSONException {
        int devSecStateId = logData.getInt("deviceSecurityStateId");
        String action = logData.getString("action");
        String stage = logData.getString("stage");
        String info = logData.getString("info");

        return new StageLog(devSecStateId, action, stage, info);
    }
}
