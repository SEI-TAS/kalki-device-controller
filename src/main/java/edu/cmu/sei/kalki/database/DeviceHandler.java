package edu.cmu.sei.kalki.database;

import edu.cmu.sei.kalki.IoTInterfaceAPI;
import edu.cmu.sei.ttg.kalki.database.Postgres;
import edu.cmu.sei.ttg.kalki.listeners.InsertHandler;
import edu.cmu.sei.ttg.kalki.models.Device;
import edu.cmu.sei.ttg.kalki.models.StageLog;

import java.util.logging.Logger;

public class DeviceHandler implements InsertHandler {
    private Logger logger = Logger.getLogger("device-controller");
    private IoTInterfaceAPI iotInterface = new IoTInterfaceAPI();

    private boolean isNewDevice;

    DeviceHandler(boolean isNewDev) {
        isNewDevice = isNewDev;
    }

    @Override
    public void handleNewInsertion(int newDeviceId) {
        logger.info("[DeviceHandler] Device found: "+newDeviceId+". Is it new? "+ isNewDevice);
        Device device = Postgres.findDevice(newDeviceId);
        logSampleRateIncreaseReact(device);
        if(isNewDevice) {
            iotInterface.sendNewDeviceInfo(device);
        } else {
            iotInterface.sendUpdatedDeviceInfo(device);
        }
    }

    private void logSampleRateIncreaseReact(Device device) {
        if(!isNewDevice){
            StageLog log = new StageLog(device.getCurrentState().getId(), StageLog.Action.INCREASE_SAMPLE_RATE, StageLog.Stage.REACT, "Device updated: "+device.getId());
            log.insert();
            logger.info("[DeviceHandler] Logging react to device being updated: "+log.toString());
        }
    }
}
