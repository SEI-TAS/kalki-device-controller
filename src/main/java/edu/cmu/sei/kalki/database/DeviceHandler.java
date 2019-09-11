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
    private final int ATTEMPTS = 15;

    private String apiUrl;

    DeviceHandler(String endpoint) {
        apiUrl = endpoint;
    }

    @Override
    public void handleNewInsertion(int newDeviceId) {
        Device device = Postgres.findDevice(newDeviceId);
        sendToIotInterface(device);
    }

    private void sendToIotInterface(Device dev) {
        for(int i=0; i<ATTEMPTS; i++){
            try {
                URL url = new URL(this.apiUrl);
                HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
                httpCon.setDoOutput(true);
                httpCon.setRequestMethod("POST");
                OutputStreamWriter out = new OutputStreamWriter(httpCon.getOutputStream());
                JSONObject json = new JSONObject(dev.toString());
                out.write(json.toString());
                out.close();
                httpCon.getInputStream();
                break;
            } catch (Exception e) {
                logger.severe("[DeviceHandler] Error sending device to IoT Interface API "+apiUrl+": "+dev.toString());
                logger.severe(e.getMessage());
                logger.severe("[DeviceHandler] Attempting to reconnect...");
                sleep(1000);
            }
        }

    }

    private void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (Exception e) {
            logger.severe("[DeviceHandler] Error attempting to sleep: "+e.getMessage());
        }
    }
}
