package edu.cmu.sei.kalki.dc;

import edu.cmu.sei.kalki.db.daos.StageLogDAO;
import edu.cmu.sei.kalki.db.models.DataNode;
import edu.cmu.sei.kalki.db.models.Device;
import edu.cmu.sei.kalki.db.models.DeviceType;
import edu.cmu.sei.kalki.db.models.StageLog;

import edu.cmu.sei.kalki.dc.database.DeviceHandler;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

public class DeviceHandlerTests extends BaseTest{
    private Device testDevice;

    @Mock
    private IoTInterfaceAPI ioTInterfaceAPI;

    @InjectMocks
    private DeviceHandler newDeviceHandler = new DeviceHandler(true);

    @InjectMocks
    private DeviceHandler updateDeviceHandler = new DeviceHandler(false);

    @BeforeEach
    public void setup() {
        super.setup();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void handleNewInsertionNewDevice(){
        newDeviceHandler.handleNewInsertion(testDevice.getId());

        Mockito.verify(ioTInterfaceAPI).sendNewDeviceInfo(Mockito.argThat((Device device) -> device.getId() == testDevice.getId()));

        List<StageLog> stageLogList = StageLogDAO.findAllStageLogs();
        assertEquals(0, stageLogList.size(), String.format("A stage log was inserted but shouldn't have been. Count: {}", stageLogList.size()));
    }

    @Test
    public void handleNewInsertionUpdateDevice(){
        testDevice.setSamplingRate(0);
        testDevice.insertOrUpdate();

        updateDeviceHandler.handleNewInsertion(testDevice.getId());

        Mockito.verify(ioTInterfaceAPI).sendUpdatedDeviceInfo(Mockito.argThat((Device device) -> device.getId() == testDevice.getId()));

        List<StageLog> stageLogList = StageLogDAO.findAllStageLogs();
        assertEquals(1, stageLogList.size(), String.format("The number of stage logs inserted was not equal to 1. Count: {}", stageLogList.size()));

        StageLog log = stageLogList.get(0);
        StageLog testLog = new StageLog(testDevice.getCurrentState().getId(), StageLog.Action.INCREASE_SAMPLE_RATE, StageLog.Stage.REACT, "Device updated: "+testDevice.getId());
        assertEquals(testLog.getDeviceSecurityStateId(), log.getDeviceSecurityStateId());
        assertEquals(testLog.getAction(), log.getAction());
        assertEquals(testLog.getStage(), log.getStage());
        assertEquals(testLog.getInfo(), log.getInfo());
    }

    public void insertData() {
        DeviceType type = new DeviceType(-1, "name");
        type.insert();

        DataNode dataNode = new DataNode("Test Node", "localhost");
        dataNode.insert();

        testDevice = new Device("1", "1", type, "ip", 1,1, dataNode);
        testDevice.insert();
    }
}
