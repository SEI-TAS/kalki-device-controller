package edu.cmu.sei.kalki.dc;

import edu.cmu.sei.kalki.db.daos.DeviceStatusDAO;
import edu.cmu.sei.kalki.db.daos.StageLogDAO;
import edu.cmu.sei.kalki.db.models.*;
import edu.cmu.sei.kalki.dc.api.ApiServerStartup;
import org.json.JSONObject;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;

public class ServletTests extends BaseTest {
    private final String apiUrl = "http://0.0.0.0:9090/device-controller-api";

    private DeviceSecurityState deviceSecurityState;

    @Test
    public void newStatusTest() {
        DeviceStatus status = new DeviceStatus(1,new HashMap<>(), new Timestamp(System.currentTimeMillis()));
        JSONObject object = new JSONObject(status.toString());

        int response = sendToApiEndpoint(object, apiUrl + "/new-status");
        assertEquals(200, response, String.format("Invalid response. Expected {} but got {}", 200, response));

        List<DeviceStatus> statusList = DeviceStatusDAO.findAllDeviceStatuses();
        assertEquals(1, statusList.size()); // should only be 1 status

        DeviceStatus result = statusList.get(0);

        // all properties should be the same except the id
        assertNotEquals(status.getId(), result.getId());
        assertEquals(status.getDeviceId(), result.getDeviceId());
        assertEquals(status.getTimestamp(), result.getTimestamp());
        assertEquals(status.getAttributes().toString(), result.getAttributes().toString());
    }

    @Test
    public void newStageLogTest() {
        StageLog log = new StageLog(deviceSecurityState.getId(), StageLog.Action.OTHER, StageLog.Stage.STIMULUS, "info");
        JSONObject object = new JSONObject(log.toString());

        int response = sendToApiEndpoint(object, apiUrl + "/new-stage-log");
        assertEquals(200, response, String.format("Invalid response. Expected {} but got {}", 200, response));

        List<StageLog> stageLogList = StageLogDAO.findAllStageLogs();
        assertEquals(1, stageLogList.size(), String.format("More than one stage log found. Expected {} but got {}", 1, stageLogList.size()));

        StageLog result = stageLogList.get(0);

        // all properties should be the same except id
        assertNotEquals(log.getId(), result.getId());
        assertEquals(log.getDeviceSecurityStateId(), result.getDeviceSecurityStateId());
        assertNotEquals(log.getTimestamp(), result.getTimestamp()); //Timestamp should be assigned on insertion
        assertEquals(log.getAction(), result.getAction());
        assertEquals(log.getStage(), result.getStage());
        assertEquals(log.getInfo(), result.getInfo());
    }

    private int sendToApiEndpoint(JSONObject payload, String apiEndpoint){
        try {
            URL fullUrl = new URL(apiEndpoint);
            HttpURLConnection httpCon = (HttpURLConnection) fullUrl.openConnection();
            httpCon.setDoOutput(true);
            httpCon.setRequestMethod("POST");
            httpCon.setRequestProperty("Accept", "application/json");

            try(OutputStreamWriter out = new OutputStreamWriter(httpCon.getOutputStream())){
                out.write(payload.toString());
            }

            httpCon.disconnect();
            return httpCon.getResponseCode();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    public void insertData() {
        ApiServerStartup.start();

        DeviceType type = new DeviceType(-1, "Description");
        type.insert();

        DataNode dataNode = new DataNode("Test Node", "localhost");
        dataNode.insert();

        Device device = new Device("Hi", "desc", type, "ip", 1, 1, dataNode);
        device.insert();

        deviceSecurityState = device.getCurrentState();
    }
}
