package edu.cmu.sei.kalki.database;

import edu.cmu.sei.ttg.kalki.database.Postgres;
import edu.cmu.sei.ttg.kalki.listeners.InsertHandler;
import edu.cmu.sei.ttg.kalki.models.Device;
import edu.cmu.sei.ttg.kalki.models.DeviceCommand;
import edu.cmu.sei.ttg.kalki.models.DeviceSecurityState;
import org.json.JSONObject;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.logging.Logger;

public class NewSecurityStateHandler implements InsertHandler {
    private Logger logger = Logger.getLogger("device-controller");

//    private final String apiUrl = "http://10.27.153.2:9090/iot-interface-api/send-command"; //production url
    private final String apiUrl = "http://10.27.151.103:9090/iot-interface-api/send-command"; //test url

    public NewSecurityStateHandler() { }

    @Override
    public void handleNewInsertion(int newStateId) {
        DeviceSecurityState ss = Postgres.findDeviceSecurityState(newStateId);
        Device device = Postgres.findDevice(ss.getDeviceId());
        List<DeviceCommand> commandList = Postgres.findCommandsByDevice(device);

        sendToIotInterface(device, commandList);
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
}
