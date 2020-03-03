package edu.cmu.sei.kalki.dc.database;

import edu.cmu.sei.kalki.db.daos.DeviceDAO;
import edu.cmu.sei.kalki.db.daos.PolicyRuleLogDAO;
import edu.cmu.sei.kalki.db.models.*;
import edu.cmu.sei.kalki.dc.IoTInterfaceAPI;
import edu.cmu.sei.kalki.db.daos.DeviceCommandDAO;
import edu.cmu.sei.kalki.db.listeners.InsertHandler;

import java.util.List;
import java.util.logging.Logger;

public class NewPolicyRuleLogHandler implements InsertHandler {
    private Logger logger = Logger.getLogger("device-controller");
    private IoTInterfaceAPI iotInterface = new IoTInterfaceAPI();

    @Override
    public void handleNewInsertion(int newLogId) {
        PolicyRuleLog log = PolicyRuleLogDAO.findPolicyRuleLog(newLogId);
        Device device = DeviceDAO.findDevice(log.getDeviceId());

        // find devices in group
        if(device.getGroup()!= null){
            List<Device> groupDevices = DeviceDAO.findDevicesByGroup(device.getGroup().getId());
            for (Device d: groupDevices){
                handleCommands(d, device, log);
            }
        }
         else {
            handleCommands(device, device, log);
        }

    }

    private void handleCommands(Device commandDevice, Device stateDevice, PolicyRuleLog log) {
        List<DeviceCommand> commands = DeviceCommandDAO.findCommandsForDeviceTypeByPolicyRuleLog(log.getId(), commandDevice.getType().getId());

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
