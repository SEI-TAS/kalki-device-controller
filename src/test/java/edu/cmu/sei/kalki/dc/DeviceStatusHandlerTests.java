package edu.cmu.sei.kalki.dc;

import edu.cmu.sei.kalki.db.models.DataNode;
import edu.cmu.sei.kalki.db.models.Device;
import edu.cmu.sei.kalki.db.models.DeviceStatus;
import edu.cmu.sei.kalki.db.models.DeviceType;

import edu.cmu.sei.kalki.dc.database.DeviceStatusHandler;
import edu.cmu.sei.kalki.dc.rulebooks.AlertConditionTester;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;

public class DeviceStatusHandlerTests extends BaseTest {
    private DeviceStatus deviceStatus;

    @Mock
    private AlertConditionTester alertConditionTester;

    @InjectMocks
    private DeviceStatusHandler statusHandler = new DeviceStatusHandler();

    @BeforeEach
    public void setup(){
        super.setup();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void HandleNewInsertionNewStatus() {
        statusHandler.handleNewInsertion(deviceStatus.getId());

        Mockito.verify(alertConditionTester).testDeviceStatus(Mockito.argThat((DeviceStatus status) -> deviceStatus.getId() == status.getId()));
    }

    public void insertData(){
        DeviceType type = new DeviceType(-1, "name");
        type.insert();

        DataNode dataNode = new DataNode("Test Node", "localhost");
        dataNode.insert();
        Device device = new Device("name", "description", type, "ip", 1,1, dataNode);
        device.insert();

        deviceStatus = new DeviceStatus(device.getId(), new HashMap<>());
        deviceStatus.insert();
    }
}
