package edu.cmu.sei.kalki.dc;

import edu.cmu.sei.kalki.db.daos.DeviceStatusDAO;
import edu.cmu.sei.kalki.db.daos.DeviceTypeDAO;
import edu.cmu.sei.kalki.db.models.Device;
import edu.cmu.sei.kalki.db.models.DeviceStatus;
import edu.cmu.sei.kalki.db.models.DeviceType;
import edu.cmu.sei.kalki.db.utils.Config;
import org.json.JSONObject;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DeviceStatusServletTest extends BaseTest {
    private final String apiEndpoint = Config.getValue("test_api") + "/new-status";

    @Test
    public void newStatusTest() {
        DeviceStatus status = new DeviceStatus(1,new HashMap<>(), new Timestamp(System.currentTimeMillis()));
        JSONObject object = new JSONObject(status.toString());

        int response = sendToApiEndpoint(object, apiEndpoint);
        assertEquals(200,response, String.format("Invalid response. Expected {} but got {}", 200, response));

        List<DeviceStatus> statusList = DeviceStatusDAO.findAllDeviceStatuses();
        assertEquals(1, statusList.size()); // should only be 1 status

        DeviceStatus result = statusList.get(0);

        // all properties should be the same except the id
        assertNotEquals(status.getId(), result.getId());
        assertEquals(status.getDeviceId(), result.getDeviceId());
        assertEquals(status.getTimestamp(), result.getTimestamp());
        assertEquals(status.getAttributes().toString(), result.getAttributes().toString());
    }

    public void insertData() {
        DeviceType type = new DeviceType(-1, "Description");
        type.insert();

        Device device = new Device("Hi", "desc", type, "ip", 1, 1);
        device.insert();
    }
}
