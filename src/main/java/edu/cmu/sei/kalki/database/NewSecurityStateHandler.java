package edu.cmu.sei.kalki.database;

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
    private String apiUrl;

    public NewSecurityStateHandler(String endpoint) { apiUrl = endpoint+"/send-command"; }

    @Override
    public void handleNewInsertion(int newStateId) {
        DeviceSecurityState ss = Postgres.findDeviceSecurityState(newStateId);
        Device device = Postgres.findDevice(ss.getDeviceId());

        // find devices in group
        List<Device> groupDevices = Postgres.findDevicesByGroup(device.getGroup().getId());
        if (groupDevices != null && groupDevices.size()>0){ // device is in a group
            for (Device d: groupDevices){
                List<DeviceCommand> commandList = Postgres.findCommandsForGroup(d, device);
                sendToIotInterface(d, commandList);
                logSendCommandReact(device, commandList.size());
            }
        } else {
            List<DeviceCommand> commandList = Postgres.findCommandsByDevice(device);
            sendToIotInterface(device, commandList);
            logSendCommandReact(device, commandList.size());
        }

    }

    private void sendToIotInterface(Device dev, List<DeviceCommand> comms) {
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
            httpCon.setDoOutput(true);
            httpCon.setRequestMethod("POST");
            OutputStreamWriter out = new OutputStreamWriter(httpCon.getOutputStream());

            JSONObject json = new JSONObject();
            json.put("device", dev.toString());
            json.put("command-list", comms);

            out.write(json.toString());
            out.close();
            httpCon.getInputStream();
        } catch (Exception e) {
            logger.severe("[NewSecurityStateHandler] Error sending device and commands to IoT Interface API: "+dev.toString());
            logger.severe(e.getMessage());
        }
    }

    private void logSendCommandReact(Device device, int numCommands) {
        StageLog log = new StageLog(device.getCurrentState().getId(), StageLog.Action.SEND_COMMAND, StageLog.Stage.REACT, "Sending "+numCommands+" commands to IoT Interface");
        log.insert();
    }
}
