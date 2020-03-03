package edu.cmu.sei.kalki.dc.database;

import edu.cmu.sei.kalki.dc.IoTInterfaceAPI;
import edu.cmu.sei.kalki.db.daos.DeviceCommandDAO;
import edu.cmu.sei.kalki.db.listeners.InsertHandler;
import edu.cmu.sei.kalki.db.models.Device;
import edu.cmu.sei.kalki.db.models.DeviceCommand;
import edu.cmu.sei.kalki.db.models.DeviceSecurityState;
import edu.cmu.sei.kalki.db.models.StageLog;

import java.util.List;
import java.util.logging.Logger;

public class NewSecurityStateHandler implements InsertHandler {
    private Logger logger = Logger.getLogger("device-controller");
    private IoTInterfaceAPI iotInterface = new IoTInterfaceAPI();

    @Override
    public void handleNewInsertion(int newStateId) {
//        DeviceSecurityState ss = Postgres.findDeviceSecurityState(newStateId);
//        Device device = Postgres.findDevice(ss.getDeviceId());

        // find devices in group
//        if(device.getGroup()!= null){
//            List<Device> groupDevices = DeviceCommandDao.findDevicesByGroup(device.getGroup().getId());
//            for (Device d: groupDevices){
//                List<DeviceCommand> commandList = DeviceCommandDao.findCommandsForGroup(d, device);
//                handleCommands(d, device, commandList);
//            }
//        }
//         else {
//            List<DeviceCommand> commandList = Postgres.findCommandsByDevice(device);
//            handleCommands(device, device, commandList);
//        }

    }

    private void handleCommands(Device commandDevice, Device stateDevice, List<DeviceCommand> commands) {
        if(commands.size() < 1)
            return;
        iotInterface.sendCommands(commandDevice, commands);
        logSendCommandReact(stateDevice, commands.size());
    }

    private void logSendCommandReact(Device device, int numCommands) {
        StageLog log = new StageLog(device.getCurrentState().getId(), StageLog.Action.SEND_COMMAND, StageLog.Stage.REACT, "Sending "+numCommands+" commands to IoT Interface");
        log.insert();
    }
}
