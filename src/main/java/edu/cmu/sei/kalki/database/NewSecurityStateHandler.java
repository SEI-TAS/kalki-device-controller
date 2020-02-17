package edu.cmu.sei.kalki.database;

import edu.cmu.sei.kalki.IoTInterfaceAPI;
import edu.cmu.sei.ttg.kalki.database.Postgres;
import edu.cmu.sei.ttg.kalki.listeners.InsertHandler;
import edu.cmu.sei.ttg.kalki.models.Device;
import edu.cmu.sei.ttg.kalki.models.DeviceCommand;
import edu.cmu.sei.ttg.kalki.models.DeviceSecurityState;
import edu.cmu.sei.ttg.kalki.models.StageLog;
import org.json.JSONObject;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.logging.Logger;

public class NewSecurityStateHandler implements InsertHandler {
    private Logger logger = Logger.getLogger("device-controller");
    private IoTInterfaceAPI iotInterface = new IoTInterfaceAPI();

    @Override
    public void handleNewInsertion(int newStateId) {
        DeviceSecurityState ss = Postgres.findDeviceSecurityState(newStateId);
        Device device = Postgres.findDevice(ss.getDeviceId());

        // find devices in group
        if(device.getGroup()!= null){
            List<Device> groupDevices = Postgres.findDevicesByGroup(device.getGroup().getId());
            for (Device d: groupDevices){
                List<DeviceCommand> commandList = Postgres.findCommandsForGroup(d, device);
                handleCommands(d, device, commandList);
            }
        }
         else {
            List<DeviceCommand> commandList = Postgres.findCommandsByDevice(device);
            handleCommands(device, device, commandList);
        }

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
