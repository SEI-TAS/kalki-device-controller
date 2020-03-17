package edu.cmu.sei.kalki.dc;

import edu.cmu.sei.kalki.dc.utils.Config;
import edu.cmu.sei.kalki.db.models.Device;
import edu.cmu.sei.kalki.db.models.DeviceCommand;
import org.json.JSONObject;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.logging.Logger;

import static java.lang.Thread.sleep;

/**
 * Implements the commands to send requests to the IoTInterface's API.
 */
public class IoTInterfaceAPI
{
    private static final String LOG_ID = "[IoTInterfaceAPI] ";
    private final int ATTEMPTS = 15;

    private static final String PORT = "5050";
    private static final String BASE_URL = "/iot-interface-api";
    private static final String SEND_COMMAND = "/send-command";
    private static final String NEW_DEVICE = "/new-device";
    private static final String UPDATE_DEVICE = "/update-device";

    private Logger logger = Logger.getLogger("device-controller");

    /**
     * Returns the base URL.
     * @param serverIP
     * @return
     */
    private String getBaseURL(String serverIP) {
        return "http://" + serverIP + ":" + PORT + BASE_URL;
    }

    /**
     * Sends device information for a new device.
     * @param dev
     */
    public void sendNewDeviceInfo(Device dev) {
        sendDeviceInfo(dev, NEW_DEVICE);
    }

    /**
     * Sends updated device information for an existing device.
     * @param dev
     */
    public void sendUpdatedDeviceInfo(Device dev) {
        sendDeviceInfo(dev, UPDATE_DEVICE);
    }

    /**
     * Sends device information.
     * @param dev
     * @param endpoint
     */
    private void sendDeviceInfo(Device dev, String endpoint) {
        for(int i=0; i<ATTEMPTS; i++){
            JSONObject json = new JSONObject(dev.toString());
            if(this.sendToIotInterface(dev, endpoint, json)) {
                break;
            }
            else {
                try {
                    logger.severe(LOG_ID + "Attempting to reconnect...");
                    sleep(1000);
                } catch (InterruptedException ex) {
                    logger.severe(LOG_ID + "Error waiting to retry sending info: " + ex.getMessage());
                }
            }
        }
    }

    /**
     * Sends a group of commands.
     * @param dev
     * @param comms
     */
    public void sendCommands(Device dev, List<DeviceCommand> comms) {
        JSONObject json = new JSONObject();
        json.put("command-list", comms);
        json.put("device",new JSONObject(dev.toString()));
        this.sendToIotInterface(dev, SEND_COMMAND, json);
    }

    /**
     * Sends a request to the IoT Interface.
     * @param dev
     * @param endpoint
     * @param payload
     * @return
     */
    private boolean sendToIotInterface(Device dev, String endpoint, JSONObject payload) {
        String fullUrlString = getBaseURL(Config.data.get("data_node_ip")) + endpoint;

        try {
            URL fullUrl = new URL(fullUrlString);
            HttpURLConnection httpCon = (HttpURLConnection) fullUrl.openConnection();
            httpCon.setDoOutput(true);
            httpCon.setRequestMethod("POST");

            OutputStreamWriter out = new OutputStreamWriter(httpCon.getOutputStream());
            out.write(payload.toString());
            out.close();
            httpCon.getInputStream();

            return true;
        } catch (Exception e) {
            logger.severe(LOG_ID + "Error sending message to IoT Interface API " + fullUrlString + ": " + dev.toString());
            logger.severe(e.getMessage());
            return false;
        }
    }
}
