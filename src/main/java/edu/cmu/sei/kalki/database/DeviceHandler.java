package edu.cmu.sei.kalki.database;

import edu.cmu.sei.ttg.kalki.database.Postgres;
import edu.cmu.sei.ttg.kalki.listeners.InsertHandler;
import edu.cmu.sei.ttg.kalki.models.Device;
import org.json.JSONObject;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Logger;

public class DeviceHandler implements InsertHandler {
    private Logger logger = Logger.getLogger("device-controller");

        private String apiUrl = "http://10.27.153.2:5050/iot-interface-api"; //production url
//    private String apiUrl = "http://10.27.153.103:9090/iot-interface-api"; //test url

    DeviceHandler(String endpoint) {
        apiUrl += endpoint;
    }

    @Override
    public void handleNewInsertion(int newDeviceId) {
        Device device = Postgres.findDevice(newDeviceId);
        sendToIotInterface(device);
    }

    private void sendToIotInterface(Device dev) {
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
            httpCon.setDoOutput(true);
            httpCon.setRequestMethod("POST");
            OutputStreamWriter out = new OutputStreamWriter(httpCon.getOutputStream());
            JSONObject json = new JSONObject(dev.toString());
            out.write(json.toString());
            out.close();
            httpCon.getInputStream();
        } catch (Exception e) {
            logger.severe("[DeviceHandler] Error sending device to IoT Interface API: "+dev.toString());
            logger.severe(e.getMessage());
        }
    }
}
